package com.debbiedoesit.debbieai.estimates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debbiedoesit.debbieai.estimates.data.local.*
import com.debbiedoesit.debbieai.estimates.data.repository.EstimateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EstimateViewModel(private val repository: EstimateRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EstimateUiState())
    val uiState: StateFlow<EstimateUiState> = _uiState.asStateFlow()
    
    private val _statusFilter = MutableStateFlow<EstimateStatus?>(null)
    val statusFilter: StateFlow<EstimateStatus?> = _statusFilter.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val allEstimates = repository.getAllEstimates().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    val drafts = repository.getDrafts().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    val pending = repository.getPending().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    val accepted = repository.getAccepted().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    val filteredEstimates: StateFlow<List<Estimate>> = combine(
        allEstimates, _statusFilter, _searchQuery
    ) { estimates, status, query ->
        estimates.filter { e ->
            (status == null || e.status == status) &&
            (query.isEmpty() || e.title.contains(query, true) || 
             e.estimateNumber.contains(query, true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _selectedEstimate = MutableStateFlow<Estimate?>(null)
    val selectedEstimate: StateFlow<Estimate?> = _selectedEstimate.asStateFlow()
    
    private val _summary = MutableStateFlow(EstimateSummary())
    val summary: StateFlow<EstimateSummary> = _summary.asStateFlow()
    
    fun loadEstimate(id: Long) {
        viewModelScope.launch {
            _selectedEstimate.value = repository.getById(id)
        }
    }
    
    fun createEstimate(
        title: String,
        contactId: Long? = null,
        jobId: Long? = null,
        description: String = "",
        lineItems: List<LineItem> = emptyList(),
        taxRate: Double = 0.0
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val id = repository.createEstimate(title, contactId, jobId, description, lineItems, taxRate)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastCreatedId = id,
                    message = "Estimate created"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    
    fun updateEstimate(estimate: Estimate) {
        viewModelScope.launch {
            repository.updateEstimate(estimate)
            _uiState.value = _uiState.value.copy(message = "Estimate updated")
        }
    }
    
    fun addLineItem(estimateId: Long, item: LineItem) {
        viewModelScope.launch {
            repository.addLineItem(estimateId, item)
            loadEstimate(estimateId)
        }
    }
    
    fun removeLineItem(estimateId: Long, itemId: String) {
        viewModelScope.launch {
            repository.removeLineItem(estimateId, itemId)
            loadEstimate(estimateId)
        }
    }
    
    fun updateLineItems(estimateId: Long, items: List<LineItem>) {
        viewModelScope.launch {
            repository.updateLineItems(estimateId, items)
            loadEstimate(estimateId)
        }
    }
    
    fun sendEstimate(id: Long) {
        viewModelScope.launch {
            repository.markSent(id)
            _uiState.value = _uiState.value.copy(message = "Estimate sent")
            loadEstimate(id)
        }
    }
    
    fun markViewed(id: Long) {
        viewModelScope.launch { repository.markViewed(id); loadEstimate(id) }
    }
    
    fun acceptEstimate(id: Long) {
        viewModelScope.launch {
            repository.markAccepted(id)
            _uiState.value = _uiState.value.copy(message = "Estimate accepted!")
            loadEstimate(id)
        }
    }
    
    fun declineEstimate(id: Long) {
        viewModelScope.launch {
            repository.markDeclined(id)
            _uiState.value = _uiState.value.copy(message = "Estimate declined")
            loadEstimate(id)
        }
    }
    
    fun duplicateEstimate(id: Long) {
        viewModelScope.launch {
            val newId = repository.duplicate(id)
            if (newId != null) {
                _uiState.value = _uiState.value.copy(
                    lastCreatedId = newId,
                    message = "Estimate duplicated"
                )
            }
        }
    }
    
    fun deleteEstimate(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
            _uiState.value = _uiState.value.copy(message = "Estimate deleted")
        }
    }
    
    fun setStatusFilter(status: EstimateStatus?) { _statusFilter.value = status }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun clearSearch() { _searchQuery.value = "" }
    fun clearMessage() { _uiState.value = _uiState.value.copy(message = null, error = null) }
    
    fun loadSummary() {
        viewModelScope.launch { _summary.value = repository.getSummary() }
    }
    
    init { loadSummary() }
}

data class EstimateUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val lastCreatedId: Long? = null
)