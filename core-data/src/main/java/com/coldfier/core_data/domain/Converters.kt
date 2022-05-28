package com.coldfier.core_data.domain

import com.coldfier.core_data.data.net.models.NetCountryExpanded
import com.coldfier.core_data.data.net.models.NetCountryShort
import com.coldfier.core_data.data.room.data_source.RoomCountryFullModel
import com.coldfier.core_data.data.room.models.*
import com.coldfier.core_data.domain.models.*

internal fun NetCountryShort.convertToRoomCountryShort() = RoomCountryShort(
    name = name ?: "",
    uri = url
)

internal fun RoomCountryShort.convertToCountryShort() = CountryShort(
    name = name,
    uri = uri
)

internal fun NetCountryExpanded.convertToRoomCountryFullModel() = RoomCountryFullModel(
    name = countryNames?.name,
    fullName = countryNames?.full,
    iso2 = countryNames?.iso2,
    continent = countryNames?.continent,
    lat = mapValues?.lat,
    lon = mapValues?.lon,
    zoom = mapValues?.zoom,
    timezone = timezone?.name,
    languages = language?.map { 
        RoomLanguage(
            countryName = countryNames?.name,
            language = it.language,
            isOfficial = it.official?.lowercase() == "yes"
        )
    },
    voltage = electricity?.voltage,
    frequency = electricity?.frequency,
    plugTypes = electricity?.plugs?.map { RoomPlugType(it) },
    callingCode = telephone?.callingCode,
    policeNumber = telephone?.police,
    ambulanceNumber = telephone?.ambulance,
    fireNumber = telephone?.fire,
    waterShort = water?.short,
    waterFull = water?.full,
    vaccinations = vaccinations?.map { 
        RoomVaccination(
            countryName = countryNames?.name,
            name = it.name,
            message = it.message
        )
    },
    currencyName = currency?.name,
    currencyCode = currency?.code,
    currencySymbol = currency?.symbol,
    weatherByMonth = weatherByMonth?.map { (month, netWeather) ->
        RoomWeatherByMonth(
            countryName = countryNames?.name,
            month = month,
            temperatureAverage = netWeather.temperatureAverage,
            pressureAverage = netWeather.pressureAverage
        )
    },
    advices = advise?.map { (adviceType, netAdvice) ->
        RoomAdvice(
            countryName = countryNames?.name,
            adviceType = adviceType,
            advise = netAdvice.advise,
            url = netAdvice.url
        )
    },
    neighborCountries = neighborsCountries?.map { neighborCountry ->
        RoomNeighborCountry(
            countryId = neighborCountry.id,
            countryName = neighborCountry.name
        ) 
    }
)

internal fun RoomCountryFullModel.convertToCountry() = Country(
    name = name,
    fullName = fullName,
    iso2 = iso2,
    continent = continent,
    lat = lat,
    lon = lon,
    zoom = zoom,
    timezone = timezone,
    languages = languages?.map { roomLanguage ->
        Language(
            language = roomLanguage.language,
            isOfficial = roomLanguage.isOfficial
        )
    },
    voltage = voltage,
    frequency = frequency,
    plugTypes = plugTypes?.map { it.plugType },
    callingCode = callingCode,
    policeNumber = policeNumber,
    ambulanceNumber = ambulanceNumber,
    fireNumber = fireNumber,
    waterShort = waterShort,
    waterFull = waterFull,
    vaccinations = vaccinations?.map { roomVaccination ->
        Vaccination(
            name = roomVaccination.name,
            message = roomVaccination.message
        )
    },
    currencyName = currencyName,
    currencyCode = currencyCode,
    currencySymbol = currencySymbol,
    weatherByMonth = kotlin.run {
        val map = mutableMapOf<Month, Weather>()
        weatherByMonth?.forEach { roomWeatherByMonth ->
            roomWeatherByMonth.month?.let { month ->
                map[month] = Weather(
                    temperatureAverage = roomWeatherByMonth.temperatureAverage,
                    pressureAverage = roomWeatherByMonth.pressureAverage
                )
            }
        }

        map
    },
    advices = kotlin.run {
        advices
        val map = mutableMapOf<AdviceType, Advice>()
        advices?.forEach { roomAdvice ->
            roomAdvice.adviceType?.let { adviceType ->
                map[adviceType] = Advice(
                    advise = roomAdvice.advise,
                    url = roomAdvice.url
                )
            }
        }

        map
    },
    neighborCountries = neighborCountries?.map { roomNeighborCountry ->
        NeighborCountry(
            countryId = roomNeighborCountry.countryId,
            countryName = roomNeighborCountry.countryName
        )
    }
)