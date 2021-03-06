package com.memtrip.eosreach.app.account.resources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.account_resources_fragment.view.*

class ReadOnlyResourcesFragment : ResourcesFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        (view.resources_ram_title.layoutParams as ConstraintLayout.LayoutParams).topMargin = 0
        return view
    }

    override fun showManageResourcesNavigation() {
        // never show the navigation in read only mode
    }

    override fun inject() {
        AndroidSupportInjection.inject(this)
    }
}