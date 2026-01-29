package com.debbiedoesit.debbieai.estimates.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.debbiedoesit.debbieai.estimates.data.local.*
import com.debbiedoesit.debbieai.estimates.viewmodel.EstimateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEstimateScreen(viewModel: EstimateViewModel, onBackClick: () -> Unit, onCreated: (Long) -> Unit, jobId: Long? = null, contactId: Long? = null) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf("50% deposit required. Balance due upon completion.") }
    var notes by remember { mutableStateOf("") }
    var validDays by remember { mutableStateOf("30") }
    var taxRate by remember { mutableStateOf("6.0") }
    var depositPercent by remember { mutableStateOf("50") }
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.lastCreatedId) { uiState.lastCreatedId?.let { onCreated(it) } }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Estimate") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.Close, "Cancel") } },
                actions = { TextButton(onClick = { if (title.isNotBlank()) viewModel.createEstimate(title = title, jobId = jobId, contactId = contactId, description = description) }, enabled = title.isNotBlank() && !uiState.isLoading) { Text("Create") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 4)
            
            Text("Settings", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = validDays, onValueChange = { validDays = it.filter { c -> c.isDigit() } }, label = { Text("Valid Days") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = taxRate, onValueChange = { taxRate = it }, label = { Text("Tax Rate %") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = depositPercent, onValueChange = { depositPercent = it.filter { c -> c.isDigit() } }, label = { Text("Deposit %") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
            
            OutlinedTextField(value = terms, onValueChange = { terms = it }, label = { Text("Terms & Conditions") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Internal Notes") }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 4)
            
            Button(onClick = { if (title.isNotBlank()) viewModel.createEstimate(title = title, jobId = jobId, contactId = contactId, description = description) }, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank() && !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("Create Estimate")
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLineItemScreen(estimateId: Long, viewModel: EstimateViewModel, onBackClick: () -> Unit) {
    var description by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("each") }
    var unitPrice by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(LineItemCategory.LABOR) }
    var isTaxable by remember { mutableStateOf(true) }
    var isOptional by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Line Item") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.Close, "Cancel") } },
                actions = {
                    TextButton(onClick = {
                        val qty = quantity.toDoubleOrNull() ?: 1.0
                        val price = unitPrice.toDoubleOrNull() ?: 0.0
                        viewModel.addLineItem(estimateId, category, description, qty, unit, price, isTaxable, isOptional)
                        onBackClick()
                    }, enabled = description.isNotBlank()) { Text("Add") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                OutlinedTextField(value = category.displayName, onValueChange = {}, readOnly = true, label = { Text("Category") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    LineItemCategory.entries.forEach { cat -> DropdownMenuItem(text = { Text(cat.displayName) }, onClick = { category = cat; categoryExpanded = false }) }
                }
            }
            
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = details, onValueChange = { details = it }, label = { Text("Details") }, modifier = Modifier.fillMaxWidth().height(80.dp), maxLines = 3)
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Qty") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(value = unitPrice, onValueChange = { unitPrice = it }, label = { Text("Price") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, prefix = { Text("$") })
            }
            
            val qty = quantity.toDoubleOrNull() ?: 0.0
            val price = unitPrice.toDoubleOrNull() ?: 0.0
            if (qty > 0 && price > 0) {
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Line Total", style = MaterialTheme.typography.titleSmall)
                        Text("$${String.format("%,.2f", qty * price)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Checkbox(checked = isTaxable, onCheckedChange = { isTaxable = it }); Text("Taxable") }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Checkbox(checked = isOptional, onCheckedChange = { isOptional = it }); Text("Optional") }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}