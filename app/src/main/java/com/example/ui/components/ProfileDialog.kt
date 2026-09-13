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
    modifier: Modifier = Modifier
) {
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
                Column {
                    Text(
                        text = "Session Utilisateur & Rôles",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Basculez facilement entre les profils pour tester les permissions",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            employees.forEach { emp ->
                val isCurrent = emp.id == currentEmployee?.id
                val roleLabel = when (emp.role) {
                    UserRole.ADMIN.name -> "Administrateur (Tous droits)"
                    UserRole.MANAGER.name -> "Gérant (Gestion & Rapports)"
                    else -> "Employé (Lavage & File)"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onSelectEmployee(emp)
                            onDismiss()
                        }
                        .testTag("switch_user_${emp.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) Color(0xFFE0F2FE) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (isCurrent) 2.dp else 1.dp,
                        color = if (isCurrent) BlueAccent else BorderLight
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
                                .background(if (isCurrent) BlueAccent else Navy900.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (emp.role == UserRole.ADMIN.name) Icons.Default.Shield else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isCurrent) Color.White else Navy900
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = emp.name,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Navy900 else TextPrimary,
                                    fontSize = 15.sp
                                )
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = BlueAccent, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            "Actif",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = roleLabel,
                                fontSize = 12.sp,
                                color = if (isCurrent) BlueAccent else TextSecondary
                            )
                        }

                        if (isCurrent) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = BlueAccent)
                        }
                    }
                }
            }
        }
    }
}
