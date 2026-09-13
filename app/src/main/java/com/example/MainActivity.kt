package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.CarWashViewModel
import com.example.ui.PlusScreen
import com.example.ui.components.CarWashBottomNav
import com.example.ui.components.CarWashTopBar
import com.example.ui.components.NewOrderWizardSheet
import com.example.ui.components.PaymentBottomSheet
import com.example.ui.components.ProfileBottomSheet
import com.example.ui.components.ReceiptBottomSheet
import com.example.ui.components.SupabaseSyncDialog
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PlusScreenView
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VehiclesScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CarWashApp()
            }
        }
    }
}

@Composable
fun CarWashApp(
    viewModel: CarWashViewModel = viewModel()
) {
    val context = LocalContext.current
    var isSplashVisible by rememberSaveable { mutableStateOf(true) }

    val currentTab by viewModel.currentTab.collectAsState()
    val company by viewModel.company.collectAsState()
    val currentEmployee by viewModel.currentEmployee.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

    val isNewOrderOpen by viewModel.isNewOrderSheetOpen.collectAsState()
    val isPaymentOpen by viewModel.paymentDialogOpen.collectAsState()
    val targetPaymentOrder by viewModel.targetPaymentOrder.collectAsState()

    val isReceiptOpen by viewModel.receiptDialogOpen.collectAsState()
    val targetReceiptOrder by viewModel.targetReceiptOrder.collectAsState()
    val targetReceiptItems by viewModel.targetReceiptItems.collectAsState()
    val payments by viewModel.payments.collectAsState()

    var showProfileSwitcher by remember { mutableStateOf(false) }

    val isOnline by viewModel.isOnline.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val isSupabaseDialogOpen by viewModel.isSupabaseDialogOpen.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Crossfade(
        targetState = isSplashVisible,
        animationSpec = tween(durationMillis = 400),
        label = "splash_fade"
    ) { showSplash ->
        if (showSplash) {
            SplashScreen(onSplashFinished = { isSplashVisible = false })
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    CarWashTopBar(
                        company = company,
                        currentEmployee = currentEmployee,
                        unreadCount = unreadCount,
                        onNotificationsClick = {
                            viewModel.selectTab(AppTab.PLUS)
                            viewModel.openPlusScreen(PlusScreen.NOTIFICATIONS)
                        },
                        onEmployeeClick = { showProfileSwitcher = true },
                        isOnline = isOnline,
                        syncState = syncState,
                        onSyncClick = { viewModel.openSupabaseDialog() }
                    )
                },
                bottomBar = {
                    CarWashBottomNav(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)) togetherWith
                                fadeOut(animationSpec = tween(180))
                        },
                        label = "tab_switch_animation"
                    ) { tab ->
                        when (tab) {
                            AppTab.ACCUEIL -> HomeScreen(viewModel = viewModel)
                            AppTab.PRESTATIONS -> OrdersScreen(viewModel = viewModel)
                            AppTab.CLIENTS -> CustomersScreen(viewModel = viewModel)
                            AppTab.VEHICULES -> VehiclesScreen(viewModel = viewModel)
                            AppTab.PLUS -> PlusScreenView(viewModel = viewModel)
                        }
                    }
                }
            }

            // Modal Sheet 1: New Order Creation Wizard (Client -> Véhicule -> Services -> Confirmation)
            if (isNewOrderOpen) {
                NewOrderWizardSheet(viewModel = viewModel)
            }

            // Modal Sheet 2: Payment Sheet (Espèces, Mobile Money, Carte, Crédit)
            if (isPaymentOpen && targetPaymentOrder != null) {
                PaymentBottomSheet(
                    order = targetPaymentOrder!!,
                    company = company,
                    onDismiss = { viewModel.closePaymentDialog() },
                    onConfirmPayment = { amt, method, ref ->
                        viewModel.executePayment(targetPaymentOrder!!, amt, method, ref)
                    }
                )
            }

            // Modal Sheet 3: POS Thermal Receipt (80mm / 58mm preview & print)
            if (isReceiptOpen && targetReceiptOrder != null) {
                ReceiptBottomSheet(
                    order = targetReceiptOrder!!,
                    items = targetReceiptItems,
                    payments = payments,
                    company = company,
                    onDismiss = { viewModel.closeReceiptDialog() },
                    onPrint = { viewModel.printReceipt(context, targetReceiptOrder!!) }
                )
            }

            // Modal Sheet 4: Profile / Role Switcher
            if (showProfileSwitcher) {
                ProfileBottomSheet(
                    employees = employees,
                    currentEmployee = currentEmployee,
                    onSelectEmployee = { viewModel.switchEmployee(it) },
                    onDismiss = { showProfileSwitcher = false }
                )
            }

            // Modal Sheet 5: Supabase Cloud & SQLite Offline Sync Manager
            if (isSupabaseDialogOpen) {
                SupabaseSyncDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeSupabaseDialog() }
                )
            }
        }
    }
}
