package com.tobo.huiset.utils
import com.tobo.huiset.realmModels.Person


import android.content.Context
import android.graphics.Color
import io.realm.Realm


object ProfileColors{
    val PROFILECOLORS = arrayOf("#388E3C", //namegreen
        "#D50000", //namered
        "#1976D2", //nameblue
        "#FFA000", //nameOrange
        "#AA00FF", //namePurple
        "#c51162", //namepink
        "#00BFA5", //nameTeal
        "#ffd600", //nameDeeporange
        "#00E5FF", //nameCyan
        "#64DD17" //nameLightGreen
    )

    fun getRandomColor(realm: Realm): String{
        val usedColors = realm.where(Person::class.java).findAll().map { it.color }
        val unused = PROFILECOLORS.toList().minus(usedColors)
        if(unused.isNotEmpty()){
            return unused[0]
        }

        //how often used color is used.
        val occurances: Map<String, Int> = usedColors.groupBy { it }.mapValues { it.value.size }
        //else find the one that is used the least
        return occurances.minBy { mapEntry -> mapEntry.value  }!!.key
    }


    fun getProfileColorsInts(ctx: Context) = PROFILECOLORS.map { Color.parseColor(it) }.toIntArray()

}
