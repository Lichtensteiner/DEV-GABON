package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.DevGabonViewModel

@Composable
fun StartupSplashScreen(viewModel: DevGabonViewModel) {
    val progress by viewModel.startupProgress.collectAsState()
    val statusText by viewModel.startupStatusText.collectAsState()
    val languageCode by viewModel.languageCode.collectAsState()
    val colors = MaterialTheme.colorScheme

    // Smooth animator for the linear progress bar
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 100),
        label = "StartupProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.background,
                        colors.surfaceVariant.copy(alpha = 0.3f),
                        colors.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background elements for Gabon Tech theme (Green Blue Yellow ambient glow)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Subtle Top Right decoration (Gabon Blue highlight)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 100.dp, y = (-80).dp)
                    .background(Color(0xFF3A86C8).copy(alpha = 0.04f), CircleShape)
            )

            // Subtle Bottom Left decoration (Gabon Green highlight)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-120).dp, y = 100.dp)
                    .background(Color(0xFF39B54A).copy(alpha = 0.04f), CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // High-fidelity framed branding logo icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, colors.primary.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .background(colors.surface, RoundedCornerShape(24.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "DEV GABON App Logo",
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            Spacer(Modifier.height(28.dp))

            // Application Name
            Text(
                text = "DEV GABON",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.onBackground,
                letterSpacing = 2.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            // Subtitle / Slogan
            Text(
                text = if (languageCode == "FR") 
                    "Le réseau social professionnel de la tech gabonaise" 
                    else "The professional social network for Gabonese tech",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(56.dp))

            // Well-structured, elegant dynamic linear progress bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                shape = RoundedCornerShape(3.dp),
                color = colors.onSurface.copy(alpha = 0.08f)
            ) {
                // Customized linear loading indicator utilizing animated spring progress
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = colors.primary,
                    trackColor = Color.Transparent
                )
            }

            Spacer(Modifier.height(18.dp))

            // Loading state details (changes beautifully based on actual steps in ViewModel init)
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primary,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            // Circular progress stats percentage
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }

        // Animated footer branding (Gabon Flag styled dots)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF3A86C8))) // Green-Yellow-Blue colors of Gabon flag
            Spacer(Modifier.width(6.dp))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFCD116)))
            Spacer(Modifier.width(6.dp))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF39B54A)))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Libreville, Gabon",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
        }
    }
}
