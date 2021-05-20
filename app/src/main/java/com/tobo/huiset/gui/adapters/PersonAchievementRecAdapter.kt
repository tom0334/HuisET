package com.tobo.huiset.gui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.achievements.AchievementManager
import com.tobo.huiset.achievements.BaseAchievement
import com.tobo.huiset.gui.activities.EditProfileActivity
import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.utils.HuisETDB
import com.tobo.huiset.utils.extensions.toMixedTimeString
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


/**
 * Shows persons in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class PersonAchievementRecAdapter(
    private val editProfileActivity: EditProfileActivity,
    val db: HuisETDB,
    data: RealmResults<AchievementCompletion>?,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<AchievementCompletion, PersonAchievementRecAdapter.PersonAchievementViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonAchievementViewHolder {
        val view = LayoutInflater.from(editProfileActivity)
            .inflate(R.layout.person_achievement_rec_item, parent, false)
        return PersonAchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonAchievementViewHolder, position: Int) {
        val achievement = data?.get(position) ?: return
        holder.nameTv.text = AchievementManager.getAchievementForCompletion(achievement).name
        holder.dateTv.text = achievement.timeStamp.toMixedTimeString()
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class PersonAchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.personAchievement_name)!!
        val dateTv = itemView.findViewById<TextView>(R.id.personAchievement_date)!!
    }
}