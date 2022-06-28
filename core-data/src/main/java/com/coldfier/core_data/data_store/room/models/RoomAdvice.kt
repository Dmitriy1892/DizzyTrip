package com.coldfier.core_data.data_store.room.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coldfier.core_data.repository.models.AdviceType

@Entity
internal data class RoomAdvice(
    @PrimaryKey(autoGenerate = true)
    var adviceId: Int? = null,

    var countryName: String? = null,

    var adviceType: AdviceType? = null,
    var advise: String? = null,
    var url: Uri? = null
)
