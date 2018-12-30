package testservice.gp.com.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by gilbert on 12/25/18.
 */
data class MobileDataUsage(@SerializedName("success")
                           var isSuccess: Boolean = false,
                           @SerializedName("resource_id")
                           var resourceId: String?,
                           var result: Result?) {

    data class Result(@SerializedName("total")
                      var total: Int,
                      @SerializedName("records")
                      var records: List<Record>) {

        data class Record(@SerializedName("_id")
                          var id: Int,
                          @SerializedName("volume_of_mobile_data")
                          var dataVolume: Double,
                          @SerializedName("quarter")
                          var quarter: String,
                          @Expose(serialize = false, deserialize = false)
                          var isDecreasing: Boolean = false,
                          @Expose(serialize = false, deserialize = false)
                          var prevRecord: String?,
                          @Expose(serialize = false, deserialize = false)
                          var showDetails: Boolean = false)
    }
}