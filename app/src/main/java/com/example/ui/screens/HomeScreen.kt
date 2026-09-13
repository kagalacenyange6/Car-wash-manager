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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.WashOrder
import com.example.ui.CarWashViewModel
import com.example.ui.PlusScreen
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PlateBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusPending
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: CarWashViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val queueOrders by viewModel.queueOrders.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    val (caToday, vehiclesToday, _) = viewModel.getTodayStats()
    val pendingCount = queueOrders.count { it.status == OrderStatus.PENDING.name }
    val inProgressCount = queueOrders.count { it.status == OrderStatus.IN_PROGRESS.name }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 1. HERO COMMAND SECTION : "NOUVELLE PRESTATION"
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0B192C),
                                    Color(0xFF162942),
                                    Color(0xFF0F1E36)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Point d'Accueil & Lavage",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = "Enregistrement rapide en 3 clics",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Atelier actif",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large CTA button
                        Button(
                            onClick = { viewModel.startNewOrder() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlueLight,
                                contentColor = Color(0xFF07101E)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("home_new_order_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nouvelle prestation",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.3.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick secondary shortcuts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.openPlusScreen(PlusScreen.QUEUE) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatListBulleted,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "File (${queueOrders.size})",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.openPlusScreen(PlusScreen.PAYMENTS) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PointOfSale,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Encaissements",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. STATS GRID (2x2 Compact Cards)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "CA aujourd'hui",
                        value = String.format(Locale.US, "%.1f %s", caToday, currency),
                        icon = Icons.Default.AttachMoney,
                        iconBgColor = Color(0xFFDCFCE7),
                        iconTintColor = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Véhicules lavés",
                        value = vehiclesToday.toString(),
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = Color(0xFFE0F2FE),
                        iconTintColor = BlueAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "En attente",
                        value = pendingCount.toString(),
                        icon = Icons.Default.HourglassEmpty,
                        iconBgColor = Color(0xFFFEF3C7),
                        iconTintColor = StatusPending,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openPlusScreen(PlusScreen.QUEUE) }
                    )
                    StatCard(
                        title = "En cours",
                        value = inProgressCount.toString(),
                        icon = Icons.Default.LocalCarWash,
                        iconBgColor = Color(0xFFE0F2FE),
                        iconTintColor = StatusInProgress,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openPlusScreen(PlusScreen.QUEUE) }
                    )
                }
            }
        }

        // 3. FILE D'ATTENTE SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "File d'attente",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 17.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Navy900,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = queueOrders.size.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                if (queueOrders.isNotEmpty()) {
                    TextButton(onClick = { viewModel.openPlusScreen(PlusScreen.QUEUE) }) {
                        Text("Tout voir", color = BlueAccent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }

        if (queueOrders.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EmptyStateView(
                        message = "La file d'attente est vide.\nCréez une nouvelle prestation ci-dessus.",
                        icon = Icons.Default.DirectionsCar
                    )
                }
            }
        } else {
            items(queueOrders.take(4), key = { it.id }) { order ->
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

        // 4. ACTIVITÉ RÉCENTE SECTION
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activité récente",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        fontSize = 17.sp
                    )
                )
            }
        }

        val recentOrders = orders.filter { it.status == OrderStatus.COMPLETED.name }.take(5)
        if (recentOrders.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EmptyStateView(
                        message = "Aucun lavage terminé pour le moment.",
                        icon = Icons.Default.Schedule
                    )
                }
            }
        } else {
            items(recentOrders, key = { it.id }) { order ->
                RecentActivityCard(
                    order = order,
                    currency = currency,
                    onPrint = { viewModel.openReceiptDialog(order) },
                    onPay = { viewModel.openPaymentDialog(order) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QueueOrderCard(
    order: WashOrder,
    currency: String,
    onStart: () -> Unit,
    onFinish: () -> Unit,
    onPay: () -> Unit,
    onPrint: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("queue_card_${order.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top: Vehicle & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.vehicleModel,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PlateBadge(plate = order.vehiclePlate)
                        Text(
                            text = "• ${order.customerName}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                StatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.servicesSummary,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = String.format(Locale.US, "%.2f %s", order.finalAmount, currency),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fast One-Tap Action Button
            when (order.status) {
                OrderStatus.PENDING.name -> {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusInProgress),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("action_start_${order.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Démarrer le lavage", fontWeight = FontWeight.Bold)
                    }
                }

                OrderStatus.IN_PROGRESS.name -> {
                    Button(
                        onClick = onFinish,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("action_finish_${order.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Terminer le lavage", fontWeight = FontWeight.Bold)
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (order.paidAmount < order.finalAmount) {
                            Button(
                                onClick = onPay,
                                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("action_pay_${order.id}")
                            ) {
                                Text("Encaisser", fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedButton(
                            onClick = onPrint,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ticket")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentActivityCard(
    order: WashOrder,
    currency: String,
    onPrint: () -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeStr = SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(order.completedAt ?: order.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recent_order_${order.id}"),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlateBadge(plate = order.vehiclePlate)
                    Text(
                        text = order.vehicleModel,
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = timeStr, fontSize = 11.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${order.customerName} • ${order.servicesSummary}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(status = order.paymentStatus)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format(Locale.US, "%.2f %s", order.finalAmount, currency),
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        fontSize = 13.sp
                    )
                }
            }

            Row {
                if (order.paidAmount < order.finalAmount) {
                    OutlinedButton(
                        onClick = onPay,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Payer", fontSize = 12.sp, color = BlueAccent)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                IconButton(onClick = onPrint, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Print, contentDescription = "Imprimer", tint = Navy900)
                }
            }
        }
    }
}
