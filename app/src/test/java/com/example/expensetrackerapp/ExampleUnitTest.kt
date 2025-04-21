package com.example.expensetrackerapp

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Response

// Define a sample model
data class Expense(val id: Int, val name: String, val amount: Double)

// Define the Retrofit API service
interface ApiService {
    @GET("expenses")
    suspend fun getExpenses(): Response<List<Expense>>
}

class ExampleUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Use the mock server URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @Test
    fun testApiEndpointIsReachable() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[]") // Mock empty list response
        mockWebServer.enqueue(mockResponse)

        val response = apiService.getExpenses()

        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}
