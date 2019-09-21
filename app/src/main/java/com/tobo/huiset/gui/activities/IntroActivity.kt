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
import com.tobo.huiset.gui.fragments.intro.SlideFactory
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


        val huisRekeningSlide = SlideFactory.newYesNoInstance("Gebruiken jullie een huisrekening?","Met een huisrekening bedoelen we een gedeelde bankrekening waar je alle inkopen van betaalt.", "Ja, wij gebruiken een huisrekening waar we inkopen van betalen.","Nee, iedereen betaalt inkopen van zijn persoonlijke rekening.",false)
        addSlide(huisRekeningSlide)

        val createPersonSlide = SlideFactory.newCreatePersonSlide("Maak alvast een profiel", "Je kan er meerdere maken als je wilt, maar het is aangeraden om er minstens eentje te maken","Maak profiel","Naam")
        addSlide(createPersonSlide)

        val cratePriceSlide = SlideFactory.newPriceSlide("Wat is de prijs voor een krat bier?","Dit kun je later nog aanpassen, en je kan ook losse prijzen voor een verschillende merken doen. Toch vragen we je voor nu even voor een standaardprijs.","Prijs in Euro",true)
        addSlide(cratePriceSlide)

        val beerPriceSlide = SlideFactory.newPriceSlide("Wat is de prijs voor een biertje?","Dit is nu gebaseerd op jullie prijs per kratje, maar kan je later aanpassen. Eventueel afhankelijk van hoe jullie statiegeld verdelen.","Prijs in Euro",false)
        addSlide(beerPriceSlide)



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




