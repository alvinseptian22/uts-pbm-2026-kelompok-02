package com.example.laundry

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

private const val NAMA_PREFERENSI = "penyimpanan_laundry"
private const val KUNCI_PESANAN = "daftar_pesanan"

fun simpanDaftarPesanan(
    context: Context,
    daftar: List<PesananLaundry>
) {
    val jsonArray = JSONArray()

    daftar.forEach { item ->
        val json = JSONObject().apply {
            put("idPesanan", item.idPesanan)
            put("NamaPelanggan", item.NamaPelanggan)
            put("NomorHp", item.NomorHp)
            put("Berat", item.Berat)
            put("Layanan", item.Layanan)
            put("PewangiPremium", item.PewangiPremium)
            put("total", item.total)
            put("lamaPengerjaan", item.lamaPengerjaan)
            put("selesai", item.selesai)
        }
        jsonArray.put(json)
    }

    context.getSharedPreferences(
        NAMA_PREFERENSI,
        Context.MODE_PRIVATE
    ).edit()
        .putString(KUNCI_PESANAN, jsonArray.toString())
        .apply()
}

fun muatDaftarPesanan(
    context: Context
): List<PesananLaundry> {
    val teksJson = context.getSharedPreferences(
        NAMA_PREFERENSI,
        Context.MODE_PRIVATE
    ).getString(KUNCI_PESANAN, null) ?: return emptyList()

    return try {
        val jsonArray = JSONArray(teksJson)
        val hasil = mutableListOf<PesananLaundry>()

        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)

            hasil.add(
                PesananLaundry(
                    idPesanan = json.getLong("idPesanan"),
                    NamaPelanggan = json.getString("NamaPelanggan"),
                    NomorHp = json.getString("NomorHp"),
                    Berat = json.getDouble("Berat"),
                    Layanan = json.getString("Layanan"),
                    PewangiPremium = json.getBoolean("PewangiPremium"),
                    total = json.getInt("total"),
                    lamaPengerjaan = json.getInt("lamaPengerjaan"),
                    selesai = json.optBoolean("selesai", false)
                )
            )
        }

        hasil
    } catch (e: Exception) {
        emptyList()
    }
}
