package com.example.gattabiju.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateHelper {
    fun hoje(): String {
        val calendario = Calendar.getInstance()
        val formato = SimpleDateFormat("dd/MM", Locale.getDefault())
        return formato.format(calendario.time)
    }

    fun anoAtual(): String {
        val calendario = Calendar.getInstance()
        val formato = SimpleDateFormat("yyyy", Locale.getDefault())
        return formato.format(calendario.time)
    }
}
