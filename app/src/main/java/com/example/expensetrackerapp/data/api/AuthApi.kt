package com.example.expensetrackerapp.data.api

import com.example.expensetrackerapp.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {

    // Register User
    @POST("auth/register")
    suspend fun register(@Body request: AuthModel.RegisterRequest): Response<Map<String, String>>

    // Login User
    @POST("auth/login")
    suspend fun login(@Body request: AuthModel.LoginRequest): Response<AuthModel.AuthResponse>

    // Get User Transactions
    @GET("api/auth/transactions")
    suspend fun getTransactions(
        @Query("user_id") userId: Int // Fetch transactions based on user_id
    ): Response<List<AuthModel.Transaction>>

    // Get User Budget
    @GET("api/auth/budget")
    suspend fun getUserBudget(
        @Query("user_id") userId: Int // Fetch budget for a user
    ): Response<AuthModel.Budget>
    @POST("api/auth/transaction")
    suspend fun addTransaction(
        @Body transaction: AuthModel.Transaction // Add a new transaction
    ): Response<AuthModel.Transaction>
    @POST("api/auth/budget")
    suspend fun setBudget(
        @Body budget: AuthModel.Budget // Set a budget for the user
    ): Response<AuthModel.Budget>
    @PUT("api/auth/transaction/{transactionId}")
    suspend fun editTransaction(
        @Path("transactionId") transactionId: Int, // This is the transactionId in the URL
        @Body transaction: AuthModel.Transaction // The transaction object in the body
    ): Response<AuthModel.Transaction>

    // Delete a transaction
    @DELETE("api/auth/transactions/{transaction_id}")
    suspend fun deleteTransaction(
        @Path("transaction_id") transactionId: Int // Transaction ID to delete
    ): Response<Void> // Assuming no response body
}
