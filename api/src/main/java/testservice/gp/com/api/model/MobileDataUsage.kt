package testservice.gp.com.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by gilbert on 12/25/18.
 */
data class MobileDataUsage(@SerializedName("success")
                           val isSuccess: Boolean? = false,
                           @SerializedName("resource_id")
                           val resourceId: String? = null,
                           val result: Result? = null) {
    data class Result(@SerializedName("total")
                      val total: Int? = 0,
                      @SerializedName("records")
                      val records: List<Record>? = null) {

        data class Record(@SerializedName("_id")
                          val id: Int = 0,
                          @SerializedName("volume_of_mobile_data")
                          val dataVolume: Double = 0.0,
                          @SerializedName("quarter")
                          val quarter: String? = null,
                          @Expose(serialize = false, deserialize = false)
                          var isDecreasing: Boolean = false,
                          @Expose(serialize = false, deserialize = false)
                          var prevRecord: String?,
                          @Expose(serialize = false, deserialize = false)
                          var showDetails: Boolean = false)
    }
}