package eu.tutorials.courseapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.courseapplication.MainViewModel
import eu.tutorials.courseapplication.R
import eu.tutorials.courseapplication.service.RegisterRequest
import eu.tutorials.courseapplication.ui.theme.MagentaLightBackground
import eu.tutorials.courseapplication.ui.theme.MagentaPrimary

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: (RegisterRequest) -> Unit,
    onBackClick: () -> Unit,
    viewState: MainViewModel.CoursesState
){
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MagentaLightBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.registration),
            contentDescription = "Register Image",
            modifier = Modifier
                .size(256.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = "Registration",
            style = MaterialTheme.typography.titleLarge,
            color = MagentaPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Firstname", color = MagentaPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            textStyle = TextStyle(color = MagentaPrimary, fontSize = 18.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Lastname", color = MagentaPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            textStyle = TextStyle(color = MagentaPrimary, fontSize = 18.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = MagentaPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            textStyle = TextStyle(color = MagentaPrimary, fontSize = 18.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            textStyle = TextStyle(color = MagentaPrimary, fontSize = 22.sp),
            onValueChange = { password = it },
            label = { Text("Password", color = MagentaPrimary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            trailingIcon = {
                val icon = if (isPasswordVisible) Icons.Default.Add else Icons.Default.Close
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = null, tint = MagentaPrimary)
                }
            }
        )
        if (viewState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = viewState.error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val registerRequest = RegisterRequest(firstName, lastName, email, password)
                onRegisterClick(registerRequest)
                      },
            enabled = !viewState.loading && !viewState.softLoading,
            modifier = Modifier
                .fillMaxWidth()
                .background(MagentaPrimary, shape = RoundedCornerShape(0.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = MagentaPrimary)
        ) {
            if (viewState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(text = "Register", color = Color.White, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Back",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Blue,
            fontSize = 16.sp,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )
    }
}