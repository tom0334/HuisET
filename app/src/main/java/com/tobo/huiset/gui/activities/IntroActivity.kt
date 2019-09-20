package com.tobo.huiset.gui.activities

import android.os.Bundle
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage



class IntroActivity :AppIntro(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sliderPage = SliderPage()
        sliderPage.title = title
        sliderPage.description = "this is a description"
//        sliderPage.imageDrawable = image
//        sliderPage.bgColor =
        addSlide(AppIntroFragment.newInstance(sliderPage))

    }
}