package com.coldfier.core_data.data_store.net

import android.net.Uri
import com.coldfier.core_data.data_store.net.models.NetAdvise
import com.coldfier.core_data.repository.models.AdviceType
import com.squareup.moshi.FromJson

internal class AdviceAdapter {

    @FromJson
    fun fromJson(obj: Any): Map<AdviceType, NetAdvise>? {
        if (obj is List<*>) {
            return null
        }

        try {
            val outputMap = mutableMapOf<AdviceType, NetAdvise>()
            val intermediateMap = obj as Map<String, Map<String, String>>
            intermediateMap.forEach { (key, value) ->
                when(key.uppercase()) {
                    AdviceType.UA.name -> outputMap[AdviceType.UA] = NetAdvise(
                        advise = value["advise"],
                        url = Uri.parse(value["url"])
                    )

                    AdviceType.CA.name -> outputMap[AdviceType.CA] = NetAdvise(
                        advise = value["advise"],
                        url = Uri.parse(value["url"])
                    )
                }
            }

            return outputMap
        } catch (e: Exception) {
            return null
        }
    }
}