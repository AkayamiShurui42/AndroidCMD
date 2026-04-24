package com.example.cmdshizulu.shizuku

import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuManager {
    fun isShizukuAvailable(): Boolean {
        return Shizuku.pingBinder()
    }

    fun hasShizukuPermission(): Boolean {
        return if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            false
        } else {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestShizukuPermission(requestCode: Int) {
        if (!hasShizukuPermission()) {
            Shizuku.requestPermission(requestCode)
        }
    }

    suspend fun executeCommand(command: String): String = withContext(Dispatchers.IO) {
        if (!isShizukuAvailable()) {
            return@withContext "Error: Shizuku is not running or available."
        }

        if (!hasShizukuPermission()) {
            return@withContext "Error: Shizuku permission not granted."
        }

        return@withContext try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))

            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            while (errorReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            val exitCode = process.waitFor()
            output.append("\n[Process exited with code $exitCode]")

            output.toString().trim()
        } catch (e: Exception) {
            "Error executing command:\n${e.message}"
        }
    }

    // Fallback Local ADB (tcpip) if Shizuku isn't available
    suspend fun executeLocalAdbCommand(command: String): String = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))

            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            while (errorReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            val exitCode = process.waitFor()
            output.append("\n[Process exited with code $exitCode]")

            output.toString().trim()
        } catch (e: Exception) {
             "Error executing local command:\n${e.message}"
        }
    }
}
