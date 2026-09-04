package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.AppNavHost
import com.example.ui.theme.SmartCalcTheme
import com.example.ui.viewmodel.SmartCalcViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SmartCalcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handlePaymentIntent(intent)
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()

            SmartCalcTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlePaymentIntent(intent)
    }

    private fun handlePaymentIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        val paymentId = uri.getQueryParameter("payment_id")
            ?: uri.getQueryParameter("razorpay_payment_id")
            ?: uri.getQueryParameter("id")
        if (!paymentId.isNullOrBlank()) {
            viewModel.activateSubscriptionWithPayment(paymentId) { success, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}
