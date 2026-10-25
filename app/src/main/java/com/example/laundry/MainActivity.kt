package com.example.laundry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.laundry.ui.theme.LaundryTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaundryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LayarKasirLaundry()
                }
            }
        }
    }
}

@Composable
fun LayarKasirLaundry() {
    var nama by remember { mutableStateOf("") }
    var hp by remember { mutableStateOf("") }
    var beratText by remember { mutableStateOf("") }
    var layanan by remember { mutableStateOf("Reguler") }
    var pewangi by remember { mutableStateOf(false) }
    var pesan by remember { mutableStateOf("") }

    val daftarPesanan = remember {
        mutableStateListOf<PesananLaundry>()
    }

    val berat = beratText.replace(",", ".").toDoubleOrNull()

    val total = if (
        berat != null && validasiBerat(berat)
    ) {
        hitungTotal(berat, layanan, pewangi)
    } else {
        0
    }

    val pesananAktif = daftarPesanan.filter { !it.selesai }
    val jumlahSelesai = daftarPesanan.count { it.selesai }
    val pendapatanSelesai = daftarPesanan
        .filter { it.selesai }
        .sumOf { it.total }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // HEADER APLIKASI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_laundry),
                    contentDescription = "Logo Laundry",
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = "LAUNDRY KILOAN",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = stringResource(R.string.judul_header),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = stringResource(R.string.info_kelompok),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // RINGKASAN
        Text(
            text = "Ringkasan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KartuRingkasan(
                judul = "Pesanan Aktif",
                nilai = pesananAktif.size.toString(),
                modifier = Modifier.weight(1f)
            )

            KartuRingkasan(
                judul = "Sudah Selesai",
                nilai = jumlahSelesai.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pendapatan pesanan selesai",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Rp${"%,d".format(pendapatanSelesai)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider()

        // FORM PESANAN
        Text(
            text = "Buat Pesanan Baru",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama pelanggan") },
            placeholder = { Text("Masukkan nama pelanggan") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = hp,
            onValueChange = { hp = it },
            label = { Text("Nomor HP") },
            placeholder = { Text("Contoh: 081234567890") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = beratText,
            onValueChange = { beratText = it },
            label = { Text("Berat cucian (kg)") },
            placeholder = { Text("Contoh: 3") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "Pilih Layanan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        val pilihanLayanan = listOf(
            Triple("Reguler", "3 hari", "Rp6.000/kg"),
            Triple("Kilat", "1 hari", "Rp10.000/kg"),
            Triple("Setrika saja", "2 hari", "Rp4.000/kg")
        )

        pilihanLayanan.forEach { pilihan ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (layanan == pilihan.first) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                ),
                onClick = { layanan = pilihan.first }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = layanan == pilihan.first,
                        onClick = { layanan = pilihan.first }
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = pilihan.first,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${pilihan.second} • ${pilihan.third}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = pewangi,
                    onCheckedChange = { pewangi = it }
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pewangi Premium",
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Tambahan Rp1.000/kg",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // ESTIMASI TAGIHAN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Estimasi Tagihan",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Berat cucian")

                    Text(
                        if (berat != null && validasiBerat(berat)) {
                            "${beratTagihan(berat)} kg"
                        } else {
                            "—"
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Lama pengerjaan")
                    Text("${lamaPengerjaan(layanan)} hari")
                }

                HorizontalDivider()

                Text(
                    text = "Rp${"%,d".format(total)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Minimum berat 2 kg. Diskon Rp5.000 untuk subtotal minimal Rp50.000.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // SIMPAN PESANAN
        Button(
            onClick = {
                pesan = when {
                    nama.isBlank() ->
                        "Nama pelanggan wajib diisi."

                    !validasiNomorHp(hp.trim()) ->
                        "Nomor HP harus diawali 08 dan terdiri dari 10–13 angka."

                    berat == null || !validasiBerat(berat) ->
                        "Berat harus lebih dari 0 dan maksimal 100 kg."

                    else -> {
                        val pesananBaru = PesananLaundry(
                            idPesanan = System.currentTimeMillis(),
                            NamaPelanggan = nama.trim(),
                            NomorHp = hp.trim().toInt(),
                            Berat = berat,
                            Layanan = layanan,
                            PewangiPremium = pewangi,
                            total = total,
                            lamaPengerjaan = lamaPengerjaan(layanan)
                        )

                        daftarPesanan.add(pesananBaru)

                        nama = ""
                        hp = ""
                        beratText = ""
                        layanan = "Reguler"
                        pewangi = false

                        "Pesanan berhasil disimpan!"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = "＋  Simpan Pesanan",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (pesan.isNotEmpty()) {
            Text(
                text = pesan,
                color = if (pesan.startsWith("Pesanan berhasil")) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        HorizontalDivider()

        // DAFTAR PESANAN AKTIF
        Text(
            text = "Pesanan Aktif (${pesananAktif.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Pesanan yang belum selesai ditampilkan di bawah ini.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (pesananAktif.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Semua pesanan beres!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Pesanan baru akan muncul di sini.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            pesananAktif.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.NamaPelanggan,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = item.NomorHp.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "Diproses",
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            KolomDetail("Berat", "${item.Berat} kg")
                            KolomDetail("Layanan", item.Layanan)
                            KolomDetail(
                                "Durasi",
                                "${item.lamaPengerjaan} hari"
                            )
                        }

                        if (item.PewangiPremium) {
                            Text(
                                text = "✓ Pewangi Premium",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "Total Tagihan",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Rp${"%,d".format(item.total)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Button(
                            onClick = {
                                val index = daftarPesanan.indexOfFirst {
                                    it.idPesanan == item.idPesanan
                                }

                                if (index >= 0) {
                                    daftarPesanan[index] =
                                        daftarPesanan[index].copy(
                                            selesai = true
                                        )
                                }

                                pesan =
                                    "Pesanan ${item.NamaPelanggan} selesai."
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("✓  Tandai Selesai")
                        }
                    }
                }
            }
        }

        // RIWAYAT SINGKAT
        if (jumlahSelesai > 0) {
            HorizontalDivider()

            Text(
                text = "Riwayat Selesai ($jumlahSelesai)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            daftarPesanan.filter { it.selesai }.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.NamaPelanggan,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "${item.Layanan} • ${item.Berat} kg",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Rp${"%,d".format(item.total)}",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Selesai ✓",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun KartuRingkasan(
    judul: String,
    nilai: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = judul,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = nilai,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun KolomDetail(
    judul: String,
    nilai: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = judul,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = nilai,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
