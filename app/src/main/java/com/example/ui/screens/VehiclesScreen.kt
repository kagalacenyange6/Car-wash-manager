package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCarWash
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.Vehicle
import com.example.ui.CarWashViewModel
import com.example.ui.components.CarWashInputField
import com.example.ui.components.CarWashPrimaryButton
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FormErrorBanner
import com.example.ui.components.FormOptionSelector
import com.example.ui.components.FormSheetHeader
import com.example.ui.components.PlateBadge
import com.example.ui.components.carWashInputTextStyle
import com.example.ui.components.carWashTextFieldColors
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
                placeholder = {
                    Text(
                        "Rechercher (Plaque, Modèle, Propriétaire)...",
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
                            },
                            onDeleteVehicle = {
                                viewModel.deleteVehicle(veh)
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
    onDeleteVehicle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer le véhicule ?", fontWeight = FontWeight.Bold, color = Navy900) },
            text = { Text("Confirmez-vous la suppression du véhicule ${vehicle.plate} (${vehicle.brand} ${vehicle.model}) ?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteVehicle()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Text("Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text("Annuler")
                }
            }
        )
    }

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
                // Plaque d'immatriculation officielle RDC
                com.example.ui.components.PlateBadge(plate = vehicle.plate)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "${vehicle.washCount} lavages",
                            fontSize = 11.sp,
                            color = Navy900,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
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
                Text("Lancer un lavage", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            FormSheetHeader(
                title = "Nouveau Véhicule",
                subtitle = "Associez un véhicule à un client pour les prestations",
                icon = Icons.Default.DirectionsCar,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (errorMessage != null) {
                FormErrorBanner(message = errorMessage!!)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Choose customer section
            Text(
                text = "CLIENT PROPRIÉTAIRE *",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (customers.isEmpty()) {
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Aucun client enregistré. Créez d'abord un client.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE11D48),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                ) {
                    items(customers, key = { it.id }) { c ->
                        val isSel = c.id == selectedCustomerId
                        Surface(
                            color = if (isSel) Color(0xFFE0F2FE) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, if (isSel) BlueAccent else Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { 
                                    selectedCustomerId = c.id 
                                    errorMessage = null
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isSel) BlueAccent else Navy900.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = c.name.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSel) Color.White else Navy900
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = c.name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSel) Navy900 else TextPrimary
                                    )
                                    Text(
                                        text = c.phone,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CarWashInputField(
                value = plate,
                onValueChange = { 
                    plate = it.uppercase() 
                    errorMessage = null
                },
                label = "Plaque d'immatriculation *",
                placeholder = "ex: 1024 AB",
                leadingIcon = Icons.Default.DirectionsCar,
                isError = errorMessage != null && plate.isBlank(),
                supportingText = if (errorMessage != null && plate.isBlank()) "La plaque d'immatriculation est obligatoire" else null,
                testTag = "add_vehicle_plate_input"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CarWashInputField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = "Marque",
                    placeholder = "ex: Toyota",
                    modifier = Modifier.weight(1f)
                )
                CarWashInputField(
                    value = model,
                    onValueChange = { model = it },
                    label = "Modèle",
                    placeholder = "ex: Hilux",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TYPE DE VÉHICULE",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            FormOptionSelector(
                options = listOf(
                    "Berline" to "Berline",
                    "SUV" to "SUV",
                    "Pick-up" to "Pick-up"
                ),
                selectedOption = vehicleType,
                onOptionSelected = { vehicleType = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            CarWashPrimaryButton(
                text = "Enregistrer le véhicule",
                onClick = {
                    if (selectedCustomerId.isBlank()) {
                        errorMessage = "Veuillez sélectionner un client"
                        return@CarWashPrimaryButton
                    }
                    if (plate.trim().isBlank()) {
                        errorMessage = "Veuillez renseigner la plaque d'immatriculation"
                        return@CarWashPrimaryButton
                    }
                    onSave(selectedCustomerId, plate.trim(), brand.trim().ifBlank { "Auto" }, model.trim().ifBlank { "Modèle" }, color, vehicleType, "")
                }
            )
        }
    }
}
