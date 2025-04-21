package com.example.expensetrackerapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.expensetrackerapp.ui.theme.ExpenseTrackerAppTheme
import com.example.expensetrackerapp.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetrackerapp.data.model.AuthModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "startup") {
                    composable("startup") {
                        StartUpPage(navController) // Navigate to the StartUpPage first
                    }
                    composable("login") {
                        LoginScreen(navController)
                    }
                    composable("signup") {
                        SignUpScreen(navController)
                    }
                    composable("home") { // Add home screen here
                        HomeScreen(navController)  // This is the screen you're navigating to after login
                    }
                    composable("add_transaction") { // Add home screen here
                        AddTransactionScreen(navController)  // This is the screen you're navigating to after login
                    }
                    composable("budget_screen") {  // Ensure you have this route correctly set up
                        BudgetScreen(navController)  // Navigate to BudgetScreen
                    }
                }
            }
        }
    }
}

@Composable
fun StartUpPage(navController: NavController) {
    // Use a painter resource to load the image
    val image: Painter = painterResource(id = R.drawable.welcome)  // Replace with your image resource ID

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display Image
        Image(
            painter = image,
            contentDescription = "App Logo",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        // Welcome Text
        Text(
            text = "Welcome to SpendSmart!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Get Started Button
        Button(
            onClick = {
                // Navigate to the Login screen when "Get Started" is clicked
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }
    }
}
@Composable
fun HomeScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    // Collecting data from the ViewModel
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val totalIncome by viewModel.totalIncome.collectAsState(initial = 0f)
    val totalSpending by viewModel.totalSpending.collectAsState(initial = 0f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // SpendSmart Title
        Text(
            text = "SpendSmart",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp  // Increase font size
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)  // Add some space from top
                .align(Alignment.CenterHorizontally),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp)) // Move the next sections down further

        // Display Total Income and Spending
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Income Section
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784)) // Light green color
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Income", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$${totalIncome}", style = MaterialTheme.typography.bodyMedium) // Increased font size
                }
            }

            // Spending Section
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBB86FC)) // Default color
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Spending", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$${totalSpending}", style = MaterialTheme.typography.bodyMedium) // Increased font size
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display Transaction List in Cards
        if (transactions.isNotEmpty()) {
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onEdit = {
                            // Edit transaction logic
                            viewModel.editTransaction(transaction) { success ->
                                if (success as Boolean) {
                                    Log.d("Edit", "Transaction updated successfully")
                                } else {
                                    Log.e("Edit", "Failed to update transaction")
                                }
                            }
                        },
                        onDelete = {
                            // Delete transaction logic
                            viewModel.deleteTransaction(transaction.transactionId) { success ->
                                if (success) {
                                    Log.d("Delete", "Transaction deleted successfully")
                                } else {
                                    Log.e("Delete", "Failed to delete transaction")
                                }
                            }
                        }
                    )
                }
            }
        } else {
            Text("No transactions found.", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))  // Add space between transactions and buttons

        // Add Transaction Button
        Button(
            onClick = { navController.navigate("add_transaction") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Add Transaction")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Budget Button
        Button(
            onClick = { navController.navigate("budget_screen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set Budget")
        }
    }
}


@Composable
fun TransactionCard(transaction: AuthModel.Transaction, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Date: ${transaction.date}", style = MaterialTheme.typography.bodyMedium)
                Text("Description: ${transaction.description}", style = MaterialTheme.typography.bodySmall)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit Button
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Transaction",
                        tint = Color(0xFF6200EE) // You can customize the color here
                    )
                }

                // Delete Button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Transaction",
                        tint = Color(0xFFB00020) // Custom color for delete icon
                    )
                }
            }
        }
    }
}

@Composable
fun AddTransactionScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var amount by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf(0) }  // categoryId should be an integer
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Add New Transaction", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Amount
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category
        TextField(
            value = categoryId.toString(),
            onValueChange = { categoryId = it.toIntOrNull() ?: 0 },  // Convert to Int safely
            label = { Text("Category ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date
        TextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val userId = viewModel.userId
                if (userId != null) {
                    viewModel.addTransaction(amount.toFloat(), categoryId, description, date)
                    navController.navigate("home")
                } else {
                    Log.e("AddTransaction", "User ID is null, cannot add transaction.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Transaction")
        }

    }
}

@Composable
fun BudgetScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val userId = viewModel.userId  // Get the userId from the ViewModel



    // Continue with the rest of the BudgetScreen UI if user is logged in
    var amount by remember { mutableStateOf(0f) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Your Budget", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Budget amount input
        TextField(
            value = amount.toString(),
            onValueChange = { amount = it.toFloatOrNull() ?: 0f },
            label = { Text("Budget Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Start date input
        TextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // End date input
        TextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    message = ""
                    isLoading = true

                    // Call the setBudget function with dynamic dates
                    viewModel.setBudget(amount, startDate, endDate) { success, error ->
                        isLoading = false
                        if (success) {
                            message = "Budget set successfully!"
                            // Navigate back to home screen after setting budget successfully
                            navController.navigate("home")
                        } else {
                            message = error ?: "Failed to set budget"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set Budget")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(text = message, color = if (message.contains("successful", ignoreCase = true)) Color.Green else Color.Red)
        }
    }
}





@Composable
fun TransactionItem(transaction: AuthModel.Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
//                Text(transaction.categoryId, style = MaterialTheme.typography.bodyMedium) // Updated for Material3
                Text(
                    transaction.date,
                    style = MaterialTheme.typography.bodyMedium
                ) // Updated for Material3
            }

            Text("$${transaction.amount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel() // Injecting your ViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), // Mask the password with dots
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""

                    viewModel.loginUser(email, password) { success, error ->
                        isLoading = false
                        if (success) {
                            // After login success, set the userId
                            val userId = viewModel.userId
                            if (userId != null) {
                                Log.d("LOGIN", "User ID: $userId")  // Log the userId to check
                                viewModel.setUserId(userId) // Set the userId in the ViewModel
                            }
                            navController.navigate("home") // Navigate to the home screen
                        } else {
                            errorMessage = error ?: "Login failed"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log In")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicText(
            text = "Don't have an account? Sign Up",
            modifier = Modifier.clickable {
                navController.navigate("signup")
            }
        )
    }
}


@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Validation function
    fun validateInputs(): Boolean {
        // Reset error message
        errorMessage = ""

        // Check if all fields are filled
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
            errorMessage = "Please fill in all fields."
            return false
        }

        // Validate email format
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        if (!email.matches(emailRegex.toRegex())) {
            errorMessage = "Please enter a valid email address."
            return false
        }

        // Validate password length (e.g., min 6 characters)
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters."
            return false
        }

        // Validate date of birth format (YYYY-MM-DD)
        val dobRegex = """^\d{4}-\d{2}-\d{2}$"""
        if (!dob.matches(dobRegex.toRegex())) {
            errorMessage = "Please enter a valid date of birth (YYYY-MM-DD)."
            return false
        }

        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), // Mask the password with dots
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("Date of Birth (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    // Validate the inputs before proceeding
                    if (validateInputs()) {
                        message = ""
                        isLoading = true

                        val fullName = "$firstName $lastName"
                        viewModel.registerUser(fullName, email, password, dob) { success, error ->
                            isLoading = false
                            if (success) {
                                message = "Registration successful! You can now log in."
                                navController.navigate("login")
                            } else {
                                message = error ?: "Registration failed"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show the error message if validation fails
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (message.contains("successful", ignoreCase = true)) Color.Green else Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicText(
            text = "Already have an account? Log In",
            modifier = Modifier.clickable {
                navController.navigate("login")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StartUpPagePreview() {
    ExpenseTrackerAppTheme {
        StartUpPage(navController = rememberNavController())  // Preview for StartUp Page
    }
}
