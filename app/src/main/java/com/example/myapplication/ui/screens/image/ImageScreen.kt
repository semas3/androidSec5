package com.example.myapplication.ui.screens.image

import android.app.Activity
import android.content.Intent
import android.media.ExifInterface
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.AppRoutes
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(
    imageViewModel: ImageViewModel = viewModel(),
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val imageUri = imageViewModel.imageUri
        val imageLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    result.data?.data?.let { uri ->
                        imageViewModel.setImageUri(uri)
                        val encodedUri =
                            URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
                        imageViewModel.encodedUri = encodedUri
                    }
                }
            }
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    .addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageLauncher.launch(intent)
            },
            Modifier.padding(top = 10.dp)
        ) {
            Text("Pick the image")
        }
        if (imageUri.value != null) {
            AsyncImage(
                model = imageUri.value,
                contentDescription = null)

            val contentResolver = context.contentResolver

            val inputStream: InputStream? = contentResolver.openInputStream(imageUri.value!!)
            if (inputStream != null) {
                val exif = ExifInterface(inputStream)

                var flag = false

                exif.getAttribute(ExifInterface.TAG_DATETIME)?.let {
                    flag = true
                    Text("Creation date: $it")
                }
                exif.getAttribute(ExifInterface.TAG_MAKE)?.let {
                    flag = true
                    Text("Created on device: $it")
                }
                exif.getAttribute(ExifInterface.TAG_MODEL)?.let {
                    flag = true
                    Text("Device model: $it")
                }
                exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let {
                    flag = true
                    Text("Latitude: $it")
                }
                exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let {
                    flag = true
                    Text("Longitude: $it")
                }

                if (flag) {
                    Button(onClick = {
                        navController.navigate("${AppRoutes.EditScreen.name}/${imageViewModel.encodedUri}")
                    }) {
                        Text(text = "Edit tags")
                    }
                } else {
                    Text("No data to display")
                }
                inputStream.close()
            }
        }
    }
}