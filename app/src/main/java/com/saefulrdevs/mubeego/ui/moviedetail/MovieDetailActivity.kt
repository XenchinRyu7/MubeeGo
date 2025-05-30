package com.saefulrdevs.mubeego.ui.moviedetail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.util.Utils.changeMinuteToDurationFormat
import com.saefulrdevs.mubeego.core.util.Utils.changeStringToDateFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.ActivityMovieDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.ImageView
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiResponse
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailViewModel

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailBinding
    private val movieDetailViewModel: MovieDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        val extras = intent.extras
        if (extras != null) {
            val movieId = extras.getInt(EXTRA_MOVIE)
            if (movieId != 0) {
                val movieDetails = movieDetailViewModel.getMovieDetail(movieId)
                movieDetails.observe(this) { movie ->
                    when (movie) {
                        is Resource.Loading -> {
                            // TODO: tampilkan progress jika ingin, misal pakai ProgressBar di layout
                        }
                        is Resource.Success -> {
                            movie.data?.let {
                                movieDetailViewModel.setMovie(it)
                                showDetailMovie(it)
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                this,
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
                Toast.makeText(this, R.string.addedToFavorite, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.removedFromFavorite, Toast.LENGTH_SHORT).show()
            }
            setFabIcon(newState)
        }

        binding.ivNotif.setOnClickListener {
            val now = java.util.Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                TimePickerDialog(this, { _, hour, minute ->
                    val cal = java.util.Calendar.getInstance()
                    cal.set(year, month, dayOfMonth, hour, minute, 0)
                    // Permission check for exact alarm (Android 12+)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = getSystemService(android.app.AlarmManager::class.java)
                        if (!alarmManager.canScheduleExactAlarms()) {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            startActivity(intent)
                            return@TimePickerDialog
                        }
                    }
                    scheduleNotification(cal.timeInMillis)
                }, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), true).show()
            }, now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for movie reminders"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel("reminder_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: android.app.NotificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(timeInMillis: Long) {
        val intent = android.content.Intent(this, ReminderReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(this, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
        alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        android.widget.Toast.makeText(this, "Reminder set!", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showDetailMovie(movieDetails: Movie) {
        with(binding) {
            setFabIcon(movieDetails.favorited)
            movieTitle.text = movieDetails.title
            movieSinopsis.text = movieDetails.overview
            movieRating.text = String.format("%.1f/10 IMDb", movieDetails.voteAverage)
            genre1.text = movieDetails.genres.getOrNull(0)?.toString() ?: "-"
            genre2.text = movieDetails.genres.getOrNull(1)?.toString() ?: "-"
            genre3.text = movieDetails.genres.getOrNull(2)?.toString() ?: "-"
            tvLength.text = "Length\n" + changeMinuteToDurationFormat(movieDetails.runtime)
            tvLanguage.text = "Language\n" + (movieDetails.originalLanguage.ifBlank { "-" })
            tvPlatform.visibility = View.VISIBLE
            tvPlatform.text = "Platform streaming\n-"
        }

        movieDetailViewModel.getMovieWatchProviders(movieDetails.movieId).asLiveData().observe(this@MovieDetailActivity) { response ->
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
            .load(movieDetails.posterPath)
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

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
    }
}