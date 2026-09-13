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
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.Expense
import com.example.data.model.Product
import com.example.data.model.WashService
import com.example.ui.CarWashViewModel
import com.example.ui.PlusScreen
import com.example.ui.components.CarWashPrimaryButton
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusChip
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
        MenuItemData(PlusScreen.EMPLOYEES, "Employés & Rôles", "Gestion de l'équipe", Icons.Default.Badge, Color(0xFFF3E8FF), Color(0xFF9333EA)),
        MenuItemData(PlusScreen.REPORTS, "Rapports & Chiffres", "Statistiques & Bilan", Icons.Default.Assessment, Color(0xFFDCFCE7), Color(0xFF059669)),
        MenuItemData(PlusScreen.LOYALTY, "Clients Fidèles", "Points & Récompenses", Icons.Default.CardGiftcard, Color(0xFFFEF3C7), Color(0xFFB45309)),
        MenuItemData(PlusScreen.NOTIFICATIONS, "Notifications", "$unreadNotifs non lue(s)", Icons.Default.Notifications, Color(0xFFFFE4E6), Color(0xFFDC2626)),
        MenuItemData(PlusScreen.SETTINGS, "Paramètres & Supabase", "POS, Ticket, DDL SQL", Icons.Default.Settings, Color(0xFFF1F5F9), Navy900),
        MenuItemData(PlusScreen.AUDIT_LOGS, "Journal d'activité", "Audit des actions", Icons.Default.History, Color(0xFFF1F5F9), Color(0xFF475569))
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openPlusScreen(item.screen) }
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
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDebt),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", fontSize = 12.sp)
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

    val categories = listOf("Produits", "Électricité / Eau", "Salaires", "Entretien matériel", "Loyer", "Autre")

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Nouvelle Dépense", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Catégorie :", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.take(3).forEach { cat ->
                    val isSel = category == cat
                    Surface(
                        color = if (isSel) Navy900 else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { category = cat }
                    ) {
                        Text(cat, color = if (isSel) Color.White else TextPrimary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Montant *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description / Motif") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val amt = amountText.replace(",", ".").toDoubleOrNull()
                    if (amt != null && amt > 0) {
                        onSave(category, amt, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusDebt),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enregistrer la dépense", fontWeight = FontWeight.Bold)
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
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nouveau", fontSize = 12.sp)
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
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Ajuster", fontSize = 11.sp)
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
            Text("Ajuster : ${product.name}", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 18.sp)
            Text("Stock actuel : ${product.currentStock} ${product.unit}", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isAdd = true },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAdd) StatusCompleted else Color(0xFFF1F5F9)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Entrée", color = if (isAdd) Color.White else TextPrimary)
                }
                Button(
                    onClick = { isAdd = false },
                    colors = ButtonDefaults.buttonColors(containerColor = if (!isAdd) StatusDebt else Color(0xFFF1F5F9)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("- Sortie", color = if (!isAdd) Color.White else TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = qtyText,
                onValueChange = { qtyText = it },
                label = { Text("Quantité (${product.unit}) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Motif (ex: Achat fournisseur, Lavage atelier)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val q = qtyText.replace(",", ".").toDoubleOrNull()
                    if (q != null && q > 0) {
                        onConfirm(if (isAdd) "ENTREE" else "SORTIE", q, reason)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Valider le mouvement", fontWeight = FontWeight.Bold)
            }
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

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Nouveau Produit au Stock", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom de l'article * (ex: Shampoing carrosserie)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock initial") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unité (L, pcs, kg)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = minThText,
                onValueChange = { minThText = it },
                label = { Text("Seuil d'alerte minimum") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val s = stockText.toDoubleOrNull() ?: 0.0
                    val m = minThText.toDoubleOrNull() ?: 5.0
                    if (name.isNotBlank()) {
                        onSave(name, s, unit, m)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enregistrer l'article", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREEN 5 : EMPLOYÉS & RÔLES
// -------------------------------------------------------------
@Composable
fun EmployeesSubScreen(viewModel: CarWashViewModel, modifier: Modifier = Modifier) {
    val employees by viewModel.employees.collectAsState()
    val orders by viewModel.orders.collectAsState()
    var showAddEmployee by remember { mutableStateOf(false) }

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
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nouveau", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(employees, key = { it.id }) { emp ->
                val washesDone = orders.count { it.employeeName.contains(emp.name.split(" ").first()) }

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
                            Text(emp.name, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                            Text("${emp.role} • ${emp.phone}", fontSize = 12.sp, color = TextSecondary)
                            Text("Lavages effectués : $washesDone", fontSize = 11.sp, color = BlueAccent, fontWeight = FontWeight.SemiBold)
                        }

                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("PIN: ${emp.pinCode}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
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
fun AddEmployeeSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, phone: String, role: String, pin: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("EMPLOYEE") }
    var pin by remember { mutableStateOf("1234") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Ajouter un Employé", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom complet *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Téléphone *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Rôle :", fontSize = 12.sp, color = TextSecondary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("EMPLOYEE" to "Employé", "MANAGER" to "Gérant", "ADMIN" to "Admin").forEach { (r, label) ->
                    val isSel = role == r
                    Surface(
                        color = if (isSel) Navy900 else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { role = r }
                    ) {
                        Text(label, color = if (isSel) Color.White else TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Code PIN (4 chiffres)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, "${name.lowercase().replace(" ", "")}@carwash.com", phone, role, pin)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enregistrer le membre", fontWeight = FontWeight.Bold)
            }
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
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    val totalRecettes = orders.sumOf { it.paidAmount }
    val totalDepenses = expenses.sumOf { it.amount }
    val beneficeNet = totalRecettes - totalDepenses
    val totalVehicles = orders.count { it.status == "COMPLETED" }
    val totalDebts = orders.sumOf { (it.finalAmount - it.paidAmount).coerceAtLeast(0.0) }

    SubScreenScaffold(
        title = "Rapports & Statistiques",
        onBack = { viewModel.closePlusScreen() },
        modifier = modifier
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bilan Financier Global", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Recettes (CA encaissé) :", color = TextSecondary, fontSize = 13.sp)
                            Text(String.format(Locale.US, "%.2f %s", totalRecettes, currency), fontWeight = FontWeight.Bold, color = StatusCompleted, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Dépenses :", color = TextSecondary, fontSize = 13.sp)
                            Text(String.format(Locale.US, "-%.2f %s", totalDepenses, currency), fontWeight = FontWeight.Bold, color = StatusDebt, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bénéfice Net :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 15.sp)
                            Text(
                                text = String.format(Locale.US, "%.2f %s", beneficeNet, currency),
                                fontWeight = FontWeight.Bold,
                                color = if (beneficeNet >= 0) Color(0xFF16A34A) else StatusDebt,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Indicateurs Opérationnels", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Véhicules lavés terminés :", color = TextSecondary)
                            Text("$totalVehicles véhicules", fontWeight = FontWeight.Bold, color = Navy900)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dettes clients non recouvrées :", color = TextSecondary)
                            Text(String.format(Locale.US, "%.2f %s", totalDebts, currency), fontWeight = FontWeight.Bold, color = StatusDebt)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        val panierMoyen = if (orders.isNotEmpty()) totalRecettes / orders.size else 0.0
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Panier moyen par prestation :", color = TextSecondary)
                            Text(String.format(Locale.US, "%.2f %s", panierMoyen, currency), fontWeight = FontWeight.Bold, color = BlueAccent)
                        }
                    }
                }
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
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Tout marquer comme lu", fontSize = 11.sp)
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
    val context = LocalContext.current

    var name by remember(company) { mutableStateOf(company?.name ?: "Car Wash Pro") }
    var phone by remember(company) { mutableStateOf(company?.phone ?: "") }
    var address by remember(company) { mutableStateOf(company?.address ?: "") }
    var currency by remember(company) { mutableStateOf(company?.currencySymbol ?: "$") }
    var paperWidth by remember(company) { mutableStateOf(company?.receiptPaperWidth ?: "80mm") }
    var headerNote by remember(company) { mutableStateOf(company?.receiptHeaderNote ?: "") }
    var footerNote by remember(company) { mutableStateOf(company?.receiptFooterNote ?: "") }

    var showSqlModal by remember { mutableStateOf(false) }

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
                    label = { Text("Nom du Car Wash") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Téléphone") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Devise ($, €, FC)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse physique") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
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
                    label = { Text("En-tête personnalisé sur le ticket") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = footerNote,
                    onValueChange = { footerNote = it },
                    label = { Text("Pied de page sur le ticket (ex: Pas de réclamation après 24h)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
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
                                receiptPaperWidth = paperWidth,
                                receiptHeaderNote = headerNote,
                                receiptFooterNote = footerNote
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Sauvegarder les modifications", fontWeight = FontWeight.Bold)
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
                            text = "Générez et copiez le script SQL DDL complet pour déployer la base PostgreSQL sur Supabase avec Row Level Security (RLS) et rôles.",
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
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Afficher et copier le script SQL DDL", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showSqlModal) {
        SupabaseSqlModal(onDismiss = { showSqlModal = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseSqlModal(onDismiss: () -> Unit) {
    val context = LocalContext.current
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
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Supabase SQL", sqlText)
                    clipboard.setPrimaryClip(clip)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copier tout le script SQL", fontWeight = FontWeight.Bold)
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
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter", fontSize = 12.sp)
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

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Nouveau Service de Lavage", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom du service * (ex: Lavage VIP Moteur)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Prix *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = durationText,
                onValueChange = { durationText = it },
                label = { Text("Durée estimée (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val p = priceText.replace(",", ".").toDoubleOrNull()
                    val d = durationText.toIntOrNull() ?: 20
                    if (name.isNotBlank() && p != null && p > 0) {
                        onSave(name, p, d)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enregistrer le service", fontWeight = FontWeight.Bold)
            }
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
