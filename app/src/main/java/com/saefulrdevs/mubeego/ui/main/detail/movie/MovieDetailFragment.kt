package com.saefulrdevs.mubeego.ui.main.detail.movie

import android.app.AlarmManager
import android.app.DatePickerDialog
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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.source.remote.response.GenreResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.databinding.FragmentMovieDetailBinding
import com.saefulrdevs.mubeego.ui.main.favorite.FavoriteViewModel
import com.saefulrdevs.mubeego.ui.common.NotificationHelper
import com.saefulrdevs.mubeego.ui.common.ReminderReceiver
import com.saefulrdevs.mubeego.ui.common.ExactAlarmPermissionActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Calendar

class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val movieDetailViewModel: MovieDetailViewModel by activityViewModel()
    private val favoriteViewModel: FavoriteViewModel by activityViewModel()

    private var castAdapter: CastAdapter? = null

    private var lastFavoriteState: Boolean? = null

    private var pendingNotificationTime: Long? = null
    private var pendingNotificationTitle: String? = null
    private var pendingNotificationMessage: String? = null

    private val exactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            // Lanjutkan schedule notification jika sudah dapat izin
            pendingNotificationTime?.let { time ->
                NotificationHelper.scheduleNotification(
                    requireContext(),
                    time,
                    title = pendingNotificationTitle ?: "Movie Reminder",
                    message = pendingNotificationMessage ?: "It's time to watch your movie!"
                )
            }
        } else {
            Toast.makeText(requireContext(), "Permission not granted!", Toast.LENGTH_SHORT).show()
        }
        pendingNotificationTime = null
        pendingNotificationTitle = null
        pendingNotificationMessage = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)

        NotificationHelper.createNotificationChannel(requireContext())

        val args = arguments
        var movieId: Int? = null
        if (args != null) {
            movieId = args.getInt(EXTRA_MOVIE)
            if (movieId != 0) {
                movieDetailViewModel.fetchFavoriteStatus(movieId)
                movieDetailViewModel.isFavorited.observe(viewLifecycleOwner) { isFav ->
                    setFabIcon(isFav == true)
                    // Tampilkan toast hanya jika state berubah karena aksi user
                    if (lastFavoriteState != null && isFav != lastFavoriteState) {
                        if (isFav == true) {
                            Toast.makeText(requireContext(), "Added to favorite", Toast.LENGTH_SHORT).show()
                        } else if (isFav == false) {
                            Toast.makeText(requireContext(), "Removed from favorite", Toast.LENGTH_SHORT).show()
                        }
                    }
                    lastFavoriteState = isFav
                }
                movieDetailViewModel.getMovieDetail(movieId).observe(viewLifecycleOwner) { resource ->
                    val movie = resource.data
                    Log.d("MovieDetailFragment", "getMovieDetail observer: movieId=$movieId")
                    if (movie != null) {
                        movieDetailViewModel.setMovie(movie)
                    }
                }
                movieDetailViewModel.genres.observe(viewLifecycleOwner) { genresList ->
                    val detail = movieDetailViewModel.getCachedMovieDetail(movieId)
                    val credits = movieDetailViewModel.getCachedMovieCredits(movieId)
                    val providers = movieDetailViewModel.getCachedMovieProviders(movieId)
                    if (detail != null && credits != null && providers != null && genresList != null) {
                        showRemoteDetailMovie(detail, genresList)
                        val castList = credits.cast?.sortedBy { it.order ?: Int.MAX_VALUE }?.take(10) ?: emptyList()
                        castAdapter = CastAdapter(castList)
                        binding.rvCast.apply {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = castAdapter
                            setHasFixedSize(true)
                        }
                    }
                }
                movieDetailViewModel.movieDetails.observe(viewLifecycleOwner) { map ->
                    val detail = map[movieId]
                    val credits = movieDetailViewModel.getCachedMovieCredits(movieId)
                    val providers = movieDetailViewModel.getCachedMovieProviders(movieId)
                    val genresList = movieDetailViewModel.getCachedGenres()
                    if (detail != null && credits != null && providers != null && genresList != null) {
                        showRemoteDetailMovie(detail, genresList)
                        val castList = credits.cast?.sortedBy { it.order ?: Int.MAX_VALUE }?.take(10) ?: emptyList()
                        castAdapter = CastAdapter(castList)
                        binding.rvCast.apply {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = castAdapter
                            setHasFixedSize(true)
                        }
                    }
                }
                movieDetailViewModel.movieCredits.observe(viewLifecycleOwner) { map ->
                    val detail = movieDetailViewModel.getCachedMovieDetail(movieId)
                    val credits = map[movieId]
                    val providers = movieDetailViewModel.getCachedMovieProviders(movieId)
                    val genresList = movieDetailViewModel.getCachedGenres()
                    if (detail != null && credits != null && providers != null && genresList != null) {
                        showRemoteDetailMovie(detail, genresList)
                        val castList = credits.cast?.sortedBy { it.order ?: Int.MAX_VALUE }?.take(10) ?: emptyList()
                        castAdapter = CastAdapter(castList)
                        binding.rvCast.apply {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = castAdapter
                            setHasFixedSize(true)
                        }
                    }
                }
                movieDetailViewModel.movieProviders.observe(viewLifecycleOwner) { map ->
                    val detail = movieDetailViewModel.getCachedMovieDetail(movieId)
                    val credits = movieDetailViewModel.getCachedMovieCredits(movieId)
                    val providers = map[movieId]
                    val genresList = movieDetailViewModel.getCachedGenres()
                    if (detail != null && credits != null && providers != null && genresList != null) {
                        showRemoteDetailMovie(detail, genresList)
                        val castList = credits.cast?.sortedBy { it.order ?: Int.MAX_VALUE }?.take(10) ?: emptyList()
                        castAdapter = CastAdapter(castList)
                        binding.rvCast.apply {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = castAdapter
                            setHasFixedSize(true)
                        }
                    }
                }
                if (movieDetailViewModel.getCachedMovieDetail(movieId) == null) {
                    movieDetailViewModel.fetchMovieDetail(movieId)
                }
                if (movieDetailViewModel.getCachedMovieCredits(movieId) == null) {
                    movieDetailViewModel.fetchMovieCredits(movieId)
                }
                if (movieDetailViewModel.getCachedMovieProviders(movieId) == null) {
                    movieDetailViewModel.fetchMovieProviders(movieId)
                }
                if (movieDetailViewModel.getCachedGenres() == null) {
                    movieDetailViewModel.fetchGenres()
                }
            }
        }

        binding.fabFavorite.setOnClickListener {
            movieId?.let { id ->
                movieDetailViewModel.toggleFavorite(id)
                favoriteViewModel.refreshFavoriteList()
            }
        }


        binding.ivOptions.setOnClickListener {
            val popup = PopupMenu(requireContext(), binding.ivOptions)
            popup.menuInflater.inflate(R.menu.menu_movie_options, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_add_to_playlist -> {
                        movieId?.let { id ->
                            AddToPlaylistDialog(id).show(parentFragmentManager, "AddToPlaylistDialog")
                        }
                        true
                    }
                    R.id.action_set_notifications -> {
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
                                            // Simpan data pending
                                            pendingNotificationTime = cal.timeInMillis
                                            pendingNotificationTitle = "Movie Reminder"
                                            pendingNotificationMessage = "It's time to watch your movie!"
                                            // Launch permission activity
                                            val intent = Intent(requireContext(), ExactAlarmPermissionActivity::class.java)
                                            exactAlarmPermissionLauncher.launch(intent)
                                            return@TimePickerDialog
                                        }
                                    }
                                    NotificationHelper.scheduleNotification(
                                        requireContext(),
                                        cal.timeInMillis,
                                        title = "Movie Reminder",
                                        message = "It's time to watch your movie!"
                                    )
                                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        ).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        return binding.root
    }

    private fun showRemoteDetailMovie(movieDetail: MovieDetailResponse, genres: List<GenreResponse>) {
        with(binding) {
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
        val providers = movieDetailViewModel.getCachedMovieProviders(movieDetail.id ?: 0)
        if (providers != null) {
            val flatrate = providers.results?.get("ID")?.flatrate
            if (!flatrate.isNullOrEmpty()) {
                val names = flatrate.joinToString(", ") { p -> p.providerName ?: "" }
                binding.tvPlatform.text = "Platform streaming\n$names"
            } else {
                binding.tvPlatform.text = "Platform streaming\n-"
            }
        } else {
            binding.tvPlatform.text = "Platform streaming\n-"
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
        Log.d("MovieDetailFragment", "setFabIcon called: isFavorited=$isFavorited")
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