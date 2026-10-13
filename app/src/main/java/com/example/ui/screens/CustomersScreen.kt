package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import com.example.data.model.Customer
import com.example.data.model.Vehicle
import com.example.data.model.WashOrder
import com.example.ui.CarWashViewModel
import com.example.ui.components.CarWashInputField
import com.example.ui.components.CarWashPrimaryButton
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FormErrorBanner
import com.example.ui.components.FormSheetHeader
import com.example.ui.components.carWashInputTextStyle
import com.example.ui.components.carWashTextFieldColors
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDebt
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    viewModel: CarWashViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val loyaltyList by viewModel.loyaltyAccounts.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCustomerForDetails by remember { mutableStateOf<Customer?>(null) }
    var customerToDeleteConfirm by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Navy900,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("customers_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un client")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customers_search_input"),
                placeholder = {
                    Text(
                        "Rechercher par nom ou numéro...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BlueAccent) },
                singleLine = true,
                textStyle = carWashInputTextStyle,
                shape = RoundedCornerShape(12.dp),
                colors = carWashTextFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredCustomers.isEmpty()) {
                EmptyStateView(
                    message = "Aucun client trouvé.",
                    icon = Icons.Default.Person
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        val customerVehicles = vehicles.filter { it.customerId == customer.id }
                        val customerOrders = orders.filter { it.customerId == customer.id }
                        val totalSpent = customerOrders.sumOf { it.paidAmount }
                        val totalDebt = customerOrders.sumOf { (it.finalAmount - it.paidAmount).coerceAtLeast(0.0) }
                        val loyalty = loyaltyList.firstOrNull { it.customerId == customer.id }

                        CustomerCard(
                            customer = customer,
                            vehicles = customerVehicles,
                            totalOrders = customerOrders.size,
                            totalSpent = totalSpent,
                            totalDebt = totalDebt,
                            loyaltyPoints = loyalty?.points ?: 0,
                            currency = currency,
                            onClick = { selectedCustomerForDetails = customer },
                            onNewOrder = {
                                val firstVeh = customerVehicles.firstOrNull()
                                viewModel.startNewOrder(customer, firstVeh)
                            },
                            onDelete = {
                                customerToDeleteConfirm = customer
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Direct Customer Delete Confirmation Dialog
    customerToDeleteConfirm?.let { cust ->
        AlertDialog(
            onDismissRequest = { customerToDeleteConfirm = null },
            title = { Text("Supprimer le client ?", fontWeight = FontWeight.Bold, color = Navy900) },
            text = { Text("Êtes-vous sûr de vouloir supprimer ${cust.name} (${cust.phone}) ? Toutes ses données associées seront supprimées.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(cust)
                        customerToDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDebt)
                ) {
                    Text("Oui, Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { customerToDeleteConfirm = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Add Customer Dialog
    if (showAddDialog) {
        AddCustomerSheet(
            onDismiss = { showAddDialog = false },
            onSave = { name, phone, address, notes ->
                viewModel.addCustomer(name, phone, address, notes)
                showAddDialog = false
            }
        )
    }

    // Customer Detail Sheet
    selectedCustomerForDetails?.let { cust ->
        val customerVehicles = vehicles.filter { it.customerId == cust.id }
        val customerOrders = orders.filter { it.customerId == cust.id }
        val loyalty = loyaltyList.firstOrNull { it.customerId == cust.id }

        CustomerDetailSheet(
            customer = cust,
            vehicles = customerVehicles,
            orders = customerOrders,
            payments = payments,
            loyaltyPoints = loyalty?.points ?: 0,
            currency = currency,
            onDismiss = { selectedCustomerForDetails = null },
            onNewOrder = {
                selectedCustomerForDetails = null
                viewModel.startNewOrder(cust, customerVehicles.firstOrNull())
            },
            onDeleteCustomer = {
                viewModel.deleteCustomer(cust)
                selectedCustomerForDetails = null
            },
            onPayDebt = { order ->
                selectedCustomerForDetails = null
                viewModel.openPaymentDialog(order)
            }
        )
    }
}

private fun getAvatarGradient(name: String): Brush {
    val palettes = listOf(
        listOf(Color(0xFF0284C7), Color(0xFF0F172A)), // Deep ocean & navy
        listOf(Color(0xFF6366F1), Color(0xFF4338CA)), // Indigo royal
        listOf(Color(0xFF059669), Color(0xFF065F46)), // Emerald
        listOf(Color(0xFFD97706), Color(0xFFB45309)), // Amber
        listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)), // Purple
        listOf(Color(0xFFEC4899), Color(0xFFBE185D)), // Rose
        listOf(Color(0xFF0891B2), Color(0xFF155E75)), // Cyan
        listOf(Color(0xFFDC2626), Color(0xFF991B1B))  // Crimson
    )
    val hash = kotlin.math.abs(name.trim().hashCode())
    return Brush.linearGradient(palettes[hash % palettes.size])
}

private fun getInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "CL"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    vehicles: List<Vehicle>,
    totalOrders: Int,
    totalSpent: Double,
    totalDebt: Double,
    loyaltyPoints: Int,
    currency: String,
    onClick: () -> Unit,
    onNewOrder: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("customer_card_${customer.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Enhanced Avatar with Modern Vibrant Gradient & Shadow Ring
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(getAvatarGradient(customer.name))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(customer.name),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customer.name,
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 15.sp
                        )
                        Text(
                            text = customer.phone,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Action buttons: Delete & Call
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer le client",
                            tint = Color(0xFFFDA4AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Call Intent Button
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Appeler", tint = BlueAccent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "${vehicles.size} véhicule(s)",
                        fontSize = 11.sp,
                        color = Navy900,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "$totalOrders passage(s)",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (loyaltyPoints > 0) {
                    Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "⭐ $loyaltyPoints pts",
                            fontSize = 11.sp,
                            color = BlueAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (totalDebt > 0) {
                    Surface(color = Color(0xFFFFE4E6), shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "Dette: ${String.format(Locale.US, "%.1f %s", totalDebt, currency)}",
                            fontSize = 11.sp,
                            color = StatusDebt,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Nouvelle prestation
            Button(
                onClick = onNewOrder,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Icon(Icons.Default.LocalCarWash, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lancer prestation", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, address: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = modifier.testTag("add_customer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            FormSheetHeader(
                title = "Nouveau Client",
                subtitle = "Enregistrez le client pour son historique, ses véhicules et sa fidélité",
                icon = Icons.Default.Person,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (errorMessage != null) {
                FormErrorBanner(message = errorMessage!!)
                Spacer(modifier = Modifier.height(12.dp))
            }

            CarWashInputField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = "Nom complet *",
                placeholder = "ex: Jean-Marc Mukendi",
                leadingIcon = Icons.Default.Person,
                isError = errorMessage != null && name.isBlank(),
                supportingText = if (errorMessage != null && name.isBlank()) "Le nom est obligatoire" else null,
                testTag = "add_customer_name"
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = phone,
                onValueChange = {
                    phone = it
                    errorMessage = null
                },
                label = "Numéro de téléphone",
                placeholder = "ex: +243 81 234 5678",
                leadingIcon = Icons.Default.Phone,
                testTag = "add_customer_phone"
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = address,
                onValueChange = { address = it },
                label = "Adresse / Commune / Quartier",
                placeholder = "ex: Gombe, Av. Justice",
                leadingIcon = Icons.Default.LocationOn
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes et préférences",
                placeholder = "ex: Préfère le lavage mousse, client régulier",
                leadingIcon = Icons.Default.Notes
            )

            Spacer(modifier = Modifier.height(20.dp))

            CarWashPrimaryButton(
                text = "Enregistrer le client",
                onClick = {
                    if (name.trim().isBlank()) {
                        errorMessage = "Veuillez entrer le nom du client"
                        return@CarWashPrimaryButton
                    }
                    onSave(name.trim(), phone.trim().ifBlank { "-" }, address.trim(), notes.trim())
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailSheet(
    customer: Customer,
    vehicles: List<Vehicle>,
    orders: List<WashOrder>,
    payments: List<com.example.data.model.Payment> = emptyList(),
    loyaltyPoints: Int,
    currency: String,
    onDismiss: () -> Unit,
    onNewOrder: () -> Unit,
    onDeleteCustomer: () -> Unit = {},
    onPayDebt: (WashOrder) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalSpent = orders.sumOf { it.paidAmount }
    val totalDebt = orders.sumOf { (it.finalAmount - it.paidAmount).coerceAtLeast(0.0) }
    val unpaidOrders = orders.filter { (it.finalAmount - it.paidAmount) > 0.0 }
    val customerPayments = remember(orders, payments) {
        val orderIds = orders.map { it.id }.toSet()
        payments.filter { it.orderId in orderIds }
    }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Supprimer le client ?", fontWeight = FontWeight.Bold, color = Navy900) },
            text = { Text("Êtes-vous sûr de vouloir supprimer ${customer.name} ? Cette action est irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteCustomer()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDebt)
                ) {
                    Text("Oui, Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = modifier.testTag("customer_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(getAvatarGradient(customer.name))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(customer.name),
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Navy900, fontSize = 18.sp)
                        )
                        Text(text = customer.phone, fontSize = 13.sp, color = TextSecondary)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat summaries
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Dépensé", fontSize = 11.sp, color = TextSecondary)
                        Text(String.format(Locale.US, "%.1f %s", totalSpent, currency), fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = if (totalDebt > 0) Color(0xFFFFE4E6) else Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Dette active", fontSize = 11.sp, color = if (totalDebt > 0) StatusDebt else TextSecondary)
                        Text(String.format(Locale.US, "%.1f %s", totalDebt, currency), fontWeight = FontWeight.Bold, color = if (totalDebt > 0) StatusDebt else Navy900, fontSize = 14.sp)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Fidélité", fontSize = 11.sp, color = BlueAccent)
                        Text("$loyaltyPoints pts", fontWeight = FontWeight.Bold, color = BlueAccent, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 1: Dettes & Impayés actifs
            if (unpaidOrders.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF1F2),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MoneyOff, contentDescription = null, tint = StatusDebt, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dettes à Recouvrer (${unpaidOrders.size})", fontWeight = FontWeight.Bold, color = Color(0xFF9F1239), fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        unpaidOrders.forEach { ord ->
                            val remainingDebt = (ord.finalAmount - ord.paidAmount).coerceAtLeast(0.0)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${ord.orderNumber} • ${ord.vehiclePlate}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Navy900)
                                    Text("Reste : ${String.format(Locale.US, "%.1f %s", remainingDebt, currency)} (Payé ${ord.paidAmount} / ${ord.finalAmount})", fontSize = 11.sp, color = StatusDebt)
                                }
                                Button(
                                    onClick = { onPayDebt(ord) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusDebt),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Régler dette", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Section 2: Historique des paiements encaissés
            Text("Historique des Règlements (${customerPayments.size}) :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            if (customerPayments.isEmpty()) {
                Text("Aucun paiement direct enregistré.", fontSize = 12.sp, color = TextSecondary)
            } else {
                customerPayments.take(6).forEach { pay ->
                    val payDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(java.util.Date(pay.createdAt))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payment, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("${pay.orderNumber} • ${pay.paymentMethod}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Navy900)
                                    Text("$payDate • Ref: ${pay.reference.ifBlank { "Direct" }}", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Text(
                                text = "+${String.format(Locale.US, "%.1f %s", pay.amount, currency)}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF16A34A),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 3: Véhicules du client
            Text("Véhicules (${vehicles.size}) :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            if (vehicles.isEmpty()) {
                Text("Aucun véhicule associé.", fontSize = 12.sp, color = TextSecondary)
            } else {
                vehicles.forEach { v ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                            Text(v.plate, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${v.brand} ${v.model} (${v.washCount} lavages)", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = StatusDebt
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = StatusDebt, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Supprimer", color = StatusDebt, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onNewOrder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Navy900,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(2f).height(48.dp)
                ) {
                    Text("Nouvelle prestation", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }
    }
}
