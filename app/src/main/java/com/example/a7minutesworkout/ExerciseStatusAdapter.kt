package com.example.a7minutesworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minutesworkout.databinding.ItemExerciseCustomBinding

class ExerciseStatusAdapter(private val models: ArrayList<ExerciseModel>):RecyclerView.Adapter<ExerciseStatusAdapter.StatusViewHolder>(){
    class StatusViewHolder(private val itemBinding: ItemExerciseCustomBinding):RecyclerView.ViewHolder(itemBinding.root) {
        val tvItem = itemBinding.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = ItemExerciseCustomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val model:ExerciseModel = models[position]
        holder.tvItem.text = model.getId().toString()
        when{
            model.getIsSelected() ->{
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_thin_color_green_border)

                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
            model.getIsCompleted() ->{
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,
                        R.drawable.item_circular_color_accent_background)

                holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
            }
            else ->{
                holder.tvItem.background =
                    ContextCompat.getDrawable(holder.itemView.context,
                        R.drawable.item_circular_color_gray_background)

                holder.tvItem.setTextColor(Color.parseColor("#212121"))
            }
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}