package com.example.navajasuiza.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navajasuiza.R

private val GreenColor = Color(0xFF2E4117)
private val BlueColor = Color(0xFF1D294E)
private val RedColor = Color(0xFF351515)
private val BrownColor = Color(0xFF544516)

@Composable
fun DatosClimaScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            Spacer(modifier = Modifier.height(32.dp))
            ClimateInfoCard(
                text = "Si la presión está bajando rápidamente. Es la señal más clara de que el mal tiempo (lluvia o viento) se acerca.",
                iconId = R.drawable.calidad_del_aire,
                cardColor = BlueColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            ClimateInfoCard(
                text = "Si el aire está muy árido (seco). Bebe agua constantemente para evitar la deshidratación, incluso si no sientes calor o sed.",
                iconId = R.drawable.humedad,
                cardColor = GreenColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            ClimateInfoCard(
                text = "El clima en la montaña cambia sin aviso. Vestir de forma adecuada te permite controlar tu comodidad y te protege tanto del frío como del sudor.",
                iconId = R.drawable.control_de_temperatura,
                cardColor = RedColor
            )


            Spacer(modifier = Modifier.height(32.dp))

            ReturnButton(navController = navController)


            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Header() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "DATOS DEL CLIMA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ClimateInfoCard(
    text: String,
    iconId: Int,
    cardColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun ReturnButton(navController: NavController) {
    Button(

        onClick = { navController.popBackStack() },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrownColor),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text(text = "REGRESAR", color = Color.White, fontWeight = FontWeight.Bold)
    }
}