package com.gp.sph.base

/**
 * Created by gilbert on 12/25/18.
 */
interface BaseContract {

    interface View<T : Presenter> {
        fun handleError(throwable: Throwable?)
        fun setPresenter(presenter: T)
    }

    interface Presenter {
        fun onStart()
        fun onStop()
    }
}