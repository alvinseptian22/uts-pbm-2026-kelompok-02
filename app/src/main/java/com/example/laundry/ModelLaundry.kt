package com.example.laundry

data class PesananLaundry(
    val idPesanan: Long,
    val NamaPelanggan: String,
    val NomorHp: String,
    val Berat: Double,
    val Layanan: String,
    val PewangiPremium: Boolean,
    val total: Int,
    val lamaPengerjaan: Int,
    val selesai: Boolean = false
)
