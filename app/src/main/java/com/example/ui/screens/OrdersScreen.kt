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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.WashOrder
import com.example.ui.CarWashViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusChip
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    viewModel: CarWashViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val company by viewModel.company.collectAsState()
    val currency = company?.currencySymbol ?: "$"

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tous") }

    val filterOptions = listOf("Tous", "En attente", "En cours", "Terminé", "Dettes")

    val filteredOrders = remember(orders, searchQuery, selectedFilter) {
        orders.filter { order ->
            val matchesSearch = searchQuery.isBlank() ||
                order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                order.customerName.contains(searchQuery, ignoreCase = true) ||
                order.vehiclePlate.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "En attente" -> order.status == OrderStatus.PENDING.name
                "En cours" -> order.status == OrderStatus.IN_PROGRESS.name
                "Terminé" -> order.status == OrderStatus.COMPLETED.name
                "Dettes" -> order.paidAmount < order.finalAmount
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startNewOrder() },
                containerColor = Navy900,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("orders_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle prestation")
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

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("orders_search_input"),
                placeholder = { Text("Rechercher (Client, Plaque, CW-000...)", fontSize = 14.sp) },
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

            Spacer(modifier = Modifier.height(10.dp))

            // Filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { opt ->
                    val isSel = selectedFilter == opt
                    Surface(
                        color = if (isSel) Navy900 else Color.White,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isSel) Navy900 else BorderLight),
                        modifier = Modifier
                            .clickable { selectedFilter = opt }
                            .testTag("filter_$opt")
                    ) {
                        Text(
                            text = opt,
                            color = if (isSel) Color.White else TextPrimary,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredOrders.isEmpty()) {
                EmptyStateView(
                    message = "Aucune prestation trouvée.",
                    icon = Icons.Default.DirectionsCar
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderItemCard(
                            order = order,
                            currency = currency,
                            onStart = { viewModel.startWash(order.id) },
                            onFinish = { viewModel.finishWash(order.id) },
                            onPay = { viewModel.openPaymentDialog(order) },
                            onPrint = { viewModel.openReceiptDialog(order) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    order: WashOrder,
    currency: String,
    onStart: () -> Unit,
    onFinish: () -> Unit,
    onPay: () -> Unit,
    onPrint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    val dateStr = dateFormat.format(Date(order.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_item_${order.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: OrderNumber, Date, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        fontSize = 15.sp
                    )
                    Text(text = dateStr, fontSize = 11.sp, color = TextMuted)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusChip(status = order.status)
                    StatusChip(status = order.paymentStatus)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vehicle & Customer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = order.vehiclePlate,
                        color = Color(0xFF92400E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${order.vehicleModel} • ${order.customerName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Services & Financials
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.servicesSummary,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "%.2f %s", order.finalAmount, currency),
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        fontSize = 15.sp
                    )
                    if (order.paidAmount < order.finalAmount) {
                        val remaining = order.finalAmount - order.paidAmount
                        Text(
                            text = "Reste: ${String.format(Locale.US, "%.2f %s", remaining, currency)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE11D48)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (order.status) {
                    OrderStatus.PENDING.name -> {
                        Button(
                            onClick = onStart,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusInProgress),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Démarrer", fontSize = 12.sp)
                        }
                    }
                    OrderStatus.IN_PROGRESS.name -> {
                        Button(
                            onClick = onFinish,
                            colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Terminer", fontSize = 12.sp)
                        }
                    }
                    else -> {
                        if (order.paidAmount < order.finalAmount) {
                            Button(
                                onClick = onPay,
                                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Encaisser solde", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = onPrint,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp), tint = Navy900)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ticket POS", fontSize = 12.sp, color = Navy900)
                }
            }
        }
    }
}
