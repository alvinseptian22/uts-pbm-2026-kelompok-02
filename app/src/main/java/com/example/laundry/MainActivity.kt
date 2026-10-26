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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
    var namaDisentuh by remember { mutableStateOf(false) }
    var hpDisentuh by remember { mutableStateOf(false) }
    var beratDisentuh by remember { mutableStateOf(false) }
    var layanan by remember { mutableStateOf("Reguler") }
    var pewangi by remember { mutableStateOf(false) }
    var pesan by remember { mutableStateOf("") }
    var pesanSukses by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val formatPesananSelesai = stringResource(R.string.pesanan_selesai)
    val pesanNamaWajib = stringResource(R.string.nama_wajib)
    val pesanHpTidakValid = stringResource(R.string.nomor_hp_tidak_valid)
    val pesanBeratTidakValid = stringResource(R.string.berat_tidak_valid)
    val pesanPesananBerhasil = stringResource(R.string.pesanan_berhasil)
    val formatPesananDihapus = stringResource(R.string.pesanan_dihapus)
    val formatRiwayatDihapus = stringResource(R.string.riwayat_dihapus)

    var daftarPesanan by remember {
        mutableStateOf(muatDaftarPesanan(context))
    }

    val berat = beratText.replace(",", ".").toDoubleOrNull()


    val namaValid = nama.isNotBlank()
    val hpValid = validasiNomorHp(hp.trim())
    val beratValid = berat != null && validasiBerat(berat)

    val formValid = namaValid && hpValid && beratValid


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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER APLIKASI

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_laundry),
                        contentDescription = stringResource(R.string.deskripsi_logo_laundry),
                        modifier = Modifier.size(72.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.label_brand),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Text(
                            text = stringResource(R.string.judul_header),
                            style = MaterialTheme.typography.headlineSmall,
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
            }
        }

        // RINGKASAN
        Text(
            text = stringResource(R.string.ringkasan),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KartuRingkasan(
                stringResource(R.string.pesanan_aktif),
                nilai = pesananAktif.size.toString(),
                modifier = Modifier.weight(1f)
            )

            KartuRingkasan(
                judul = stringResource(R.string.sudah_selesai),
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
                    text = stringResource(R.string.pendapatan_selesai),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(
                        R.string.format_rupiah,
                        "%,d".format(pendapatanSelesai)
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider()

        // FORM PESANAN
        Text(
            text = stringResource(R.string.buat_pesanan_baru),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )


        OutlinedTextField(
            value = nama,
            onValueChange = {
                nama = it
                namaDisentuh = true
            },
            label = { Text(stringResource(R.string.nama_pelanggan)) },
            placeholder = { Text(stringResource(R.string.masukkan_nama)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = namaDisentuh && !namaValid,
            supportingText = {
                if (namaDisentuh && !namaValid) {
                    Text(stringResource(R.string.nama_wajib))
                }
            }
        )


        OutlinedTextField(
            value = hp,
            onValueChange = {
                hp = it
                hpDisentuh = true
            },
            label = { Text(stringResource(R.string.nomor_hp)) },
            placeholder = { Text(stringResource(R.string.contoh_nomor_hp)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = hpDisentuh && !hpValid,
            supportingText = {
                if (hpDisentuh && !hpValid) {
                    Text(stringResource(R.string.nomor_hp_tidak_valid))
                }
            }
        )

        OutlinedTextField(
            value = beratText,
            onValueChange = {
                beratText = it
                beratDisentuh = true
            },
            label = { Text(stringResource(R.string.berat_cucian)) },
            placeholder = { Text(stringResource(R.string.contoh_berat)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = beratDisentuh && !beratValid,
            supportingText = {
                if (beratDisentuh && !beratValid) {
                    Text(stringResource(R.string.berat_tidak_valid))
                }
            }
        )

        Text(
            text = stringResource(R.string.pilih_layanan),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        val pilihanLayanan = listOf(
            Triple(
                stringResource(R.string.reguler),
                stringResource(R.string.durasi_reguler),
                stringResource(R.string.tarif_reguler)
            ),
            Triple(
                stringResource(R.string.kilat),
                stringResource(R.string.durasi_kilat),
                stringResource(R.string.tarif_kilat)
            ),
            Triple(
                stringResource(R.string.setrika_saja),
                stringResource(R.string.durasi_setrika),
                stringResource(R.string.tarif_setrika)
            )
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
                        .padding(horizontal = 12.dp, vertical = 8.dp),
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
                        text = stringResource(R.string.pewangi_premium),
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = stringResource(R.string.tambahan_pewangi),
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
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.estimasi_tagihan),
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.detail_berat))

                    Text(
                        text = if (berat != null && validasiBerat(berat)) {
                            stringResource(
                                R.string.format_berat_kg,
                                beratTagihan(berat)
                            )
                        } else {
                            stringResource(R.string.belum_tersedia)
                        }
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.lama_pengerjaan))

                    Text(
                        text = stringResource(
                            R.string.format_durasi,
                            lamaPengerjaan(layanan)
                        )
                    )
                }

                HorizontalDivider()


                Text(
                    text = stringResource(
                        R.string.format_rupiah,
                        "%,d".format(total)
                    ),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(R.string.minimum_berat_diskon),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // SIMPAN PESANAN
        Button(
            onClick = {
                pesanSukses = false

                pesan = when {
                    nama.isBlank() ->
                        pesanNamaWajib

                    !validasiNomorHp(hp.trim()) ->
                        pesanHpTidakValid

                    berat == null || !validasiBerat(berat) ->
                        pesanBeratTidakValid

                    else -> {
                        val pesananBaru = PesananLaundry(
                            idPesanan = System.currentTimeMillis(),
                            NamaPelanggan = nama.trim(),
                            NomorHp = hp.trim(),
                            Berat = berat,
                            Layanan = layanan,
                            PewangiPremium = pewangi,
                            total = total,
                            lamaPengerjaan = lamaPengerjaan(layanan)
                        )

                        daftarPesanan = daftarPesanan + pesananBaru
                        simpanDaftarPesanan(context, daftarPesanan)

                        pesanSukses = true

                        nama = ""
                        hp = ""
                        beratText = ""
                        layanan = "Reguler"
                        pewangi = false

                        namaDisentuh = false
                        hpDisentuh = false
                        beratDisentuh = false

                        pesanPesananBerhasil
                    }
                }
            },
            enabled = formValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = stringResource(R.string.simpan_pesanan),
                style = MaterialTheme.typography.titleMedium
            )
        }

            if (pesan.isNotEmpty()) {
                Text(
                    text = pesan,
                    color = if (pesanSukses) {
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
            text = stringResource(R.string.daftar_pesanan_aktif, pesananAktif.size),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.deskripsi_pesanan_aktif),
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
                        text = stringResource(R.string.simbol_centang),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(R.string.semua_pesanan_beres),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.pesanan_baru_muncul),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            pesananAktif.forEach { item ->
                KartuPesanan(
                    nama = item.NamaPelanggan,
                    nomorHp = item.NomorHp,
                    lamaPengerjaan = item.lamaPengerjaan,
                    pewangiPremium = item.PewangiPremium,
                    total = item.total,

                    onSelesai = {
                        daftarPesanan = daftarPesanan.map { pesanan ->
                            if (pesanan.idPesanan == item.idPesanan) {
                                pesanan.copy(selesai = true)
                            } else {
                                pesanan
                            }
                        }

                        simpanDaftarPesanan(context, daftarPesanan)

                        pesanSukses = true
                        pesan = formatPesananSelesai.format(
                            item.NamaPelanggan
                        )
                    },

                    onHapus = {
                        daftarPesanan = daftarPesanan.filterNot {
                            it.idPesanan == item.idPesanan
                        }

                        simpanDaftarPesanan(context, daftarPesanan)

                        pesanSukses = false
                        pesan = formatPesananDihapus.format(
                            item.NamaPelanggan
                        )
                    }
                )
            }
        }

                        // RIWAYAT SINGKAT
        if (jumlahSelesai > 0) {
            HorizontalDivider()

            Text(
                text = stringResource(R.string.riwayat_selesai, jumlahSelesai),
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.NamaPelanggan,
                                fontWeight = FontWeight.SemiBold
                            )


                            Text(
                                text = stringResource(
                                    R.string.format_layanan_berat,
                                    item.Layanan,
                                    item.Berat
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )

                        }


                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = stringResource(
                                    R.string.format_rupiah,
                                    "%,d".format(item.total)
                                ),
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = formatPesananSelesai.format(item.NamaPelanggan),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))


                            Button(
                                onClick = {
                                    daftarPesanan = daftarPesanan.filterNot {
                                        it.idPesanan == item.idPesanan
                                    }

                                    simpanDaftarPesanan(context, daftarPesanan)

                                    pesanSukses = false
                                    pesan = formatRiwayatDihapus.format(item.NamaPelanggan)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                contentPadding = PaddingValues(
                                    horizontal = 12.dp,
                                    vertical = 4.dp
                                )
                            ) {
                                Text(stringResource(R.string.hapus_riwayat))
                            }
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
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


@Composable
private fun KartuPesanan(
    nama: String,
    nomorHp: String,
    lamaPengerjaan: Int,
    pewangiPremium: Boolean,
    total: Int,
    onSelesai: () -> Unit,
    onHapus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = nama,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = nomorHp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.status_diproses),
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            HorizontalDivider()

            KolomDetail(
                judul = stringResource(R.string.label_durasi),
                nilai = stringResource(
                    R.string.format_durasi,
                    lamaPengerjaan
                )
            )

            if (pewangiPremium) {
                Text(
                    text = stringResource(R.string.pewangi_aktif),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = stringResource(R.string.total_tagihan),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(
                    R.string.format_rupiah,
                    "%,d".format(total)
                ),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = onSelesai,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(stringResource(R.string.tandai_selesai))
            }

            Button(
                onClick = onHapus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.hapus_pesanan))
            }
        }
    }
}
