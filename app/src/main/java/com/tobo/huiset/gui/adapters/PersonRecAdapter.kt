package com.tobo.huiset.gui.adapters

import FragmentProfiles
import android.R.attr.*
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.extensions.getBalanceColorString
import com.tobo.huiset.utils.extensions.setTextColorFromHex
import com.tobo.huiset.utils.extensions.toCurrencyString
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


/**
 * Shows persons in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class PersonRecAdapter(
    val fragmentProfiles: FragmentProfiles,
    val realm: Realm,
    data: RealmResults<Person>?,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Person, PersonRecAdapter.PersonViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(fragmentProfiles.context).inflate(R.layout.person_rec_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = data?.get(position) ?: return

        if (person.isHuisRekening) {
            holder.iconIv.visibility = View.VISIBLE
        } else {
            holder.iconIv.visibility = View.GONE
        }

        holder.nameTv.text = person.name

        holder.balanceTv.text = person.balance.toCurrencyString()
        val colorString = data?.get(position)!!.balance.getBalanceColorString()
        holder.balanceTv.setTextColorFromHex(colorString)

        holder.hiddenTv.text = "verborgen bij turven"
        holder.hiddenTv.visibility = View.VISIBLE
        if (person.show)
            holder.hiddenTv.visibility = View.GONE

        // make item go up
        holder.upIv.setOnClickListener {
            fragmentProfiles.updateRows()
            realm.executeTransaction {
                val other = realm.where(Person::class.java).equalTo("row", person.row - 1).findFirst()
                if (other != null) {
                    other.row += 1
                    person.row -= 1
                }
            }
        }

        // make item go down
        holder.downIv.setOnClickListener {
            fragmentProfiles.updateRows()
            realm.executeTransaction {
                val other = realm.where(Person::class.java).equalTo("row", person.row + 1).findFirst()
                if (other != null) {
                    other.row -= 1
                    person.row += 1
                }
            }
        }

        holder.colorLine.setBackgroundColor(Color.parseColor(person.color))

    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconIv = itemView.findViewById<ImageView>(R.id.personRecItem_icon)!!
        val nameTv = itemView.findViewById<TextView>(R.id.personRecItem_name)!!
        val balanceTv = itemView.findViewById<TextView>(R.id.personRecItem_balance)!!
        val upIv = itemView.findViewById<ImageView>(R.id.personRecItem_up)!!
        val downIv = itemView.findViewById<ImageView>(R.id.personRecItem_down)!!
        val hiddenTv = itemView.findViewById<TextView>(R.id.personRecItem_hidden)!!
        val colorLine = itemView.findViewById<View>(R.id.personRecItem_color_line)!!
    }
}