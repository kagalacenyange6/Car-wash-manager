package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueLight
import com.example.ui.theme.Navy900
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Logo entrance animation
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }

    var statusText by remember { mutableStateOf("Initialisation de l'atelier...") }

    // Pulsing ambient glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "halo_pulse")
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_alpha"
    )

    LaunchedEffect(Unit) {
        // Smooth scale and alpha reveal
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
    }

    LaunchedEffect(Unit) {
        // Step 1: Initializing
        statusText = "Démarrage des modules..."
        progress.animateTo(0.35f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        delay(300)

        // Step 2: Database / Queue loading
        statusText = "Synchronisation de la file d'attente..."
        progress.animateTo(0.75f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        delay(300)

        // Step 3: Ready
        statusText = "Prêt !"
        progress.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        delay(250)

        // Seamless transition
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050B14),
                        Color(0xFF0F1E36),
                        Color(0xFF07101E)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSplashFinished
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient cyan radial glow in the center
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(scale.value)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BlueLight.copy(alpha = 0.18f * haloAlpha),
                            BlueAccent.copy(alpha = 0.08f * haloAlpha),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // App Emblem with Glossy Frame
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(Color(0xFF0F1D32))
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                BlueLight,
                                Color(0xFF38BDF8),
                                BlueAccent,
                                Color(0xFF60A5FA),
                                BlueLight
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_carwash_logo),
                    contentDescription = "Car Wash Pro Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Brand Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CAR WASH ",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.5.sp,
                        fontSize = 28.sp
                    )
                )
                Surface(
                    color = BlueAccent,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "PRO",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp,
                            fontSize = 22.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Tagline
            Text(
                text = "GESTION D'ATELIER & ENCAISSEMENT POS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF94A3B8),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Sleek Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(BlueAccent, BlueLight, Color(0xFF38BDF8))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic status feedback
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // Bottom version and fast-skip hint
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Toucher l'écran pour accéder",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "v2.1 • Mobile-First Edition",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 10.sp
                )
            )
        }
    }
}
