package com.example.data.repository

import com.example.data.db.CarWashDao
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
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CarWashRepository(private val dao: CarWashDao) {

    // Flows
    val companyFlow: Flow<Company?> = dao.getCompanyFlow()
    val customersFlow: Flow<List<Customer>> = dao.getAllCustomers()
    val vehiclesFlow: Flow<List<Vehicle>> = dao.getAllVehicles()
    val servicesFlow: Flow<List<WashService>> = dao.getAllServices()
    val activeServicesFlow: Flow<List<WashService>> = dao.getActiveServices()
    val allOrdersFlow: Flow<List<WashOrder>> = dao.getAllOrders()
    val queueOrdersFlow: Flow<List<WashOrder>> = dao.getQueueOrders()
    val paymentsFlow: Flow<List<Payment>> = dao.getAllPayments()
    val expensesFlow: Flow<List<Expense>> = dao.getAllExpenses()
    val productsFlow: Flow<List<Product>> = dao.getAllProducts()
    val stockMovementsFlow: Flow<List<StockMovement>> = dao.getAllStockMovements()
    val employeesFlow: Flow<List<Employee>> = dao.getAllEmployees()
    val loyaltyAccountsFlow: Flow<List<LoyaltyAccount>> = dao.getAllLoyaltyAccounts()
    val notificationsFlow: Flow<List<AppNotification>> = dao.getAllNotifications()
    val unreadNotificationsCountFlow: Flow<Int> = dao.getUnreadNotificationsCount()
    val auditLogsFlow: Flow<List<AuditLog>> = dao.getAllAuditLogs()

    suspend fun getCompany(): Company {
        return dao.getCompany() ?: Company().also { dao.upsertCompany(it) }
    }

    suspend fun updateCompany(company: Company) {
        dao.upsertCompany(company)
        logAudit("Admin", "ADMIN", "Configuration mise à jour", "Paramètres du Car Wash modifiés")
    }

    // Customer operations
    suspend fun addCustomer(name: String, phone: String, address: String = "", notes: String = ""): Customer {
        val customer = Customer(name = name, phone = phone, address = address, notes = notes)
        dao.insertCustomer(customer)
        dao.upsertLoyaltyAccount(LoyaltyAccount(customerId = customer.id))
        logAudit("Système", "APP", "Nouveau client", "Client créé : $name ($phone)")
        return customer
    }

    suspend fun updateCustomer(customer: Customer) {
        dao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        dao.deleteCustomer(customer)
        logAudit("Admin", "ADMIN", "Suppression client", "Client supprimé : ${customer.name}")
    }

    // Vehicle operations
    suspend fun addVehicle(
        customerId: String,
        plate: String,
        brand: String,
        model: String,
        color: String = "Noir",
        vehicleType: String = "Berline",
        notes: String = ""
    ): Vehicle {
        val vehicle = Vehicle(
            customerId = customerId,
            plate = plate.uppercase().trim(),
            brand = brand.trim(),
            model = model.trim(),
            color = color,
            vehicleType = vehicleType,
            notes = notes
        )
        dao.insertVehicle(vehicle)
        logAudit("Système", "APP", "Nouveau véhicule", "Véhicule ajouté : ${vehicle.brand} ${vehicle.model} [${vehicle.plate}]")
        return vehicle
    }

    suspend fun updateVehicle(vehicle: Vehicle) {
        dao.updateVehicle(vehicle)
    }

    suspend fun deleteVehicle(vehicle: Vehicle) {
        dao.deleteVehicle(vehicle)
    }

    // Service operations
    suspend fun addService(name: String, price: Double, duration: Int, iconKey: String = "car_wash") {
        dao.insertService(WashService(name = name, price = price, durationMinutes = duration, iconKey = iconKey))
        logAudit("Admin", "ADMIN", "Nouveau service", "Service créé : $name ($price $)")
    }

    suspend fun updateService(service: WashService) {
        dao.updateService(service)
        logAudit("Gérant", "MANAGER", "Service modifié", "Service mis à jour : ${service.name} (${service.price} $)")
    }

    suspend fun deleteService(service: WashService) {
        dao.deleteService(service)
    }

    // New Prestation (Wash Order)
    suspend fun createWashOrder(
        customer: Customer,
        vehicle: Vehicle,
        selectedServices: List<WashService>,
        discount: Double = 0.0,
        notes: String = "",
        employeeName: String = "Jean-Paul (Laveur)"
    ): WashOrder {
        val totalCount = dao.getOrdersCount() + 1
        val orderNumber = String.format(Locale.US, "CW-%06d", totalCount)
        val subtotal = selectedServices.sumOf { it.price }
        val finalAmount = (subtotal - discount).coerceAtLeast(0.0)
        val servicesSummary = selectedServices.joinToString(", ") { it.name }

        val order = WashOrder(
            orderNumber = orderNumber,
            customerId = customer.id,
            customerName = customer.name,
            customerPhone = customer.phone,
            vehicleId = vehicle.id,
            vehiclePlate = vehicle.plate,
            vehicleModel = "${vehicle.brand} ${vehicle.model}",
            servicesSummary = servicesSummary,
            status = OrderStatus.PENDING.name,
            totalAmount = subtotal,
            discountAmount = discount,
            finalAmount = finalAmount,
            paidAmount = 0.0,
            paymentStatus = PaymentStatus.UNPAID.name,
            notes = notes,
            employeeName = employeeName,
            createdAt = System.currentTimeMillis()
        )
        dao.insertOrder(order)

        val items = selectedServices.map {
            WashOrderItem(
                orderId = order.id,
                serviceId = it.id,
                serviceName = it.name,
                price = it.price
            )
        }
        dao.insertOrderItems(items)

        // Notification
        dao.insertNotification(
            AppNotification(
                title = "Nouvelle prestation $orderNumber",
                message = "${vehicle.brand} ${vehicle.model} (${vehicle.plate}) - $servicesSummary",
                type = "NEW_ORDER"
            )
        )

        logAudit(employeeName, "EMPLOYEE", "Prestation créée", "$orderNumber pour ${customer.name} [${vehicle.plate}]")
        return order
    }

    // Order status update
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus, employeeName: String) {
        val order = dao.getOrderById(orderId) ?: return
        val now = System.currentTimeMillis()
        val updated = when (newStatus) {
            OrderStatus.IN_PROGRESS -> order.copy(
                status = newStatus.name,
                startedAt = now
            )
            OrderStatus.COMPLETED -> order.copy(
                status = newStatus.name,
                completedAt = now
            )
            else -> order.copy(status = newStatus.name)
        }
        dao.updateOrder(updated)

        if (newStatus == OrderStatus.COMPLETED) {
            dao.insertNotification(
                AppNotification(
                    title = "Lavage terminé : ${order.orderNumber}",
                    message = "${order.vehicleModel} (${order.vehiclePlate}) est prêt pour l'encaissement !",
                    type = "ORDER_COMPLETED"
                )
            )
        }

        logAudit(employeeName, "EMPLOYEE", "Statut modifié", "${order.orderNumber} -> ${newStatus.label}")
    }

    // Record Payment
    suspend fun recordPayment(
        orderId: String,
        amountToPay: Double,
        method: PaymentMethod,
        reference: String = "",
        employeeName: String = "Caissier"
    ): Payment {
        val order = dao.getOrderById(orderId) ?: throw IllegalStateException("Order not found")
        val newPaidTotal = order.paidAmount + amountToPay
        val remaining = (order.finalAmount - newPaidTotal).coerceAtLeast(0.0)

        val newPaymentStatus = when {
            remaining <= 0.001 -> PaymentStatus.PAID.name
            newPaidTotal > 0 -> PaymentStatus.DEBT.name
            else -> PaymentStatus.UNPAID.name
        }

        val updatedOrder = order.copy(
            paidAmount = newPaidTotal,
            paymentStatus = newPaymentStatus
        )
        dao.updateOrder(updatedOrder)

        val payment = Payment(
            orderId = order.id,
            orderNumber = order.orderNumber,
            customerName = order.customerName,
            vehiclePlate = order.vehiclePlate,
            amount = amountToPay,
            paymentMethod = method.name,
            reference = reference,
            employeeName = employeeName,
            createdAt = System.currentTimeMillis()
        )
        dao.insertPayment(payment)

        // Update Vehicle wash count
        val vehicle = dao.getVehicleById(order.vehicleId)
        if (vehicle != null) {
            dao.updateVehicle(vehicle.copy(washCount = vehicle.washCount + 1))
        }

        // Update customer loyalty
        val loyalty = dao.getLoyaltyAccount(order.customerId)
        val company = getCompany()
        if (loyalty != null && company.loyaltyEnabled) {
            val newTotalWashes = loyalty.totalWashes + 1
            val newPoints = loyalty.points + 1
            val newFree = if (newPoints >= company.loyaltyThreshold) {
                loyalty.freeWashesAvailable + (newPoints / company.loyaltyThreshold)
            } else loyalty.freeWashesAvailable
            val remPoints = newPoints % company.loyaltyThreshold
            dao.upsertLoyaltyAccount(
                loyalty.copy(
                    totalWashes = newTotalWashes,
                    points = remPoints,
                    freeWashesAvailable = newFree,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        if (newPaymentStatus == PaymentStatus.DEBT.name) {
            dao.insertNotification(
                AppNotification(
                    title = "Dette enregistrée : ${order.orderNumber}",
                    message = "${order.customerName} a un reste à payer de ${String.format(Locale.US, "%.2f", remaining)} ${company.currencySymbol}",
                    type = "DEBT"
                )
            )
        } else {
            dao.insertNotification(
                AppNotification(
                    title = "Paiement reçu : ${order.orderNumber}",
                    message = "Montant : ${String.format(Locale.US, "%.2f", amountToPay)} ${company.currencySymbol} (${method.label})",
                    type = "PAYMENT"
                )
            )
        }

        logAudit(employeeName, "CASHIER", "Paiement enregistré", "${order.orderNumber} : $amountToPay ${company.currencySymbol} via ${method.label}")
        return payment
    }

    suspend fun getOrderItems(orderId: String): List<WashOrderItem> {
        return dao.getItemsForOrder(orderId)
    }

    // Expense operations
    suspend fun addExpense(category: String, amount: Double, description: String, employeeName: String) {
        val expense = Expense(
            category = category,
            amount = amount,
            description = description,
            date = System.currentTimeMillis(),
            employeeName = employeeName
        )
        dao.insertExpense(expense)
        logAudit(employeeName, "MANAGER", "Dépense enregistrée", "$category : $amount $ ($description)")
    }

    suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense)
    }

    // Stock operations
    suspend fun addProduct(name: String, currentStock: Double, unit: String, minThreshold: Double, unitPrice: Double) {
        val product = Product(
            name = name,
            currentStock = currentStock,
            unit = unit,
            minThreshold = minThreshold,
            unitPrice = unitPrice
        )
        dao.insertProduct(product)
        logAudit("Gérant", "MANAGER", "Nouveau produit stock", "Produit ajouté : $name ($currentStock $unit)")
    }

    suspend fun adjustStock(productId: String, type: String, qty: Double, reason: String, employeeName: String) {
        val product = dao.getProductById(productId) ?: return
        val newStock = when (type) {
            "Entrée" -> product.currentStock + qty
            "Sortie" -> (product.currentStock - qty).coerceAtLeast(0.0)
            else -> qty
        }
        dao.updateProduct(product.copy(currentStock = newStock))

        dao.insertStockMovement(
            StockMovement(
                productId = product.id,
                productName = product.name,
                type = type,
                quantity = qty,
                reason = reason,
                employeeName = employeeName
            )
        )

        if (newStock <= product.minThreshold) {
            dao.insertNotification(
                AppNotification(
                    title = "Alerte Stock Faible !",
                    message = "Le produit ${product.name} n'a plus que $newStock ${product.unit} (Seuil min: ${product.minThreshold} ${product.unit})",
                    type = "STOCK_LOW"
                )
            )
        }

        logAudit(employeeName, "MANAGER", "Mouvement de stock", "$type de $qty ${product.unit} sur ${product.name} ($reason)")
    }

    // Employee operations
    suspend fun addEmployee(name: String, email: String, phone: String, role: String, pinCode: String = "1234") {
        val employee = Employee(name = name, email = email, phone = phone, role = role, pinCode = pinCode)
        dao.insertEmployee(employee)
        logAudit("Admin", "ADMIN", "Nouvel employé", "Employé créé : $name ($role)")
    }

    suspend fun updateEmployee(employee: Employee) {
        dao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        dao.deleteEmployee(employee)
    }

    // Notifications
    suspend fun markNotificationRead(id: String) {
        dao.markNotificationRead(id)
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    // Audit helper
    suspend fun logAudit(userName: String, role: String, action: String, details: String) {
        dao.insertAuditLog(
            AuditLog(
                userName = userName,
                userRole = role,
                action = action,
                details = details,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Seed Demo Data
    suspend fun seedDemoDataIfEmpty() {
        if (dao.getOrdersCount() > 0) return

        // 1. Company
        val company = Company(
            name = "Flash Auto Wash Pro",
            phone = "+243 81 234 5678",
            address = "124 Avenue de la Libération, Kinshasa",
            currencySymbol = "$",
            loyaltyEnabled = true,
            loyaltyThreshold = 10,
            loyaltyRewardName = "1 Lavage Extérieur Offert",
            receiptPaperWidth = "80mm",
            receiptHeaderNote = "FLASH AUTO WASH - SERVICE DE QUALITÉ",
            receiptFooterNote = "Merci pour votre visite ! À très bientôt.",
            mobileMoneyNumber = "+243 81 234 5678"
        )
        dao.upsertCompany(company)

        // 2. Employees (3 employees with distinct roles)
        val empAdmin = Employee(
            id = "emp-admin",
            name = "Patrick Kabeya",
            email = "patrick@flashautowash.com",
            phone = "+243 81 111 2233",
            role = UserRole.ADMIN.name,
            pinCode = "1111"
        )
        val empManager = Employee(
            id = "emp-manager",
            name = "Michel Banze",
            email = "michel@flashautowash.com",
            phone = "+243 82 222 3344",
            role = UserRole.MANAGER.name,
            pinCode = "2222"
        )
        val empWorker = Employee(
            id = "emp-worker",
            name = "Jean-Paul Mulamba",
            email = "jeanpaul@flashautowash.com",
            phone = "+243 89 333 4455",
            role = UserRole.EMPLOYEE.name,
            pinCode = "3333"
        )
        dao.insertEmployees(listOf(empAdmin, empManager, empWorker))

        // 3. Services (6 standard + tyre clean)
        val sExterior = WashService(id = "srv-1", name = "Lavage extérieur", price = 3.0, durationMinutes = 20, iconKey = "car")
        val sInExt = WashService(id = "srv-2", name = "Lavage intérieur + extérieur", price = 5.0, durationMinutes = 35, iconKey = "stars")
        val sComplete = WashService(id = "srv-3", name = "Lavage complet VIP", price = 7.0, durationMinutes = 50, iconKey = "diamond")
        val sVacuum = WashService(id = "srv-4", name = "Aspiration cabine & coffre", price = 2.0, durationMinutes = 15, iconKey = "clean")
        val sEngine = WashService(id = "srv-5", name = "Nettoyage moteur vapeur", price = 4.0, durationMinutes = 25, iconKey = "engine")
        val sWax = WashService(id = "srv-6", name = "Cirage & Lustrage carrosserie", price = 4.0, durationMinutes = 30, iconKey = "wax")
        val sTires = WashService(id = "srv-7", name = "Nettoyage & Rénovateur pneus", price = 1.5, durationMinutes = 10, iconKey = "tire")
        dao.insertServices(listOf(sExterior, sInExt, sComplete, sVacuum, sEngine, sWax, sTires))

        // 4. Customers (10 realistic customers)
        val customers = listOf(
            Customer(id = "cust-1", name = "Marc Kalala", phone = "+243 81 456 7890", address = "Gombe, Av. Roi Baudouin"),
            Customer(id = "cust-2", name = "Sarah Mwamba", phone = "+243 82 567 8901", address = "Ngaliema, Macampagne"),
            Customer(id = "cust-3", name = "David Tshisekedi", phone = "+243 85 678 9012", address = "Mont-Ngafula"),
            Customer(id = "cust-4", name = "Patrick Ilunga", phone = "+243 89 789 0123", address = "Limete, 7ème Rue"),
            Customer(id = "cust-5", name = "Christian Kabila", phone = "+243 81 890 1234", address = "Kalamu, Matonge"),
            Customer(id = "cust-6", name = "Grace Mukendi", phone = "+243 82 901 2345", address = "Bandalungwa"),
            Customer(id = "cust-7", name = "Eric Mutombo", phone = "+243 84 012 3456", address = "Kasa-Vubu"),
            Customer(id = "cust-8", name = "Nathalie Kasongo", phone = "+243 85 123 4567", address = "Gombe, Batetela"),
            Customer(id = "cust-9", name = "Michel Lumumba", phone = "+243 89 234 5678", address = "Lingwala"),
            Customer(id = "cust-10", name = "Alain Kazadi", phone = "+243 81 345 6789", address = "Ngaliema, UPN")
        )
        dao.insertCustomers(customers)
        customers.forEach {
            dao.upsertLoyaltyAccount(LoyaltyAccount(customerId = it.id, points = (2..9).random(), totalWashes = (3..12).random()))
        }

        // 5. Vehicles (15 vehicles assigned to customers)
        val vehicles = listOf(
            Vehicle(id = "veh-1", customerId = "cust-1", plate = "KN-1024-AA", brand = "Toyota", model = "Corolla", color = "Gris argent", vehicleType = "Berline", washCount = 8),
            Vehicle(id = "veh-2", customerId = "cust-1", plate = "KN-8842-BB", brand = "Toyota", model = "Rav4", color = "Blanc nacré", vehicleType = "SUV", washCount = 4),
            Vehicle(id = "veh-3", customerId = "cust-2", plate = "KN-3391-AC", brand = "Peugeot", model = "208", color = "Bleu métallisé", vehicleType = "Berline", washCount = 5),
            Vehicle(id = "veh-4", customerId = "cust-3", plate = "KN-7712-AD", brand = "Mercedes-Benz", model = "C200", color = "Noir obsidienne", vehicleType = "Berline", washCount = 12),
            Vehicle(id = "veh-5", customerId = "cust-4", plate = "KN-5520-AE", brand = "Renault", model = "Duster", color = "Marron", vehicleType = "SUV", washCount = 6),
            Vehicle(id = "veh-6", customerId = "cust-4", plate = "KN-9102-AF", brand = "Ford", model = "Ranger Wildtrak", color = "Orange", vehicleType = "Pick-up", washCount = 7),
            Vehicle(id = "veh-7", customerId = "cust-5", plate = "KN-6644-AG", brand = "Hyundai", model = "Tucson", color = "Bleu nuit", vehicleType = "SUV", washCount = 9),
            Vehicle(id = "veh-8", customerId = "cust-6", plate = "KN-2211-AH", brand = "Range Rover", model = "Evoque", color = "Blanc", vehicleType = "SUV", washCount = 11),
            Vehicle(id = "veh-9", customerId = "cust-7", plate = "KN-4433-AI", brand = "Toyota", model = "Hilux Double Cabine", color = "Gris anthracite", vehicleType = "Pick-up", washCount = 5),
            Vehicle(id = "veh-10", customerId = "cust-8", plate = "KN-1188-AJ", brand = "Kia", model = "Sportage", color = "Rouge", vehicleType = "SUV", washCount = 3),
            Vehicle(id = "veh-11", customerId = "cust-9", plate = "KN-9977-AK", brand = "Volkswagen", model = "Golf 7", color = "Noir", vehicleType = "Berline", washCount = 7),
            Vehicle(id = "veh-12", customerId = "cust-10", plate = "KN-8833-AL", brand = "BMW", model = "Série 3 320i", color = "Blanc alpin", vehicleType = "Berline", washCount = 6),
            Vehicle(id = "veh-13", customerId = "cust-2", plate = "KN-7744-AM", brand = "Suzuki", model = "Grand Vitara", color = "Gris", vehicleType = "SUV", washCount = 2),
            Vehicle(id = "veh-14", customerId = "cust-3", plate = "KN-5566-AN", brand = "Toyota", model = "Land Cruiser Prado", color = "Noir", vehicleType = "SUV", washCount = 14),
            Vehicle(id = "veh-15", customerId = "cust-5", plate = "KN-3322-AO", brand = "Honda", model = "CR-V", color = "Bleu ciel", vehicleType = "SUV", washCount = 4)
        )
        dao.insertVehicles(vehicles)

        // 6. Products (Stock)
        val products = listOf(
            Product(id = "prod-1", name = "Shampooing carrosserie mousse active", currentStock = 18.0, unit = "L", minThreshold = 5.0, unitPrice = 14.0),
            Product(id = "prod-2", name = "Savon moussant neige canon", currentStock = 3.5, unit = "L", minThreshold = 5.0, unitPrice = 16.0), // Low stock
            Product(id = "prod-3", name = "Cire déperlante premium lustrage", currentStock = 8.0, unit = "L", minThreshold = 3.0, unitPrice = 25.0),
            Product(id = "prod-4", name = "Rénovateur noir pneus effet mouillé", currentStock = 2.0, unit = "L", minThreshold = 4.0, unitPrice = 12.0), // Low stock
            Product(id = "prod-5", name = "Nettoyant vitres & tableau de bord", currentStock = 6.5, unit = "L", minThreshold = 3.0, unitPrice = 9.0),
            Product(id = "prod-6", name = "Chiffons microfibres professionnelles", currentStock = 24.0, unit = "Pièces", minThreshold = 10.0, unitPrice = 1.5)
        )
        dao.insertProducts(products)

        // 7. Expenses
        val expenses = listOf(
            Expense(category = "Eau", amount = 45.0, description = "Facture d'eau régie de distribution", date = System.currentTimeMillis() - 86400000L),
            Expense(category = "Électricité", amount = 65.0, description = "Abonnement électricité haute tension", date = System.currentTimeMillis() - 86400000L * 2),
            Expense(category = "Produits de nettoyage", amount = 85.0, description = "Achat bidons savon et dégraissant", date = System.currentTimeMillis() - 86400000L * 3),
            Expense(category = "Carburant", amount = 30.0, description = "Gasoil pour le groupe électrogène", date = System.currentTimeMillis() - 86400000L)
        )
        dao.insertExpenses(expenses)

        // 8. Wash Orders (Mix of Queue: Pending, In Progress, Completed, Paid, and Debt)
        val now = System.currentTimeMillis()
        val order1 = WashOrder(
            id = "ord-1",
            orderNumber = "CW-000001",
            customerId = "cust-1",
            customerName = "Marc Kalala",
            customerPhone = "+243 81 456 7890",
            vehicleId = "veh-1",
            vehiclePlate = "KN-1024-AA",
            vehicleModel = "Toyota Corolla",
            servicesSummary = "Lavage complet VIP, Cirage & Lustrage",
            status = OrderStatus.COMPLETED.name,
            totalAmount = 11.0,
            discountAmount = 1.0,
            finalAmount = 10.0,
            paidAmount = 10.0,
            paymentStatus = PaymentStatus.PAID.name,
            notes = "Client habituel - VIP",
            employeeName = "Jean-Paul Mulamba",
            startedAt = now - 3600000L,
            completedAt = now - 1800000L,
            createdAt = now - 3600000L
        )
        val order2 = WashOrder(
            id = "ord-2",
            orderNumber = "CW-000002",
            customerId = "cust-3",
            customerName = "David Tshisekedi",
            customerPhone = "+243 85 678 9012",
            vehicleId = "veh-4",
            vehiclePlate = "KN-7712-AD",
            vehicleModel = "Mercedes-Benz C200",
            servicesSummary = "Lavage intérieur + extérieur, Nettoyage des pneus",
            status = OrderStatus.IN_PROGRESS.name,
            totalAmount = 6.5,
            discountAmount = 0.0,
            finalAmount = 6.5,
            paidAmount = 0.0,
            paymentStatus = PaymentStatus.UNPAID.name,
            employeeName = "Jean-Paul Mulamba",
            startedAt = now - 900000L,
            createdAt = now - 1200000L
        )
        val order3 = WashOrder(
            id = "ord-3",
            orderNumber = "CW-000003",
            customerId = "cust-4",
            customerName = "Patrick Ilunga",
            customerPhone = "+243 89 789 0123",
            vehicleId = "veh-6",
            vehiclePlate = "KN-9102-AF",
            vehicleModel = "Ford Ranger Wildtrak",
            servicesSummary = "Lavage extérieur, Nettoyage moteur vapeur",
            status = OrderStatus.PENDING.name,
            totalAmount = 7.0,
            discountAmount = 0.0,
            finalAmount = 7.0,
            paidAmount = 0.0,
            paymentStatus = PaymentStatus.UNPAID.name,
            employeeName = "Jean-Paul Mulamba",
            createdAt = now - 600000L
        )
        val order4 = WashOrder(
            id = "ord-4",
            orderNumber = "CW-000004",
            customerId = "cust-6",
            customerName = "Grace Mukendi",
            customerPhone = "+243 82 901 2345",
            vehicleId = "veh-8",
            vehiclePlate = "KN-2211-AH",
            vehicleModel = "Range Rover Evoque",
            servicesSummary = "Lavage complet VIP",
            status = OrderStatus.COMPLETED.name,
            totalAmount = 7.0,
            discountAmount = 0.0,
            finalAmount = 7.0,
            paidAmount = 4.0,
            paymentStatus = PaymentStatus.DEBT.name,
            employeeName = "Michel Banze",
            startedAt = now - 7200000L,
            completedAt = now - 5400000L,
            createdAt = now - 7200000L
        )
        dao.insertOrders(listOf(order1, order2, order3, order4))

        // Order items
        dao.insertOrderItems(
            listOf(
                WashOrderItem(orderId = "ord-1", serviceId = "srv-3", serviceName = "Lavage complet VIP", price = 7.0),
                WashOrderItem(orderId = "ord-1", serviceId = "srv-6", serviceName = "Cirage & Lustrage carrosserie", price = 4.0),
                WashOrderItem(orderId = "ord-2", serviceId = "srv-2", serviceName = "Lavage intérieur + extérieur", price = 5.0),
                WashOrderItem(orderId = "ord-2", serviceId = "srv-7", serviceName = "Nettoyage & Rénovateur pneus", price = 1.5),
                WashOrderItem(orderId = "ord-3", serviceId = "srv-1", serviceName = "Lavage extérieur", price = 3.0),
                WashOrderItem(orderId = "ord-3", serviceId = "srv-5", serviceName = "Nettoyage moteur vapeur", price = 4.0),
                WashOrderItem(orderId = "ord-4", serviceId = "srv-3", serviceName = "Lavage complet VIP", price = 7.0)
            )
        )

        // Payments
        dao.insertPayments(
            listOf(
                Payment(
                    orderId = "ord-1",
                    orderNumber = "CW-000001",
                    customerName = "Marc Kalala",
                    vehiclePlate = "KN-1024-AA",
                    amount = 10.0,
                    paymentMethod = PaymentMethod.CASH.name,
                    reference = "REC-CASH-01",
                    employeeName = "Michel Banze",
                    createdAt = now - 1800000L
                ),
                Payment(
                    orderId = "ord-4",
                    orderNumber = "CW-000004",
                    customerName = "Grace Mukendi",
                    vehiclePlate = "KN-2211-AH",
                    amount = 4.0,
                    paymentMethod = PaymentMethod.MOBILE_MONEY.name,
                    reference = "M-PESA-88392",
                    employeeName = "Michel Banze",
                    createdAt = now - 5400000L
                )
            )
        )

        // Notifications
        dao.insertNotification(
            AppNotification(
                title = "Alerte Stock Faible",
                message = "Savon moussant neige canon : 3.5 L restant (seuil min : 5.0 L)",
                type = "STOCK_LOW"
            )
        )
        dao.insertNotification(
            AppNotification(
                title = "Dette enregistrée",
                message = "Grace Mukendi (Range Rover) : reste à payer 3.00 $",
                type = "DEBT"
            )
        )

        // Initial Audit Logs
        logAudit("Admin", "ADMIN", "Initialisation du système", "Données de démonstration chargées avec succès")
    }
}
