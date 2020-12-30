package com.tobo.huiset.gui.fragments.intro

import android.os.Bundle

object SlideFactory {

    val ARG_TITLE = "slide_title"
    val ARG_DESCRIPTION = "slide_description"

    val ARG_YES_TEXT = "yes_text"
    val ARG_NO_TEXT = "no_text"
    val ARG_DEFAULT_CHOICE = "def_choice"

    val ARG_HINT = "slide_hint"
    val ARG_BUTTON_TEXT = "slide_createButtonText"

    fun newYesNoInstance(
        title: String,
        description: String,
        yesText: String,
        noText: String,
        defaultChoice: Boolean
    ): YesNoIntroFragment {

        val yesNoIntroFragment = YesNoIntroFragment()
        val args = Bundle()

        args.putString(ARG_TITLE, title)
        args.putString(ARG_DESCRIPTION, description)
        args.putString(ARG_YES_TEXT, yesText)
        args.putString(ARG_NO_TEXT, noText)
        args.putBoolean(ARG_DEFAULT_CHOICE, defaultChoice)

        yesNoIntroFragment.arguments = args
        return yesNoIntroFragment
    }


    fun newCreatePersonSlide(
        title: String,
        description: String,
        buttonText: String,
        hint: String
    ): CreatePersonSlide {

        val sampleSlide = CreatePersonSlide()

        val args = Bundle()

        args.putString(ARG_TITLE, title)
        args.putString(ARG_DESCRIPTION, description)
        args.putString(ARG_BUTTON_TEXT, buttonText)
        args.putString(ARG_HINT, hint)

        sampleSlide.arguments = args
        return sampleSlide
    }

    fun newPriceSlide(
        title: String,
        description: String,
        hint: String
    ): AbstractPickPriceSlide {

        val args = Bundle()
        args.putString(ARG_TITLE, title)
        args.putString(ARG_DESCRIPTION, description)
        args.putString(ARG_HINT, hint)

        val sampleSlide = PickCratePriceSlide()
        sampleSlide.arguments = args
        return sampleSlide
    }

}