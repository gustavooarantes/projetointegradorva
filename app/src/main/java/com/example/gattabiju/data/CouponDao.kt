package com.example.gattabiju.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Insert
    suspend fun inserir(coupon: Coupon)

    @Query("SELECT * FROM cupons WHERE ativo = 1 ORDER BY id DESC")
    fun listarAtivos(): Flow<List<Coupon>>

    @Delete
    suspend fun deletar(coupon: Coupon)
}
