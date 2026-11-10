package com.example.trilogi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trilogi.ui.theme.TrilogiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrilogiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BukuCatatanScreen()
                }
            }
        }
    }
}

@Composable
fun BukuCatatanScreen() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).bukuCatatanDao() }
    val scope = rememberCoroutineScope()

    // ---------- STATE FORM ----------
    var judul by rememberSaveable { mutableStateOf("") }
    var kategori by rememberSaveable { mutableStateOf("") }
    var isi by rememberSaveable { mutableStateOf("") }
    var judulError by rememberSaveable { mutableStateOf<String?>(null) }
    var kategoriError by rememberSaveable { mutableStateOf<String?>(null) }
    var isiError by rememberSaveable { mutableStateOf<String?>(null) }

    // null = mode TAMBAH, berisi id = mode EDIT (catatan yang sedang diubah)
    var idEdit by rememberSaveable { mutableStateOf<Int?>(null) }
    // null = dialog tertutup, berisi id = dialog konfirmasi hapus tampil
    var idHapus by rememberSaveable { mutableStateOf<Int?>(null) }

    // ---------- STATE DAFTAR (sumber datanya: Room) ----------
    val daftarCatatan = remember { mutableStateListOf<BukuCatatanEntity>() }

    // READ: baca dari database (IO), isi ke state (Main)
    suspend fun muatUlang() {
        val data = withContext(Dispatchers.IO) { dao.getAllCatatan() }
        daftarCatatan.clear()
        daftarCatatan.addAll(data)
    }

    fun resetForm() {
        judul = ""
        kategori = ""
        isi = ""
        judulError = null
        kategoriError = null
        isiError = null
        idEdit = null
    }

    LaunchedEffect(Unit) { muatUlang() }

    // ---------- DIALOG KONFIRMASI HAPUS ----------
    idHapus?.let { id ->
        val target = daftarCatatan.firstOrNull { it.id == id }
        AlertDialog(
            onDismissRequest = { idHapus = null },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus catatan \"${target?.judul ?: ""}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        idHapus = null
                        scope.launch {
                            withContext(Dispatchers.IO) { dao.deleteById(id) }
                            if (idEdit == id) resetForm()
                            muatUlang()
                            Toast.makeText(context, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Ya, Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { idHapus = null }) { Text("Batal") }
            }
        )
    }

    // ---------- TAMPILAN ----------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (idEdit == null) "Tambah Catatan" else "Edit Catatan",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider()

        OutlinedTextField(
            value = judul,
            onValueChange = { judul = it; judulError = null },
            label = { Text("Judul") },
            isError = judulError != null,
            supportingText = { judulError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = kategori,
            onValueChange = { kategori = it; kategoriError = null },
            label = { Text("Kategori (mis. Kuliah, Pribadi)") },
            isError = kategoriError != null,
            supportingText = { kategoriError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = isi,
            onValueChange = { isi = it; isiError = null },
            label = { Text("Isi Catatan") },
            minLines = 2,
            isError = isiError != null,
            supportingText = { isiError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    // 1) Validasi
                    var isValid = true
                    if (judul.isBlank()) { judulError = "Judul wajib diisi!"; isValid = false }
                    if (kategori.isBlank()) { kategoriError = "Kategori wajib diisi!"; isValid = false }
                    if (isi.isBlank()) { isiError = "Isi catatan wajib diisi!"; isValid = false }
                    if (!isValid) return@Button

                    // 2) Salin nilai ke variabel lokal sebelum pindah thread
                    val sedangEdit = idEdit
                    val judulFinal = judul.trim()
                    val kategoriFinal = kategori.trim()
                    val isiFinal = isi.trim()
                    val tanggalLama = daftarCatatan.firstOrNull { it.id == sedangEdit }?.tanggalDibuat
                    val tanggalBaru = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                        .format(Date())

                    // 3) CREATE atau UPDATE di background thread
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            if (sedangEdit == null) {
                                dao.insertCatatan(
                                    BukuCatatanEntity(
                                        judul = judulFinal,
                                        isi = isiFinal,
                                        kategori = kategoriFinal,
                                        tanggalDibuat = tanggalBaru
                                    )
                                )
                            } else {
                                dao.updateCatatan(
                                    BukuCatatanEntity(
                                        id = sedangEdit, // id sama = Room tahu baris mana yang diubah
                                        judul = judulFinal,
                                        isi = isiFinal,
                                        kategori = kategoriFinal,
                                        tanggalDibuat = tanggalLama ?: tanggalBaru
                                    )
                                )
                            }
                        }
                        // 4) Kembali ke Main thread: refresh daftar + kosongkan form
                        muatUlang()
                        resetForm()
                        Toast.makeText(
                            context,
                            if (sedangEdit == null) "Catatan tersimpan" else "Catatan diperbarui",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (idEdit == null) "Simpan Catatan" else "Perbarui Catatan")
            }

            // Tombol batal hanya muncul saat mode edit
            if (idEdit != null) {
                OutlinedButton(onClick = { resetForm() }) {
                    Text("Batal")
                }
            }
        }

        HorizontalDivider()

        Text(
            text = "Daftar Catatan (${daftarCatatan.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (daftarCatatan.isEmpty()) {
            Text(
                text = "Belum ada catatan",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // LazyColumn = pengganti RecyclerView (hanya merender item yang terlihat)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // key = id membantu Compose mengenali item yang berubah (peran mirip DiffUtil)
                items(daftarCatatan, key = { it.id }) { catatan ->
                    CatatanCard(
                        catatan = catatan,
                        onEdit = {
                            // Isi form dengan data yang dipilih, lalu masuk mode edit
                            judul = catatan.judul
                            kategori = catatan.kategori
                            isi = catatan.isi
                            judulError = null
                            kategoriError = null
                            isiError = null
                            idEdit = catatan.id
                        },
                        onDelete = { idHapus = catatan.id }
                    )
                }
            }
        }
    }
}

// Lambda callback (onEdit, onDelete) = sama seperti onClick: (Mahasiswa) -> Unit di materi
@Composable
fun CatatanCard(
    catatan: BukuCatatanEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = catatan.judul,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "${catatan.kategori} • ${catatan.tanggalDibuat}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = catatan.isi,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// Preview hanya untuk kartu catatan (dengan tombol Edit dan Hapus):
// layar utama memakai Room sehingga tidak bisa dirender di panel Preview.
@Preview(showBackground = true, name = "Preview Kartu Catatan CRUD")
@Composable
fun CatatanCardPreview() {
    TrilogiTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CatatanCard(
                    catatan = BukuCatatanEntity(
                        id = 1,
                        judul = "Tugas Pemrograman Mobile",
                        isi = "Selesaikan latihan Pertemuan 7 dan push ke GitHub.",
                        kategori = "Kuliah",
                        tanggalDibuat = "10 Nov 2026 10:00"
                    ),
                    onEdit = {},
                    onDelete = {}
                )
                CatatanCard(
                    catatan = BukuCatatanEntity(
                        id = 2,
                        judul = "Belanja Bulanan",
                        isi = "Beli buku catatan dan alat tulis.",
                        kategori = "Pribadi",
                        tanggalDibuat = "10 Nov 2026 11:30"
                    ),
                    onEdit = {},
                    onDelete = {}
                )
            }
        }
    }
}