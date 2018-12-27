package com.gp.sph.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.gp.sph.R
import com.gp.sph.base.BaseActivity
import kotlinx.android.synthetic.main.activity_mobile_data_usage.*
import retrofit2.HttpException
import testservice.gp.com.api.model.MobileDataUsage
import testservice.gp.com.api.service.MobileDataUsageService
import testservice.gp.com.api.service.ServiceFactory
import java.net.UnknownHostException

class MainActivity : BaseActivity<MainContract.Presenter>(), MainContract.View {

    private val adapter: MobileDataUsageAdapter by lazy {
        MobileDataUsageAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_data_usage)
        setPresenter(MainPresenter(this, ServiceFactory.getService(MobileDataUsageService::class.java)))
        getPresenter().searchDataStore(true)

        initViews()
    }

    private fun initViews() {

        this@MainActivity.adapter.adapterListener = object : MobileDataUsageAdapter.AdapterListener {
            override fun onEndReach() {
                getPresenter().searchDataStore(false)
            }

            override fun retryPage() {
                adapter.addRefresh()
            }
        }

        rvMobileDataUsage.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        srMobileDataUsage.setOnRefreshListener {
            getPresenter().searchDataStore(true)
        }
    }

    override fun resetRefreshing() {
        srMobileDataUsage.isRefreshing = true
    }

    override fun successSearchDataStore(mobileDataUsage: MobileDataUsage, reset: Boolean, offset: Int) {

        if (reset) adapter.lastItemQuarter = null

        srMobileDataUsage.isRefreshing = false
        tvError.visibility = View.GONE
        rvMobileDataUsage.visibility = View.VISIBLE

        val map: MutableList<Pair<String, MutableList<MobileDataUsage.Result.Record>>> = mutableListOf()
        var tmpYear = mobileDataUsage.result?.records?.get(0)?.quarter?.substring(0, 4)
        var tmpList: MutableList<MobileDataUsage.Result.Record> = mutableListOf()
        val records = mobileDataUsage.result?.records!!
        for (rec: MobileDataUsage.Result.Record in records) {
            if (!tmpYear.equals(rec.quarter?.substring(0, 4))) {
                map.add(Pair(tmpYear!!, tmpList))
                tmpYear = rec.quarter?.substring(0, 4)
                tmpList = mutableListOf()
            }

            if (records.first() == rec) {
                var lastAdapterQuarter: Record? = adapter.lastItemQuarter
                if (lastAdapterQuarter != null && !reset) {
                    val prev = lastAdapterQuarter.dataVolume
                    val current = rec.dataVolume
                    if (prev > current) {
                        rec.isDecreasing = true
                        rec.prevRecord = lastAdapterQuarter.quarter
                    }
                }
            } else {
                val prevData = records[records.indexOf(rec) - 1]
                val prev = prevData.dataVolume
                val current = rec.dataVolume
                if (prev > current) {
                    rec.isDecreasing = true
                    rec.prevRecord = prevData.quarter
                }
            }
            tmpList.add(rec)

            if (records.indexOf(rec) == records.lastIndex) {
                map.add(Pair(tmpYear!!, tmpList))
            }
        }

        adapter.addData(map, reset)

        if (mobileDataUsage.result?.total!! > offset) {
            adapter.addRefresh()
        } else {
            adapter.maxEndReach()
        }
    }

    override fun failedSearchDataStore(throwable: Throwable, reset: Boolean) {
        srMobileDataUsage.isRefreshing = false
        if (reset) {
            when (throwable) {
                is UnknownHostException -> tvError.text = getString(R.string.lbl_internet_error)
                is HttpException -> {
                    if (throwable.code() == 504) {
                        tvError.text = getString(R.string.lbl_internet_error)
                    } else {
                        tvError.text = throwable.message
                    }
                }
                else -> tvError.text = throwable.message
            }
            tvError.visibility = View.VISIBLE
            rvMobileDataUsage.visibility = View.GONE
            return
        }

        adapter.addError()
    }
}