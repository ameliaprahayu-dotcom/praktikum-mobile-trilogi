package com.example.trilogi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trilogi.ui.theme.TrilogiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrilogiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BukuCatatanScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Layar berstatus: menghubungkan UI dengan database Room
@Composable
fun BukuCatatanScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).bukuCatatanDao() }
    val scope = rememberCoroutineScope()
    val daftarCatatan = remember { mutableStateListOf<BukuCatatanEntity>() }

    // READ saat layar pertama tampil -> bukti data tetap ada setelah app di-kill
    LaunchedEffect(Unit) {
        val data = withContext(Dispatchers.IO) { dao.getAllCatatan() }
        daftarCatatan.clear()
        daftarCatatan.addAll(data)
    }

    BukuCatatanContent(
        daftar = daftarCatatan,
        onSimpan = { judul, kategori, isi ->
            val tanggal = SimpleDateFormat(
                "dd MMM yyyy HH:mm",
                Locale.forLanguageTag("id-ID")
            ).format(Date())
            val dataBaru = BukuCatatanEntity(
                judul = judul.trim(),
                isi = isi.trim(),
                kategori = kategori.trim(),
                tanggalDibuat = tanggal
            )
            scope.launch(Dispatchers.IO) { // background thread
                dao.insertCatatan(dataBaru)
                val listData = dao.getAllCatatan()
                withContext(Dispatchers.Main) { // kembali ke UI thread
                    daftarCatatan.clear()
                    daftarCatatan.addAll(listData)
                }
            }
        },
        modifier = modifier
    )
}

// Layar UI murni (tanpa database) supaya bisa tampil di Preview
@Composable
fun BukuCatatanContent(
    daftar: List<BukuCatatanEntity>,
    onSimpan: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var judul by rememberSaveable { mutableStateOf("") }
    var kategori by rememberSaveable { mutableStateOf("") }
    var isi by rememberSaveable { mutableStateOf("") }

    var judulError by remember { mutableStateOf<String?>(null) }
    var kategoriError by remember { mutableStateOf<String?>(null) }
    var isiError by remember { mutableStateOf<String?>(null) }

    val msgWajib = "Wajib diisi!"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Buku Catatan", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = judul,
            onValueChange = { judul = it; judulError = null },
            label = { Text("Judul") },
            isError = judulError != null,
            supportingText = { judulError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = kategori,
            onValueChange = { kategori = it; kategoriError = null },
            label = { Text("Kategori") },
            isError = kategoriError != null,
            supportingText = { kategoriError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = isi,
            onValueChange = { isi = it; isiError = null },
            label = { Text("Isi Catatan") },
            isError = isiError != null,
            supportingText = { isiError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Button(
            onClick = {
                var valid = true
                if (judul.isBlank()) { judulError = msgWajib; valid = false }
                if (kategori.isBlank()) { kategoriError = msgWajib; valid = false }
                if (isi.isBlank()) { isiError = msgWajib; valid = false }
                if (valid) {
                    onSimpan(judul, kategori, isi)
                    judul = ""; kategori = ""; isi = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Simpan Catatan")
        }

        HorizontalDivider()

        Text(
            text = "Daftar Catatan (${daftar.size})",
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(daftar, key = { it.id }) { catatan ->
                CatatanCard(catatan)
            }
        }
    }
}

@Composable
fun CatatanCard(catatan: BukuCatatanEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = catatan.judul, fontWeight = FontWeight.Bold)
            Text(
                text = "${catatan.kategori} \u2022 ${catatan.tanggalDibuat}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = catatan.isi)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BukuCatatanPreview() {
    TrilogiTheme {
        BukuCatatanContent(
            daftar = listOf(
                BukuCatatanEntity(
                    id = 2, judul = "Tugas Mobile", isi = "Kerjakan latihan Room Database",
                    kategori = "Kuliah", tanggalDibuat = "09 Nov 2026 15:00"
                ),
                BukuCatatanEntity(
                    id = 1, judul = "Belanja", isi = "Beli kuota internet",
                    kategori = "Pribadi", tanggalDibuat = "09 Nov 2026 14:30"
                )
            ),
            onSimpan = { _, _, _ -> }
        )
    }
}