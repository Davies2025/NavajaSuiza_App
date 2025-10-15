package com.example.navajasuiza.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navajasuiza.R
import com.example.navajasuiza.data.entities.User
import com.example.navajasuiza.ui.navigation.AppScreen
import com.example.navajasuiza.ui.viewmodels.DashboardUiState
import com.example.navajasuiza.ui.viewmodels.DashboardViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel

) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            WelcomeSection(user = uiState.currentUser)
            CompassSection(rotation = uiState.compassRotation)
            InfoCardsRow(uiState = uiState)
            ActionButtonsSection(navController = navController)
        }
    }
}


@Composable
private fun WelcomeSection(user: User?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.explorador),
                    contentDescription = "Icono explorador",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "BIENVENIDO\nEXPLORADOR",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                            append("Nombre: ")
                        }
                        append(user?.fullName ?: "Explorador Invitado")
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                            append("Actividad Deportiva: ")
                        }
                        append(user?.sportsActivity ?: "Descubriendo")
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CompassSection(rotation: Float) {
    val animatedRotation by animateFloatAsState(
        targetValue = -rotation,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.modelo_brujula),
                contentDescription = "Base de la brújula",
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedRotation)
            )


            Canvas(modifier = Modifier.fillMaxSize()) {
                withTransform({
                    rotate(degrees = animatedRotation, pivot = center)
                }) {
                    val redPath = Path().apply {
                        moveTo(center.x, center.y - size.height * 0.4f)
                        lineTo(center.x + size.width * 0.05f, center.y)
                        lineTo(center.x - size.width * 0.05f, center.y)
                        close()
                    }
                    drawPath(path = redPath, color = Color.Red)
                    val grayPath = Path().apply {
                        moveTo(center.x, center.y + size.height * 0.4f)
                        lineTo(center.x + size.width * 0.05f, center.y)
                        lineTo(center.x - size.width * 0.05f, center.y)
                        close()
                    }
                    drawPath(path = grayPath, color = Color.LightGray)
                    drawCircle(color = Color.Black, radius = size.width * 0.03f, center = center)
                }
            }
        }
    }
}


@Composable
private fun InfoCardsRow(uiState: DashboardUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InfoCard(
            title = "Ángulo",
            value = uiState.angleText,
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            title = "Punto Cardinal",
            value = uiState.cardinalPoint,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionButton(
            text = "Cerrar Sesión",
            iconId = R.drawable.cerrar_sesion,
            onClick = {
                navController.navigate(AppScreen.LoginScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            text = "Estación",
            iconId = R.drawable.clima,
            onClick = { navController.navigate(AppScreen.EstacionScreen.route) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionButton(text: String, iconId: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = text,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

















