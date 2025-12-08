package com.example.gattabiju.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gattabiju.data.Client
import com.example.gattabiju.data.Coupon
import com.example.gattabiju.viewmodel.ClientViewModel
import com.example.gattabiju.viewmodel.CouponViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    clientViewModel: ClientViewModel,
    couponViewModel: CouponViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (selectedTab == 0) "Clientes GattaBiju" else "Cupons Ativos") 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Clientes") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("Cupons") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (selectedTab == 0) {
                ClientScreenContent(clientViewModel, couponViewModel)
            } else {
                CouponScreenContent(couponViewModel)
            }
        }
    }
}

// ==========================================
// TELA 1: CONTEÚDO DE CLIENTES
// ==========================================
@Composable
fun ClientScreenContent(viewModel: ClientViewModel, couponViewModel: CouponViewModel) {
    val clientList by viewModel.allClients.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf<Client?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Cliente")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            items(clientList) { client ->
                ClientItem(client = client, onDelete = { viewModel.deleteClient(client) }, onUpdate = {
                    showUpdateDialog = client
                })
            }
        }

        if (showAddDialog) {
            AddClientDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nome, tel, email, aniversario ->
                    val novoCliente = Client(nomeCompleto = nome, telefone = tel, email = email, dataNascimento = aniversario)
                    viewModel.saveClient(novoCliente)
                    // chama a verificação para criar cupom caso hoje seja o aniversário do novo cliente
                    couponViewModel.verificarAniversarioPara(novoCliente)
                    showAddDialog = false
                }
            )
        }

        showUpdateDialog?.let { client ->
            UpdateClientDialog(
                client = client,
                onDismiss = { showUpdateDialog = null },
                onConfirm = { nome, tel, email, aniversario ->
                    viewModel.saveClient(client.copy(nomeCompleto = nome, telefone = tel, email = email, dataNascimento = aniversario))
                    showUpdateDialog = null
                }
             )
         }
    }
}

// ==========================================
// TELA 2: CONTEÚDO DE CUPONS
// ==========================================
@Composable
fun CouponScreenContent(viewModel: CouponViewModel) {
    val cuponsList by viewModel.activeCoupons.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, "Criar") },
                text = { Text("Novo Cupom") },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            if (cuponsList.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum cupom ativo hoje.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            items(cuponsList) { coupon ->
                CupomItem(coupon = coupon, onDelete = { viewModel.deletarCupom(coupon) })
            }
        }

        if (showDialog) {
            AddCouponDialog(
                onDismiss = { showDialog = false },
                onConfirm = { codigo, desc, porc ->
                    viewModel.criarCupomManual(codigo, desc, porc.toIntOrNull() ?: 10)
                    showDialog = false
                }
            )
        }
    }
}

// ==========================================
// COMPONENTES VISUAIS (Itens e Diálogos)
// ==========================================

@Composable
fun ClientItem(client: Client, onDelete: () -> Unit, onUpdate: (Client) -> Unit =  {

}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(client.nomeCompleto, style = MaterialTheme.typography.titleMedium)
                Text("Tel: ${client.telefone}", style = MaterialTheme.typography.bodyMedium)
                if (client.dataNascimento.isNotEmpty()) {
                    Text("Niver: ${client.dataNascimento}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
            }
            IconButton(onClick = { onUpdate(client) }) {
                Icon(Icons.Default.Edit, contentDescription = "Atualizar")
            }
        }
    }
}

@Composable
fun CupomItem(coupon: Coupon, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(coupon.codigo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(coupon.descricao, style = MaterialTheme.typography.bodyMedium)
                Text("${coupon.porcentagem}% OFF", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Usar/Deletar")
            }
        }
    }
}

@Composable
fun AddClientDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var niver by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
                OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = niver, onValueChange = { niver = it }, label = { Text("Niver (dd/MM)") }, placeholder = { Text("Ex: 15/05") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(nome, telefone, email, niver) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun UpdateClientDialog(client: Client,onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var nome by remember { mutableStateOf(client.nomeCompleto) }
    var telefone by remember { mutableStateOf(client.telefone) }
    var email by remember { mutableStateOf(client.email) }
    var niver by remember { mutableStateOf(client.dataNascimento) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
                OutlinedTextField(value = telefone, onValueChange = { telefone = it }, label = { Text("Telefone") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = niver, onValueChange = { niver = it }, label = { Text("Niver (dd/MM)") }, placeholder = { Text("Ex: 15/05") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(nome, telefone, email, niver) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AddCouponDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var codigo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var porcentagem by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criar Cupom Manual") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = codigo, onValueChange = { codigo = it.uppercase() }, label = { Text("Código (Ex: VERAO10)") })
                OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") })
                OutlinedTextField(value = porcentagem, onValueChange = { porcentagem = it }, label = { Text("Desconto (%)") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(codigo, descricao, porcentagem) }) { Text("Criar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
