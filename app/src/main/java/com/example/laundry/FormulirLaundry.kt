
package com.example.laundry

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormulirLaundry(
    onTambahPesanan: (
        String, String, Double, String, Boolean
    ) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var noHP by remember { mutableStateOf("") }
    var BeratInput by remember { mutableStateOf("") }
    var layanan by remember { mutableStateOf("Reguler") }
    var pewangi by remember { mutableStateOf(false) }
    var pesanError by remember { mutableStateOf("") }

    val berat = BeratInput.replace(",", ".").toDoubleOrNull()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama pelanggan") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = noHP,
            onValueChange = { noHP = it },
            label = { Text("Nomor HP") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = BeratInput,
            onValueChange = { BeratInput = it },
            label = { Text("Berat Cucian (kg)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            "Pilih Layanan",
            style = MaterialTheme.typography.titleMedium
        )

        listOf("Reguler", "Kilat", "Setrika saja").forEach { pilihan ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = layanan == pilihan,
                    onClick = { layanan = pilihan }
                )
                Text(pilihan)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = pewangi,
                    onCheckedChange = { pewangi = it }
                )
                Column {
                    Text("Pewangi Premium")
                    Text("Tambahan Rp1.000/kg")
                }
            }
        }

        // ESTIMASI TAGIHAN
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Estimasi Tagihan",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Berat cucian")
                    Text(
                        if (berat != null && validasiBerat(berat))
                            "${maxOf(berat, 2.0)} kg"
                        else "0"
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
                    "Rp${"%,d".format(
                        if (berat != null && validasiBerat(berat))
                            hitungTotal(berat, layanan, pewangi)
                        else 0
                    )}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    "Minimum berat 2 kg. Untuk subtotal minimal Rp50.000, mendapat diskon Rp5.000.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (pesanError.isNotEmpty()) {
            Text(
                pesanError,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                pesanError = when {
                    nama.isBlank() ->
                        "Nama pelanggan wajib diisi."
                    !validasiNomorHp(noHP.trim()) ->
                        "Nomor HP harus diawali 08 dan terdiri dari 10–13 angka."
                    berat == null || !validasiBerat(berat) ->
                        "Berat harus lebih dari 0 dan maksimal 100 kg."
                    else -> ""
                }

                if (pesanError.isEmpty()) {
                    onTambahPesanan(
                        nama.trim(),
                        noHP.trim(),
                        berat!!,
                        layanan,
                        pewangi
                    )

                    nama = ""
                    noHP = ""
                    BeratInput = ""
                    layanan = "Reguler"
                    pewangi = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("＋  Simpan Pesanan")
        }
    }
}
