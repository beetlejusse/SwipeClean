package com.app.swipeclean.ui.permission

import android.Manifest
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.swipeclean.ui.components.BrutalButton
import com.app.swipeclean.ui.theme.*
import com.google.accompanist.permissions.*
import com.google.androidbrowserhelper.trusted.PermissionStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PermissionScreen(onPermissionGranted: () -> Unit) {
    // Choose the right permission based on Android version
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE
    val permState = rememberPermissionState(permission) { granted ->
        if(granted) onPermissionGranted()
    }

    //Auto-Navigate if already granted
    LaunchedEffect(permState.status) {
        if(permState.status == PermissionStatus.Granted) onPermissionGranted()
    }
    Column(
        modifier = Modifier.fillMaxSize().background(BrutalCream).padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text("GALLERY\nACCESS", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = BrutalBlack)
        Spacer(Modifier.height(16.dp))
        Text(
            "To start swiping, we need access to your photos. Don't worry, we only read and delete photos you choose to swipe away. No data ever leaves your device.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        BrutalButton(
            text = "Grant Permission ->",
            onClick = { permState.launchPermissionRequest() },
            background = BrutalYellow,
            modifier = Modifier.fillMaxWidth()
        )

        if(permState.status is PermissionStatus.Denied && (permState.status as PermissionStatus.Denied).shouldShowRationale){
            Spacer(Modifier.height(12.dp))
            Text(
                "Permission is required to use the app. Please grant access to your photos.",
                style = MaterialTheme.typography.bodyMedium,
                color = BrutalRed
            )
        }
    }
}