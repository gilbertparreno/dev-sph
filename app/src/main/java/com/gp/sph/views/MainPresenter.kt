package com.gp.sph.views

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import testservice.gp.com.api.service.MobileDataUsageService
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by gilbert on 12/25/18.
 */
class MainPresenter(val view: MainContract.View, private val mobileDataUsageService: MobileDataUsageService) : MainContract.Presenter {

    private val disposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    val offset: AtomicInteger = AtomicInteger(0)

    override fun searchDataStore(reset: Boolean) {
        if (reset) {
            offset.set(0)
        }

        val tmpOffset = if (offset.get() == 0) null else offset.get()
        val id = "a807b7ab-6cad-4aa6-87d0-e283a7353a0f"

        disposable.add(mobileDataUsageService.searchDataStore(id, 20, tmpOffset)
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