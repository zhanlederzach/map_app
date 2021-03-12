package com.main.ui_core.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.main.ui_core.Loader
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {

    private var progressDialog: Loader? = null

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Loader(requireActivity())
    }

    override fun onDestroyView() {
        progressDialog?.hideLoading()
        progressDialog = null
        compositeDisposable.clear()
        super.onDestroyView()
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun showLoading() {
        progressDialog?.showLoading()
    }

    fun hideLoading() {
        progressDialog?.hideLoading()
    }
}
