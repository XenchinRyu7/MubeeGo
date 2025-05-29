package com.saefulrdevs.mubeego.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import kotlinx.coroutines.launch

class HomeViewModel
    (private val tmdbUseCase: TmdbUseCase) :
    ViewModel() {

    private val _popular = MutableLiveData<List<SearchItem>>()
    val popular: LiveData<List<SearchItem>> = _popular

    // Untuk menyimpan posisi scroll Home agar survive antar fragment
    var scrollPositionY: Int = 0

    fun fetchPopular() {
        if (_popular.value != null && _popular.value!!.isNotEmpty()) return // Sudah ada cache
        viewModelScope.launch {
            tmdbUseCase.getPopular().asLiveData().observeForever { resource ->
                resource.data?.let {
                    _popular.value = it
                }
            }
        }
    }

    fun getNowPlaying(): LiveData<Resource<List<Movie>>> =
        tmdbUseCase.getNowPlayingMovies().asLiveData()


    fun getSearchResult(title: String): LiveData<Resource<List<SearchItem>>> =
        tmdbUseCase.getSearchResult(title).asLiveData()

    fun getUpcomingMoviesByDate(minDate: String, maxDate: String) =
        tmdbUseCase.getUpcomingMoviesByDate(minDate, maxDate).asLiveData()

}