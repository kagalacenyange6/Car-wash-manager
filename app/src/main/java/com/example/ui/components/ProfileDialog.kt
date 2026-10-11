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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Employee
import com.example.data.model.UserRole
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(
    employees: List<Employee>,
    currentEmployee: Employee?,
    onSelectEmployee: (Employee) -> Unit,
    onDismiss: () -> Unit,
    isOnline: Boolean = true,
    syncState: com.example.data.sync.SyncState = com.example.data.sync.SyncState.IDLE,
    onSyncClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val emp = currentEmployee ?: employees.firstOrNull()
    val initialLetter = emp?.name?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = modifier.testTag("profile_bottom_sheet")
    ) {
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
                Text(
                    text = "Mon Compte & Session",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        fontSize = 18.sp
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with Initial Letter
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF0F172A))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initialLetter,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = emp?.name ?: "Utilisateur",
                            fontWeight = FontWeight.ExtraBold,
                            color = Navy900,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Rôle : ${emp?.role ?: "EMPLOYEE"} • Tél : ${emp?.phone ?: "-"}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (emp?.role == "ADMIN") {
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    "Accès Administrateur",
                                    color = Color(0xFF166534),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Connection Status & Management (Cloud or SQLite)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isOnline) Color(0xFFBBF7D0) else Color(0xFFFDE68A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onSyncClick()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF16A34A) else Color(0xFFD97706))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isOnline) "Connexion Cloud Supabase" else "Stockage Local SQLite",
                                fontWeight = FontWeight.Bold,
                                color = Navy900,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isOnline) "En ligne • Synchronisation automatique" else "Hors-ligne • Données conservées localement",
                                fontSize = 11.sp,
                                color = if (isOnline) Color(0xFF166534) else Color(0xFF92400E)
                            )
                        }
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

            Spacer(modifier = Modifier.height(18.dp))

            // Logout Button
            Button(
                onClick = {
                    onDismiss()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE11D48),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_button")
            ) {
                Text("Se déconnecter (Verrouiller)", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
        }
    }
}
