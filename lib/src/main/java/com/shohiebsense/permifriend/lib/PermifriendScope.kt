package com.shohiebsense.permifriend.lib

import androidx.compose.runtime.MutableState


interface PermifriendScope {
    val isPermissionRequestButtonClicked: MutableState<Boolean>
    val isPermissionDialogShowing: MutableState<Boolean>
    fun onPermissionGranted()
    fun showRequestPermissionRationaleDialog()
}