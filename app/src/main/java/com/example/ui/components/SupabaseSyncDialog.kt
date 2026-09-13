package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.sync.SyncState
import com.example.ui.CarWashViewModel
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Navy900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseSyncDialog(
    viewModel: CarWashViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val isOnline by viewModel.isOnline.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val lastSyncTime by viewModel.lastSyncTimestamp.collectAsState()

    val currentConfig = remember { viewModel.syncManager.configManager.getConfig() }
    var urlInput by remember { mutableStateOf(currentConfig.url) }
    var anonKeyInput by remember { mutableStateOf(currentConfig.anonKey) }
    var autoSyncEnabled by remember { mutableStateOf(currentConfig.autoSyncEnabled) }

    var testStatusMessage by remember { mutableStateOf<String?>(null) }
    var isTestingConnection by remember { mutableStateOf(false) }

    val formattedTime = remember(lastSyncTime) {
        SimpleDateFormat("HH:mm:ss", Locale.FRANCE).format(Date(lastSyncTime))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = modifier.testTag("supabase_sync_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Top Header: Title and Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Synchronisation Supabase & SQLite",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Navy900,
                            fontSize = 19.sp
                        )
                    )
                    Text(
                        text = "Stockage hybride : SQLite local hors-ligne & Supabase en ligne",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF16A34A) else Color(0xFFD97706))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isOnline) "En ligne" else "Hors-ligne",
                            color = if (isOnline) Color(0xFF166534) else Color(0xFF92400E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Mode Banner Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline) Color(0xFF0F1E36) else Color(0xFF2E2211)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.CloudSync else Icons.Default.Storage,
                            contentDescription = null,
                            tint = if (isOnline) BlueLight else Color(0xFFFBBF24),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isOnline) "Mode Connecté (Supabase Actif)" else "Mode Hors-ligne (SQLite Local)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = if (isOnline) {
                                    "Les modifications sont enregistrées dans SQLite et synchronisées avec Supabase."
                                } else {
                                    "Pas d'accès internet : l'application fonctionne à 100% sur la base SQLite locale."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dernier état :",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$formattedTime • $syncMessage",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sync button inside card
                    Button(
                        onClick = { viewModel.triggerSync() },
                        enabled = syncState != SyncState.SYNCING,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOnline) BlueLight else Color(0xFFF59E0B),
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        if (syncState == SyncState.SYNCING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF0F172A),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synchronisation en cours...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isOnline) "Synchroniser avec Supabase" else "Forcer la vérification réseau",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Configuration Section
            Text(
                text = "Paramètres du projet Supabase",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900,
                    fontSize = 15.sp
                )
            )
            Text(
                text = "Entrez vos identifiants Supabase pour synchroniser la flotte et les encaissements",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // URL input field
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                label = { Text("URL Supabase (ex: https://xxx.supabase.co)") },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null, tint = BlueAccent)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Anon Key input field
            OutlinedTextField(
                value = anonKeyInput,
                onValueChange = { anonKeyInput = it },
                label = { Text("Clé Anon / API Key Supabase") },
                leadingIcon = {
                    Icon(Icons.Default.Key, contentDescription = null, tint = BlueAccent)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Auto-sync Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Synchronisation automatique",
                        fontWeight = FontWeight.SemiBold,
                        color = Navy900,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Dès qu'une connexion internet est détectée",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = autoSyncEnabled,
                    onCheckedChange = { autoSyncEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BlueAccent
                    )
                )
            }

            // Test connection result feedback
            if (testStatusMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (testStatusMessage?.contains("réussie", ignoreCase = true) == true ||
                        testStatusMessage?.contains("accessible", ignoreCase = true) == true
                    ) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = testStatusMessage ?: "",
                        color = if (testStatusMessage?.contains("réussie", ignoreCase = true) == true ||
                            testStatusMessage?.contains("accessible", ignoreCase = true) == true
                        ) Color(0xFF166534) else Color(0xFF991B1B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Tester & Enregistrer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isTestingConnection = true
                            testStatusMessage = null
                            val (ok, msg) = viewModel.syncManager.testConnection(urlInput, anonKeyInput)
                            testStatusMessage = msg
                            isTestingConnection = false
                        }
                    },
                    enabled = !isTestingConnection,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    if (isTestingConnection) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Tester", fontWeight = FontWeight.SemiBold, color = Navy900)
                    }
                }

                Button(
                    onClick = {
                        viewModel.syncManager.configManager.saveConfig(
                            url = urlInput,
                            anonKey = anonKeyInput,
                            autoSync = autoSyncEnabled
                        )
                        viewModel.triggerSync()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                ) {
                    Text("Enregistrer", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
