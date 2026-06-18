package com.bonaventure.psautier.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bonaventure.psautier.data.Psautier
import com.bonaventure.psautier.data.PsautierRepository
import com.bonaventure.psautier.data.SearchResult
import com.bonaventure.psautier.data.ThemeMode
import com.bonaventure.psautier.data.ThemePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PsautierViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PsautierRepository(application)
    private val themePrefs = ThemePreferences(application)

    private val _psautier = MutableStateFlow<Psautier?>(null)
    val psautier: StateFlow<Psautier?> = _psautier.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) { repository.getPsautier() }
            _psautier.value = data
        }
        viewModelScope.launch {
            themePrefs.themeMode.collect { _themeMode.value = it }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            val results = withContext(Dispatchers.Default) { repository.search(query) }
            _searchResults.value = results
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePrefs.setThemeMode(mode)
        }
    }
}
