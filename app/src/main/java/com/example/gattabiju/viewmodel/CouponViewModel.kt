package com.example.gattabiju.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gattabiju.data.Client
import com.example.gattabiju.data.ClientDao
import com.example.gattabiju.data.Coupon
import com.example.gattabiju.data.CouponDao
import com.example.gattabiju.data.DateHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CouponViewModel(
    private val couponDao: CouponDao,
    private val clientDao: ClientDao,
) : ViewModel() {
    val activeCoupons =
        couponDao
            .listarAtivos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    init {
        verificarDatasEspeciais()
        verificarAniversarios()
    }

    private fun verificarDatasEspeciais() {
        val hoje = DateHelper.hoje()
        val ano = DateHelper.anoAtual()

        viewModelScope.launch {
            // Mapa de datas: "Dia/Mês" -> "Nome do Evento"
            val datasFixas =
                mapOf(
                    "01/01" to "ANO_NOVO",
                    "08/03" to "DIA_MULHER",
                    "12/06" to "NAMORADOS",
                    "12/10" to "DIA_CRIANCAS",
                    "25/12" to "NATAL",
                )

            // Se hoje estiver na lista...
            if (datasFixas.containsKey(hoje)) {
                val evento = datasFixas[hoje]!!
                val codigoCupom = "$evento$ano"

                criarCupomAutomatico(
                    codigo = codigoCupom,
                    descricao = "Especial de $evento",
                    porcentagem = 15,
                )
            }
        }
    }

    private fun verificarAniversarios() {
        val hoje = DateHelper.hoje()

        viewModelScope.launch {
            val clientes = clientDao.listarTodos().first()

            for (cliente in clientes) {
                if (cliente.dataNascimento == hoje) {
                    val codigoNiver = "NIVER-${cliente.nomeCompleto.take(3).uppercase()}"

                    criarCupomAutomatico(
                        codigo = codigoNiver,
                        descricao = "Parabéns ${cliente.nomeCompleto}!",
                        porcentagem = 20,
                    )
                }
            }
        }
    }

    // -------------------------
    // Novo método público
    // -------------------------
    // Verifica se o cliente passado tem aniversário hoje e cria o cupom se for o caso.
    // Pode ser chamado pela UI logo após salvar um cliente recém-criado.
    fun verificarAniversarioPara(cliente: Client) {
        val hoje = DateHelper.hoje()

        viewModelScope.launch {
            if (cliente.dataNascimento == hoje) {
                val codigoNiver = "NIVER-${cliente.nomeCompleto.take(3).uppercase()}"

                criarCupomAutomatico(
                    codigo = codigoNiver,
                    descricao = "Parabéns ${cliente.nomeCompleto}!",
                    porcentagem = 20,
                )
            }
        }
    }

    private suspend fun criarCupomAutomatico(
        codigo: String,
        descricao: String,
        porcentagem: Int,
    ) {
        val novoCupom =
            Coupon(
                codigo = codigo,
                descricao = descricao,
                porcentagem = porcentagem,
                ativo = true,
            )
        couponDao.inserir(novoCupom)
        //criarCupomManual(novoCupom.codigo, novoCupom.descricao, novoCupom.porcentagem)
    }

    fun criarCupomManual(
        codigo: String,
        descricao: String,
        porcentagem: Int,
    ) {
        viewModelScope.launch {
            couponDao.inserir(
                Coupon(
                    codigo = codigo.uppercase(),
                    descricao = descricao,
                    porcentagem = porcentagem,
                ),
            )
        }
    }

    fun deletarCupom(cupom: Coupon) {
        viewModelScope.launch {
            couponDao.deletar(cupom)
        }
    }
}

class CouponViewModelFactory(
    private val couponDao: CouponDao,
    private val clientDao: ClientDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CouponViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CouponViewModel(couponDao, clientDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
