package com.saefulrdevs.mubeego.ui.main.detail.movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.source.remote.response.CastItem

class CastAdapter(private val castList: List<CastItem>) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(castList[position])
    }

    override fun getItemCount(): Int = castList.size

    class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvCharacter: TextView = itemView.findViewById(R.id.tvCharacter)
        fun bind(cast: CastItem) {
            val profileUrl = cast.profilePath?.let {
                if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w185$it"
            } ?: ""
            Glide.with(itemView.context)
                .load(profileUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.placholder))
                .into(imgProfile)
            tvName.text = cast.name ?: "-"
            tvCharacter.text = cast.character ?: ""
        }
    }
}
