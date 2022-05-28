package com.coldfier.core_data.data.net.models

import com.coldfier.core_data.domain.models.AdviceType
import com.coldfier.core_data.domain.models.Month
import com.squareup.moshi.Json

internal data class NetCountryExpanded (

    @Json(name = "names"        ) var countryNames        : NetCountryNames?            = null,
    @Json(name = "maps"         ) var mapValues           : NetMapValues?               = null,
    @Json(name = "timezone"     ) var timezone            : NetTimezone?                = null,
    @Json(name = "language"     ) var language            : List<NetLanguage>?          = null,
    @Json(name = "electricity"  ) var electricity         : NetElectricity?             = null,
    @Json(name = "telephone"    ) var telephone           : NetTelephone?               = null,
    @Json(name = "water"        ) var water               : NetWater?                   = null,
    @Json(name = "vaccinations" ) var vaccinations        : List<NetVaccination>?       = null,
    @Json(name = "currency"     ) var currency            : NetCurrency?                = null,
    @Json(name = "weather"      ) var weatherByMonth      : Map<Month, NetWeather>?     = null,
    @Json(name = "advise"       ) var advise              : Map<AdviceType, NetAdvise>? = null,
    @Json(name = "neighbors"    ) var neighborsCountries  : List<NetNeighborCountry>?   = null

)