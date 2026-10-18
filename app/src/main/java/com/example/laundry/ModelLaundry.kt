package com.example.laundry

data class PesananLaundry(
    val id: Long,
    val namaPelanggan: String,
    val nomorHp: String,
    val berat: Double,
    val layanan: String,
    val pewangiPremium: Boolean,
    val total: Int,
    val lamaPengerjaan: Int,
    val selesai: Boolean = false
)
