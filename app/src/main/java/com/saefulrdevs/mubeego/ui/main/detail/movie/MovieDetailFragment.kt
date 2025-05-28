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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiResponse
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.Genre
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.databinding.FragmentMovieDetailBinding
import com.saefulrdevs.mubeego.ui.moviedetail.MovieDetailViewModel
import com.saefulrdevs.mubeego.ui.moviedetail.ReminderReceiver
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val movieDetailViewModel: MovieDetailViewModel by viewModel()

    private var pendingMovie: Movie? = null
    private var genreList: List<Genre> = emptyList()

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

        // Ambil genre list dari ViewModel
        movieDetailViewModel.getGenres().observe(viewLifecycleOwner) { resource ->
            if (resource is com.saefulrdevs.mubeego.core.data.Resource.Success) {
                genreList = resource.data ?: emptyList()
                // Jika ada movie yang sudah ready tapi genre baru ready sekarang
                pendingMovie?.let {
                    showDetailMovie(it)
                    pendingMovie = null
                }
            }
        }

        val args = arguments
        if (args != null) {
            val movieId = args.getInt(EXTRA_MOVIE)
            if (movieId != 0) {
                val movieDetails = movieDetailViewModel.getMovieDetail(movieId)
                movieDetails.observe(viewLifecycleOwner) { movie ->
                    when (movie) {
                        is Resource.Loading -> {
                            // TODO: tampilkan progress jika ingin, misal pakai ProgressBar di layout
                        }

                        is Resource.Success -> {
                            movie.data?.let {
                                movieDetailViewModel.setMovie(it)
                                // Jika genreList sudah ready, langsung show
                                if (genreList.isNotEmpty()) {
                                    showDetailMovie(it)
                                } else {
                                    // Simpan dulu, nanti dipanggil setelah genreList ready
                                    pendingMovie = it
                                }
                            }
                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_while_getting_data),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
                        // Permission check for exact alarm (Android 12+)
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

    private fun showDetailMovie(movieDetails: Movie) {
        with(binding) {
            setFabIcon(movieDetails.favorited)
            movieTitle.text = movieDetails.title
            movieSinopsis.text = movieDetails.overview
            movieRating.text = String.format("%.1f/10 IMDb", movieDetails.voteAverage)

            val genreIds = try {
                val arr = JSONArray(movieDetails.genres)
                List(arr.length()) { arr.getInt(it) }
            } catch (e: Exception) {
                emptyList<Int>()
            }
            val genreNames = genreIds.mapNotNull { id -> genreList.find { it.id == id }?.name }
            genre1.text = genreNames.getOrNull(0) ?: "-"
            genre2.text = genreNames.getOrNull(1) ?: "-"
            genre3.text = genreNames.getOrNull(2) ?: "-"
            tvLength.text = "Length\n" + Utils.changeMinuteToDurationFormat(movieDetails.runtime)
            tvLanguage.text = "Language\n" + (movieDetails.originalLanguage.ifBlank { "-" })
            tvPlatform.text = "Platform streaming\n-"
            movieSinopsis.text = movieDetails.overview
            Log.d("MovieDetailFragment", "genreIds: $genreIds")
            Log.d("MovieDetailFragment", "genreNames: $genreNames")
        }

        movieDetailViewModel.getMovieWatchProviders(movieDetails.movieId).asLiveData()
            .observe(requireActivity()) { response ->
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

        Glide.with(this)
            .load(movieDetails.backdropPath)
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