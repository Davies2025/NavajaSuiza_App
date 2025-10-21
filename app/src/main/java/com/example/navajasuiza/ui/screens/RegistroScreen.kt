package com.example.navajasuiza.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.navajasuiza.data.AppDatabase
import com.example.navajasuiza.data.repository.UserRepository
import com.example.navajasuiza.ui.navigation.AppScreen
import com.example.navajasuiza.ui.theme.NavajaSuizaTheme
import com.example.navajasuiza.ui.viewmodels.RegistrationUiState
import com.example.navajasuiza.ui.viewmodels.RegistroViewModel
import com.example.navajasuiza.ui.viewmodels.RegistroViewModelFactory


@Composable
fun RegistroScreen(navController: NavController) {
    val context = LocalContext.current
    if (context.javaClass.simpleName != "PreviewContext") {
        val db = AppDatabase.getInstance(context)
        val repository = UserRepository(db.userDao())
        val factory = RegistroViewModelFactory(repository)
        val viewModel: RegistroViewModel = viewModel(factory = factory)
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(key1 = true) {
            viewModel.registrationMessage.collect { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(key1 = uiState.isRegistrationSuccessful) {
            if (uiState.isRegistrationSuccessful) {
                navController.navigate(AppScreen.LoginScreen.route) {
                    popUpTo(0)
                }
            }
        }

        RegistroScreenContent(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onFullNameChange = viewModel::onFullNameChange,
            onSportsActivityChange = viewModel::onSportsActivityChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onRegisterClicked = viewModel::onRegisterClicked,
            onBackClicked = { navController.popBackStack() }
        )
    }
}


@Composable
fun RegistroScreenContent(
    uiState: RegistrationUiState,
    onEmailChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onSportsActivityChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClicked: () -> Unit,
    onBackClicked: () -> Unit
) {

    var backButtonEnabled by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.acerca),
            contentDescription = "Fondo de la pantalla",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        backButtonEnabled = false
                        onBackClicked()
                    },
                    enabled = backButtonEnabled
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0x993E4932), shape = RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.senderismo),
                        contentDescription = "Icono de explorador",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "NUEVO\nUSUARIO", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                CustomTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = "Correo Electrónico",
                    iconId = R.drawable.gmail,
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    value = uiState.fullName,
                    onValueChange = onFullNameChange,
                    label = "Nombre Completo",
                    iconId = R.drawable.usuario,
                )
                Spacer(modifier = Modifier.height(16.dp))
                SportsActivityDropdown(
                    selectedValue = uiState.sportsActivity,
                    onValueChange = onSportsActivityChange
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = "Contraseña",
                    iconId = R.drawable.icono_contra,
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirmar Contraseña",
                    iconId = R.drawable.icono_contra,
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onRegisterClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF291D0A), contentColor = Color.White)
                ) {
                    Text(text = "GUARDAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}


@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    iconId: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
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
                focusedBorderColor = Color(0xFF3E4932),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SportsActivityDropdown(
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    val sportsActivities = listOf(
        "Ninguna", "Senderismo", "Alpinismo", "Ciclismo de montaña",
        "Campismo", "Pesca deportiva", "Correr en senderos", "Espeleología", "Otro"
    )
    var expanded by remember { mutableStateOf(false) }

    val placeholderText = "Tocar para seleccionar la actividad"
    val textColor = if (selectedValue.isBlank()) Color.Gray else Color.Black

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Actividad Deportiva", color = Color.White, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = if (selectedValue.isBlank()) placeholderText else selectedValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3E4932),
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                textStyle = TextStyle(color = textColor),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color.White)) {
                sportsActivities.forEach { activity ->
                    DropdownMenuItem(
                        text = { Text(activity, color = Color.Black) },
                        onClick = {
                            onValueChange(activity)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RegistroScreenPreview() {
    val fakeUiState = RegistrationUiState(
        email = "explorador@email.com",
        fullName = "DAVID ERAZO",
        sportsActivity = ""
    )
    NavajaSuizaTheme {
        RegistroScreenContent(
            uiState = fakeUiState,
            onEmailChange = {},
            onFullNameChange = {},
            onSportsActivityChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRegisterClicked = {},
            onBackClicked = {}
        )
    }
}







