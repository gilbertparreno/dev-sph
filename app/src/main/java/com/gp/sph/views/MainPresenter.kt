package com.gp.sph.views

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import testservice.gp.com.api.service.MobileDataUsageService
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by gilbert on 12/25/18.
 */
class MainPresenter(val view: MainContract.View, private val mobileDataUsageService: MobileDataUsageService) : MainContract.Presenter {

    private val disposable = CompositeDisposable()

    val offset: AtomicInteger = AtomicInteger(0)
    val id = "a807b7ab-6cad-4aa6-87d0-e283a7353a0f"

    override fun searchDataStore(reset: Boolean) {
        if (reset) {
            offset.set(0)
        }

        disposable.add(mobileDataUsageService.searchDataStore(id, 20, offset.get()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    offset.set(offset.get() + 20)
                    view.successSearchDataStore(data, reset, offset.get())
                }, { throwable ->
                    view.failedSearchDataStore(throwable, reset)
                }, {}, { _ -> if (reset) view.resetRefreshing() }))
    }

    override fun onStart() {
        // on activity or fragment start
    }

    override fun onStop() {
        disposable.clear()
    }
}