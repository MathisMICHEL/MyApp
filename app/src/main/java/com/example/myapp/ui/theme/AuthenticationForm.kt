package com.example.myapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthenticationForm(
    onBackClick: () -> Unit,
    onAuthenticateUser: (email: String, password: String) -> Unit,
    onCreateAccount: (email: String, password: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isCreatingAccount by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isCreatingAccount) "Create Account" else "Authentication",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isCreatingAccount) {
            Button(onClick = { onCreateAccount(email, password) }) {
                Text("Create Account")
            }
        } else {
            Button(onClick = { onAuthenticateUser(email, password) }) {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { isCreatingAccount = !isCreatingAccount }) {
            Text(if (isCreatingAccount) "Log In" else "Create account")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}
