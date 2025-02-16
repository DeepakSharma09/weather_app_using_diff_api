package com.example.weather_app

import ForecastDay
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.weather_app.R

class DailyAdapter(private val dailyList: List<ForecastDay>) : RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

    class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day: TextView = itemView.findViewById(R.id.txtDay)
        val temp: TextView = itemView.findViewById(R.id.txtTemp)
        val lottieAnimationView: LottieAnimationView = itemView.findViewById(R.id.lottieAnimation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = dailyList[position]
        holder.day.text = item.date // Display the date
        holder.temp.text = "Min: ${item.day.mintemp_c}°C  Max: ${item.day.maxtemp_c}°C"

        // Set Lottie animation based on condition
        when {
            item.day.condition.text.contains("rain", true) -> {
                holder.lottieAnimationView.setAnimation(R.raw.rain)
            }
            item.day.condition.text.contains("snow", true) -> {
                holder.lottieAnimationView.setAnimation(R.raw.snow)
            }
            item.day.condition.text.contains("cloud", true) -> {
                holder.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            else -> {
                holder.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        holder.lottieAnimationView.playAnimation()
    }

    override fun getItemCount(): Int = dailyList.size
}