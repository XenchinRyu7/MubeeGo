package com.saefulrdevs.mubeego.ui.main.detail.movie

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.GenreResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.Genre
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.databinding.FragmentMovieDetailBinding
import com.saefulrdevs.mubeego.ui.moviedetail.MovieDetailViewModel
import com.saefulrdevs.mubeego.ui.moviedetail.ReminderReceiver
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val movieDetailViewModel: MovieDetailViewModel by viewModel()

    private var pendingMovie: Movie? = null
    private var genreList: List<Genre> = emptyList()

    private var remoteMovieDetail: MovieDetailResponse? = null
    private var remoteGenres: List<GenreResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)

        createNotificationChannel()

        val args = arguments
        if (args != null) {
            val movieId = args.getInt(EXTRA_MOVIE)
            if (movieId != 0) {
                lifecycleScope.launch {
                    val movieDetail = movieDetailViewModel.getMovieDetailRemote(movieId)
                    val genres = movieDetailViewModel.getGenresRemote()
                    if (movieDetail != null && genres != null) {
                        remoteMovieDetail = movieDetail
                        remoteGenres = genres
                        showRemoteDetailMovie(movieDetail, genres)
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch detail or genres", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.fabFavorite.setOnClickListener {
            val newState = movieDetailViewModel.setFavorite()
            if (newState) {
                Toast.makeText(requireContext(), R.string.addedToFavorite, Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), R.string.removedFromFavorite, Toast.LENGTH_SHORT)
                    .show()
            }
            setFabIcon(newState)
        }


        binding.ivNotif.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    TimePickerDialog(requireContext(), { _, hour, minute ->
                        val cal = Calendar.getInstance()
                        cal.set(year, month, dayOfMonth, hour, minute, 0)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val alarmManager =
                                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            if (!alarmManager.canScheduleExactAlarms()) {
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                startActivity(intent)
                                return@TimePickerDialog
                            }
                        }
                        scheduleNotification(cal.timeInMillis)
                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        return binding.root
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for movie reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("reminder_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(timeInMillis: Long) {
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        Toast.makeText(requireContext(), "Reminder set!", Toast.LENGTH_SHORT).show()
    }

    private fun showRemoteDetailMovie(movieDetail: MovieDetailResponse, genres: List<GenreResponse>) {
        with(binding) {
            setFabIcon(false)
            movieTitle.text = movieDetail.title ?: ""
            movieSinopsis.text = movieDetail.overview ?: ""
            movieRating.text = String.format("%.1f/10 IMDb", movieDetail.voteAverage ?: 0.0)
            val genreNames = movieDetail.genres?.mapNotNull { g -> genres.find { it.id == g?.id }?.name } ?: emptyList()
            genre1.text = genreNames.getOrNull(0) ?: "-"
            genre2.text = genreNames.getOrNull(1) ?: "-"
            genre3.text = genreNames.getOrNull(2) ?: "-"
            tvLength.text = "Length\n" + Utils.changeMinuteToDurationFormat(movieDetail.runtime ?: 0)
            tvLanguage.text = "Language\n" + (movieDetail.originalLanguage ?: "-")
            tvPlatform.text = "Platform streaming\n-"
            movieSinopsis.text = movieDetail.overview ?: ""
            Log.d("MovieDetailFragment", "genreNames: $genreNames")
        }
        lifecycleScope.launch {
            val providersResponse = movieDetailViewModel.getMovieWatchProviders(movieDetail.id ?: 0).asLiveData()
            providersResponse.observe(viewLifecycleOwner) { response ->
                response?.let {
                    if (it is ApiResponse.Success) {
                        val providers = it.data.results?.get("ID")?.flatrate
                        if (!providers.isNullOrEmpty()) {
                            val names = providers.joinToString(", ") { p -> p.providerName ?: "" }
                            binding.tvPlatform.text = "Platform streaming\n$names"
                        } else {
                            binding.tvPlatform.text = "Platform streaming\n-"
                        }
                    }
                }
            }
        }
        val backdropUrl = movieDetail.backdropPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
        } ?: ""
        Glide.with(this)
            .load(backdropUrl)
            .transform(RoundedCorners(16))
            .apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading)
                    .error(R.drawable.placholder)
            )
            .into(binding.moviePoster)
    }

    private fun setFabIcon(isFavorited: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorited) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}