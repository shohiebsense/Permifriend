package com.shohiebsense.permifriend.lib

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState


@ExperimentalPermissionsApi
private fun PermifriendScope.isOnClickPermissionGranted(
    cameraPermissionState: PermissionState,
    isFirstTimeAccepted: Boolean
): Boolean {
    return isPermissionRequestButtonClicked.value
            && cameraPermissionState.hasPermission
            || isFirstTimeAccepted
}

@ExperimentalPermissionsApi
private fun PermifriendScope.isOnClickPermissionDenied(
    cameraPermissionState: PermissionState
): Boolean {
    return !cameraPermissionState.hasPermission
            && isPermissionRequestButtonClicked.value
}


@ExperimentalPermissionsApi
private fun PermissionState.isRequestedButNotShowing(
    isClicked: Boolean,
    isPermissionDialogShowing: Boolean
): Boolean {
    return isClicked && permissionRequested && !shouldShowRationale && !hasPermission && !isPermissionDialogShowing
}

@ExperimentalPermissionsApi
private fun PermissionState.shouldRequestPermissionForFirstTime(
    isPermissionDialogShowing: Boolean
): Boolean {
    return permissionRequested && shouldShowRationale && !hasPermission && !isPermissionDialogShowing
}


@ExperimentalPermissionsApi
private fun PermissionState.shouldRequestPermissionAfterDenied(): Boolean {
    return permissionRequested && !shouldShowRationale && !hasPermission
}

@ExperimentalPermissionsApi
private fun PermissionState.isPermissionFirstTimeGranted(
    isPermissionDialogShowing: Boolean
): Boolean {
    return hasPermission && !shouldShowRationale && permissionRequested && !isPermissionDialogShowing
}


@ExperimentalPermissionsApi
fun PermissionState.handleDenyPermission(
    permifriendScope: PermifriendScope,
    isPermissionDialogShowing: MutableState<Boolean>,
    onRationaleShowing: () -> Unit,
    onGranted: () -> Unit,
) {

    if (permifriendScope.isPermissionRequestButtonClicked.value) {
        when {
            shouldRequestPermissionForFirstTime(isPermissionDialogShowing.value) -> {
                onRationaleShowing()
                permifriendScope.isPermissionRequestButtonClicked.value = false
                isPermissionDialogShowing.value = true
            }
            shouldRequestPermissionAfterDenied() -> {
                onRationaleShowing()
                permifriendScope.isPermissionRequestButtonClicked.value = false
                isPermissionDialogShowing.value = true
            }
            isPermissionFirstTimeGranted(isPermissionDialogShowing.value) -> {
                onGranted()
            }
        }
    } else if (isRequestedButNotShowing(
            permifriendScope.isPermissionRequestButtonClicked.value,
            isPermissionDialogShowing.value
        )
    ) {
        onRationaleShowing()
        isPermissionDialogShowing.value = true
    }
}

@ExperimentalPermissionsApi
@Composable
fun HandleRuntimePermission(
    permifriendScope: PermifriendScope,
    cameraPermissionState: PermissionState,
) {

    if (!permifriendScope.isPermissionRequestButtonClicked.value) return

    val isFirstTimeAccepted = remember { mutableStateOf(false) }
    val ocurrenceNumber = remember { mutableStateOf(0)}

    HandleOnStopLifeCycle {
        if (permifriendScope.isPermissionRequestButtonClicked.value) {
            permifriendScope.isPermissionRequestButtonClicked.value = false
        }
    }

    if (permifriendScope.isOnClickPermissionGranted(
            cameraPermissionState,
            isFirstTimeAccepted.value
        )
    ) {
        LaunchedEffect(
            permifriendScope.isPermissionRequestButtonClicked.value,
            cameraPermissionState.hasPermission
                    || isFirstTimeAccepted.value
        ) {
            permifriendScope.onPermissionGranted()
        }
    }


    if (permifriendScope.isOnClickPermissionDenied(cameraPermissionState)) {
        LaunchedEffect(
            !cameraPermissionState.hasPermission,
            permifriendScope.isPermissionRequestButtonClicked.value
        ) {
            ocurrenceNumber.value++

            cameraPermissionState.launchPermissionRequest()

            if (!cameraPermissionState.hasPermission
                && cameraPermissionState.permissionRequested
                && !permifriendScope.isPermissionDialogShowing.value
            ) {
                return@LaunchedEffect
            }

            if (cameraPermissionState.hasPermission
                && cameraPermissionState.permissionRequested
                && !permifriendScope.isPermissionDialogShowing.value
            ) {
                permifriendScope.isPermissionRequestButtonClicked.value = false
                permifriendScope.onPermissionGranted()
            }


        }
    }

    cameraPermissionState.handleDenyPermission(
        permifriendScope = permifriendScope,
        isPermissionDialogShowing = permifriendScope.isPermissionDialogShowing,
        onRationaleShowing = {
            isFirstTimeAccepted.value = false
            permifriendScope.showRequestPermissionRationaleDialog()
        }
    ) {
        isFirstTimeAccepted.value = true
    }
}

@Composable
fun HandleOnStopLifeCycle(onStopOrDestroy: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY || event == Lifecycle.Event.ON_STOP) {
                onStopOrDestroy()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
