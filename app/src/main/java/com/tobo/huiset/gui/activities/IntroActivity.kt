package com.tobo.huiset.gui.activities

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.tobo.huiset.R
import com.tobo.huiset.gui.fragments.intro.SlideDismissListener
import com.tobo.huiset.gui.fragments.intro.SlideFactory
import com.tobo.huiset.gui.fragments.intro.SlideShowListener
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.HuisETDB
import com.tobo.huiset.utils.extensions.edit
import io.realm.Realm


class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firstSlide = SliderPage()
        firstSlide.title = "Welkom bij HuisET!"
        firstSlide.description =
            "HuisET is een Elektronisch Turfsysteem(ET) voor jouw studentenhuis. Voor studenten door studenten!"
        firstSlide.imageDrawable = R.mipmap.ic_launcher_huiset
        firstSlide.bgColor = ContextCompat.getColor(this, R.color.primaryColor)
        addSlide(AppIntroFragment.newInstance(firstSlide))


        val huisRekeningSlide = SlideFactory.newYesNoInstance(
            "Gebruiken jullie een huisrekening?",
            "Een huisrekening is een gedeelde bankrekening waar gezamenlijke inkopen van worden betaald.",
            "Ja, wij gebruiken een huisrekening.",
            "Nee, iedereen betaalt inkopen van zijn persoonlijke rekening.",
            false
        )
        addSlide(huisRekeningSlide)

        val createPersonSlide = SlideFactory.newCreatePersonSlide(
            "Maak een huisgenoot profiel aan.",
            "Later kun je meer profielen aanmaken (ook voor gasten).",
            "Maak profiel",
            "Naam"
        )
        addSlide(createPersonSlide)

        val cratePriceSlide = SlideFactory.newPriceSlide(
            "Wat is de gemiddelde prijs voor een krat bier (excl. statiegeld)?",
            "Dit kun je later nog aanpassen. Ook is het mogelijk om verschillende prijzen per merk toe te voegen. Toch vragen we voor nu om een standaard bierprijs in te voeren.",
            "Prijs in Euro's"
        )
        addSlide(cratePriceSlide)

        skipButtonEnabled = false
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val db = HuisETDB(Realm.getDefaultInstance())
        db.createProduct("Statie krat", 390, Product.KIND_BOTH, 1, Product.SPECIES_OTHER, 1)
        db.createProduct("Statie fles", 25, Product.KIND_BOTH, 2, Product.SPECIES_OTHER, 1)
        db.close()

        prefs.edit {
            it.putBoolean(PREFS_INTRO_SHOWN, true)
            it.putBoolean(PREFS_TURF_CONFETTI_ID, true)
        }

        this.finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)

        if (oldFragment is SlideDismissListener) {
            (oldFragment as SlideDismissListener).onSlideDismissed()
        }
        if (newFragment is SlideShowListener) {
            (newFragment as SlideShowListener).onSlideShown()
        }
    }

    /**
     * Hides keyboard when something else is clicked
     * @param view: The view that is clicked.
     */
    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}




