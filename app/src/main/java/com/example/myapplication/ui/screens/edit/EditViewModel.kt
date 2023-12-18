package com.example.myapplication.ui.screens.edit

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.MAIN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File


class EditViewModel(app: Application): AndroidViewModel(app) {
    private lateinit var uri: Uri

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val _navigateToImageScreen = MutableStateFlow(false)
    val navigateToImageScreen: Flow<Boolean>
        get() = _navigateToImageScreen

    fun initUiState(uri: Uri, editUiState: EditUiState) {
        this.uri = uri
        _uiState.value = editUiState
    }

    fun onDateChange(value: String) {
        _uiState.value = _uiState.value.copy(date = value)
    }

    fun onDeviceChange(value: String) {
        _uiState.value = _uiState.value.copy(device = value)
    }

    fun onModelChange(value: String) {
        _uiState.value = _uiState.value.copy(model = value)
    }

    fun onLatitudeChange(value: String) {
        _uiState.value = _uiState.value.copy(latitude = value)
    }

    fun onLongitudeChange(value: String) {
        _uiState.value = _uiState.value.copy(longitude = value)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun save(contentResolver: ContentResolver) {
        val imageFile = FileUtil.getPath(MAIN.applicationContext, this.uri)?.let { File(it) }
        try {
            imageFile?.let {
                val exif = ExifInterface(imageFile)

                exif.setAttribute(ExifInterface.TAG_DATETIME, _uiState.value.date)
                exif.setAttribute(ExifInterface.TAG_MAKE, _uiState.value.device)
                exif.setAttribute(ExifInterface.TAG_MODEL, _uiState.value.model)
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, _uiState.value.latitude)
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, _uiState.value.longitude)

                exif.saveAttributes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EditViewModel", "Error saving image: ${e.message}")
        }
        _navigateToImageScreen.value = true
    }
}

object FileUtil {
    fun getPath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path
    }
}

data class EditUiState(
    val date: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val device: String = "",
    val model: String = "",
)