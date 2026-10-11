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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.shadow
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
    val dateStr = SimpleDateFormat("EEE d MMMM", Locale.FRANCE).format(Date())
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

    Surface(
        color = Color(0xFF091424),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F213A),
                            Color(0xFF081220)
                        )
                    )
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Modern App Logo & Brand Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Elevated Modern Logo with Glow
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(8.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0B192C))
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                    )
                                ),
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_carwash_logo),
                            contentDescription = "Car Wash Pro Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = company?.name?.ifBlank { "Car Wash Pro" } ?: "Car Wash Pro",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 17.sp,
                                letterSpacing = (-0.2).sp
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) Color(0xFF22C55E) else Color(0xFFF59E0B))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "$dateStr • ${if (isOnline) "Connecté" else "Local"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Right: Quick Action Controls (Avatar initial button & Notification Bell)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // User Avatar with Initial Letter (click opens user info, sync status & logout)
                    val empName = currentEmployee?.name?.trim() ?: "Admin"
                    val initialLetter = empName.firstOrNull()?.uppercaseChar()?.toString() ?: "A"

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0284C7),
                        border = BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onEmployeeClick)
                            .testTag("employee_avatar_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = initialLetter,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Notification Bell Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = onNotificationsClick,
                            modifier = Modifier
                                .fillMaxSize()
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
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
