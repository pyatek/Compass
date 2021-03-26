package com.pyatek.compass.app.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseActivity<T: ViewDataBinding, out V: ViewModel>: AppCompatActivity() {

    @LayoutRes
    protected abstract fun getLayoutId(): Int
    val viewModel: V by lazy { getViewModels() }
    private var _viewDataBinding: T? = null
    private val viewDataBinding get() = _viewDataBinding!!
    protected abstract fun getViewModels(): V
    protected abstract fun getDataBindingVariable(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindData()
    }

    private fun bindData() {
        _viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewDataBinding.setVariable(getDataBindingVariable(), viewModel)
        viewDataBinding.executePendingBindings()
    }
}