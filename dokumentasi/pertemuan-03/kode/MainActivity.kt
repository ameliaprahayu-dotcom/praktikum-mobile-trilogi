package com.example.trilogi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trilogi.ui.theme.TrilogiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrilogiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UILayoutDemoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun UILayoutDemoScreen(modifier: Modifier = Modifier) {
    var nameInput by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var prodi by rememberSaveable { mutableStateOf("") }

    var submittedName by rememberSaveable { mutableStateOf("") }
    var submittedEmail by rememberSaveable { mutableStateOf("") }
    var submittedProdi by rememberSaveable { mutableStateOf("") }

    var namaError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var prodiError by remember { mutableStateOf<String?>(null) }

    // string diambil DI LUAR onClick (stringResource hanya boleh di composable)
    val msgWajib = stringResource(R.string.error_wajib)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        Text(
            text = stringResource(R.string.header_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider()

        // 2. Box berisi Row { Image + Text }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = stringResource(R.string.box_text),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 3. Row SpaceBetween berisi 3 BadgeCard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BadgeCard(stringResource(R.string.badge_android), Color(0xFFC8E6C9))
            BadgeCard(stringResource(R.string.badge_kotlin), Color(0xFFD1C4E9))
            BadgeCard(stringResource(R.string.badge_compose), Color(0xFFBBDEFB))
        }

        // 4. Card form: Nama, Email, Prodi + validasi wajib isi
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.card_title),
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it; namaError = null },
                    label = { Text(stringResource(R.string.label_nama)) },
                    isError = namaError != null,
                    supportingText = { namaError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    label = { Text(stringResource(R.string.label_email)) },
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = prodi,
                    onValueChange = { prodi = it; prodiError = null },
                    label = { Text(stringResource(R.string.label_prodi)) },
                    isError = prodiError != null,
                    supportingText = { prodiError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        var valid = true
                        if (nameInput.isBlank()) { namaError = msgWajib; valid = false }
                        if (email.isBlank()) { emailError = msgWajib; valid = false }
                        if (prodi.isBlank()) { prodiError = msgWajib; valid = false }
                        if (valid) {
                            submittedName = nameInput
                            submittedEmail = email
                            submittedProdi = prodi
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.btn_tampilkan))
                }

                if (submittedName.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.sapaan, submittedName),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.hasil_detail, submittedEmail, submittedProdi)
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeCard(title: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UILayoutDemoPreview() {
    TrilogiTheme {
        UILayoutDemoScreen()
    }
}