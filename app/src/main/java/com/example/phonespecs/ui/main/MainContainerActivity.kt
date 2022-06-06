package com.example.phonespecs.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.phonespecs.R
import com.example.phonespecs.ui.details.DetailsFragment
import com.example.phonespecs.ui.phone.PhoneListingFragment
import dagger.android.support.DaggerAppCompatActivity

private const val CURRENT_FRAGMENT = "current_fragment"
private const val PHONE_SLUG = "phone_slug"

class MainContainerActivity : DaggerAppCompatActivity(), PhoneListingFragment.NavigationInterface {
    var currentFragment: String? = null
    var slug:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(PhoneListingFragment.newInstance(), "PhoneDataFragment")
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        currentFragment = tag
        supportFragmentManager.beginTransaction().add(R.id.activity_main_container, fragment, tag)
            .addToBackStack(tag).commit()
    }

    override fun onPhoneItemClicked(phoneSlug: String) {
        slug=phoneSlug
        showFragment(DetailsFragment.newInstance(phoneSlug), "DetailsFragment")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(CURRENT_FRAGMENT, currentFragment)
        outState.putString(PHONE_SLUG, slug)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val currentFragment=savedInstanceState.getString(
            CURRENT_FRAGMENT)
        val slug=savedInstanceState.getString(PHONE_SLUG)
        if(currentFragment=="DetailsFragment")
        showFragment(DetailsFragment.newInstance(slug!!),currentFragment)
        else{
         showFragment(PhoneListingFragment.newInstance(),"PhoneDataFragment")
        }
    }
}