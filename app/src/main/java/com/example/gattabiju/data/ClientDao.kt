package com.example.gattabiju.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert
    suspend fun inserir(client: Client)

    @Query("SELECT * FROM clientes ORDER BY nome_completo ASC")
    fun listarTodos(): Flow<List<Client>>

    @Update
    suspend fun atualizar(client: Client)

    @Delete
    suspend fun deletar(client: Client)

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Client?

}