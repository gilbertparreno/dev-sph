package com.gp.sph

import com.gp.sph.views.MainContract
import com.gp.sph.views.MainPresenter
import com.gp.sph.views.Record
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import testservice.gp.com.api.model.MobileDataUsage
import testservice.gp.com.api.service.MobileDataUsageService
import java.util.*

/**
 * Created by gilbert on 12/28/18.
 */
class MainPresenterTest {

    @Mock
    private val view: MainContract.View? = null

    @Mock
    private val service: MobileDataUsageService? = null

    private var presenter: MainPresenter? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = MainPresenter(view!!, service!!)

        RxJavaPlugins.setIoSchedulerHandler { scheduler -> Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { scheduler -> Schedulers.trampoline() }
    }

    @Test
    fun successSearchDataStore_test() {
        val data = MobileDataUsage(false, "", MobileDataUsage.Result(2, ArrayList<Record>()))
        `when`(service!!.searchDataStore(anyString(), anyInt(), anyInt())).thenReturn(Observable.just(data))
        presenter!!.searchDataStore(true)
        val inOrder = inOrder(view)
        inOrder.verify<MainContract.View>(view).resetRefreshing()
        inOrder.verify(view!!).successSearchDataStore(data, true, 20)
    }

    @Test
    fun failSearchDataStore_test() {
        val t = Throwable("Something went wrong!")
        val fail = Observable.create<MobileDataUsage> { emitter -> emitter.onError(t) }
        `when`(service!!.searchDataStore(anyString(), anyInt(), anyInt())).thenReturn(fail)
        presenter!!.searchDataStore(true)
        verify(view!!).failedSearchDataStore(t, true)
    }
}
