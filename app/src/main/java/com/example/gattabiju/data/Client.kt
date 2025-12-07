package com.example.gattabiju.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nome_completo") val nomeCompleto: String,
    @ColumnInfo(name = "telefone") val telefone: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "data_nascimento") val dataNascimento: String,
)
