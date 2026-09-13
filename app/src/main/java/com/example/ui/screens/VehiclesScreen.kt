package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.Vehicle
import com.example.ui.CarWashViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    viewModel: CarWashViewModel,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val customers by viewModel.customers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddVehicleDialog by remember { mutableStateOf(false) }

    val filteredVehicles = remember(vehicles, customers, searchQuery) {
        if (searchQuery.isBlank()) vehicles
        else vehicles.filter { veh ->
            val cust = customers.firstOrNull { it.id == veh.customerId }
            veh.plate.contains(searchQuery, ignoreCase = true) ||
                veh.brand.contains(searchQuery, ignoreCase = true) ||
                veh.model.contains(searchQuery, ignoreCase = true) ||
                (cust != null && cust.name.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddVehicleDialog = true },
                containerColor = Navy900,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("vehicles_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter véhicule")
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
                    .testTag("vehicles_search_input"),
                placeholder = { Text("Rechercher (Plaque, Modèle, Propriétaire)...", fontSize = 14.sp) },
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

            if (filteredVehicles.isEmpty()) {
                EmptyStateView(
                    message = "Aucun véhicule trouvé.",
                    icon = Icons.Default.DirectionsCar
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVehicles, key = { it.id }) { veh ->
                        val customer = customers.firstOrNull { it.id == veh.customerId }
                        VehicleCard(
                            vehicle = veh,
                            customerName = customer?.name ?: "Client inconnu",
                            onLaunchWash = {
                                viewModel.startNewOrder(customer, veh)
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

    if (showAddVehicleDialog) {
        AddVehicleSheet(
            customers = customers,
            onDismiss = { showAddVehicleDialog = false },
            onSave = { custId, plate, brand, model, color, type, notes ->
                viewModel.addVehicle(custId, plate, brand, model, color, type, notes)
                showAddVehicleDialog = false
            }
        )
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    customerName: String,
    onLaunchWash: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vehicle_card_${vehicle.id}"),
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
                // Plaque d'immatriculation (TRÈS VISIBLE comme requis)
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Text(
                        text = vehicle.plate,
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "${vehicle.washCount} lavages",
                        fontSize = 11.sp,
                        color = Navy900,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${vehicle.brand} ${vehicle.model}",
                fontWeight = FontWeight.Bold,
                color = Navy900,
                fontSize = 16.sp
            )

            Text(
                text = "Propriétaire : $customerName • ${vehicle.vehicleType}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onLaunchWash,
                colors = ButtonDefaults.buttonColors(containerColor = Navy900.copy(alpha = 0.07f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(38.dp)
            ) {
                Icon(Icons.Default.LocalCarWash, contentDescription = null, modifier = Modifier.size(16.dp), tint = Navy900)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lancer un lavage", color = Navy900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleSheet(
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onSave: (customerId: String, plate: String, brand: String, model: String, color: String, type: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCustomerId by remember { mutableStateOf(customers.firstOrNull()?.id ?: "") }
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("Noir") }
    var vehicleType by remember { mutableStateOf("Berline") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = modifier.testTag("add_vehicle_sheet")
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
                    text = "Nouveau Véhicule",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Navy900)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Choose customer dropdown/chips
            Text("Attribuer au client :", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
            Spacer(modifier = Modifier.height(6.dp))
            LazyColumn(modifier = Modifier.height(110.dp)) {
                items(customers, key = { it.id }) { c ->
                    val isSel = c.id == selectedCustomerId
                    Surface(
                        color = if (isSel) Color(0xFFE0F2FE) else Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isSel) BlueAccent else BorderLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable { selectedCustomerId = c.id }
                    ) {
                        Text(
                            text = "${c.name} (${c.phone})",
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Navy900 else TextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it.uppercase() },
                label = { Text("Plaque d'immatriculation *") },
                modifier = Modifier.fillMaxWidth().testTag("add_vehicle_plate_input"),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marque") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Modèle") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (plate.isNotBlank() && selectedCustomerId.isNotBlank()) {
                        onSave(selectedCustomerId, plate, brand.ifBlank { "Auto" }, model.ifBlank { "Modèle" }, color, vehicleType, "")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_vehicle_button")
            ) {
                Text("Enregistrer le véhicule", fontWeight = FontWeight.Bold)
            }
        }
    }
}
