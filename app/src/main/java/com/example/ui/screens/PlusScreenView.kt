package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import com.example.data.model.Company
import com.example.data.model.Employee
import com.example.data.model.Expense
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.data.model.WashService
import com.example.ui.CarWashViewModel
import com.example.ui.PlusScreen
import com.example.ui.components.CarWashInputField
import com.example.ui.components.CarWashPrimaryButton
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FormErrorBanner
import com.example.ui.components.FormOptionSelector
import com.example.ui.components.FormSheetHeader
import com.example.ui.components.StatusChip
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
import com.example.util.SupabaseSqlExporter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun PlusScreenView(
    viewModel: CarWashViewModel,
    modifier: Modifier = Modifier
) {
    val activeSubScreen by viewModel.currentPlusScreen.collectAsState()

    when (activeSubScreen) {
        PlusScreen.NONE -> PlusMenuGrid(viewModel, modifier)
        PlusScreen.QUEUE -> QueueSubScreen(viewModel, modifier)
        PlusScreen.PAYMENTS -> PaymentsSubScreen(viewModel, modifier)
        PlusScreen.EXPENSES -> ExpensesSubScreen(viewModel, modifier)
        PlusScreen.STOCK -> StockSubScreen(viewModel, modifier)
        PlusScreen.EMPLOYEES -> EmployeesSubScreen(viewModel, modifier)
        PlusScreen.REPORTS -> ReportsSubScreen(viewModel, modifier)
        PlusScreen.LOYALTY -> LoyaltySubScreen(viewModel, modifier)
        PlusScreen.NOTIFICATIONS -> NotificationsSubScreen(viewModel, modifier)
        PlusScreen.SETTINGS -> SettingsSubScreen(viewModel, modifier)
        PlusScreen.AUDIT_LOGS -> AuditLogsSubScreen(viewModel, modifier)
        PlusScreen.SERVICES_MGMT -> ServicesSubScreen(viewModel, modifier)
        PlusScreen.ABOUT -> AboutSubScreen(viewModel, modifier)
    }
}

data class MenuItemData(
    val screen: PlusScreen,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

@Composable
private fun PlusMenuGrid(
    viewModel: CarWashViewModel,
    modifier: Modifier = Modifier
) {
    val queueCount by viewModel.queueOrders.collectAsState()
    val unreadNotifs by viewModel.unreadNotificationsCount.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    val menuItems = listOf(
        MenuItemData(PlusScreen.QUEUE, "File d'attente", "${queueCount.size} véhicule(s)", Icons.Default.FormatListNumbered, Color(0xFFFEF3C7), Color(0xFFD97706)),
        MenuItemData(PlusScreen.PAYMENTS, "Paiements & Caisse", "Historique paiements", Icons.Default.AttachMoney, Color(0xFFDCFCE7), Color(0xFF16A34A)),
        MenuItemData(PlusScreen.EXPENSES, "Dépenses", "Gestion des charges", Icons.Default.MoneyOff, Color(0xFFFFE4E6), Color(0xFFE11D48)),
        MenuItemData(PlusScreen.STOCK, "Gestion du Stock", "Produits & alertes", Icons.Default.Inventory, Color(0xFFE0F2FE), Color(0xFF0284C7)),
        MenuItemData(PlusScreen.SERVICES_MGMT, "Services & Tarifs", "Configuration lavages", Icons.Default.LocalCarWash, Color(0xFFF1F5F9), Navy900),
        MenuItemData(PlusScreen.EMPLOYEES, "Employés & Laveurs", "Réservé Admin", Icons.Default.Badge, Color(0xFFF3E8FF), Color(0xFF9333EA)),
        MenuItemData(PlusScreen.REPORTS, "Rapports & Chiffres", "Statistiques & Bilan", Icons.Default.Assessment, Color(0xFFDCFCE7), Color(0xFF059669)),
        MenuItemData(PlusScreen.LOYALTY, "Clients Fidèles", "Points & Récompenses", Icons.Default.CardGiftcard, Color(0xFFFEF3C7), Color(0xFFB45309)),
        MenuItemData(PlusScreen.NOTIFICATIONS, "Notifications", "$unreadNotifs non lue(s)", Icons.Default.Notifications, Color(0xFFFFE4E6), Color(0xFFDC2626)),
        MenuItemData(PlusScreen.SETTINGS, "Paramètres & Supabase", "POS, Ticket, DDL SQL", Icons.Default.Settings, Color(0xFFF1F5F9), Navy900),
        MenuItemData(PlusScreen.AUDIT_LOGS, "Journal d'activité", "Audit des actions", Icons.Default.History, Color(0xFFF1F5F9), Color(0xFF475569)),
        MenuItemData(PlusScreen.ABOUT, "À propos", "Réalisée par JuaTech", Icons.Default.Info, Color(0xFFE0F2FE), Color(0xFF0284C7))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toutes les fonctionnalités",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900,
                    fontSize = 18.sp
                )
            )
            Text(
                text = "Accès aux modules de gestion opérationnelle et financière",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openSupabaseDialog() }
                    .testTag("menu_cloud_sync_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    if (isOnline) Color(0xFFBBF7D0) else Color(0xFFFDE68A)
                )
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
                            .background(if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = if (isOnline) Color(0xFF16A34A) else Color(0xFFD97706),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Base Hybride : SQLite & Supabase",
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isOnline) "Connecté • Synchronisation cloud active" else "Hors-ligne • Sauvegarde SQLite locale",
                            fontSize = 12.sp,
                            color = if (isOnline) Color(0xFF166534) else Color(0xFF92400E)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isOnline) Color(0xFF16A34A) else Color(0xFFD97706)
                    ) {
                        Text(
                            text = "Gérer",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        items(menuItems) { item ->
            val currentEmp by viewModel.currentEmployee.collectAsState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (item.screen == PlusScreen.EMPLOYEES && currentEmp?.role != "ADMIN") {
                            viewModel.showMessage("Accès refusé : Seul l'administrateur peut accéder à la gestion des employés et laveurs.")
                        } else {
                            viewModel.openPlusScreen(item.screen)
                        }
                    }
                    .testTag("menu_item_${item.screen.name.lowercase()}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
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
                            .background(item.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 15.sp
                        )
                        Text(
                            text = item.subtitle,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 1 : FILE D'ATTENTE (QUEUE)
// -------------------------------------------------------------
@Composable
fun QueueSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val queueOrders by viewModel.queueOrders.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    SubScreenScaffold(
        title = "File d'attente (${queueOrders.size})",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        if (queueOrders.isEmpty()) {
            EmptyStateView(
                message = "Aucun véhicule dans la file d'attente.",
                icon = Icons.Default.LocalCarWash
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(queueOrders, key = { it.id }) { order ->
                    QueueOrderCard(
                        order = order,
                        currency = currency,
                        onStart = { viewModel.startWash(order.id) },
                        onFinish = { viewModel.finishWash(order.id) },
                        onPay = { viewModel.openPaymentDialog(order) },
                        onPrint = { viewModel.openReceiptDialog(order) }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 2 : PAIEMENTS & ENCAISSEMENTS
// -------------------------------------------------------------
@Composable
fun PaymentsSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val payments by viewModel.payments.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    val totalEncaisse = payments.sumOf { it.amount }

    SubScreenScaffold(
        title = "Paiements & Caisse",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        // Summary
        Card(
            colors = CardDefaults.cardColors(containerColor = Navy900),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total encaissé", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Text(
                    text = String.format(Locale.US, "%.2f %s", totalEncaisse, currency),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Text("${payments.size} transaction(s) enregistrée(s)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (payments.isEmpty()) {
            EmptyStateView(message = "Aucun paiement enregistré.", icon = Icons.Default.AttachMoney)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(payments, key = { it.id }) { pay ->
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(pay.createdAt))
                    val relatedOrder = orders.firstOrNull { it.id == pay.orderId }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${pay.customerName} • ${pay.vehiclePlate}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Navy900
                                )
                                Text(
                                    text = "${pay.orderNumber} • ${pay.paymentMethod} • $dateFormat",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                if (pay.reference.isNotBlank()) {
                                    Text("Réf: ${pay.reference}", fontSize = 10.sp, color = BlueAccent)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format(Locale.US, "%.2f %s", pay.amount, currency),
                                    fontWeight = FontWeight.Bold,
                                    color = StatusCompleted,
                                    fontSize = 15.sp
                                )
                                if (relatedOrder != null) {
                                    IconButton(
                                        onClick = { viewModel.openReceiptDialog(relatedOrder) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Print, contentDescription = "Ticket", tint = Navy900, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 3 : DÉPENSES
// -------------------------------------------------------------
@Composable
fun ExpensesSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val expenses by viewModel.expenses.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    var showAddExpense by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Produits") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val totalExpenses = expenses.sumOf { it.amount }

    SubScreenScaffold(
        title = "Dépenses & Charges",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFFECDD3)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total des dépenses", fontSize = 12.sp, color = StatusDebt)
                    Text(
                        text = String.format(Locale.US, "%.2f %s", totalExpenses, currency),
                        fontWeight = FontWeight.Bold,
                        color = StatusDebt,
                        fontSize = 22.sp
                    )
                }

                Button(
                    onClick = { showAddExpense = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusDebt,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (expenses.isEmpty()) {
            EmptyStateView(message = "Aucune dépense enregistrée.", icon = Icons.Default.MoneyOff)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(expenses, key = { it.id }) { exp ->
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date(exp.date))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exp.category, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                                if (exp.description.isNotBlank()) {
                                    Text(exp.description, fontSize = 12.sp, color = TextSecondary)
                                }
                                Text("$dateFormat • par ${exp.employeeName}", fontSize = 11.sp, color = TextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format(Locale.US, "-%.2f %s", exp.amount, currency),
                                    fontWeight = FontWeight.Bold,
                                    color = StatusDebt,
                                    fontSize = 15.sp
                                )
                                IconButton(onClick = { viewModel.deleteExpense(exp) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddExpense) {
        AddExpenseSheet(
            onDismiss = { showAddExpense = false },
            onSave = { cat, amt, desc ->
                viewModel.addExpense(cat, amt, desc)
                showAddExpense = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    onDismiss: () -> Unit,
    onSave: (category: String, amount: Double, description: String) -> Unit
) {
    var category by remember { mutableStateOf("Produits") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Produits", "Électricité / Eau", "Salaires", "Entretien matériel", "Loyer", "Autre")

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            FormSheetHeader(
                title = "Nouvelle Dépense",
                subtitle = "Enregistrez une sortie de caisse ou frais d'exploitation",
                icon = Icons.Default.MoneyOff,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (errorMessage != null) {
                FormErrorBanner(message = errorMessage!!)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "CATÉGORIE DE DÉPENSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.take(3).forEach { cat ->
                    val isSel = category == cat
                    Surface(
                        color = if (isSel) Navy900 else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSel) Navy900 else BorderLight),
                        modifier = Modifier.weight(1f).clickable { category = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSel) Color.White else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CarWashInputField(
                value = amountText,
                onValueChange = { 
                    amountText = it 
                    errorMessage = null
                },
                label = "Montant de la dépense *",
                placeholder = "ex: 25.0",
                leadingIcon = Icons.Default.AttachMoney,
                isError = errorMessage != null && amountText.isBlank(),
                supportingText = if (errorMessage != null && amountText.isBlank()) "Le montant est obligatoire" else null
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = description,
                onValueChange = { description = it },
                label = "Description / Motif de la dépense",
                placeholder = "ex: Bidon de shampoing moussant 5L"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.trim().replace(",", ".").toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMessage = "Veuillez entrer un montant valide supérieur à 0"
                        return@Button
                    }
                    onSave(category, amt, description.trim())
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatusDebt,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Enregistrer la dépense", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 15.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 4 : STOCK
// -------------------------------------------------------------
@Composable
fun StockSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val products by viewModel.products.collectAsState()
    var showAdjustDialog by remember { mutableStateOf<Product?>(null) }
    var showAddProduct by remember { mutableStateOf(false) }

    SubScreenScaffold(
        title = "Gestion du Stock",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${products.size} article(s) suivi(s)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextSecondary)
            Button(
                onClick = { showAddProduct = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy900,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nouveau", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products, key = { it.id }) { prod ->
                val isLow = prod.currentStock <= prod.minThreshold
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isLow) Color(0xFFFECDD3) else BorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.name, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                            Text(
                                text = "Seuil min: ${prod.minThreshold} ${prod.unit}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            if (isLow) {
                                Text("⚠️ Stock faible !", color = StatusDebt, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${prod.currentStock} ${prod.unit}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isLow) StatusDebt else Navy900
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { showAdjustDialog = prod },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = Navy900
                                ),
                                border = BorderStroke(1.5.dp, Navy900),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Ajuster", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Navy900)
                            }
                        }
                    }
                }
            }
        }
    }

    // Adjust Stock Sheet
    showAdjustDialog?.let { prod ->
        StockAdjustSheet(
            product = prod,
            onDismiss = { showAdjustDialog = null },
            onConfirm = { type, qty, reason ->
                viewModel.adjustStock(prod.id, type, qty, reason)
                showAdjustDialog = null
            }
        )
    }

    if (showAddProduct) {
        AddProductSheet(
            onDismiss = { showAddProduct = false },
            onSave = { name, stock, unit, minTh ->
                viewModel.addProduct(name, stock, unit, minTh, 0.0)
                showAddProduct = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustSheet(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (type: String, qty: Double, reason: String) -> Unit
) {
    var isAdd by remember { mutableStateOf(true) }
    var qtyText by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            FormSheetHeader(
                title = "Ajustement Stock",
                subtitle = "${product.name} • Actuel: ${product.currentStock} ${product.unit}",
                icon = Icons.Default.Inventory,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "TYPE DE MOUVEMENT",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = if (isAdd) Color(0xFF059669) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.2.dp, if (isAdd) Color(0xFF059669) else BorderLight),
                    modifier = Modifier.weight(1f).clickable { isAdd = true }
                ) {
                    Text(
                        text = "+ ENTRÉE",
                        color = if (isAdd) Color.White else Navy900,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                    )
                }
                Surface(
                    color = if (!isAdd) Color(0xFFE11D48) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.2.dp, if (!isAdd) Color(0xFFE11D48) else BorderLight),
                    modifier = Modifier.weight(1f).clickable { isAdd = false }
                ) {
                    Text(
                        text = "- SORTIE",
                        color = if (!isAdd) Color.White else Navy900,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CarWashInputField(
                value = qtyText,
                onValueChange = { qtyText = it },
                label = "Quantité (${product.unit}) *",
                placeholder = "ex: 5.0"
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = reason,
                onValueChange = { reason = it },
                label = "Motif ou référence",
                placeholder = "ex: Achat fournisseur, Consommation atelier"
            )

            Spacer(modifier = Modifier.height(20.dp))

            CarWashPrimaryButton(
                text = "Valider le mouvement",
                onClick = {
                    val q = qtyText.replace(",", ".").toDoubleOrNull()
                    if (q != null && q > 0) {
                        onConfirm(if (isAdd) "ENTREE" else "SORTIE", q, reason)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, stock: Double, unit: String, minThreshold: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("10") }
    var unit by remember { mutableStateOf("L") }
    var minThText by remember { mutableStateOf("5") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            FormSheetHeader(
                title = "Nouveau Produit au Stock",
                subtitle = "Enregistrez un article, stock initial et alerte de réapprovisionnement",
                icon = Icons.Default.Inventory,
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
                label = "Désignation de l'article *",
                placeholder = "ex: Shampoing carrosserie moussant",
                isError = errorMessage != null && name.isBlank(),
                supportingText = if (errorMessage != null && name.isBlank()) "Le nom est obligatoire" else null
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CarWashInputField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = "Stock initial",
                    placeholder = "ex: 10",
                    modifier = Modifier.weight(1f)
                )
                CarWashInputField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = "Unité",
                    placeholder = "L, pcs, kg",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = minThText,
                onValueChange = { minThText = it },
                label = "Seuil d'alerte minimum",
                placeholder = "ex: 5",
                supportingText = "Une alerte sera affichée si le stock passe sous ce seuil."
            )

            Spacer(modifier = Modifier.height(20.dp))

            CarWashPrimaryButton(
                text = "Enregistrer l'article",
                onClick = {
                    if (name.trim().isBlank()) {
                        errorMessage = "Veuillez renseigner le nom de l'article"
                        return@CarWashPrimaryButton
                    }
                    val s = stockText.trim().replace(",", ".").toDoubleOrNull() ?: 0.0
                    val m = minThText.trim().replace(",", ".").toDoubleOrNull() ?: 5.0
                    onSave(name.trim(), s, unit.trim().ifBlank { "L" }, m)
                }
            )
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 5 : EMPLOYÉS & RÔLES
// -------------------------------------------------------------
private fun getEmployeeAvatarGradient(name: String, role: String): Brush {
    if (role.equals("ADMIN", ignoreCase = true)) {
        return Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF0284C7)))
    }
    val palettes = listOf(
        listOf(Color(0xFF4F46E5), Color(0xFF3730A3)), // Indigo
        listOf(Color(0xFF059669), Color(0xFF065F46)), // Emerald
        listOf(Color(0xFFD97706), Color(0xFFB45309)), // Amber
        listOf(Color(0xFF7C3AED), Color(0xFF5B21B6)), // Purple
        listOf(Color(0xFF0891B2), Color(0xFF155E75)), // Cyan
        listOf(Color(0xFFE11D48), Color(0xFF9F1239))  // Rose
    )
    val hash = kotlin.math.abs(name.trim().hashCode())
    return Brush.linearGradient(palettes[hash % palettes.size])
}

private fun getEmployeeInitials(name: String): String {
    val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "EM"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
    }
}

@Composable
fun EmployeesSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val employees by viewModel.employees.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val currentEmp by viewModel.currentEmployee.collectAsState()
    var showAddEmployee by remember { mutableStateOf(false) }
    var employeeToEdit by remember { mutableStateOf<Employee?>(null) }
    var employeeToDelete by remember { mutableStateOf<Employee?>(null) }

    SubScreenScaffold(
        title = "Employés & Laveurs",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${employees.size} membre(s) de l'équipe", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextSecondary)
            Button(
                onClick = { showAddEmployee = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy900,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nouveau", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(employees, key = { it.id }) { emp ->
                val washesDone = orders.count { it.employeeName.contains(emp.name.split(" ").first()) }
                val isCurrentSession = currentEmp?.id == emp.id

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Enhanced Employee Avatar
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(getEmployeeAvatarGradient(emp.name, emp.role))
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getEmployeeInitials(emp.name),
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(emp.name, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                                    if (isCurrentSession) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFDCFCE7),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                "Moi",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF16A34A),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text("${emp.role} • ${emp.phone.ifBlank { "Aucun tél" }}", fontSize = 12.sp, color = TextSecondary)
                                Text("Lavages effectués : $washesDone", fontSize = 11.sp, color = BlueAccent, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "PIN: ${emp.pinCode}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            // Edit button
                            IconButton(
                                onClick = { employeeToEdit = emp },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Modifier l'employé",
                                    tint = Navy900,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Delete button (disabled or hidden for active session)
                            if (!isCurrentSession) {
                                IconButton(
                                    onClick = { employeeToDelete = emp },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Supprimer l'employé",
                                        tint = Color(0xFFFDA4AF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog for deleting an employee
    employeeToDelete?.let { emp ->
        AlertDialog(
            onDismissRequest = { employeeToDelete = null },
            title = { Text("Supprimer l'employé ?", fontWeight = FontWeight.Bold, color = Navy900) },
            text = { Text("Êtes-vous sûr de vouloir supprimer ${emp.name} (${emp.role}) de l'équipe ? Ses accès seront immédiatement révoqués.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEmployee(emp)
                        employeeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDebt)
                ) {
                    Text("Oui, Supprimer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { employeeToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Edit Employee Sheet
    employeeToEdit?.let { emp ->
        EditEmployeeSheet(
            employee = emp,
            onDismiss = { employeeToEdit = null },
            onSave = { updatedEmp ->
                viewModel.updateEmployee(updatedEmp)
                employeeToEdit = null
            }
        )
    }

    if (showAddEmployee) {
        AddEmployeeSheet(
            onDismiss = { showAddEmployee = false },
            onSave = { name, email, phone, role, pin ->
                viewModel.addEmployee(name, email, phone, role, pin)
                showAddEmployee = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployeeSheet(
    employee: Employee,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var name by remember { mutableStateOf(employee.name) }
    var phone by remember { mutableStateOf(employee.phone) }
    var role by remember { mutableStateOf(employee.role) }
    var pin by remember { mutableStateOf(employee.pinCode) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            FormSheetHeader(
                title = "Modifier l'Employé",
                subtitle = "Mettez à jour le profil, téléphone, rôle ou code d'accès",
                icon = Icons.Default.Badge,
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
                placeholder = "ex: Patrick KARUBANDIKA",
                leadingIcon = Icons.Default.Person,
                isError = errorMessage != null && name.isBlank(),
                supportingText = if (errorMessage != null && name.isBlank()) "Le nom est obligatoire" else null
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = phone,
                onValueChange = {
                    phone = it
                    errorMessage = null
                },
                label = "Numéro de téléphone",
                placeholder = "ex: +243 99 123 4567",
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "RÔLE ET RESPONSABILITÉ",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            FormOptionSelector(
                options = listOf(
                    "EMPLOYEE" to "Employé",
                    "MANAGER" to "Gérant",
                    "ADMIN" to "Admin"
                ),
                selectedOption = role,
                onOptionSelected = { role = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CarWashInputField(
                value = pin,
                onValueChange = {
                    if (it.length <= 8) pin = it
                },
                label = "Code PIN d'accès (4 chiffres)",
                placeholder = "ex: 1234",
                leadingIcon = Icons.Default.Lock
            )

            Spacer(modifier = Modifier.height(20.dp))

            CarWashPrimaryButton(
                text = "Enregistrer les modifications",
                onClick = {
                    if (name.trim().isBlank()) {
                        errorMessage = "Veuillez renseigner le nom complet de l'employé"
                        return@CarWashPrimaryButton
                    }
                    val cleanPhone = phone.trim().ifBlank { "-" }
                    val cleanPin = pin.trim().ifBlank { employee.pinCode }
                    val updated = employee.copy(
                        name = name.trim(),
                        phone = cleanPhone,
                        role = role,
                        pinCode = cleanPin
                    )
                    onSave(updated)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, phone: String, role: String, pin: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("EMPLOYEE") }
    var pin by remember { mutableStateOf("1234") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            FormSheetHeader(
                title = "Nouveau Collaborateur",
                subtitle = "Ajoutez un employé, gérant ou administrateur du car wash",
                icon = Icons.Default.Badge,
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
                placeholder = "ex: Patrick KARUBANDIKA",
                leadingIcon = Icons.Default.Person,
                isError = errorMessage != null && name.isBlank(),
                supportingText = if (errorMessage != null && name.isBlank()) "Le nom est obligatoire" else null
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = phone,
                onValueChange = { 
                    phone = it 
                    errorMessage = null
                },
                label = "Numéro de téléphone",
                placeholder = "ex: +243 99 123 4567",
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "RÔLE ET RESPONSABILITÉ",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            FormOptionSelector(
                options = listOf(
                    "EMPLOYEE" to "Employé",
                    "MANAGER" to "Gérant",
                    "ADMIN" to "Admin"
                ),
                selectedOption = role,
                onOptionSelected = { role = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CarWashInputField(
                value = pin,
                onValueChange = { pin = it },
                label = "Code PIN d'accès (4 chiffres)",
                placeholder = "ex: 1234",
                leadingIcon = Icons.Default.Lock
            )

            Spacer(modifier = Modifier.height(20.dp))

            CarWashPrimaryButton(
                text = "Enregistrer le membre",
                onClick = {
                    if (name.trim().isBlank()) {
                        errorMessage = "Veuillez renseigner le nom complet de l'employé"
                        return@CarWashPrimaryButton
                    }
                    val cleanPhone = phone.trim().ifBlank { "-" }
                    val cleanPin = pin.trim().ifBlank { "1234" }
                    onSave(name.trim(), "${name.trim().lowercase().replace(" ", "")}@carwash.com", cleanPhone, role, cleanPin)
                }
            )
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 6 : RAPPORTS & BILAN
// -------------------------------------------------------------
@Composable
fun ReportsSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val orders by viewModel.orders.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"
    val showDecimals = company?.showDecimals ?: true

    fun formatAmount(amount: Double, curr: String, decimals: Boolean): String {
        val formatted = if (decimals) {
            String.format(Locale.US, "%,.2f", amount)
        } else {
            String.format(Locale.US, "%,.0f", amount)
        }
        return "$formatted $curr"
    }

    var selectedPeriod by remember { mutableStateOf("MOIS") } // JOUR, MOIS, ANNEE, TOUT
    var searchQuery by remember { mutableStateOf("") }

    // Calendar bounds for filters
    val now = remember { Calendar.getInstance() }
    val todayStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val monthStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val yearStart = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // Filtered lists based on period & search query
    val filteredOrders = remember(orders, selectedPeriod, searchQuery) {
        orders.filter { ord ->
            val matchesPeriod = when (selectedPeriod) {
                "JOUR" -> ord.createdAt >= todayStart
                "MOIS" -> ord.createdAt >= monthStart
                "ANNEE" -> ord.createdAt >= yearStart
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                ord.customerName.contains(searchQuery, ignoreCase = true) ||
                ord.vehiclePlate.contains(searchQuery, ignoreCase = true) ||
                ord.employeeName.contains(searchQuery, ignoreCase = true) ||
                ord.orderNumber.contains(searchQuery, ignoreCase = true)

            matchesPeriod && matchesSearch
        }
    }

    val filteredExpenses = remember(expenses, selectedPeriod, searchQuery) {
        expenses.filter { exp ->
            val matchesPeriod = when (selectedPeriod) {
                "JOUR" -> exp.date >= todayStart
                "MOIS" -> exp.date >= monthStart
                "ANNEE" -> exp.date >= yearStart
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                exp.category.contains(searchQuery, ignoreCase = true) ||
                exp.description.contains(searchQuery, ignoreCase = true) ||
                exp.employeeName.contains(searchQuery, ignoreCase = true)

            matchesPeriod && matchesSearch
        }
    }

    val totalRecettes = filteredOrders.sumOf { it.paidAmount }
    val totalDettes = filteredOrders.sumOf { (it.finalAmount - it.paidAmount).coerceAtLeast(0.0) }
    val totalDepenses = filteredExpenses.sumOf { it.amount }
    val beneficeNet = totalRecettes - totalDepenses
    val totalVehicles = filteredOrders.count { it.status == "COMPLETED" }
    val panierMoyen = if (filteredOrders.isNotEmpty()) totalRecettes / filteredOrders.size else 0.0

    SubScreenScaffold(
        title = "Rapports & Statistiques",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Period Filter Chips (Jour, Mois, Année, Tout)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "JOUR" to "Aujourd'hui",
                        "MOIS" to "Ce Mois",
                        "ANNEE" to "Cette Année",
                        "TOUT" to "Tout"
                    ).forEach { (code, label) ->
                        val isSelected = selectedPeriod == code
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Navy900 else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) Navy900 else BorderLight),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPeriod = code }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Navy900
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar for Reports
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filtrer par client, plaque, employé...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Navy900, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Effacer", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = carWashInputTextStyle.copy(fontSize = 12.sp),
                    colors = carWashTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Metric Cards Grid (2x2)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Card 1 : Recettes
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Recettes Encaissement", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatAmount(totalRecettes, currency, showDecimals),
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = Color(0xFF15803D)
                            )
                            Text("${filteredOrders.size} prestation(s)", fontSize = 10.sp, color = Color(0xFF166534))
                        }
                    }

                    // Card 2 : Dépenses
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Charges & Dépenses", fontSize = 11.sp, color = Color(0xFF9F1239), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "-${formatAmount(totalDepenses, currency, showDecimals)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = Color(0xFFBE123C)
                            )
                            Text("${filteredExpenses.size} dépense(s)", fontSize = 10.sp, color = Color(0xFF9F1239))
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Card 3 : Bénéfice Net
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (beneficeNet >= 0) Color(0xFFEFF6FF) else Color(0xFFFFFBEB)
                        ),
                        border = BorderStroke(1.dp, if (beneficeNet >= 0) Color(0xFFBFDBFE) else Color(0xFFFDE68A)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Bénéfice Net", fontSize = 11.sp, color = Navy900, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatAmount(beneficeNet, currency, showDecimals),
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = if (beneficeNet >= 0) Color(0xFF1D4ED8) else Color(0xFFB45309)
                            )
                            Text(if (beneficeNet >= 0) "Marge positive" else "Déficit temporaire", fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    // Card 4 : Véhicules lavés & Panier Moyen
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Véhicules & Panier", fontSize = 11.sp, color = Navy900, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalVehicles terminés",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Navy900
                            )
                            Text("Moy: ${formatAmount(panierMoyen, currency, showDecimals)}", fontSize = 10.sp, color = BlueAccent)
                        }
                    }
                }
            }

            // Visual Chart Section : Bar chart with Jetpack Compose Canvas
            item {
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
                            Text(
                                text = "Graphique Comparatif : Recettes vs Dépenses",
                                fontWeight = FontWeight.Bold,
                                color = Navy900,
                                fontSize = 14.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF16A34A)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Recettes", fontSize = 10.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE11D48)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Dépenses", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom animated comparison bar layout
                        val maxComparisonVal = (totalRecettes.coerceAtLeast(totalDepenses)).coerceAtLeast(100.0)
                        val recetteBarRatio = (totalRecettes / maxComparisonVal).toFloat().coerceIn(0.05f, 1f)
                        val depenseBarRatio = (totalDepenses / maxComparisonVal).toFloat().coerceIn(0.05f, 1f)

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Recettes horizontal gauge bar
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Volume Recettes", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                                    Text(formatAmount(totalRecettes, currency, showDecimals), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF1F5F9))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(recetteBarRatio)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                    listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                                                )
                                            )
                                    )
                                }
                            }

                            // Dépenses horizontal gauge bar
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Volume Dépenses", fontSize = 11.sp, color = Color(0xFFE11D48), fontWeight = FontWeight.Bold)
                                    Text(formatAmount(totalDepenses, currency, showDecimals), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF1F5F9))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(depenseBarRatio)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                    listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
                                                )
                                            )
                                    )
                                }
                            }
                        }

                        if (totalDettes > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFFBEB),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Dettes clients non recouvrées sur la période :", fontSize = 11.sp, color = Color(0xFF92400E))
                                    Text(formatAmount(totalDettes, currency, showDecimals), fontWeight = FontWeight.Bold, color = Color(0xFFB45309), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Top Washers Leaderboard Section
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Classement de l'équipe (Laveurs & Opérateurs)",
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (employees.isEmpty()) {
                            Text("Aucun employé configuré.", fontSize = 12.sp, color = TextSecondary)
                        } else {
                            employees.forEachIndexed { idx, emp ->
                                val washesCount = filteredOrders.count { it.employeeName.contains(emp.name.split(" ").first()) }
                                val empRevenue = filteredOrders
                                    .filter { it.employeeName.contains(emp.name.split(" ").first()) }
                                    .sumOf { it.paidAmount }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = when (idx) {
                                                0 -> Color(0xFFFEF3C7)
                                                1 -> Color(0xFFF1F5F9)
                                                else -> Color(0xFFEDE9FE)
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${idx + 1}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Navy900
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(emp.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Navy900)
                                            Text("${emp.role} • $washesCount lavage(s)", fontSize = 10.sp, color = TextSecondary)
                                        }
                                    }

                                    Text(
                                        text = formatAmount(empRevenue, currency, showDecimals),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BlueAccent
                                    )
                                }
                                if (idx < employees.size - 1) {
                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 7 : FIDÉLITÉ
// -------------------------------------------------------------
@Composable
fun LoyaltySubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val loyaltyAccounts by viewModel.loyaltyAccounts.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val company by viewModel.company.collectAsState()
    val threshold = company?.loyaltyThreshold ?: 10

    SubScreenScaffold(
        title = "Programme de Fidélité",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Règle du programme", fontWeight = FontWeight.Bold, color = Color(0xFF92400E), fontSize = 14.sp)
                Text(
                    text = "Chaque lavage = 1 point. Après $threshold lavages, le client bénéficie d'un lavage extérieur offert !",
                    fontSize = 12.sp,
                    color = Color(0xFF78350F)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(loyaltyAccounts, key = { it.id }) { acc ->
                val cust = customers.firstOrNull { it.id == acc.customerId }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(cust?.name ?: "Client", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                            Text("Total lavages : ${acc.totalWashes}", fontSize = 12.sp, color = TextSecondary)
                        }

                        Surface(
                            color = Color(0xFFE0F2FE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⭐ ${acc.points} / $threshold pts",
                                fontWeight = FontWeight.Bold,
                                color = BlueAccent,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 8 : NOTIFICATIONS
// -------------------------------------------------------------
@Composable
fun NotificationsSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val notifs by viewModel.notifications.collectAsState()

    SubScreenScaffold(
        title = "Notifications",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${notifs.size} notification(s)", color = TextSecondary, fontSize = 13.sp)
            OutlinedButton(
                onClick = { viewModel.markAllNotificationsRead() },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Navy900
                ),
                border = BorderStroke(1.5.dp, Navy900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Tout marquer comme lu", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Navy900)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (notifs.isEmpty()) {
            EmptyStateView(message = "Aucune notification.", icon = Icons.Default.Notifications)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notifs, key = { it.id }) { n ->
                    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.FRANCE).format(Date(n.createdAt))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.markNotificationRead(n.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (!n.isRead) Color(0xFFE0F2FE) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(n.title, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
                                Text(dateFormat, fontSize = 10.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(n.message, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 9 : PARAMÈTRES & SUPABASE EXPORT
// -------------------------------------------------------------
@Composable
fun SettingsSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val company by viewModel.company.collectAsState()
    val currentEmp by viewModel.currentEmployee.collectAsState()
    val context = LocalContext.current

    var name by remember(company) { mutableStateOf(company?.name ?: "Car Wash Pro") }
    var phone by remember(company) { mutableStateOf(company?.phone ?: "") }
    var address by remember(company) { mutableStateOf(company?.address ?: "") }
    var currency by remember(company) { mutableStateOf(company?.currencySymbol ?: "$") }
    var showDecimals by remember(company) { mutableStateOf(company?.showDecimals ?: true) }
    var paperWidth by remember(company) { mutableStateOf(company?.receiptPaperWidth ?: "80mm") }
    var headerNote by remember(company) { mutableStateOf(company?.receiptHeaderNote ?: "") }
    var footerNote by remember(company) { mutableStateOf(company?.receiptFooterNote ?: "") }

    var showSqlModal by remember { mutableStateOf(false) }
    var showClearDbDialog by remember { mutableStateOf(false) }

    if (showClearDbDialog) {
        AlertDialog(
            onDismissRequest = { showClearDbDialog = false },
            title = {
                Text(
                    text = "⚠️ Vider la base locale et en ligne ?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE11D48),
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Attention : Cette opération administrative irréversible va vider simultanément toutes les prestations, clients, véhicules, paiements, dépenses et produits de la base SQLite locale ET de la base distante Supabase.\n\nÊtes-vous certain de vouloir tout réinitialiser ?",
                    color = Navy900,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDbDialog = false
                        viewModel.clearAllDatabases {
                            viewModel.showMessage("Bases locale et distante vidées avec succès !")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Text("Oui, tout effacer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDbDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    SubScreenScaffold(
        title = "Paramètres de l'entreprise",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Text("Coordonnées de l'établissement :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du Car Wash", fontWeight = FontWeight.SemiBold) },
                    textStyle = carWashInputTextStyle,
                    colors = carWashTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Téléphone", fontWeight = FontWeight.SemiBold) },
                        textStyle = carWashInputTextStyle,
                        colors = carWashTextFieldColors(),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Devise ($, €, FC)", fontWeight = FontWeight.SemiBold) },
                        textStyle = carWashInputTextStyle,
                        colors = carWashTextFieldColors(),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse physique", fontWeight = FontWeight.SemiBold) },
                    textStyle = carWashInputTextStyle,
                    colors = carWashTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Choix exclusif : Format des nombres (Entier ou Décimal, pas les deux à la fois)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Format d'affichage des montants :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                Text("Choisissez l'affichage en entier ou en décimal (exclusif)", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        // Option 1 : Entier
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDecimals = false }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = !showDecimals,
                                onClick = { showDecimals = false },
                                colors = RadioButtonDefaults.colors(selectedColor = Navy900)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Nombres entiers (sans décimales)",
                                    fontWeight = if (!showDecimals) FontWeight.Bold else FontWeight.Medium,
                                    color = Navy900,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Exemple : 15 000 $currency (recommandé pour FC, XOF, XAF, YEN)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

                        // Option 2 : Décimal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDecimals = true }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = showDecimals,
                                onClick = { showDecimals = true },
                                colors = RadioButtonDefaults.colors(selectedColor = Navy900)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Nombres décimaux (avec 2 décimales)",
                                    fontWeight = if (showDecimals) FontWeight.Bold else FontWeight.Medium,
                                    color = Navy900,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Exemple : 15 000.00 $currency (recommandé pour USD, EUR)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Configuration Imprimante POS Thermique :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("80mm" to "80 mm (Standard POS)", "58mm" to "58 mm (POS portable)").forEach { (w, label) ->
                        val isSel = paperWidth == w
                        Surface(
                            color = if (isSel) Navy900 else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSel) Navy900 else BorderLight),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paperWidth = w }
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = headerNote,
                    onValueChange = { headerNote = it },
                    label = { Text("En-tête personnalisé sur le ticket", fontWeight = FontWeight.SemiBold) },
                    textStyle = carWashInputTextStyle,
                    colors = carWashTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = footerNote,
                    onValueChange = { footerNote = it },
                    label = { Text("Pied de page sur le ticket (ex: Pas de réclamation après 24h)", fontWeight = FontWeight.SemiBold) },
                    textStyle = carWashInputTextStyle,
                    colors = carWashTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        val current = company ?: Company()
                        viewModel.updateCompany(
                            current.copy(
                                name = name,
                                phone = phone,
                                address = address,
                                currencySymbol = currency,
                                showDecimals = showDecimals,
                                receiptPaperWidth = paperWidth,
                                receiptHeaderNote = headerNote,
                                receiptFooterNote = footerNote
                            )
                        )
                        viewModel.showMessage("Paramètres de l'entreprise enregistrés !")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Navy900,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Sauvegarder les modifications", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Supabase Architecture DDL SQL Viewer & Exporter
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = BlueAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Base Supabase & RLS Multi-Tenant", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Générez et copiez le script SQL DDL complet pour déployer la base PostgreSQL sur Supabase avec Row Level Security (RLS) et permissions.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        CarWashPrimaryButton(
                            text = "Configurer la synchronisation Supabase",
                            icon = Icons.Default.Storage,
                            onClick = { viewModel.openSupabaseDialog() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showSqlModal = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Navy900
                            ),
                            border = BorderStroke(1.5.dp, Navy900),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Afficher et copier le script SQL DDL", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Navy900)
                        }
                    }
                }
            }

            // Danger Zone : Vider la base de données locale ET en ligne (Admin uniquement)
            if (currentEmp?.role == "ADMIN") {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Zone d'Administration : Réinitialisation globale",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Seul l'administrateur peut exécuter cette action. Ce bouton vide simultanément la base de données SQLite locale de l'appareil et toutes les tables de la base de données distante Supabase.",
                                fontSize = 12.sp,
                                color = Color(0xFF881337),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showClearDbDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE11D48),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Vider la base locale et en ligne", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showSqlModal) {
        SupabaseSqlModal(
            viewModel = viewModel,
            onDismiss = { showSqlModal = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseSqlModal(
    viewModel: CarWashViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val sqlText = remember { SupabaseSqlExporter.generateCompleteSql() }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
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
                Text("Script SQL Supabase DDL + RLS", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 16.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    item {
                        Text(
                            text = sqlText,
                            color = Color(0xFF38BDF8),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(sqlText))
                    android.widget.Toast.makeText(context, "Script SQL Supabase copié dans le presse-papiers !", android.widget.Toast.LENGTH_SHORT).show()
                    viewModel.showMessage("Script SQL copié avec succès !")
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy900,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copier tout le script SQL", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 10 : JOURNAL D'AUDIT
// -------------------------------------------------------------
@Composable
fun AuditLogsSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val logs by viewModel.auditLogs.collectAsState()

    SubScreenScaffold(
        title = "Journal d'Audit",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        if (logs.isEmpty()) {
            EmptyStateView(message = "Aucun événement d'audit.", icon = Icons.Default.History)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(logs, key = { it.id }) { log ->
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE).format(Date(log.timestamp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
                                Text(dateFormat, fontSize = 10.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(log.details, fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Par : ${log.userName} (${log.userRole})", fontSize = 10.sp, color = BlueAccent)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 11 : SERVICES & TARIFS
// -------------------------------------------------------------
@Composable
fun ServicesSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val services by viewModel.services.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    var showAddService by remember { mutableStateOf(false) }

    SubScreenScaffold(
        title = "Services & Tarifs",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${services.size} service(s) configuré(s)", color = TextSecondary, fontSize = 13.sp)
            Button(
                onClick = { showAddService = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy900,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(services, key = { it.id }) { s ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.name, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                            Text("Durée estimée : ~${s.durationMinutes} minutes", fontSize = 12.sp, color = TextSecondary)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format(Locale.US, "%.2f %s", s.price, currency),
                                fontWeight = FontWeight.Bold,
                                color = BlueAccent,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(onClick = { viewModel.deleteService(s) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = TextMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddService) {
        AddServiceSheet(
            onDismiss = { showAddService = false },
            onSave = { name, price, duration ->
                viewModel.addService(name, price, duration)
                showAddService = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double, duration: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("25") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            FormSheetHeader(
                title = "Nouveau Service de Lavage",
                subtitle = "Configurez la formule, son tarif et la durée estimée",
                icon = Icons.Default.LocalCarWash,
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
                label = "Nom de la formule *",
                placeholder = "ex: Lavage VIP Mousse + Châssis",
                leadingIcon = Icons.Default.LocalCarWash,
                isError = errorMessage != null && name.isBlank(),
                supportingText = if (errorMessage != null && name.isBlank()) "Le nom est obligatoire" else null
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = priceText,
                onValueChange = { 
                    priceText = it 
                    errorMessage = null
                },
                label = "Tarif / Prix *",
                placeholder = "ex: 15.0",
                leadingIcon = Icons.Default.AttachMoney,
                isError = errorMessage != null && (priceText.isBlank() || priceText.replace(",", ".").toDoubleOrNull() == null),
                supportingText = if (errorMessage != null && (priceText.isBlank() || priceText.replace(",", ".").toDoubleOrNull() == null)) "Prix valide obligatoire" else null
            )

            Spacer(modifier = Modifier.height(10.dp))

            CarWashInputField(
                value = durationText,
                onValueChange = { durationText = it },
                label = "Durée estimée (minutes)",
                placeholder = "ex: 25",
                leadingIcon = Icons.Default.History
            )

            Spacer(modifier = Modifier.height(20.dp))

            CarWashPrimaryButton(
                text = "Enregistrer le service",
                onClick = {
                    if (name.trim().isBlank()) {
                        errorMessage = "Veuillez renseigner le nom du service"
                        return@CarWashPrimaryButton
                    }
                    val p = priceText.trim().replace(",", ".").toDoubleOrNull()
                    if (p == null || p <= 0) {
                        errorMessage = "Veuillez entrer un prix valide supérieur à 0"
                        return@CarWashPrimaryButton
                    }
                    val d = durationText.trim().toIntOrNull() ?: 20
                    onSave(name.trim(), p, d)
                }
            )
        }
    }
}

// -------------------------------------------------------------
// HELPER SCAFFOLD FOR SUB-SCREENS
// -------------------------------------------------------------
@Composable
fun SubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Navy900)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900,
                    fontSize = 18.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        content()
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 12 : À PROPOS (JuaTech)
// -------------------------------------------------------------
@Composable
fun AboutSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val company by viewModel.company.collectAsState()

    SubScreenScaffold(
        title = "À propos de l'application",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.LocalCarWash,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = company?.name?.ifBlank { "Flash Car Wash Pro" } ?: "Flash Car Wash Pro",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Système de Gestion & Caisse POS pour Station de Lavage",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Version 2.5.0 (Édition Pro 2026)",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Realisation & Développeur : JuaTech
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF0284C7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE0F2FE),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Conception & Développement",
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Cette application a été réalisée par JuaTech",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Navy900,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "JuaTech conçoit et déploie des solutions numériques performantes, intelligentes et adaptées aux réalités des entreprises modernes (mode hors-ligne résilient et cloud managé).",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Contact & Partenariat :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Entreprise : JuaTech Technologies", fontSize = 12.sp, color = TextSecondary)
                                Text("• Support : juatech.ia@gmail.com", fontSize = 12.sp, color = TextSecondary)
                                Text("• Disponibilité : 24/7 pour les stations partenaires", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Points clés de l'application
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fonctionnalités Incluses", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            "Sécurité & Profils" to "Authentification immédiate par code PIN personnel (Admin / Gérant / Laveurs).",
                            "Architecture Hybride" to "Fonctionnement 100% hors-ligne SQLite avec réplication cloud Supabase temps-réel.",
                            "Caisse & Impression POS" to "Support thermique 80mm & 58mm avec aperçu centré et découpe automatique.",
                            "Rapports & Statistiques" to "Analyses par jour, mois, année avec graphiques visuels et répartition des encaissements.",
                            "Fidélité & Dettes" to "Historique complet des paiements, suivi des arriérés et fidélité automatique."
                        ).forEach { (titre, desc) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("✔ $titre", fontWeight = FontWeight.Bold, color = Color(0xFF0284C7), fontSize = 12.sp)
                                Text(desc, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "© 2026 JuaTech. Tous droits réservés.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}
