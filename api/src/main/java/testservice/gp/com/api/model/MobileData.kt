package testservice.gp.com.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by gilbert on 12/25/18.
 */
class MobileData(@SerializedName("success")
                 var isSuccess: Boolean = false,
                 @SerializedName("resource_id")
                 var resourceId: String)