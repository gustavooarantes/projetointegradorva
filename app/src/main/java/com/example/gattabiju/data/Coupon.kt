package com.example.gattabiju.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cupons")
data class Coupon(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "codigo") val codigo: String,
    @ColumnInfo(name = "descricao") val descricao: String,
    @ColumnInfo(name = "porcentagem") val porcentagem: Int,
    @ColumnInfo(name = "ativo") val ativo: Boolean = true,
)
