package com.example.blisscakes.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.blisscakes.R
import com.example.blisscakes.datastore.DataStoreManager
import com.example.blisscakes.navigation.NavRoutes
import com.example.blisscakes.network.LoginRequest
import com.example.blisscakes.network.LoginResponse
import com.example.blisscakes.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginPage(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "LOGIN",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Username Field
                    Text("Email", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Enter your email") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Password Field
                    Text("Password", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .fillMaxWidth(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Login Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val request = LoginRequest(username, password)

                            RetrofitClient.instance.login(request)
                                .enqueue(object : Callback<LoginResponse> {
                                    override fun onResponse(
                                        call: Call<LoginResponse>,
                                        response: Response<LoginResponse>
                                    ) {
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            val token = response.body()?.token
                                            if (!token.isNullOrEmpty()) {
                                                scope.launch {
                                                    DataStoreManager(context).saveToken(token)
                                                }
                                            }
                                            Toast.makeText(context, "Login Success!", Toast.LENGTH_SHORT).show()
                                            navController.navigate(NavRoutes.Home) {
                                                popUpTo(NavRoutes.Login) { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, "Login Failed!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        },
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("LOGIN")
                    }

                    // Sign up prompt
                    TextButton(
                        onClick = { navController.navigate(NavRoutes.Signup) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Don't have an account? click here")
                    }
                }
            }
        }
    }
}
