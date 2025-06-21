package com.saefulrdevs.mubeego.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.mubeego.core.domain.model.HonorableItem
import com.saefulrdevs.mubeego.core.domain.usecase.HonorableUseCase
import kotlinx.coroutines.launch

class HonorableViewModel(private val honorableUseCase: HonorableUseCase) : ViewModel() {
    private val _honorableList = MutableLiveData<List<HonorableItem>>()
    val honorableList: LiveData<List<HonorableItem>> = _honorableList

    fun fetchHonorableMentions() {
        viewModelScope.launch {
            val result = honorableUseCase.getHonorableMentions()
            _honorableList.value = result
        }
    }
}
