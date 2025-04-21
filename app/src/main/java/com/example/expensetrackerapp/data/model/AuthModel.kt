package com.example.expensetrackerapp.data.model

class AuthModel {
    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String,
        val DOB: String
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class User(
        val id: Int,
        val name: String,
        val email: String,
        val DOB: String,
        val created_at: String
    )

    data class AuthResponse(
        val token: String,
        val user: User
    )
    data class Transaction(
        val transactionId: Int,
        val userId: Int,
        val categoryId: Int,
        val amount: Float,
        val date: String,
        val description: String,
        val type: String // "Income" or "Expense"
    )

    data class Budget(
        val userId: Int,
        val startDate: String,
        val amount: Float,  // The set budget amount
        val endDate: String
    )

}