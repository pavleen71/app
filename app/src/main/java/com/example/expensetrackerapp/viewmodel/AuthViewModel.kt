package com.example.expensetrackerapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.api.RetrofitInstance
import com.example.expensetrackerapp.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    var userId: Int? = null  // Store logged-in user's ID

    private val _transactions = MutableStateFlow<List<AuthModel.Transaction>>(emptyList())
    val transactions: StateFlow<List<AuthModel.Transaction>> = _transactions

    private val _totalIncome = MutableStateFlow(0f)
    val totalIncome: StateFlow<Float> = _totalIncome

    private val _totalSpending = MutableStateFlow(0f)
    val totalSpending: StateFlow<Float> = _totalSpending

    private val _budget = MutableStateFlow(0f)
    val budget: StateFlow<Float> = _budget

    private val _isBudgetExceeded = MutableStateFlow(false)
    val isBudgetExceeded: StateFlow<Boolean> = _isBudgetExceeded

    fun setUserId(id: Int) {
        userId = id
        getRecentTransactions()
        getUserBudget()
    }

    private fun getRecentTransactions() {
        userId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.getTransactions(userId = id)
                    if (response.isSuccessful) {
                        val transactionList = response.body() ?: emptyList()
                        _transactions.value = transactionList
                        Log.d("Transactions", "Fetched Transactions: $transactionList")
                        checkBudgetExceeded()
                    }
                } catch (e: Exception) {
                    Log.e("Transactions", "Failed to fetch transactions: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun getUserBudget() {
        userId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.getUserBudget(userId = id)
                    if (response.isSuccessful) {
                        val budgetInfo = response.body()
                        _budget.value = budgetInfo?.amount ?: 0f
                    }
                } catch (e: Exception) {
                    Log.e("Budget", "Failed to fetch budget: ${e.localizedMessage}")
                }
            }
        }
    }

    fun addTransaction(amount: Float, categoryId: Int, description: String, date: String) {
        userId?.let { id ->
            val newTransaction = AuthModel.Transaction(
                transactionId = (_transactions.value.size + 1),
                userId = id,
                categoryId = categoryId,
                amount = amount,
                date = date,
                description = description,
                type = "Expense"
            )
            _transactions.value = _transactions.value + newTransaction
        }
    }

    fun setBudget(amount: Float, startDate: String, endDate: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            userId?.let { id ->
                try {
                    val budget = AuthModel.Budget(
                        userId = id,
                        startDate = startDate,
                        amount = amount,
                        endDate = endDate
                    )
                    val response = RetrofitInstance.api.setBudget(budget)
                    if (response.isSuccessful) {
                        _budget.value = amount
                        Log.d("Set Budget", "Budget set successfully to $amount")
                        onResult(true, null)
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("Set Budget", "Failed to set budget: $error")
                        onResult(false, error)
                    }
                } catch (e: Exception) {
                    Log.e("Set Budget", "Failed to set budget: ${e.localizedMessage}")
                    onResult(false, e.localizedMessage)
                }
            } ?: run {
                onResult(false, "User is not logged in.")
            }
        }
    }

    fun updateTransaction(
        transactionId: Int,
        amount: Float,
        description: String,
        date: String,
        categoryId: Int,
        onResult: (Boolean) -> Unit
    ) {
        userId?.let { id ->
            val transaction = AuthModel.Transaction(
                transactionId = transactionId,
                userId = id,
                categoryId = categoryId,
                amount = amount,
                date = date,
                description = description,
                type = "Expense"
            )
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.editTransaction(transactionId, transaction)
                    onResult(response.isSuccessful)
                } catch (e: Exception) {
                    Log.e("Transaction", "Failed to update transaction: ${e.localizedMessage}")
                    onResult(false)
                }
            }
        }
    }

    fun deleteTransaction(transactionId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteTransaction(transactionId)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                Log.e("Transaction", "Failed to delete transaction: ${e.localizedMessage}")
                onResult(false)
            }
        }
    }

    private fun checkBudgetExceeded() {
        _isBudgetExceeded.value = _totalSpending.value > _budget.value
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        dob: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val request = AuthModel.RegisterRequest(name, email, password, dob)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.register(request)
                if (response.isSuccessful) {
                    onResult(true, null)
                } else {
                    val error = response.errorBody()?.string()
                    onResult(false, error)
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage)
            }
        }
    }

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val request = AuthModel.LoginRequest(email, password)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(request)
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.user?.id?.let {
                        setUserId(it)
                        onResult(true, null)
                    } ?: run {
                        onResult(false, "Failed to retrieve user ID.")
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("LOGIN", "Failed: $error")
                    onResult(false, error)
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "Exception: ${e.localizedMessage}")
                onResult(false, e.localizedMessage)
            }
        }
    }

    fun editTransaction(transaction: AuthModel.Transaction, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.editTransaction(transaction.transactionId, transaction)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                Log.e("Transaction", "Failed to update transaction: ${e.localizedMessage}")
                onResult(false)
            }
        }
    }
}
