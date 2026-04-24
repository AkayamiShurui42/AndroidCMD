package com.example.cmdshizulu

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.cmdshizulu.shizuku.ShizukuManager
import com.example.cmdshizulu.ui.MainScreen
import com.example.cmdshizulu.ui.theme.CMDShizuluTheme
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {

    private val shizukuPermissionRequestCode = 1

    private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == shizukuPermissionRequestCode) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Shizuku Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Shizuku Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shizuku.addRequestPermissionResultListener(permissionListener)

        if (ShizukuManager.isShizukuAvailable() && !ShizukuManager.hasShizukuPermission()) {
            ShizukuManager.requestShizukuPermission(shizukuPermissionRequestCode)
        }

        setContent {
            CMDShizuluTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var output by remember { mutableStateOf("") }
                    var isExecuting by remember { mutableStateOf(false) }

                    MainScreen(
                        output = output,
                        isExecuting = isExecuting,
                        onExecuteCommand = { cmd ->
                            isExecuting = true
                            lifecycleScope.launch {
                                // Try Shizuku first, fallback to local execution
                                val result = if (ShizukuManager.isShizukuAvailable() && ShizukuManager.hasShizukuPermission()) {
                                    ShizukuManager.executeCommand(cmd)
                                } else {
                                    output = "Shizuku not available/permitted. Attempting local execution...\n"
                                    ShizukuManager.executeLocalAdbCommand(cmd)
                                }
                                output += result
                                isExecuting = false
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(permissionListener)
    }
}
