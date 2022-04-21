package com.shohiebsense.permifriend.sample

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.shohiebsense.permifriend.lib.HandleRuntimePermission
import com.shohiebsense.permifriend.sample.ui.theme.MainViewModel
import com.shohiebsense.permifriend.sample.ui.theme.SampleTheme
import com.shohiebsense.permifriend.sample.ui.theme.Shapes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Sample()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Sample() {
    val viewModel =  hiltViewModel<MainViewModel>()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val context = LocalContext.current

    HandleRuntimePermission(permifriendScope = viewModel, cameraPermissionState = cameraPermissionState)

    if(cameraPermissionState.hasPermission && viewModel.shouldShowNavigateToSettingDialog.value)
    LaunchedEffect(
        cameraPermissionState.permission,
        viewModel.shouldShowNavigateToSettingDialog.value
    ){
        viewModel.shouldShowNavigateToSettingDialog.value = false
    }

    if(viewModel.shouldShowNavigateToSettingDialog.value){
        Dialog(
            onDismissRequest = {
                viewModel.shouldShowNavigateToSettingDialog.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .clip(Shapes.medium)
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("should accept permission in order to get this app operates")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                    navigateToAppPermissionSetting(context)
                }) {
                    Text("navigate to app setting")
                }
            }
        }
    }

    Column{
        when {
            cameraPermissionState.hasPermission -> Text("permitted")
            cameraPermissionState.shouldShowRationale -> Text("Rationale")
            else -> Text("Not")
        }
        Spacer(modifier = Modifier.height(8.dp))
        if(!cameraPermissionState.hasPermission)
        Button(onClick = { viewModel.isPermissionRequestButtonClicked.value = true }) {
            Text("Request permission")
        }
    }

}

fun navigateToAppPermissionSetting(context: Context) {
    val packageName = context.packageName
    val activity = context.getActivity()
    activity?.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
    )

}

fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SampleTheme {
        Greeting("Android")
    }
}