package com.example.laundry

fun hargaPerKg(layanan: String): Int {
    return when (layanan) {
        "Reguler" -> 6000
        "Kilat" -> 10000
        "Setrika saja" -> 4000
        else -> 0
    }
}

fun lamaPengerjaan(layanan: String): Int {
    return when (layanan) {
        "Reguler" -> 3
        "Kilat" -> 1
        "Setrika saja" -> 2
        else -> 0
    }
}

fun beratTagihan(berat: Double): Double {
    return if (berat < 2.0) 2.0 else berat
}

fun hitungTotal(
    berat: Double,
    layanan: String,
    pewangiPremium: Boolean
): Int {
    val beratDibayar = beratTagihan(berat)
    val tarif = hargaPerKg(layanan)
    val tambahanPewangi =
        if (pewangiPremium) 1000 else 0

    val subtotal = (
            beratDibayar * (tarif + tambahanPewangi)
            ).toInt()

    return if (subtotal >= 50000) {
        subtotal - 5000
    } else {
        subtotal
    }
}

fun validasiNomorHp(nomorHp: String): Boolean {
    return nomorHp.startsWith("08") &&
            nomorHp.length in 10..13 &&
            nomorHp.all { it.isDigit() }
}

fun validasiBerat(berat: Double): Boolean {
    return berat > 0.0 && berat <= 100.0
}

fun jumlahBelumSelesai(
    daftar: List<PesananLaundry>
): Int {
    return daftar.count { !it.selesai }
}

fun totalBerat(
    daftar: List<PesananLaundry>
): Double {
    return daftar.sumOf { it.Berat }
}

fun pendapatanSelesai(
    daftar: List<PesananLaundry>
): Int {
    return daftar
        .filter { it.selesai }
        .sumOf { it.total }
}

fun pendapatanMenunggu(
    daftar: List<PesananLaundry>
): Int {
    return daftar
        .filter { !it.selesai }
        .sumOf { it.total }
}
