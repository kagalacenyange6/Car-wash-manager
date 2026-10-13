package com.example.ui.components

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.Vehicle
import com.example.data.model.WashService
import com.example.ui.CarWashViewModel
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderWizardSheet(
    viewModel: CarWashViewModel,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentStep by viewModel.newOrderStep.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val selectedVehicle by viewModel.selectedVehicle.collectAsState()
    val selectedServices by viewModel.selectedServices.collectAsState()
    val discount by viewModel.discount.collectAsState()
    val notes by viewModel.orderNotes.collectAsState()

    val customers by viewModel.customers.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val services by viewModel.activeServices.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeNewOrder() },
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = modifier.testTag("new_order_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .imePadding()
                .padding(bottom = 32.dp)
        ) {
            // Sheet Header with Steps indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentStep > 1) {
                        IconButton(
                            onClick = { viewModel.setOrderStep(currentStep - 1) },
                            modifier = Modifier.size(36.dp).testTag("wizard_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = Navy900
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Column {
                        Text(
                            text = "Nouvelle Prestation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = when (currentStep) {
                                1 -> "Étape 1/4 : Identification du client"
                                2 -> "Étape 2/4 : Sélection du véhicule"
                                3 -> "Étape 3/4 : Choix des prestations"
                                else -> "Étape 4/4 : Validation & Mise en file"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = BlueAccent, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.closeNewOrder() },
                    modifier = Modifier.size(36.dp).testTag("wizard_close_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Progress Stepper Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stepNames = listOf("Client", "Véhicule", "Services", "Validation")
                stepNames.forEachIndexed { index, name ->
                    val stepNum = index + 1
                    val isPast = stepNum < currentStep
                    val isCurrent = stepNum == currentStep

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            isCurrent -> Navy900
                            isPast -> Color(0xFFDCFCE7)
                            else -> Color(0xFFF1F5F9)
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                isCurrent -> Navy900
                                isPast -> Color(0xFF86EFAC)
                                else -> BorderLight
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clickable(enabled = isPast) { viewModel.setOrderStep(stepNum) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isPast) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = "$stepNum. $name",
                                color = when {
                                    isCurrent -> Color.White
                                    isPast -> Color(0xFF166534)
                                    else -> TextMuted
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wizard Step Content
            when (currentStep) {
                1 -> Step1ChooseCustomer(
                    customers = customers,
                    onCustomerSelected = { viewModel.selectCustomerForOrder(it) },
                    onQuickAddCustomer = { name, phone ->
                        viewModel.addCustomer(name, phone, "", "") { created ->
                            viewModel.selectCustomerForOrder(created)
                        }
                    }
                )

                2 -> Step2ChooseVehicle(
                    customer = selectedCustomer,
                    vehicles = vehicles.filter { it.customerId == selectedCustomer?.id },
                    onVehicleSelected = { viewModel.selectVehicleForOrder(it) },
                    onQuickAddVehicle = { plate, brand, model, type ->
                        selectedCustomer?.let { cust ->
                            viewModel.addVehicle(cust.id, plate, brand, model, "Noir", type, "") { created ->
                                viewModel.selectVehicleForOrder(created)
                            }
                        }
                    }
                )

                3 -> Step3ChooseServices(
                    services = services,
                    selectedServices = selectedServices,
                    currency = currency,
                    onToggleService = { viewModel.toggleServiceSelection(it) },
                    discount = discount,
                    onDiscountChanged = { viewModel.setDiscount(it) },
                    onNext = {
                        if (selectedServices.isNotEmpty()) {
                            viewModel.setOrderStep(4)
                        } else {
                            viewModel.showMessage("Sélectionnez au moins un service")
                        }
                    }
                )

                4 -> Step4Confirm(
                    customer = selectedCustomer,
                    vehicle = selectedVehicle,
                    services = selectedServices,
                    discount = discount,
                    notes = notes,
                    currency = currency,
                    onNotesChange = { viewModel.setOrderNotes(it) },
                    onConfirm = { viewModel.confirmCreateOrder() }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// ÉTAPE 1 : CHOISIR LE CLIENT
// -------------------------------------------------------------
@Composable
private fun Step1ChooseCustomer(
    customers: List<Customer>,
    onCustomerSelected: (Customer) -> Unit,
    onQuickAddCustomer: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showQuickAdd by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }

    val filtered = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("customer_search_input"),
                placeholder = {
                    Text(
                        "Rechercher un client...",
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

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showQuickAdd = !showQuickAdd },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy900,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(52.dp).testTag("quick_add_customer_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nouveau", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        if (showQuickAdd) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Création rapide client",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nom complet *", fontWeight = FontWeight.SemiBold) },
                        textStyle = carWashInputTextStyle,
                        colors = carWashTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_customer_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newPhone,
                        onValueChange = { newPhone = it },
                        label = { Text("Téléphone *", fontWeight = FontWeight.SemiBold) },
                        textStyle = carWashInputTextStyle,
                        colors = carWashTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_customer_phone"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CarWashPrimaryButton(
                        text = "Enregistrer et sélectionner",
                        onClick = {
                            if (newName.isNotBlank() && newPhone.isNotBlank()) {
                                onQuickAddCustomer(newName, newPhone)
                            }
                        },
                        modifier = Modifier.testTag("submit_quick_customer")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            EmptyStateView(
                message = if (searchQuery.isNotBlank()) "Aucun client trouvé pour \"$searchQuery\"" else "Aucun client enregistré",
                icon = Icons.Default.Person
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                items(filtered, key = { it.id }) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onCustomerSelected(customer) }
                            .testTag("customer_item_${customer.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Navy900.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = customer.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = customer.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = customer.phone,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BlueAccent.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ÉTAPE 2 : CHOISIR LE VÉHICULE
// -------------------------------------------------------------
@Composable
private fun Step2ChooseVehicle(
    customer: Customer?,
    vehicles: List<Vehicle>,
    onVehicleSelected: (Vehicle) -> Unit,
    onQuickAddVehicle: (plate: String, brand: String, model: String, type: String) -> Unit
) {
    var showAddVehicle by remember { mutableStateOf(vehicles.isEmpty()) }
    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Berline") }

    val vehicleTypes = listOf("Berline", "SUV", "Pick-up", "Camionnette", "Minibus", "Moto", "Autre")

    Column {
        // Customer Header summary
        customer?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Navy900)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(it.name, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                        Text(it.phone, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Véhicules du client (${vehicles.size})",
                fontWeight = FontWeight.Bold,
                color = Navy900,
                fontSize = 15.sp
            )

            OutlinedButton(
                onClick = { showAddVehicle = !showAddVehicle },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Navy900
                ),
                border = BorderStroke(1.5.dp, Navy900),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(40.dp).testTag("quick_add_vehicle_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showAddVehicle) "Annuler" else "Nouveau", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Navy900)
            }
        }

        if (showAddVehicle) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ajouter un véhicule",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it.uppercase() },
                        label = { Text("Plaque d'immatriculation * (ex: 1024 AB)", fontWeight = FontWeight.SemiBold) },
                        textStyle = carWashInputTextStyle,
                        colors = carWashTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_vehicle_plate"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Marque (ex: Toyota)", fontWeight = FontWeight.SemiBold) },
                            textStyle = carWashInputTextStyle,
                            colors = carWashTextFieldColors(),
                            modifier = Modifier.weight(1f).testTag("new_vehicle_brand"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Modèle (ex: Corolla)", fontWeight = FontWeight.SemiBold) },
                            textStyle = carWashInputTextStyle,
                            colors = carWashTextFieldColors(),
                            modifier = Modifier.weight(1f).testTag("new_vehicle_model"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Type de véhicule :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Berline", "SUV", "Pick-up").forEach { t ->
                            val isSel = vehicleType == t
                            Surface(
                                color = if (isSel) Navy900 else Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSel) Navy900 else Color(0xFFCBD5E1)),
                                modifier = Modifier.weight(1f).clickable { vehicleType = t }
                            ) {
                                Text(
                                    text = t,
                                    color = if (isSel) Color.White else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    CarWashPrimaryButton(
                        text = "Enregistrer et continuer",
                        onClick = {
                            if (plate.isNotBlank()) {
                                onQuickAddVehicle(plate, brand.ifBlank { "Auto" }, model.ifBlank { "Véhicule" }, vehicleType)
                            }
                        },
                        modifier = Modifier.testTag("submit_quick_vehicle")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (vehicles.isEmpty() && !showAddVehicle) {
            EmptyStateView(
                message = "Aucun véhicule enregistré pour ce client.\nCliquez sur \"Nouveau\" ci-dessus.",
                icon = Icons.Default.DirectionsCar
            )
        } else {
            LazyColumn(modifier = Modifier.height(280.dp)) {
                items(vehicles, key = { it.id }) { veh ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onVehicleSelected(veh) }
                            .testTag("vehicle_item_${veh.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BlueAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = BlueAccent)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = veh.plate,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                )
                                Text(
                                    text = "${veh.brand} ${veh.model} • ${veh.vehicleType}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${veh.washCount} lavages",
                                    fontSize = 11.sp,
                                    color = Navy900,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ÉTAPE 3 : CHOISIR LES SERVICES
// -------------------------------------------------------------
@Composable
private fun Step3ChooseServices(
    services: List<WashService>,
    selectedServices: List<WashService>,
    currency: String,
    onToggleService: (WashService) -> Unit,
    discount: Double,
    onDiscountChanged: (Double) -> Unit,
    onNext: () -> Unit
) {
    val subtotal = selectedServices.sumOf { it.price }
    val total = (subtotal - discount).coerceAtLeast(0.0)

    Column {
        Text(
            text = "Sélectionnez un ou plusieurs services :",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Large touchable service cards
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(services, key = { it.id }) { service ->
                val isSelected = selectedServices.any { it.id == service.id }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleService(service) }
                        .testTag("service_card_${service.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFE0F2FE) else Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BlueAccent else BorderLight
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) BlueAccent else Color.Transparent)
                                    .then(if (!isSelected) Modifier.background(BorderLight) else Modifier),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = service.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Navy900 else TextPrimary
                                    )
                                )
                                Text(
                                    text = "Durée estimée : ~${service.durationMinutes} min",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        Text(
                            text = String.format(Locale.US, "%.2f %s", service.price, currency),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) BlueAccent else Navy900,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sous-total :", color = TextSecondary, fontSize = 13.sp)
                    Text(String.format(Locale.US, "%.2f %s", subtotal, currency), fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Remise accordée :", color = TextSecondary, fontSize = 13.sp)
                        Text("Saisissez le montant en $currency", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                    var discountInput by remember(discount) { mutableStateOf(if (discount == 0.0) "" else discount.toString()) }
                    OutlinedTextField(
                        value = discountInput,
                        onValueChange = {
                            discountInput = it
                            val parsed = it.replace(",", ".").toDoubleOrNull() ?: 0.0
                            onDiscountChanged(parsed.coerceAtLeast(0.0))
                        },
                        placeholder = { Text("0.0", fontSize = 13.sp) },
                        textStyle = carWashInputTextStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                        colors = carWashTextFieldColors(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        modifier = Modifier.width(110.dp).height(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL À PAYER :",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Navy900)
                    )
                    Text(
                        text = String.format(Locale.US, "%.2f %s", total, currency),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BlueAccent,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        CarWashPrimaryButton(
            text = "Continuer (${selectedServices.size} sélectionné(s))",
            onClick = onNext,
            modifier = Modifier.testTag("step3_continue_button")
        )
    }
}

// -------------------------------------------------------------
// ÉTAPE 4 : CONFIRMATION & VALIDATION
// -------------------------------------------------------------
@Composable
private fun Step4Confirm(
    customer: Customer?,
    vehicle: Vehicle?,
    services: List<WashService>,
    discount: Double,
    notes: String,
    currency: String,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    val subtotal = services.sumOf { it.price }
    val total = (subtotal - discount).coerceAtLeast(0.0)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // En-tête de récapitulatif
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Récapitulatif de la commande",
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Vérifiez les informations avant envoi à l'atelier",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Fiche Client & Véhicule
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Client
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(customer?.name ?: "Client standard", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
                            if (!customer?.phone.isNullOrBlank()) {
                                Text(customer?.phone ?: "", color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                        }
                    }
                    Text(
                        text = "Client enregistré",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0284C7)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))

                // Véhicule & Plaque
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${vehicle?.brand ?: ""} ${vehicle?.model ?: ""}".trim().ifEmpty { "Véhicule" },
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Catégorie : ${vehicle?.vehicleType ?: "Berline"}",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                    PlateBadge(plate = vehicle?.plate ?: "KN-0000-AA")
                }
            }
        }

        // Fiche Prestations & Tarifs
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prestations sélectionnées", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
                    Surface(
                        color = Color(0xFFE0F2FE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${services.size} service(s)",
                            color = Color(0xFF0284C7),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                services.forEach { s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(s.name, fontSize = 13.sp, color = Navy900, fontWeight = FontWeight.Medium)
                        }
                        Text(
                            text = String.format(Locale.US, "%.2f %s", s.price, currency),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    }
                }

                if (discount > 0.0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Remise fidélité :", fontSize = 12.sp, color = Color(0xFF059669))
                        Text(
                            text = String.format(Locale.US, "-%.2f %s", discount, currency),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))
                Spacer(modifier = Modifier.height(12.dp))

                // Total Final High-Contrast
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TOTAL À ENCAISSER", fontWeight = FontWeight.Bold, color = Color(0xFF64748B), fontSize = 11.sp, letterSpacing = 0.5.sp)
                        Text("TTC / Net", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                    Surface(
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.2f %s", total, currency),
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF065F46),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Instructions spéciales
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = {
                Text(
                    "Instructions spéciales pour le laveur (optionnel)...",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            },
            textStyle = carWashInputTextStyle,
            colors = carWashTextFieldColors(),
            modifier = Modifier.fillMaxWidth().testTag("order_notes_input"),
            maxLines = 2,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // =========================================================
        // BOUTON DE VALIDATION HAUTE PERFORMANCE & DESIGN MODERNE
        // =========================================================
        Surface(
            onClick = onConfirm,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("confirm_create_order_button")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0B192C),
                                Color(0xFF1E3A8A),
                                Color(0xFF0284C7)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Badge Icône Valider avec éclat
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Valider la prestation",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Mettre en file d'attente atelier",
                                color = Color(0xFFBAE6FD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Badge dynamique du prix
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.2f %s", total, currency),
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
