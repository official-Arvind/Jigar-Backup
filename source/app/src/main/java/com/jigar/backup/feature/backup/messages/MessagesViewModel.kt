package com.jigar.backup.feature.backup.messages

import androidx.lifecycle.viewModelScope
import com.jigar.backup.data.MessageRepository
import com.jigar.backup.database.entity.deserializeMms
import com.jigar.backup.database.entity.deserializeSms
import com.jigar.backup.util.BaseViewModel
import com.jigar.backup.util.DatabaseHelper
import com.jigar.backup.util.filterMms
import com.jigar.backup.util.filterSms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class UiState(
    val selectedIndex: Int
)

open class MessagesViewModel(
    messageRepo: MessageRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(UiState(0))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()
    val smsList = combine(
        messageRepo.smsList.deserializeSms(),
        _searchText,
    ) { contacts, searchText ->
        contacts.filterSms(searchText)
    }.stateIn(
        scope = viewModelScope,
        initialValue = listOf(),
        started = SharingStarted.WhileSubscribed(5_000),
    )
    val mmsList = combine(
        messageRepo.mmsList.deserializeMms(),
        _searchText,
    ) { contacts, searchText ->
        contacts.filterMms(searchText)
    }.stateIn(
        scope = viewModelScope,
        initialValue = listOf(),
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val selected = combine(smsList, mmsList) { sms, mms ->
        sms.count { it.selected } + mms.count { it.selected }
    }.stateIn(
        scope = viewModelScope,
        initialValue = 0,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    fun updateUiState(uiState: UiState) {
        withLock(Dispatchers.IO) {
            _uiState.emit(uiState)
        }
    }

    fun selectSms(id: Long, selected: Boolean) {
        withLock(Dispatchers.IO) {
            DatabaseHelper.messageDao.selectSms(id, selected)
        }
    }

    fun selectAllSms() {
        withLock(Dispatchers.IO) {
            DatabaseHelper.messageDao.selectAllSms(smsList.value.map { it.id }, smsList.value.count { it.selected } != smsList.value.size)
        }
    }

    fun selectMms(id: Long, selected: Boolean) {
        withLock(Dispatchers.IO) {
            DatabaseHelper.messageDao.selectMms(id, selected)
        }
    }

    fun selectAllMms() {
        withLock(Dispatchers.IO) {
            DatabaseHelper.messageDao.selectAllMms(mmsList.value.map { it.id }, mmsList.value.count { it.selected } != mmsList.value.size)
        }
    }

    fun changeSearchText(text: String) {
        withLock(Dispatchers.Default) {
            _searchText.emit(text)
        }
    }
}
