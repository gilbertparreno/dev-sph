package com.gp.sph.views

import com.gp.sph.base.BaseContract
import testservice.gp.com.api.model.MobileDataUsage

/**
 * Created by gilbert on 12/25/18.
 */
interface MainContract {

    interface View : BaseContract.View<Presenter> {
        fun successSearchDataStore(mobileDataUsage: MobileDataUsage, reset: Boolean, offset: Int)
        fun failedSearchDataStore(throwable: Throwable, reset: Boolean)
        fun resetRefreshing()
    }

    interface Presenter : BaseContract.Presenter {
        fun searchDataStore(reset: Boolean)
    }
}