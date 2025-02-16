package com.example.weather_app

import HourlyWeather
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class HourlyAdapter(private val hourlyList: List<HourlyWeather>) : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.txtTime)
        val temp: TextView = itemView.findViewById(R.id.txtTemp)
        val lottieAnimationView: LottieAnimationView = itemView.findViewById(R.id.lottieAnimation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = hourlyList[position]
        holder.time.text = item.time.split(" ")[1] // Extract HH:MM
        holder.temp.text = "${item.temp_c}°C"

        // Set Lottie animation dynamically based on weather condition
        when {
            item.condition.text.contains("rain", true) -> {
                holder.lottieAnimationView.setAnimation(R.raw.rain)
            }
            item.condition.text.contains("snow", true) -> {
                holder.lottieAnimationView.setAnimation(R.raw.snow)
            }
            item.condition.text.contains("cloud", true) -> {
                holder.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            else -> {
                holder.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        holder.lottieAnimationView.playAnimation()
    }

    override fun getItemCount(): Int = hourlyList.size
}