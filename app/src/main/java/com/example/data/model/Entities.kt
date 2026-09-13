package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "companies")
data class Company(
    @PrimaryKey val id: String = "comp-default",
    val name: String = "Flash Auto Wash",
    val phone: String = "+243 81 234 5678",
    val address: String = "124 Avenue de la Libération, Centre-Ville",
    val currencySymbol: String = "$",
    val loyaltyEnabled: Boolean = true,
    val loyaltyThreshold: Int = 10,
    val loyaltyRewardName: String = "Lavage extérieur offert",
    val receiptPaperWidth: String = "80mm",
    val receiptHeaderNote: String = "Bienvenue chez Flash Auto Wash - Service Premium",
    val receiptFooterNote: String = "Merci pour votre fidélité ! À très bientôt.",
    val mobileMoneyNumber: String = "+243 81 234 5678",
    val supabaseUrl: String = "https://your-project.supabase.co",
    val supabaseAnonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val name: String,
    val phone: String,
    val address: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val customerId: String,
    val plate: String,
    val brand: String,
    val model: String,
    val color: String = "Noir",
    val vehicleType: String = "Berline", // Berline, SUV, Pick-up, Camionnette, Minibus, Moto, Autre
    val notes: String = "",
    val washCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "services")
data class WashService(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val name: String,
    val price: Double,
    val durationMinutes: Int = 25,
    val isActive: Boolean = true,
    val iconKey: String = "car_wash"
)

enum class OrderStatus(val label: String) {
    PENDING("En attente"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé")
}

enum class PaymentStatus(val label: String) {
    UNPAID("Non payé"),
    PARTIAL("Partiel"),
    DEBT("Dette"),
    PAID("Payé")
}

enum class PaymentMethod(val label: String) {
    CASH("Espèces"),
    MOBILE_MONEY("Mobile Money"),
    CARD("Carte"),
    CREDIT("Crédit")
}

@Entity(tableName = "wash_orders")
data class WashOrder(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val orderNumber: String, // CW-000001
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val vehicleId: String,
    val vehiclePlate: String,
    val vehicleModel: String,
    val servicesSummary: String, // e.g. "Lavage complet, Cirage"
    val status: String = OrderStatus.PENDING.name,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val finalAmount: Double,
    val paidAmount: Double = 0.0,
    val paymentStatus: String = PaymentStatus.UNPAID.name,
    val notes: String = "",
    val employeeId: String = "emp-1",
    val employeeName: String = "Jean-Paul (Laveur)",
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wash_order_items")
data class WashOrderItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val orderId: String,
    val serviceId: String,
    val serviceName: String,
    val price: Double
)

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val orderId: String,
    val orderNumber: String,
    val customerName: String,
    val vehiclePlate: String,
    val amount: Double,
    val paymentMethod: String = PaymentMethod.CASH.name,
    val reference: String = "",
    val employeeName: String = "Jean-Paul",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val category: String, // Eau, Électricité, Produits de nettoyage, Salaires, Carburant, Maintenance, Loyer, Transport, Autres
    val amount: Double,
    val description: String,
    val date: Long = System.currentTimeMillis(),
    val employeeName: String = "Directeur",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val name: String,
    val currentStock: Double,
    val unit: String = "L", // L, Kg, Pièce, Bidon
    val minThreshold: Double = 5.0,
    val unitPrice: Double = 12.0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val productId: String,
    val productName: String,
    val type: String, // Entrée, Sortie, Ajustement
    val quantity: Double,
    val reason: String,
    val employeeName: String = "Gérant",
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole(val label: String) {
    ADMIN("Administrateur"),
    MANAGER("Gérant"),
    EMPLOYEE("Employé")
}

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val name: String,
    val email: String,
    val phone: String,
    val role: String = UserRole.EMPLOYEE.name,
    val status: String = "Actif",
    val pinCode: String = "1234",
    val activeOrdersCount: Int = 0,
    val totalWashesCompleted: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "loyalty_accounts")
data class LoyaltyAccount(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val customerId: String,
    val points: Int = 0,
    val totalWashes: Int = 0,
    val freeWashesAvailable: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val title: String,
    val message: String,
    val type: String, // STOCK_LOW, PAYMENT, DEBT, ORDER_COMPLETED, NEW_ORDER, GENERAL
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val companyId: String = "comp-default",
    val userName: String,
    val userRole: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
