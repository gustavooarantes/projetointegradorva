package com.example.gattabiju.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.gattabiju.data.AppDatabase
import com.example.gattabiju.ui.screens.HomeScreen
import com.example.gattabiju.ui.theme.GattaBijuTheme // Ou o nome do seu tema atual
import com.example.gattabiju.viewmodel.ClientViewModel
import com.example.gattabiju.viewmodel.ClientViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializa o Banco de Dados
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.clientDao()

        // 2. Cria o ViewModel usando a Factory
        val viewModel: ClientViewModel by viewModels {
            ClientViewModelFactory(dao)
        }

        // 3. Define o conteúdo da tela
        setContent {
            // Use o nome do tema que está no arquivo Theme.kt (provavelmente GattabijuTheme ou similar)
            GattaBijuTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}