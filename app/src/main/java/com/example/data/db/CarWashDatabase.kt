package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        Company::class,
        Customer::class,
        Vehicle::class,
        WashService::class,
        WashOrder::class,
        WashOrderItem::class,
        Payment::class,
        Expense::class,
        Product::class,
        StockMovement::class,
        Employee::class,
        LoyaltyAccount::class,
        AppNotification::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CarWashDatabase : RoomDatabase() {
    abstract fun carWashDao(): CarWashDao

    companion object {
        @Volatile
        private var INSTANCE: CarWashDatabase? = null

        fun getDatabase(context: Context): CarWashDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarWashDatabase::class.java,
                    "car_wash_pro.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
