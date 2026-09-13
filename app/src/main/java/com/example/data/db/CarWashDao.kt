package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppNotification
import com.example.data.model.AuditLog
import com.example.data.model.Company
import com.example.data.model.Customer
import com.example.data.model.Employee
import com.example.data.model.Expense
import com.example.data.model.LoyaltyAccount
import com.example.data.model.Payment
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.data.model.Vehicle
import com.example.data.model.WashOrder
import com.example.data.model.WashOrderItem
import com.example.data.model.WashService
import kotlinx.coroutines.flow.Flow

@Dao
interface CarWashDao {

    // Company
    @Query("SELECT * FROM companies WHERE id = :id LIMIT 1")
    fun getCompanyFlow(id: String = "comp-default"): Flow<Company?>

    @Query("SELECT * FROM companies WHERE id = :id LIMIT 1")
    suspend fun getCompany(id: String = "comp-default"): Company?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompany(company: Company)

    // Customers
    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers")
    suspend fun getAllCustomersDirect(): List<Customer>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<Customer>)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    // Vehicles
    @Query("SELECT * FROM vehicles ORDER BY createdAt DESC")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles")
    suspend fun getAllVehiclesDirect(): List<Vehicle>

    @Query("SELECT * FROM vehicles WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getVehiclesByCustomer(customerId: String): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE customerId = :customerId")
    suspend fun getVehiclesByCustomerDirect(customerId: String): List<Vehicle>

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    suspend fun getVehicleById(id: String): Vehicle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<Vehicle>)

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)

    // Services
    @Query("SELECT * FROM services ORDER BY price ASC")
    fun getAllServices(): Flow<List<WashService>>

    @Query("SELECT * FROM services")
    suspend fun getAllServicesDirect(): List<WashService>

    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY price ASC")
    fun getActiveServices(): Flow<List<WashService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: WashService)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<WashService>)

    @Update
    suspend fun updateService(service: WashService)

    @Delete
    suspend fun deleteService(service: WashService)

    // Wash Orders
    @Query("SELECT * FROM wash_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<WashOrder>>

    @Query("SELECT * FROM wash_orders")
    suspend fun getAllOrdersDirect(): List<WashOrder>

    @Query("SELECT * FROM wash_orders WHERE status IN ('PENDING', 'IN_PROGRESS') ORDER BY createdAt ASC")
    fun getQueueOrders(): Flow<List<WashOrder>>

    @Query("SELECT * FROM wash_orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): WashOrder?

    @Query("SELECT COUNT(*) FROM wash_orders")
    suspend fun getOrdersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: WashOrder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<WashOrder>)

    @Update
    suspend fun updateOrder(order: WashOrder)

    @Delete
    suspend fun deleteOrder(order: WashOrder)

    // Wash Order Items
    @Query("SELECT * FROM wash_order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrder(orderId: String): List<WashOrderItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<WashOrderItem>)

    // Payments
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments")
    suspend fun getAllPaymentsDirect(): List<Payment>

    @Query("SELECT * FROM payments WHERE orderId = :orderId ORDER BY createdAt DESC")
    fun getPaymentsForOrder(orderId: String): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<Payment>)

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpensesDirect(): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<Expense>)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // Products (Stock)
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    // Stock Movements
    @Query("SELECT * FROM stock_movements ORDER BY createdAt DESC")
    fun getAllStockMovements(): Flow<List<StockMovement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(movement: StockMovement)

    // Employees
    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id LIMIT 1")
    suspend fun getEmployeeById(id: String): Employee?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<Employee>)

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    // Loyalty Accounts
    @Query("SELECT * FROM loyalty_accounts WHERE customerId = :customerId LIMIT 1")
    suspend fun getLoyaltyAccount(customerId: String): LoyaltyAccount?

    @Query("SELECT * FROM loyalty_accounts")
    fun getAllLoyaltyAccounts(): Flow<List<LoyaltyAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLoyaltyAccount(account: LoyaltyAccount)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)
}
