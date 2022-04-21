# Permifriend
Power-up your Accompanist Permission by handling runtime permission event. It complements with `HiltViewModel`.  It will save you from the tedious job regarding Runtime Permissions.

## Installation

## Usage

1. Implement your  `hiltViewModel` with `PermifriendScope`

```MainViewModel.kt
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(), PermifriendScope  {

    override val isPermissionRequestButtonClicked = mutableStateOf(false)
    override val isPermissionDialogShowing = mutableStateOf(false)

    override fun onPermissionGranted() {
        shouldShowNavigateToSettingDialog.value = false
    }

    override fun showRequestPermissionRationaleDialog() {
      //your app's rationale dialog when user denies permission dialog.
    }

}
```

2. Call `HandleRuntimePermission(permissionScope, permissionState)` inside the composable

```MainScreen.kt
@Composable
fun MainScreen(){
    val viewModel =  hiltViewModel<MainViewModel>()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val context = LocalContext.current

    HandleRuntimePermission(permifriendScope = viewModel, cameraPermissionState = cameraPermissionState)
    ...
}
```

3. Trigger the permission request by changing the `isPermissionRequestButtonClicked` value to true

```MainScreen.kt
@Composable
fun MainScreen(){
  ...
  Button(onClick = { viewModel.isPermissionRequestButtonClicked.value = true }) {
    Text("Request permission")
  }
}
```