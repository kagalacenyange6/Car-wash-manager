package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.Payment
import com.example.data.model.WashOrder
import com.example.data.model.WashOrderItem
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.util.ReceiptPrinter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptBottomSheet(
    order: WashOrder,
    items: List<WashOrderItem>,
    payments: List<Payment>,
    company: Company?,
    onDismiss: () -> Unit,
    onPrint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val currentCompany = company ?: Company()

    val thermalText = remember(order, items, payments, currentCompany) {
        ReceiptPrinter.generateThermalText(
            order = order,
            items = items,
            payments = payments,
            company = currentCompany,
            paperWidth = currentCompany.receiptPaperWidth
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = modifier.testTag("receipt_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ticket de caisse thermique",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Format POS ${currentCompany.receiptPaperWidth} • ${order.orderNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(color = BlueAccent, fontWeight = FontWeight.SemiBold)
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Realistic Thermal Paper Ticket simulation card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = thermalText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFF111827),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Share / PDF Export
                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, thermalText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Partager le ticket ${order.orderNumber}")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("share_ticket_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Partager / PDF", fontSize = 13.sp)
                }

                // Native Print (Bluetooth, USB, Network, System Print)
                Button(
                    onClick = onPrint,
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(50.dp)
                        .testTag("print_ticket_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Imprimer ticket", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
