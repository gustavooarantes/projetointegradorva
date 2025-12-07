package com.example.gattabiju.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.gattabiju.data.AppDatabase
import com.example.gattabiju.ui.screens.HomeScreen
import com.example.gattabiju.ui.theme.GattaBijuTheme
import com.example.gattabiju.viewmodel.ClientViewModel
import com.example.gattabiju.viewmodel.ClientViewModelFactory
import com.example.gattabiju.viewmodel.CouponViewModel
import com.example.gattabiju.viewmodel.CouponViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val clientDao = database.clientDao()
        val couponDao = database.couponDao()

        val clientViewModel: ClientViewModel by viewModels {
            ClientViewModelFactory(clientDao)
        }

        val couponViewModel: CouponViewModel by viewModels {
            CouponViewModelFactory(couponDao, clientDao)
        }

        setContent {
            GattaBijuTheme {
                HomeScreen(
                    clientViewModel = clientViewModel,
                    couponViewModel = couponViewModel,
                )
            }
        }
    }
}

