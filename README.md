# Permifriend

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.shohiebsense/permifriend/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.shohiebsense/permifriend) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Power-up your [Accompanist](https://google.github.io/accompanist/permissions/) Permission by handling runtime permission event. It complements with [HiltViewModel](https://developer.android.com/training/dependency-injection/hilt-jetpack#viewmodels).  It will save you from the tedious job around Runtime Permissions.


## Requirements

1. `minSdk 24`  
2. [Accompanist permission library](https://google.github.io/accompanist/permissions/)

## Installation
```build.gradle
dependencies {
    implementation 'io.github.shohiebsense:permifriend:0.1.0'
}
```

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

## License
```
Copyright (C) 2022 Shohieb Nasruddin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
