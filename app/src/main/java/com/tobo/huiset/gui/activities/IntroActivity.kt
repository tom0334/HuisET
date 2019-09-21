package com.tobo.huiset.gui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.tobo.huiset.R
import com.tobo.huiset.gui.fragments.intro.SlideDismissListener
import com.tobo.huiset.gui.fragments.intro.CreatePersonSlide
import com.tobo.huiset.gui.fragments.intro.YesNoIntroFragment
import com.tobo.huiset.utils.HuisETDB
import io.realm.Realm


class IntroActivity : AppIntro2(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firstSlide = SliderPage()
        firstSlide.title = "Welkom bij HuisET!"
        firstSlide.description = "HuisET is een Elektronisch Turfsysteem(ET) voor jouw studenthuis. Voor studenten door studenten!"
        firstSlide.imageDrawable = R.mipmap.ic_launcher_huiset
        firstSlide.bgColor = ContextCompat.getColor(this,R.color.primaryColor)
        addSlide(AppIntroFragment.newInstance(firstSlide))


        val huisRekeningSlide = YesNoIntroFragment.newInstance("Gebruiken jullie een huisrekening?","Met een huisrekening bedoelen we een gedeelde bankrekening waar je alle inkopen van betaalt.", "Ja, wij gebruiken een huisrekening waar we inkopen van betalen.","Nee, iedereen betaalt inkopen van zijn persoonlijke rekening.",false)
        addSlide(huisRekeningSlide)

        val createPersonSlide = CreatePersonSlide.newInstance("Maak alvast een profiel", "Je kan er meerdere maken als je wilt, maar het is aangeraden om er minstens eentje te maken","Maak profiel","Naam")
        addSlide(createPersonSlide)

        skipButtonEnabled = false
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        this.finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)

        if(oldFragment is SlideDismissListener){
            (oldFragment as SlideDismissListener).onSlideDismissed()
        }
    }

    fun createPerson(name: String) {
        if(name.isEmpty()){
            Toast.makeText(this,"Typ eerst een naam in!",Toast.LENGTH_SHORT).show()
            return
        }
        val realm = Realm.getDefaultInstance()
        val db = HuisETDB(realm)
        db.createAndSavePerson(name,
            guest = false,
            show = true,
            huisEtRekening = false,
            first = false
        )
        Toast.makeText(this,"Profiel gemaakt:$name",Toast.LENGTH_SHORT).show()


    }
}




