package com.example.navajasuiza.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.navajasuiza.R
import com.example.navajasuiza.ui.navigation.AppScreen
import com.example.navajasuiza.ui.theme.NavajaSuizaTheme
import com.example.navajasuiza.ui.viewmodels.LoginEvent
import com.example.navajasuiza.ui.viewmodels.LoginUiState
import com.example.navajasuiza.ui.viewmodels.LoginViewModel
import com.example.navajasuiza.ui.viewmodels.ViewModelFactoryProvider

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application


    if (context.javaClass.simpleName != "PreviewContext") {


        val viewModel: LoginViewModel = viewModel(
            factory = ViewModelFactoryProvider.getLoginViewModelFactory(application)
        )


        val uiState by viewModel.uiState.collectAsState()


        LaunchedEffect(Unit) {
            viewModel.loginEvent.collect { event ->
                when (event) {
                    is LoginEvent.Success -> {
                        Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        navController.navigate(AppScreen.DashboardScreen.createRoute(event.user.id)) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    is LoginEvent.Error -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        LoginScreenContent(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = viewModel::onLoginClicked,
            onRegisterClick = { navController.navigate(AppScreen.RegistroScreen.route) },
            onAboutClick = { navController.navigate(AppScreen.AboutScreen.route) },
            onGuestClick = {
                navController.navigate(AppScreen.DashboardScreen.createRoute(-1)) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}



@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onAboutClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondos),
            contentDescription = "Fondo de montaña",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo_de_app),
                    contentDescription = "Logo de la app",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 32.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0x993E4932), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = "CORREO ELECTRÓNICO",
                        iconId = R.drawable.gmail,
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LoginTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = "CONTRASEÑA",
                        iconId = R.drawable.icono_contra,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3E4932),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "INICIAR SESIÓN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF291D0A),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "REGISTRO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onGuestClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3E4932),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "INVITADO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            TextButton(onClick = onAboutClick) {
                Text(text = "ACERCA DE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    iconId: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black
            ),
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    NavajaSuizaTheme {
        LoginScreenContent(
            uiState = LoginUiState(email = "explorador@email.com"),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onRegisterClick = {},
            onAboutClick = {},
            onGuestClick = {}
        )
    }
}










