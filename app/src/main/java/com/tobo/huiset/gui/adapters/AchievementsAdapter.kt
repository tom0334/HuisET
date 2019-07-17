package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.utils.Achievement



class AchievementsAdapter(val items: List<Achievement>, val context: Context) :
    RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        return AchievementViewHolder(LayoutInflater.from(context).inflate(R.layout.achievement_rec_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val item = items[position]
        holder.achievementName.text = item.name
        holder.description.text = item.description

        val placeHolder = holder.nameHolder
        placeHolder.removeAllViews()

        for (i in 0 .. 5){
            val child = View.inflate(context,R.layout.achievement_person,null)
            child.findViewById<TextView>(R.id.achievement_person_name).text = "person $i"
            placeHolder.addView(child)
        }

    }

    class AchievementViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val achievementName = view.findViewById<TextView>(R.id.achievement_name)
        val description = view.findViewById<TextView>(R.id.achievement_description)
        val nameHolder = view.findViewById<LinearLayout>(R.id.achivement_person_container)

    }
}

