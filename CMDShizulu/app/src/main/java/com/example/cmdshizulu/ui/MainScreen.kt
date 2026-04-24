package com.example.cmdshizulu.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cmdshizulu.data.AdbCommand
import com.example.cmdshizulu.data.CommandRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    output: String,
    isExecuting: Boolean,
    onExecuteCommand: (String) -> Unit
) {
    var selectedCommand by remember { mutableStateOf<AdbCommand?>(null) }
    var customArgument by remember { mutableStateOf("") }
    var customCommand by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CMDShizulu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Command List / Selector
            Text(
                "Available Commands",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(CommandRepository.categories) { category ->
                    val isExpanded = expandedCategory == category.name
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedCategory = if (isExpanded) null else category.name
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = category.name, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand/Collapse"
                                )
                            }
                            if (isExpanded) {
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    category.commands.forEach { cmd ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedCommand = cmd
                                                    customArgument = ""
                                                    customCommand = cmd.commandTemplate
                                                }
                                                .padding(vertical = 8.dp)
                                        ) {
                                            Column {
                                                Text(cmd.name, fontWeight = FontWeight.SemiBold)
                                                Text(cmd.description, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Command Builder
            Text(
                "Command to Execute",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customCommand,
                onValueChange = { customCommand = it },
                label = { Text("Command") },
                modifier = Modifier.fillMaxWidth()
            )

            if (selectedCommand?.requiresArgument == true) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customArgument,
                    onValueChange = {
                        customArgument = it
                        selectedCommand?.let { cmd ->
                            customCommand = cmd.commandTemplate.replace("%s", it)
                        }
                    },
                    label = { Text(selectedCommand?.argumentHint ?: "Argument") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onExecuteCommand(customCommand) },
                modifier = Modifier.fillMaxWidth(),
                enabled = customCommand.isNotBlank() && !isExecuting
            ) {
                Text(if (isExecuting) "Executing..." else "Execute")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Output Console
            Text(
                "Output",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                SelectionContainer {
                    Text(
                        text = output.ifEmpty { "Command output will appear here..." },
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}
