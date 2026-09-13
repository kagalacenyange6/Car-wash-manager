package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.CarWashDatabase
import com.example.data.model.AppNotification
import com.example.data.model.AuditLog
import com.example.data.model.Company
import com.example.data.model.Customer
import com.example.data.model.Employee
import com.example.data.model.Expense
import com.example.data.model.LoyaltyAccount
import com.example.data.model.OrderStatus
import com.example.data.model.Payment
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.data.model.UserRole
import com.example.data.model.Vehicle
import com.example.data.model.WashOrder
import com.example.data.model.WashOrderItem
import com.example.data.model.WashService
import com.example.data.repository.CarWashRepository
import com.example.data.sync.SupabaseSyncManager
import com.example.data.sync.SyncState
import com.example.util.ReceiptPrinter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppTab(val title: String) {
    ACCUEIL("Accueil"),
    PRESTATIONS("Prestations"),
    CLIENTS("Clients"),
    VEHICULES("Véhicules"),
    PLUS("Plus")
}

enum class PlusScreen(val title: String) {
    NONE("Menu"),
    QUEUE("File d'attente"),
    PAYMENTS("Paiements"),
    EXPENSES("Dépenses"),
    STOCK("Stock"),
    EMPLOYEES("Employés"),
    REPORTS("Rapports"),
    LOYALTY("Fidélité"),
    NOTIFICATIONS("Notifications"),
    SETTINGS("Paramètres"),
    AUDIT_LOGS("Journal d'activité"),
    SERVICES_MGMT("Gestion des Services")
}

class CarWashViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CarWashRepository
    val syncManager: SupabaseSyncManager

    init {
        val db = CarWashDatabase.getDatabase(application)
        val dao = db.carWashDao()
        repository = CarWashRepository(dao)
        syncManager = SupabaseSyncManager(application, dao, viewModelScope)
        viewModelScope.launch {
            repository.seedDemoDataIfEmpty()
        }
    }

    // Cloud / SQLite Sync State
    val isOnline: StateFlow<Boolean> get() = syncManager.isOnline
    val syncState: StateFlow<SyncState> get() = syncManager.syncState
    val syncMessage: StateFlow<String> get() = syncManager.syncMessage
    val lastSyncTimestamp: StateFlow<Long> get() = syncManager.lastSyncTimestamp

    private val _isSupabaseDialogOpen = MutableStateFlow(false)
    val isSupabaseDialogOpen: StateFlow<Boolean> = _isSupabaseDialogOpen.asStateFlow()

    fun openSupabaseDialog() { _isSupabaseDialogOpen.value = true }
    fun closeSupabaseDialog() { _isSupabaseDialogOpen.value = false }

    fun triggerSync() {
        viewModelScope.launch {
            val success = syncManager.syncNow()
            if (success) {
                showMessage("Synchronisation Supabase réussie !")
            } else {
                showMessage(syncManager.syncMessage.value)
            }
        }
    }

    // State Flows from DB
    val company: StateFlow<Company?> = repository.companyFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    val customers: StateFlow<List<Customer>> = repository.customersFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val vehicles: StateFlow<List<Vehicle>> = repository.vehiclesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val services: StateFlow<List<WashService>> = repository.servicesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val activeServices: StateFlow<List<WashService>> = repository.activeServicesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val orders: StateFlow<List<WashOrder>> = repository.allOrdersFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val queueOrders: StateFlow<List<WashOrder>> = repository.queueOrdersFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val payments: StateFlow<List<Payment>> = repository.paymentsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val expenses: StateFlow<List<Expense>> = repository.expensesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val products: StateFlow<List<Product>> = repository.productsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val stockMovements: StateFlow<List<StockMovement>> = repository.stockMovementsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val employees: StateFlow<List<Employee>> = repository.employeesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val loyaltyAccounts: StateFlow<List<LoyaltyAccount>> = repository.loyaltyAccountsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val notifications: StateFlow<List<AppNotification>> = repository.notificationsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCountFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )
    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Current Session
    private val _currentEmployee = MutableStateFlow<Employee?>(null)
    val currentEmployee: StateFlow<Employee?> = _currentEmployee.asStateFlow()

    init {
        viewModelScope.launch {
            employees.collect { list ->
                if (_currentEmployee.value == null && list.isNotEmpty()) {
                    _currentEmployee.value = list.firstOrNull { it.role == UserRole.ADMIN.name } ?: list.first()
                }
            }
        }
    }

    fun switchEmployee(employee: Employee) {
        _currentEmployee.value = employee
        viewModelScope.launch {
            repository.logAudit(employee.name, employee.role, "Connexion", "Session changée pour ${employee.name}")
        }
    }

    // Navigation
    private val _currentTab = MutableStateFlow(AppTab.ACCUEIL)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _currentPlusScreen = MutableStateFlow(PlusScreen.NONE)
    val currentPlusScreen: StateFlow<PlusScreen> = _currentPlusScreen.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
        if (tab != AppTab.PLUS) {
            _currentPlusScreen.value = PlusScreen.NONE
        }
    }

    fun openPlusScreen(screen: PlusScreen) {
        _currentPlusScreen.value = screen
    }

    fun closePlusScreen() {
        _currentPlusScreen.value = PlusScreen.NONE
    }

    // UI Message Events
    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    fun showMessage(msg: String) {
        viewModelScope.launch {
            _uiMessage.emit(msg)
        }
    }

    // NEW ORDER WIZARD STATE
    private val _isNewOrderSheetOpen = MutableStateFlow(false)
    val isNewOrderSheetOpen: StateFlow<Boolean> = _isNewOrderSheetOpen.asStateFlow()

    private val _newOrderStep = MutableStateFlow(1) // 1: Client, 2: Véhicule, 3: Services, 4: Confirmer
    val newOrderStep: StateFlow<Int> = _newOrderStep.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    private val _selectedVehicle = MutableStateFlow<Vehicle?>(null)
    val selectedVehicle: StateFlow<Vehicle?> = _selectedVehicle.asStateFlow()

    private val _selectedServices = MutableStateFlow<List<WashService>>(emptyList())
    val selectedServices: StateFlow<List<WashService>> = _selectedServices.asStateFlow()

    private val _discount = MutableStateFlow(0.0)
    val discount: StateFlow<Double> = _discount.asStateFlow()

    private val _orderNotes = MutableStateFlow("")
    val orderNotes: StateFlow<String> = _orderNotes.asStateFlow()

    fun startNewOrder(preselectedCustomer: Customer? = null, preselectedVehicle: Vehicle? = null) {
        _selectedCustomer.value = preselectedCustomer
        _selectedVehicle.value = preselectedVehicle
        _selectedServices.value = emptyList()
        _discount.value = 0.0
        _orderNotes.value = ""
        _newOrderStep.value = if (preselectedCustomer != null && preselectedVehicle != null) 3 else if (preselectedCustomer != null) 2 else 1
        _isNewOrderSheetOpen.value = true
    }

    fun closeNewOrder() {
        _isNewOrderSheetOpen.value = false
    }

    fun setOrderStep(step: Int) {
        _newOrderStep.value = step
    }

    fun selectCustomerForOrder(customer: Customer) {
        _selectedCustomer.value = customer
        _selectedVehicle.value = null
        _newOrderStep.value = 2
    }

    fun selectVehicleForOrder(vehicle: Vehicle) {
        _selectedVehicle.value = vehicle
        _newOrderStep.value = 3
    }

    fun toggleServiceSelection(service: WashService) {
        val current = _selectedServices.value.toMutableList()
        if (current.any { it.id == service.id }) {
            current.removeAll { it.id == service.id }
        } else {
            current.add(service)
        }
        _selectedServices.value = current
    }

    fun setDiscount(amount: Double) {
        _discount.value = amount.coerceAtLeast(0.0)
    }

    fun setOrderNotes(notes: String) {
        _orderNotes.value = notes
    }

    fun confirmCreateOrder() {
        val cust = _selectedCustomer.value ?: return
        val veh = _selectedVehicle.value ?: return
        val srvs = _selectedServices.value
        if (srvs.isEmpty()) {
            showMessage("Veuillez sélectionner au moins un service")
            return
        }

        val employeeName = _currentEmployee.value?.name ?: "Laveur"

        viewModelScope.launch {
            val order = repository.createWashOrder(
                customer = cust,
                vehicle = veh,
                selectedServices = srvs,
                discount = _discount.value,
                notes = _orderNotes.value,
                employeeName = employeeName
            )
            _isNewOrderSheetOpen.value = false
            showMessage("Prestation ${order.orderNumber} créée et ajoutée à la file d'attente !")
            if (syncManager.isOnline.value) {
                syncManager.syncNow()
            }
        }
    }

    // ORDER ACTIONS (QUEUE TRANSITIONS)
    fun startWash(orderId: String) {
        val emp = _currentEmployee.value?.name ?: "Opérateur"
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.IN_PROGRESS, emp)
            showMessage("Lavage démarré !")
        }
    }

    fun finishWash(orderId: String) {
        val emp = _currentEmployee.value?.name ?: "Opérateur"
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.COMPLETED, emp)
            showMessage("Lavage terminé ! Prêt pour encaissement.")
            // Automatically open payment dialog for convenience
            val order = orders.value.firstOrNull { it.id == orderId }
            if (order != null) {
                openPaymentDialog(order)
            }
        }
    }

    // PAYMENT DIALOG & FLOW
    private val _paymentDialogOpen = MutableStateFlow(false)
    val paymentDialogOpen: StateFlow<Boolean> = _paymentDialogOpen.asStateFlow()

    private val _targetPaymentOrder = MutableStateFlow<WashOrder?>(null)
    val targetPaymentOrder: StateFlow<WashOrder?> = _targetPaymentOrder.asStateFlow()

    fun openPaymentDialog(order: WashOrder) {
        _targetPaymentOrder.value = order
        _paymentDialogOpen.value = true
    }

    fun closePaymentDialog() {
        _paymentDialogOpen.value = false
        _targetPaymentOrder.value = null
    }

    fun executePayment(
        order: WashOrder,
        amount: Double,
        method: PaymentMethod,
        reference: String
    ) {
        val emp = _currentEmployee.value?.name ?: "Caissier"
        viewModelScope.launch {
            val payment = repository.recordPayment(order.id, amount, method, reference, emp)
            closePaymentDialog()
            showMessage("Paiement de ${payment.amount} enregistré !")
            // Fetch updated order & items to immediately show POS thermal ticket
            val updated = orders.value.firstOrNull { it.id == order.id } ?: order.copy(
                paidAmount = order.paidAmount + amount,
                paymentStatus = if (order.finalAmount - (order.paidAmount + amount) <= 0.001) PaymentStatus.PAID.name else PaymentStatus.DEBT.name
            )
            openReceiptDialog(updated)
            if (syncManager.isOnline.value) {
                syncManager.syncNow()
            }
        }
    }

    // POS THERMAL RECEIPT DIALOG
    private val _receiptDialogOpen = MutableStateFlow(false)
    val receiptDialogOpen: StateFlow<Boolean> = _receiptDialogOpen.asStateFlow()

    private val _targetReceiptOrder = MutableStateFlow<WashOrder?>(null)
    val targetReceiptOrder: StateFlow<WashOrder?> = _targetReceiptOrder.asStateFlow()

    private val _targetReceiptItems = MutableStateFlow<List<WashOrderItem>>(emptyList())
    val targetReceiptItems: StateFlow<List<WashOrderItem>> = _targetReceiptItems.asStateFlow()

    fun openReceiptDialog(order: WashOrder) {
        _targetReceiptOrder.value = order
        viewModelScope.launch {
            _targetReceiptItems.value = repository.getOrderItems(order.id)
            _receiptDialogOpen.value = true
        }
    }

    fun closeReceiptDialog() {
        _receiptDialogOpen.value = false
        _targetReceiptOrder.value = null
        _targetReceiptItems.value = emptyList()
    }

    fun printReceipt(context: Context, order: WashOrder) {
        val comp = company.value ?: Company()
        val allPays = payments.value
        ReceiptPrinter.printThermalReceipt(context, order, _targetReceiptItems.value, allPays, comp)
    }

    // CUSTOMER MANAGEMENT
    fun addCustomer(name: String, phone: String, address: String, notes: String, onCreated: (Customer) -> Unit = {}) {
        viewModelScope.launch {
            val c = repository.addCustomer(name, phone, address, notes)
            showMessage("Client ${c.name} ajouté !")
            onCreated(c)
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            showMessage("Client mis à jour !")
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            showMessage("Client supprimé")
        }
    }

    // VEHICLE MANAGEMENT
    fun addVehicle(
        customerId: String,
        plate: String,
        brand: String,
        model: String,
        color: String,
        type: String,
        notes: String,
        onCreated: (Vehicle) -> Unit = {}
    ) {
        viewModelScope.launch {
            val v = repository.addVehicle(customerId, plate, brand, model, color, type, notes)
            showMessage("Véhicule ${v.plate} enregistré !")
            onCreated(v)
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
            showMessage("Véhicule supprimé")
        }
    }

    // EXPENSES
    fun addExpense(category: String, amount: Double, description: String) {
        val emp = _currentEmployee.value?.name ?: "Gérant"
        viewModelScope.launch {
            repository.addExpense(category, amount, description, emp)
            showMessage("Dépense de $amount enregistrée !")
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            showMessage("Dépense supprimée")
        }
    }

    // STOCK
    fun addProduct(name: String, stock: Double, unit: String, minThreshold: Double, unitPrice: Double) {
        viewModelScope.launch {
            repository.addProduct(name, stock, unit, minThreshold, unitPrice)
            showMessage("Produit $name ajouté au stock !")
        }
    }

    fun adjustStock(productId: String, type: String, qty: Double, reason: String) {
        val emp = _currentEmployee.value?.name ?: "Gérant"
        viewModelScope.launch {
            repository.adjustStock(productId, type, qty, reason, emp)
            showMessage("Mouvement de stock enregistré !")
        }
    }

    // EMPLOYEES
    fun addEmployee(name: String, email: String, phone: String, role: String, pinCode: String) {
        viewModelScope.launch {
            repository.addEmployee(name, email, phone, role, pinCode)
            showMessage("Employé $name ajouté !")
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
            showMessage("Employé supprimé")
        }
    }

    // SERVICES
    fun addService(name: String, price: Double, duration: Int) {
        viewModelScope.launch {
            repository.addService(name, price, duration)
            showMessage("Service $name créé !")
        }
    }

    fun updateService(service: WashService) {
        viewModelScope.launch {
            repository.updateService(service)
            showMessage("Service mis à jour !")
        }
    }

    fun deleteService(service: WashService) {
        viewModelScope.launch {
            repository.deleteService(service)
            showMessage("Service supprimé")
        }
    }

    // NOTIFICATIONS
    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            showMessage("Toutes les notifications marquées comme lues")
        }
    }

    // SETTINGS
    fun updateCompany(updated: Company) {
        viewModelScope.launch {
            repository.updateCompany(updated)
            showMessage("Paramètres enregistrés avec succès !")
        }
    }

    // TODAY STATS COMPUTATION
    fun getTodayStats(): Triple<Double, Int, Int> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = cal.timeInMillis

        val todayOrders = orders.value.filter { it.createdAt >= startOfToday }
        val caToday = todayOrders.sumOf { it.paidAmount }
        val vehiclesToday = todayOrders.count { it.status == OrderStatus.COMPLETED.name }
        val queueCount = orders.value.count { it.status == OrderStatus.PENDING.name || it.status == OrderStatus.IN_PROGRESS.name }

        return Triple(caToday, vehiclesToday, queueCount)
    }
}
