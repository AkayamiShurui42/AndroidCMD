package com.example.cmdshizulu.data

data class AdbCommandCategory(
    val name: String,
    val commands: List<AdbCommand>
)

data class AdbCommand(
    val name: String,
    val description: String,
    val commandTemplate: String,
    val requiresArgument: Boolean = false,
    val argumentHint: String = ""
)

object CommandRepository {
    val categories = listOf(
        AdbCommandCategory(
            "Package Manager (pm)",
            listOf(
                AdbCommand("List Packages", "Lists all installed packages", "pm list packages"),
                AdbCommand("List System Packages", "Lists system packages", "pm list packages -s"),
                AdbCommand("List 3rd Party Packages", "Lists third-party packages", "pm list packages -3"),
                AdbCommand("Clear App Data", "Clears data of a specific app", "pm clear %s", true, "package.name"),
                AdbCommand("Disable App", "Disables a package", "pm disable-user --user 0 %s", true, "package.name"),
                AdbCommand("Enable App", "Enables a package", "pm enable %s", true, "package.name")
            )
        ),
        AdbCommandCategory(
            "Window Manager (wm)",
            listOf(
                AdbCommand("Screen Size", "Shows the physical size of the display", "wm size"),
                AdbCommand("Screen Density", "Shows the physical density of the display", "wm density"),
                AdbCommand("Change Density", "Changes the display density", "wm density %s", true, "e.g., 320"),
                AdbCommand("Reset Density", "Resets density to default", "wm density reset")
            )
        ),
        AdbCommandCategory(
            "Device Information",
            listOf(
                AdbCommand("Android Version", "Gets the Android release version", "getprop ro.build.version.release"),
                AdbCommand("Device Model", "Gets the device model", "getprop ro.product.model"),
                AdbCommand("Battery Info", "Dumps battery information", "dumpsys battery"),
                AdbCommand("Uptime", "Shows system uptime", "uptime")
            )
        ),
        AdbCommandCategory(
            "Settings",
            listOf(
                AdbCommand("Put Global Setting", "Sets a global setting", "settings put global %s", true, "key value"),
                AdbCommand("Put System Setting", "Sets a system setting", "settings put system %s", true, "key value"),
                AdbCommand("Put Secure Setting", "Sets a secure setting", "settings put secure %s", true, "key value")
            )
        )
    )
}
