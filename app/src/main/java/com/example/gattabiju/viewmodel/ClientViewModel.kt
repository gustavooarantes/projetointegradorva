package com.example.gattabiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gattabiju.data.Client
import com.example.gattabiju.data.ClientDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientViewModel(private val dao: ClientDao) : ViewModel() {
    // LISTAR: Converte o fluxo do banco em um estado que a tela entende
    val allClients = dao.listarTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // CRIAR / ATUALIZAR
    fun saveClient(client: Client) {
        viewModelScope.launch {
            if (client.id == 0) {
                dao.inserir(client)
            } else {
                dao.atualizar(client)
            }
        }
    }

    // DELETAR
    fun deleteClient(client: Client) {
        viewModelScope.launch {
            dao.deletar(client)
        }
    }
}

// FÁBRICA: Ensina o Android a criar o ViewModel passando o Banco de Dados
class ClientViewModelFactory(private val dao: ClientDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}