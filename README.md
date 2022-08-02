# DizzyTrip

DizzyTrip is an application that presents the utility information about country that you want to travel.
You can find the information about the local languages, weather average temperature in country, useful plug types, 
currency, phone numbers for local services, recommended vaccinations etc.

The app architecture includes combining a MVVM and MVI patterns, layers are separated according to the Clean architecture principles. 
Modules assembled as independent features, sample apps are present for each feature module for easier debugging. App module links feature modules into one application.

## Tech stack

- Clean architecture, multi-module architecture;
- MVVM, MVI;
- Dagger2 as DI framework;
- Kotlin flows and coroutines as reactive/async tools;
- Android Jetpack navigation;
- Maps based on OSMDroid library (https://github.com/osmdroid/osmdroid);
- Coil for images loading;
- Room for local storage;
- Retrofit + Moshi for network communiation;
- Sources of network data (countries info, images source) - Open API's: https://travelbriefing.org/api, https://pixabay.com/service/about/api/

## Notes

This is a sample project for practice in multi-module architecture combining with Dagger2 and MVI pattern.
