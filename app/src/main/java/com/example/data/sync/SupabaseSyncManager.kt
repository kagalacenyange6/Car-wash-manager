package com.example.data.sync

import android.content.Context
import com.example.data.db.CarWashDao
import com.example.data.model.Customer
import com.example.data.model.Expense
import com.example.data.model.Payment
import com.example.data.model.Vehicle
import com.example.data.model.WashOrder
import com.example.data.model.WashService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class SyncState {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR,
    OFFLINE
}

class SupabaseSyncManager(
    private val context: Context,
    private val dao: CarWashDao,
    private val scope: CoroutineScope
) {
    private val networkMonitor = NetworkMonitor(context)
    val configManager = SupabaseConfigManager(context)

    private val _isOnline = MutableStateFlow(networkMonitor.isCurrentlyOnline())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private val _syncMessage = MutableStateFlow("Prêt pour la synchronisation")
    val syncMessage: StateFlow<String> = _syncMessage.asStateFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    init {
        // Monitor online/offline transitions
        networkMonitor.isOnline
            .onEach { online ->
                val previous = _isOnline.value
                _isOnline.value = online
                if (!online) {
                    _syncState.value = SyncState.OFFLINE
                    _syncMessage.value = "Hors-ligne : Utilisation exclusive du stockage local SQLite"
                } else if (!previous && online) {
                    _syncMessage.value = "Connexion rétablie : Prêt à synchroniser avec Supabase"
                    // Auto-sync if enabled
                    val cfg = configManager.getConfig()
                    if (cfg.autoSyncEnabled && cfg.isConfigured) {
                        syncNow()
                    }
                }
            }
            .launchIn(scope)
    }

    suspend fun syncNow(): Boolean = withContext(Dispatchers.IO) {
        val online = networkMonitor.isCurrentlyOnline()
        _isOnline.value = online

        if (!online) {
            _syncState.value = SyncState.OFFLINE
            _syncMessage.value = "Hors-ligne : Données stockées localement dans SQLite"
            return@withContext false
        }

        val config = configManager.getConfig()
        if (!config.isConfigured) {
            _syncState.value = SyncState.OFFLINE
            _syncMessage.value = "Mode SQLite : Supabase non configuré (URL ou clé manquante)"
            return@withContext false
        }

        _syncState.value = SyncState.SYNCING
        _syncMessage.value = "Synchronisation Supabase en cours..."

        try {
            // 1. Gather all local SQLite data
            val customers = dao.getAllCustomersDirect()
            val vehicles = dao.getAllVehiclesDirect()
            val services = dao.getAllServicesDirect()
            val orders = dao.getAllOrdersDirect()
            val payments = dao.getAllPaymentsDirect()
            val expenses = dao.getAllExpensesDirect()

            // 2. Upload customers to Supabase table "customers"
            if (customers.isNotEmpty()) {
                uploadTable(config, "customers", customersToJson(customers))
            }

            // 3. Upload vehicles to Supabase table "vehicles"
            if (vehicles.isNotEmpty()) {
                uploadTable(config, "vehicles", vehiclesToJson(vehicles))
            }

            // 4. Upload services to Supabase table "services"
            if (services.isNotEmpty()) {
                uploadTable(config, "services", servicesToJson(services))
            }

            // 5. Upload orders to Supabase table "wash_orders"
            if (orders.isNotEmpty()) {
                uploadTable(config, "wash_orders", ordersToJson(orders))
            }

            // 6. Upload payments to Supabase table "payments"
            if (payments.isNotEmpty()) {
                uploadTable(config, "payments", paymentsToJson(payments))
            }

            // 7. Upload expenses to Supabase table "expenses"
            if (expenses.isNotEmpty()) {
                uploadTable(config, "expenses", expensesToJson(expenses))
            }

            _lastSyncTimestamp.value = System.currentTimeMillis()
            _syncState.value = SyncState.SUCCESS
            _syncMessage.value = "Synchronisé avec Supabase (${orders.size} prestations, ${customers.size} clients)"
            true
        } catch (e: Exception) {
            // SQLite local remains completely safe and unaffected!
            _syncState.value = SyncState.ERROR
            _syncMessage.value = "Erreur synchro Supabase : ${e.localizedMessage ?: "Serveur injoignable"}. Utilisation continue de SQLite."
            false
        }
    }

    private fun uploadTable(config: SupabaseConfig, table: String, jsonArray: JSONArray) {
        val baseUrl = config.url.trim().removeSuffix("/").removeSuffix("/rest/v1")
        val url = "$baseUrl/rest/v1/$table"
        val requestBody = jsonArray.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.anonKey.trim())
            .addHeader("Authorization", "Bearer ${config.anonKey.trim()}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        val responseCode = response.code
        val responseBody = response.body?.string() ?: ""
        response.close()

        if (responseCode !in 200..299) {
            val diagnostic = when {
                responseCode == 401 || responseCode == 403 ->
                    "Accès refusé (HTTP $responseCode). La clé anonKey n'est pas autorisée ou RLS bloque l'écriture. Exécutez le script SQL fourni."
                responseCode == 404 || responseBody.contains("does not exist") || responseBody.contains("42P01") ->
                    "La table '$table' n'existe pas encore sur votre base Supabase. Cliquez sur 'Copier le script SQL' et exécutez-le dans Supabase > SQL Editor."
                responseBody.contains("row-level security") || responseBody.contains("violates row-level") ->
                    "Sécurité RLS active sur '$table'. Exécutez le script SQL qui applique 'ALTER TABLE $table DISABLE ROW LEVEL SECURITY;'."
                else ->
                    "HTTP $responseCode: ${responseBody.take(150)}"
            }
            throw Exception("Table '$table': $diagnostic")
        }
    }

    suspend fun clearRemoteTables(config: SupabaseConfig): Pair<Boolean, String> =
        withContext(Dispatchers.IO) {
            if (!config.isConfigured) {
                return@withContext Pair(false, "Supabase non configuré")
            }
            try {
                val baseUrl = config.url.trim().removeSuffix("/").removeSuffix("/rest/v1")
                val tables = listOf(
                    "wash_order_items",
                    "payments",
                    "wash_orders",
                    "expenses",
                    "stock_movements",
                    "products",
                    "vehicles",
                    "customers",
                    "loyalty_accounts",
                    "notifications",
                    "audit_logs"
                )
                for (tbl in tables) {
                    val url = "$baseUrl/rest/v1/$tbl?id=not.is.null"
                    val request = Request.Builder()
                        .url(url)
                        .addHeader("apikey", config.anonKey.trim())
                        .addHeader("Authorization", "Bearer ${config.anonKey.trim()}")
                        .delete()
                        .build()
                    val res = httpClient.newCall(request).execute()
                    res.close()
                }
                Pair(true, "Base de données Supabase distante vidée avec succès !")
            } catch (e: Exception) {
                Pair(false, "Échec vidage Supabase : ${e.localizedMessage}")
            }
        }

    suspend fun testConnection(url: String, anonKey: String): Pair<Boolean, String> =
        withContext(Dispatchers.IO) {
            var trimmedUrl = url.trim().replace("\n", "").replace("\r", "").removeSuffix("/")
            val trimmedKey = anonKey.trim().replace("\n", "").replace("\r", "")

            if (trimmedUrl.contains("supabase.com/dashboard") || trimmedUrl.contains("/project/")) {
                return@withContext Pair(
                    false,
                    "URL incorrecte : Vous avez entré l'URL du tableau de bord de votre navigateur. Vous devez renseigner l'URL d'API de votre projet (ex: https://[identifiant].supabase.co) trouvable dans Supabase > Project Settings > Configuration > API."
                )
            }

            // Normalize URL if protocol was omitted
            if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
                trimmedUrl = "https://$trimmedUrl"
            }
            trimmedUrl = trimmedUrl.removeSuffix("/rest/v1").removeSuffix("/")

            if (!trimmedUrl.startsWith("https://")) {
                return@withContext Pair(false, "L'URL Supabase doit débuter par https:// (ex: https://xyz.supabase.co)")
            }
            if (trimmedKey.isBlank()) {
                return@withContext Pair(false, "La clé API anon Supabase est requise (clé publique anon du projet).")
            }

            try {
                // Perform check on Supabase PostgREST table endpoint
                // Querying /rest/v1/companies or /rest/v1/customers with limit 1 is the standard test with an anon key
                val testTableReq = Request.Builder()
                    .url("$trimmedUrl/rest/v1/companies?limit=1")
                    .addHeader("apikey", trimmedKey)
                    .addHeader("Authorization", "Bearer $trimmedKey")
                    .get()
                    .build()

                val tableRes = httpClient.newCall(testTableReq).execute()
                val tableCode = tableRes.code
                val tableBody = tableRes.body?.string() ?: ""
                tableRes.close()

                if (tableCode in 200..299) {
                    return@withContext Pair(true, "Connexion et tables Supabase prêtes ! Synchronisation possible.")
                } else if (tableCode == 404 || tableBody.contains("does not exist") || tableBody.contains("42P01")) {
                    return@withContext Pair(true, "Connexion API Supabase validée (HTTP 200) !\nAttention : Les tables du Car Wash ne sont pas encore créées dans votre base. Utilisez le bouton 'Copier le script SQL' et exécutez-le dans Supabase > SQL Editor.")
                }

                // If companies returned another code, check /rest/v1/customers
                val testCustReq = Request.Builder()
                    .url("$trimmedUrl/rest/v1/customers?limit=1")
                    .addHeader("apikey", trimmedKey)
                    .addHeader("Authorization", "Bearer $trimmedKey")
                    .get()
                    .build()

                val custRes = httpClient.newCall(testCustReq).execute()
                val custCode = custRes.code
                val custBody = custRes.body?.string() ?: ""
                custRes.close()

                when {
                    custCode in 200..299 -> {
                        Pair(true, "Connexion et tables Supabase prêtes ! Synchronisation possible.")
                    }
                    custCode == 404 || custBody.contains("does not exist") || custBody.contains("42P01") -> {
                        Pair(true, "Connexion API Supabase validée !\nAttention : Les tables ne sont pas encore créées. Utilisez le bouton 'Copier le script SQL' pour les générer dans Supabase.")
                    }
                    custCode == 401 || custCode == 403 || tableCode == 401 || tableCode == 403 -> {
                        Pair(false, "Erreur d'authentification (HTTP ${if (custCode == 401 || custCode == 403) custCode else tableCode}) : Clé API non autorisée ou RLS actif. Vérifiez la clé 'anon / public' dans Supabase.")
                    }
                    else -> {
                        Pair(false, "Réponse Supabase inattendue (HTTP $custCode) : ${custBody.take(160)}")
                    }
                }
            } catch (e: java.net.UnknownHostException) {
                Pair(false, "Hôte Supabase introuvable ($trimmedUrl) : Vérifiez que l'URL est correcte et que l'appareil a accès à Internet.")
            } catch (e: java.net.SocketTimeoutException) {
                Pair(false, "Délai d'attente dépassé (Timeout) : Le serveur Supabase n'a pas répondu à temps. Vérifiez votre connexion Internet.")
            } catch (e: Exception) {
                Pair(false, "Erreur de connexion : ${e.localizedMessage ?: e.javaClass.simpleName}")
            }
        }

    // JSON serialisation helpers
    private fun customersToJson(customers: List<Customer>): JSONArray {
        val array = JSONArray()
        customers.forEach { c ->
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("phone", c.phone)
                put("address", c.address)
                put("notes", c.notes)
                put("created_at", c.createdAt)
            }
            array.put(obj)
        }
        return array
    }

    private fun vehiclesToJson(vehicles: List<Vehicle>): JSONArray {
        val array = JSONArray()
        vehicles.forEach { v ->
            val obj = JSONObject().apply {
                put("id", v.id)
                put("customer_id", v.customerId)
                put("plate", v.plate)
                put("brand", v.brand)
                put("model", v.model)
                put("color", v.color)
                put("vehicle_type", v.vehicleType)
                put("notes", v.notes)
                put("created_at", v.createdAt)
            }
            array.put(obj)
        }
        return array
    }

    private fun servicesToJson(services: List<WashService>): JSONArray {
        val array = JSONArray()
        services.forEach { s ->
            val obj = JSONObject().apply {
                put("id", s.id)
                put("company_id", s.companyId)
                put("name", s.name)
                put("price", s.price)
                put("duration_minutes", s.durationMinutes)
                put("is_active", s.isActive)
                put("icon_key", s.iconKey)
            }
            array.put(obj)
        }
        return array
    }

    private fun ordersToJson(orders: List<WashOrder>): JSONArray {
        val array = JSONArray()
        orders.forEach { o ->
            val obj = JSONObject().apply {
                put("id", o.id)
                put("company_id", o.companyId)
                put("order_number", o.orderNumber)
                put("customer_id", o.customerId)
                put("customer_name", o.customerName)
                put("customer_phone", o.customerPhone)
                put("vehicle_id", o.vehicleId)
                put("vehicle_plate", o.vehiclePlate)
                put("vehicle_model", o.vehicleModel)
                put("status", o.status)
                put("payment_status", o.paymentStatus)
                put("total_amount", o.totalAmount)
                put("discount_amount", o.discountAmount)
                put("final_amount", o.finalAmount)
                put("paid_amount", o.paidAmount)
                put("services_summary", o.servicesSummary)
                put("created_at", o.createdAt)
            }
            array.put(obj)
        }
        return array
    }

    private fun paymentsToJson(payments: List<Payment>): JSONArray {
        val array = JSONArray()
        payments.forEach { p ->
            val obj = JSONObject().apply {
                put("id", p.id)
                put("company_id", p.companyId)
                put("order_id", p.orderId)
                put("order_number", p.orderNumber)
                put("customer_name", p.customerName)
                put("vehicle_plate", p.vehiclePlate)
                put("amount", p.amount)
                put("payment_method", p.paymentMethod)
                put("reference", p.reference)
                put("employee_name", p.employeeName)
                put("created_at", p.createdAt)
            }
            array.put(obj)
        }
        return array
    }

    private fun expensesToJson(expenses: List<Expense>): JSONArray {
        val array = JSONArray()
        expenses.forEach { e ->
            val obj = JSONObject().apply {
                put("id", e.id)
                put("description", e.description)
                put("amount", e.amount)
                put("category", e.category)
                put("created_at", e.date)
            }
            array.put(obj)
        }
        return array
    }
}
