package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.InputBorderDefault
import com.example.ui.theme.InputBorderFocused
import com.example.ui.theme.InputPlaceholderColor
import com.example.ui.theme.InputTextColor
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusCompletedBg
import com.example.ui.theme.StatusDebt
import com.example.ui.theme.StatusDebtBg
import com.example.ui.theme.StatusInProgress
import com.example.ui.theme.StatusInProgressBg
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPaidBg
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag("stat_card_${title.lowercase().replace(" ", "_")}")
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp
                )
            )
        }
    }
}

/**
 * Automotive License Plate Badge - Format officiel RDC (République Démocratique du Congo)
 * Structure : [Bandeau drapeau RDC + CGO] [Numéro d'immatriculation (ex: 1024 AB)]
 */
@Composable
fun PlateBadge(
    plate: String,
    modifier: Modifier = Modifier
) {
    // Nettoyer et formater la plaque pour l'affichage RDC
    val cleanPlate = plate.uppercase().trim()

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.5.dp, Color(0xFF1E293B)),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bandeau Drapeau RDC avec code pays "CGO"
            Box(
                modifier = Modifier
                    .background(Color(0xFF0077C8)) // Bleu ciel officiel RDC
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Étoile jaune en haut du drapeau
                    Text(
                        text = "★",
                        color = Color(0xFFFFD100), // Jaune vif RDC
                        fontSize = 8.5.sp,
                        lineHeight = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Code international du pays
                    Text(
                        text = "CGO",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        lineHeight = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Numéro de plaque d'immatriculation RDC (sans code province)
            Text(
                text = cleanPlate,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.5.sp,
                letterSpacing = 1.4.sp,
                modifier = Modifier.padding(start = 8.dp, end = 10.dp, top = 2.5.dp, bottom = 2.5.dp)
            )
        }
    }
}

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bg, text, label) = when (status.uppercase()) {
        "PENDING", "EN ATTENTE" -> Triple(StatusPendingBg, StatusPending, "En attente")
        "IN_PROGRESS", "EN COURS" -> Triple(StatusInProgressBg, StatusInProgress, "En cours")
        "COMPLETED", "TERMINÉ", "TERMINE" -> Triple(StatusCompletedBg, StatusCompleted, "Terminé")
        "PAID", "PAYÉ", "PAYE" -> Triple(StatusPaidBg, StatusPaid, "Payé")
        "DEBT", "DETTE", "PARTIAL", "PARTIEL" -> Triple(StatusDebtBg, StatusDebt, "Dette")
        else -> Triple(Color(0xFFF1F5F9), TextSecondary, status)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = text,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyStateView(
    message: String,
    icon: ImageVector = Icons.Default.Info,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun carWashTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0F172A),
    unfocusedTextColor = Color(0xFF0F172A),
    disabledTextColor = Color(0xFF64748B),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFFAFAFA),
    disabledContainerColor = Color(0xFFF1F5F9),
    cursorColor = BlueAccent,
    focusedBorderColor = BlueAccent,
    unfocusedBorderColor = Color(0xFF94A3B8),
    disabledBorderColor = Color(0xFFE2E8F0),
    focusedLabelColor = BlueAccent,
    unfocusedLabelColor = Color(0xFF334155),
    disabledLabelColor = Color(0xFF94A3B8),
    focusedPlaceholderColor = Color(0xFF64748B),
    unfocusedPlaceholderColor = Color(0xFF64748B),
    focusedLeadingIconColor = BlueAccent,
    unfocusedLeadingIconColor = Color(0xFF475569),
    focusedTrailingIconColor = BlueAccent,
    unfocusedTrailingIconColor = Color(0xFF475569)
)

val carWashInputTextStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp,
    color = Color(0xFF0F172A),
    letterSpacing = 0.15.sp
)

/**
 * Modern Form Sheet Header with Icon Badge, Typography and Close button
 */
@Composable
fun FormSheetHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))
                        )
                    )
                    .border(1.dp, Color(0xFF7DD3FC), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BlueAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy900,
                        fontSize = 18.sp
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
        Surface(
            shape = CircleShape,
            color = Color(0xFFF1F5F9),
            modifier = Modifier.size(36.dp)
        ) {
            IconButton(onClick = onDismiss, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Modern High-Visibility Form Error Banner
 */
@Composable
fun FormErrorBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFFF1F2),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFFE11D48),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                color = Color(0xFFBE123C),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * Form Section Header with micro-label styling
 */
@Composable
fun FormSectionHeader(
    title: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BlueAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = title.uppercase(),
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        )
    }
}

/**
 * Modern Segmented/Pill Option Selector
 */
@Composable
fun FormOptionSelector(
    options: List<Pair<String, String>>, // key to label
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (key, label) ->
            val isSelected = selectedOption == key
            Surface(
                color = if (isSelected) Navy900 else Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isSelected) Navy900 else Color(0xFFCBD5E1)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOptionSelected(key) }
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CarWashInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    testTag: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            )
        },
        placeholder = if (placeholder != null) {
            {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                )
            }
        } else null,
        supportingText = if (supportingText != null) {
            {
                Text(
                    text = supportingText,
                    fontSize = 11.sp,
                    color = if (isError) Color(0xFFE11D48) else TextMuted
                )
            }
        } else null,
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isError) Color(0xFFE11D48) else BlueAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else null,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = carWashInputTextStyle,
        colors = carWashTextFieldColors(),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    )
}

/**
 * High-craft Primary Button with gradient depth and responsive state
 */
@Composable
fun CarWashPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFE2E8F0),
            disabledContentColor = Color(0xFF475569)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled) 3.dp else 0.dp
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(listOf(Color(0xFF0F1E36), Color(0xFF0284C7)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                },
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) Color.White else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    color = if (enabled) Color.White else Color(0xFF475569),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.2.sp
                )
            )
        }
    }
}

/**
 * Polished Secondary Button
 */
@Composable
fun CarWashSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Navy900
        ),
        border = BorderStroke(1.5.dp, Navy900),
        modifier = modifier.height(52.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Navy900,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    color = Navy900,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

