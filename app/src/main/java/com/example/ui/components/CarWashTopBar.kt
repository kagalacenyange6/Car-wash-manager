package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Company
import com.example.data.model.Employee
import com.example.data.sync.SyncState
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CarWashTopBar(
    company: Company?,
    currentEmployee: Employee?,
    unreadCount: Int,
    onNotificationsClick: () -> Unit,
    onEmployeeClick: () -> Unit,
    isOnline: Boolean = true,
    syncState: SyncState = SyncState.IDLE,
    onSyncClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("EEEE d MMMM", Locale.FRANCE).format(Date())
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.FRANCE) else it.toString() }

    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF070E1B),
                        Color(0xFF0F1E36),
                        Color(0xFF091424)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo & Company Name with Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, BlueLight.copy(alpha = 0.6f), CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_carwash_logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = company?.name ?: "Car Wash Pro",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 16.sp,
                                letterSpacing = 0.2.sp
                            ),
                            maxLines = 1
                        )
                    }
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Right: Interactive Supabase/SQLite Cloud Pill + Employee + Notifications
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                // SQLite / Supabase Sync status pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        syncState == SyncState.SYNCING -> Color(0xFF1E3A8A).copy(alpha = 0.8f)
                        isOnline -> Color(0xFF064E3B).copy(alpha = 0.85f)
                        else -> Color(0xFF78350F).copy(alpha = 0.85f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            syncState == SyncState.SYNCING -> BlueLight.copy(alpha = 0.5f)
                            isOnline -> Color(0xFF34D399).copy(alpha = 0.5f)
                            else -> Color(0xFFFBBF24).copy(alpha = 0.5f)
                        }
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onSyncClick)
                        .testTag("sync_status_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        when {
                            syncState == SyncState.SYNCING -> {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Synchronisation",
                                    tint = BlueLight,
                                    modifier = Modifier
                                        .size(13.dp)
                                        .rotate(rotation)
                                )
                                Text(
                                    text = "Synchro",
                                    color = BlueLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            isOnline -> {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34D399))
                                )
                                Text(
                                    text = "Supabase",
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFBBF24))
                                )
                                Text(
                                    text = "SQLite",
                                    color = Color(0xFFFDE68A),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Employee Profile Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1B2A44).copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, BlueLight.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onEmployeeClick)
                        .testTag("employee_profile_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Utilisateur",
                            tint = BlueLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = currentEmployee?.name?.split(" ")?.firstOrNull() ?: "Admin",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Notification Bell with Red Badge
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }
}
