package com.tobo.huiset.gui.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtFragment
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


        val secondSlide = CustomLayoutSlide.newInstance(R.layout.intro_slide_yes_no, "Gebruiken jullie een huisrekening?","Met een huisrekening bedoelen we een gedeelde bankrekening waar je alle inkopen van betaalt.", "Ja wij gebruiken een huisrekening waar we inkopen van betalen.","Nee, iedereen betaalt inkopen van zijn persoonlijke rekening.",false)
        addSlide(secondSlide)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        this.finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)


        if(oldFragment is SlideDissmissListener){
            (oldFragment as SlideDissmissListener).onSlideDismissed()
        }
    }
}

interface SlideDissmissListener{
    fun onSlideDismissed()
}



class CustomLayoutSlide : Fragment(), SlideDissmissListener {
    private var layoutResId: Int = 0
    private lateinit var title: String
    private lateinit var description:String
    private lateinit var yesText:String
    private lateinit var noText:String

    private var selection:Boolean = false

    override fun onSlideDismissed(){

        val realm = Realm.getDefaultInstance()
        val db = HuisETDB(realm)

        if(selection){
            db.addHuisRekeningIfNotExisting()
        }

        realm.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            val args = arguments!!

            layoutResId = args.getInt(ARG_LAYOUT_RES_ID)
            title = args.getString(ARG_TITLE)!!
            description = args.getString(ARG_DESCRIPTION)!!
            noText = args.getString(ARG_NO_TEXT)!!
            yesText = args.getString(ARG_YES_TEXT)!!
            selection = args.getBoolean(ARG_DEFAULT_CHOICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.description).text = description

        val yes = view.findViewById<RadioButton>(R.id.intro_slide_option_yes)
        val no = view.findViewById<RadioButton>(R.id.intro_slide_option_no)
        yes.text = yesText
        no.text = noText

        yes.setOnClickListener {
            selection = true
        }
        no.setOnClickListener {
            selection = false
        }

    }

    companion object {

        private val ARG_LAYOUT_RES_ID = "layoutResId"
        private val ARG_TITLE = "slide_title"
        private val ARG_DESCRIPTION = "slide_description"
        private val ARG_YES_TEXT = "yes_text"
        private val ARG_NO_TEXT = "no_text"
        private val ARG_DEFAULT_CHOICE = "def_choice"

        fun newInstance(
            layoutResId: Int,
            title: String,
            description: String,
            yesText: String,
            noText: String,
            defaultChoice: Boolean
        ): CustomLayoutSlide {
            val sampleSlide = CustomLayoutSlide()

            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION,description)
            args.putString(ARG_YES_TEXT,yesText)
            args.putString(ARG_NO_TEXT,noText)
            args.putBoolean(ARG_DEFAULT_CHOICE,defaultChoice)
            sampleSlide.arguments = args
            return sampleSlide
        }
    }
}