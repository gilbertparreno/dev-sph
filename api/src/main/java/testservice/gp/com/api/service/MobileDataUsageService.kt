package testservice.gp.com.api.service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import testservice.gp.com.api.model.MobileDataUsage

/**
 * Created by gilbert on 12/25/18.
 */
interface MobileDataUsageService {
    @GET("action/datastore_search")
    fun searchDataStore(@Query("resource_id") id: String, @Query("limit") limit: Int, @Query("offset") offset: Int?): Observable<MobileDataUsage>
}
