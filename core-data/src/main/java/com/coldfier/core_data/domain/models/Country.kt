package com.coldfier.core_data.domain.models

data class Country(
    var name: String? = null,
    var fullName: String? = null,
    var iso2: String? = null,
    var continent: String? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var zoom: Double? = null,
    var timezone: String? = null,
    var languages: List<Language>? = null,
    var voltage: Int? = null,
    var frequency: Int? = null,
    var plugTypes: List<PlugType>? = null,
    var callingCode: Int? = null,
    var policeNumber: Int? = null,
    var ambulanceNumber: Int? = null,
    var fireNumber: Int? = null,
    var waterShort: String? = null,
    var waterFull: String? = null,
    var vaccinations: List<Vaccination>? = null,
    var currencyName: String? = null,
    var currencyCode: String? = null,
    var currencySymbol: String? = null,
    var weatherByMonth: Map<Month, Weather>? = null,
    var advices: Map<AdviceType, Advice>? = null,
    var neighborCountries: List<NeighborCountry>? = null
)
