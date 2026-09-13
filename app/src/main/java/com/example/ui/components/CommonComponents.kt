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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
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
 * Automotive License Plate Badge (European / Modern Style)
 */
@Composable
fun PlateBadge(
    plate: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFAFAFA),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.5.dp, Color(0xFF1E293B)),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 6.dp)
        ) {
            // Blue EU / Country Strip
            Box(
                modifier = Modifier
                    .background(Color(0xFF003399))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Plate registration number
            Text(
                text = plate.uppercase(),
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                letterSpacing = 1.sp
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
    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        color = if (enabled) Navy900 else Color(0xFFCBD5E1),
        shadowElevation = if (enabled) 3.dp else 0.dp,
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0F1E36), Color(0xFF1E3A8A))
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
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
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.3.sp
                    )
                )
            }
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
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Navy900,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Navy900,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

