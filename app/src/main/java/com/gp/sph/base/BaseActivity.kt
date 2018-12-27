package com.gp.sph.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by gilbert on 12/25/18.
 */
open class BaseActivity<T : BaseContract.Presenter> : AppCompatActivity(), BaseContract.View<T> {

    private lateinit var presenter: T

    override fun setPresenter(presenter: T) {
        this.presenter = presenter
    }

    fun getPresenter(): T {
        return this.presenter
    }

    override fun handleError(throwable: Throwable?) {
        Toast.makeText(this, throwable?.message, Toast.LENGTH_SHORT).show()
    }
}