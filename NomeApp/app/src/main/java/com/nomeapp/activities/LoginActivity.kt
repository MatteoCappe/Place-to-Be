
package com.nomeapp.activities



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.viewpager.widget.ViewPager
import com.example.nomeapp.R
import com.google.android.material.tabs.TabLayout
import com.nomeapp.adapter.LoginRegisterAdapter
import com.nomeapp.fragments.LoginFragment
import com.nomeapp.fragments.RegisterFragment



class LoginActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewpager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tabLayout = findViewById(R.id.tab_layout)
        viewpager = findViewById(R.id.view_pager)

        tabLayout.addTab(tabLayout.newTab().setText("Login"))
        tabLayout.addTab(tabLayout.newTab().setText("Register"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = LoginRegisterAdapter(this, supportFragmentManager, tabLayout.tabCount)
        viewpager.adapter = adapter

        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }
}


