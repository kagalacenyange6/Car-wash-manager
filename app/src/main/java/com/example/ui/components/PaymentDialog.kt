package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.PaymentMethod
import com.example.data.model.WashOrder
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
fun PaymentBottomSheet(
    order: WashOrder,
    company: Company?,
    onDismiss: () -> Unit,
    onConfirmPayment: (amount: Double, method: PaymentMethod, reference: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currency = company?.currencySymbol ?: "$"

    val total = order.finalAmount
    val alreadyPaid = order.paidAmount
    val remaining = (total - alreadyPaid).coerceAtLeast(0.0)

    var amountInput by remember { mutableStateOf(String.format(Locale.US, "%.2f", remaining)) }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var referenceInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = modifier.testTag("payment_bottom_sheet")
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
                        text = "Encaissement - ${order.orderNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "${order.customerName} • ${order.vehiclePlate}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount summary card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total prestation :", color = TextSecondary, fontSize = 13.sp)
                        Text(String.format(Locale.US, "%.2f %s", total, currency), fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Déjà payé :", color = TextSecondary, fontSize = 13.sp)
                        Text(String.format(Locale.US, "%.2f %s", alreadyPaid, currency), fontWeight = FontWeight.SemiBold, color = StatusCompleted)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("RESTE À PAYER :", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 15.sp)
                        Text(
                            text = String.format(Locale.US, "%.2f %s", remaining, currency),
                            fontWeight = FontWeight.Bold,
                            color = if (remaining > 0) StatusDebt else StatusCompleted,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment method selector
            Text(
                text = "Mode de paiement :",
                fontWeight = FontWeight.SemiBold,
                color = Navy900,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val methods = listOf(
                    Triple(PaymentMethod.CASH, "Espèces", Icons.Default.AttachMoney),
                    Triple(PaymentMethod.MOBILE_MONEY, "Mobile", Icons.Default.PhoneAndroid),
                    Triple(PaymentMethod.CARD, "Carte", Icons.Default.CreditCard),
                    Triple(PaymentMethod.CREDIT, "Crédit", Icons.Default.Receipt)
                )

                methods.forEach { (m, label, icon) ->
                    val isSelected = selectedMethod == m
                    Surface(
                        color = if (isSelected) Navy900 else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSelected) Navy900 else BorderLight),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMethod = m }
                            .testTag("method_${m.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color.White else Navy900,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount input field
            OutlinedTextField(
                value = amountInput,
                onValueChange = {
                    amountInput = it
                    errorMessage = null
                },
                label = { Text("Montant à encaisser ($currency) *") },
                modifier = Modifier.fillMaxWidth().testTag("payment_amount_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                    } else {
                        Text("Paiement partiel autorisé. Reste = Dette.", color = TextMuted, fontSize = 11.sp)
                    }
                },
                shape = RoundedCornerShape(10.dp)
            )

            // Optional Reference input for Mobile Money or Card
            if (selectedMethod == PaymentMethod.MOBILE_MONEY || selectedMethod == PaymentMethod.CARD) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = referenceInput,
                    onValueChange = { referenceInput = it },
                    label = { Text("N° de transaction / Référence (optionnel)") },
                    placeholder = { Text("ex: M-PESA 892301") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_reference_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Confirm Button
            Button(
                onClick = {
                    val enteredAmount = amountInput.replace(",", ".").toDoubleOrNull()
                    if (enteredAmount == null || enteredAmount <= 0) {
                        errorMessage = "Veuillez entrer un montant valide supérieur à 0"
                        return@Button
                    }
                    if (enteredAmount > remaining + 0.001) {
                        errorMessage = "Le montant ne peut pas dépasser le reste à payer (${String.format(Locale.US, "%.2f %s", remaining, currency)})"
                        return@Button
                    }
                    onConfirmPayment(enteredAmount, selectedMethod, referenceInput)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_payment_button")
            ) {
                Text("Valider l'encaissement", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
