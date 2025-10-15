package com.example.navajasuiza.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.navajasuiza.R


val DarkGreenBackground = Color(0xFF3E4932).copy(alpha = 0.7f)
val ButtonColor = Color(0xFF291D0A)

@Composable
fun AboutScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.fondos),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "ACERCA DE",
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )


            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp)),
                color = DarkGreenBackground,
            ) {
                Text(
                    text = "Creada para el aventurero de hoy, Navaja Suiza transforma tu teléfono en la herramienta definitiva para cualquier expedición. Mide tu entorno, oriéntate y anticípate al clima utilizando el poder de los sensores de tu dispositivo, preparándote para tu próxima gran aventura.",
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))


            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "REGRESAR",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    MaterialTheme {
        AboutScreen(rememberNavController())
    }
}

