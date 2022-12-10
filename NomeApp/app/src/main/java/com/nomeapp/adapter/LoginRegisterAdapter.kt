package com.nomeapp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nomeapp.fragments.LoginFragment
import com.nomeapp.fragments.RegisterFragment

internal class LoginRegisterAdapter(var context: Context, fm: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                LoginFragment()
            }

            1 -> {
                RegisterFragment()
            }
            else -> getItem(position)
        }
        //si poteva usare anche un case switch ma questo sembrava fico
    }

    override fun getCount(): Int {
        return totalTabs
    }
}