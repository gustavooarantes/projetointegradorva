package com.example.gattabiju.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gattabiju.data.Client
import com.example.gattabiju.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ClientViewModel) {
    // Coleta a lista do banco de dados em tempo real
    val clientList by viewModel.allClients.collectAsState()

    // Estado para controlar se o diálogo de adicionar está visível
    var showDialog by remember { mutableStateOf(false) }

    // Scaffold é o esqueleto da tela (Barra superior, Conteúdo, Botão Flutuante)
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("GattaBiju Clientes") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        // LISTA DE CLIENTES
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(clientList) { client ->
                ClientItem(
                    client = client,
                    onDelete = { viewModel.deleteClient(client) }
                )
            }
        }

        // DIÁLOGO DE CADASTRO (Aparece quando clica no botão +)
        if (showDialog) {
            AddClientDialog(
                onDismiss = { showDialog = false },
                onConfirm = { nome, tel, email ->
                    viewModel.saveClient(Client(nomeCompleto = nome, telefone = tel, email = email))
                    showDialog = false
                }
            )
        }
    }
}

// COMPONENTE: Um item da lista (Card)
@Composable
fun ClientItem(client: Client, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = client.nomeCompleto, style = MaterialTheme.typography.titleMedium)
                Text(text = client.telefone, style = MaterialTheme.typography.bodyMedium)
                Text(text = client.email, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// COMPONENTE: O formulário flutuante
@Composable
fun AddClientDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
                OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nome, telefone, email) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}