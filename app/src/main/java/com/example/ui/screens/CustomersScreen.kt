package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.Vehicle
import com.example.data.model.WashOrder
import com.example.ui.CarWashViewModel
import com.example.ui.components.EmptyStateView
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
    val loyaltyList by viewModel.loyaltyAccounts.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCustomerForDetails by remember { mutableStateOf<Customer?>(null) }

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
                placeholder = { Text("Rechercher par nom ou numéro...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
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
            loyaltyPoints = loyalty?.points ?: 0,
            currency = currency,
            onDismiss = { selectedCustomerForDetails = null },
            onNewOrder = {
                selectedCustomerForDetails = null
                viewModel.startNewOrder(cust, customerVehicles.firstOrNull())
            }
        )
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Navy900.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.name.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
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
                colors = ButtonDefaults.buttonColors(containerColor = Navy900.copy(alpha = 0.06f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            ) {
                Icon(Icons.Default.LocalCarWash, contentDescription = null, modifier = Modifier.size(16.dp), tint = Navy900)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lancer prestation", color = Navy900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nouveau Client",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Navy900)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom complet *") },
                modifier = Modifier.fillMaxWidth().testTag("add_customer_name"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Téléphone *") },
                modifier = Modifier.fillMaxWidth().testTag("add_customer_phone"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Adresse / Quartier (optionnel)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optionnel)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, phone, address, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_customer_button")
            ) {
                Text("Enregistrer le client", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailSheet(
    customer: Customer,
    vehicles: List<Vehicle>,
    orders: List<WashOrder>,
    loyaltyPoints: Int,
    currency: String,
    onDismiss: () -> Unit,
    onNewOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSpent = orders.sumOf { it.paidAmount }
    val totalDebt = orders.sumOf { (it.finalAmount - it.paidAmount).coerceAtLeast(0.0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = modifier.testTag("customer_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Navy900, fontSize = 18.sp)
                    )
                    Text(text = customer.phone, fontSize = 13.sp, color = TextSecondary)
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
                        Text("Dette", fontSize = 11.sp, color = if (totalDebt > 0) StatusDebt else TextSecondary)
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

            Text("Véhicules (${vehicles.size}) :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            vehicles.forEach { v ->
                Text("• ${v.plate} - ${v.brand} ${v.model} (${v.washCount} lavages)", fontSize = 13.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onNewOrder,
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Nouvelle prestation pour ce client", fontWeight = FontWeight.Bold)
            }
        }
    }
}
