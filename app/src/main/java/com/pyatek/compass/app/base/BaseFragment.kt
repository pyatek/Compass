package com.pyatek.compass.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

abstract class BaseFragment<T: ViewDataBinding, out V: ViewModel>: Fragment() {

    @LayoutRes
    protected abstract fun getLayoutId(): Int
    val viewModel: V by lazy { getViewModels() }
    private var _viewDataBinding: T? = null
    private val viewDataBinding get() = _viewDataBinding!!
    private var rootView: View? = null
    protected abstract fun getViewModels(): V
    protected abstract fun getBindingVariable(): Int


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        rootView = viewDataBinding.root

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.setVariable(getBindingVariable(), viewModel)
        viewDataBinding.executePendingBindings()
    }

    override fun onDestroyView() {
        rootView = null
        _viewDataBinding = null
        super.onDestroyView()
    }

    inline fun <reified T: BaseActivity<*,*>> getParent(): T? = activity as? T
}