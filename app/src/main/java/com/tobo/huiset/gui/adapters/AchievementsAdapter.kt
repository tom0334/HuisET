package com.tobo.huiset.gui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.achievements.BaseAchievement
import com.tobo.huiset.utils.extensions.toMixedTimeString
import com.tobo.huiset.utils.extensions.toTimeAgoString


class AchievementsAdapter(val items: List<BaseAchievement>, val persons: List<Person>, val context: Context) :
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

        val personsThatArchievedThis = persons.filter { item.wasAchieved(it) }.sortedBy { it.row }

        holder.noOneText.visibility = if(personsThatArchievedThis.isEmpty()) View.VISIBLE else View.GONE


        for (p in personsThatArchievedThis){
            val child = View.inflate(context,R.layout.achievement_person,null)
            child.findViewById<TextView>(R.id.achievement_person_name).text = p.name
            child.findViewById<TextView>(R.id.achievement_person_date).text = item.getAchievemoment(p)?.timeStamp?.toMixedTimeString()
            child.findViewById<View>(R.id.achievement_person_color).setBackgroundColor(Color.parseColor(p.color))
            placeHolder.addView(child)
        }

    }

    class AchievementViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val achievementName = view.findViewById<TextView>(R.id.achievement_name)
        val description = view.findViewById<TextView>(R.id.achievement_description)
        val nameHolder = view.findViewById<LinearLayout>(R.id.achivement_person_container)
        val noOneText = view.findViewById<TextView>(R.id.achivement_noOneText)

    }
}

