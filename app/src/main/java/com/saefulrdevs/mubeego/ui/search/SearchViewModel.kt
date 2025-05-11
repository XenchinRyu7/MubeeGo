package com.saefulrdevs.mubeego.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

class SearchViewModel
    (private val tmdbUseCase: TmdbUseCase) :
    ViewModel() {

    fun getTrendings(): LiveData<Resource<List<SearchItem>>> =
        tmdbUseCase.getTrendings().asLiveData()

    fun getSearchResult(title: String): LiveData<Resource<List<SearchItem>>> =
        tmdbUseCase.getSearchResult(title).asLiveData()

    fun getUpcomingMoviesByDate(minDate: String, maxDate: String) =
        tmdbUseCase.getUpcomingMoviesByDate(minDate, maxDate).asLiveData()
}