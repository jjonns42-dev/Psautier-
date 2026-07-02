package org.maronite.shhimo.ui.office

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.maronite.shhimo.data.model.AppLanguage
import org.maronite.shhimo.data.model.CanonicalHour
import org.maronite.shhimo.data.model.DailyOffice
import org.maronite.shhimo.data.model.LiturgicalDay
import org.maronite.shhimo.data.repository.LiturgyRepository
import org.maronite.shhimo.data.repository.SettingsRepository
import java.time.LocalDate

data class OfficeUiState(
    val date: LocalDate = LocalDate.now(),
    val day: LiturgicalDay? = null,
    val office: DailyOffice? = null,
    val selectedHour: CanonicalHour = CanonicalHour.RAMCHO,
    val language: AppLanguage = AppLanguage.ARABIC,
    val fontScale: Float = 1.0f,
    val loading: Boolean = true
)

class OfficeViewModel(
    private val liturgy: LiturgyRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OfficeUiState())
    val state: StateFlow<OfficeUiState> = _state.asStateFlow()

    init {
        load(LocalDate.now())
        viewModelScope.launch {
            combine(settings.language, settings.fontScale) { lang, scale -> lang to scale }
                .collect { (lang, scale) ->
                    _state.value = _state.value.copy(language = lang, fontScale = scale)
                }
        }
    }

    fun load(date: LocalDate) {
        _state.value = _state.value.copy(loading = true, date = date)
        val day = liturgy.liturgicalDay(date)
        val office = liturgy.officeFor(day)
        _state.value = _state.value.copy(day = day, office = office, loading = false)
    }

    fun selectHour(hour: CanonicalHour) {
        _state.value = _state.value.copy(selectedHour = hour)
    }

    fun goToToday() = load(LocalDate.now())
    fun nextDay() = load(_state.value.date.plusDays(1))
    fun previousDay() = load(_state.value.date.minusDays(1))

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settings.setLanguage(language) }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { settings.setFontScale(scale) }
    }

    class Factory(
        private val liturgy: LiturgyRepository,
        private val settings: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            OfficeViewModel(liturgy, settings) as T
    }
}
