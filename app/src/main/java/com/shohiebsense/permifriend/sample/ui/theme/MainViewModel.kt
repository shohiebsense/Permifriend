package com.shohiebsense.permifriend.sample.ui.theme

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.shohiebsense.permifriend.lib.PermifriendScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(), PermifriendScope  {

    override val isPermissionRequestButtonClicked = mutableStateOf(false)
    override val isPermissionDialogShowing = mutableStateOf(false)

    val shouldShowNavigateToSettingDialog = mutableStateOf(false)

    override fun onPermissionGranted() {
        shouldShowNavigateToSettingDialog.value = false
    }

    override fun showRequestPermissionRationaleDialog() {
        shouldShowNavigateToSettingDialog.value = true
    }

}