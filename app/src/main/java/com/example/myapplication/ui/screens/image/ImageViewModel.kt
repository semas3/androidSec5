package com.example.myapplication.ui.screens.image

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

class ImageViewModel(app: Application): AndroidViewModel(app) {
    private var _imageUri: MutableState<Uri?> = mutableStateOf(null)
    var imageUri: State<Uri?> = _imageUri
    lateinit var encodedUri: String

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }
}