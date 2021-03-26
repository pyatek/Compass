package com.pyatek.compass.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.pyatek.compass.BR
import com.pyatek.compass.R
import com.pyatek.compass.app.base.BaseActivity
import com.pyatek.compass.databinding.ActivityMainBinding
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class MainActivity: BaseActivity<ActivityMainBinding, MainViewModel>() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun getLayoutId(): Int = R.layout.activity_main
    override fun getViewModels(): MainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    override fun getDataBindingVariable(): Int = BR.vm

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_fragment_container)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}