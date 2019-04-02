# glovo-challenge-mobile

Before compiling and run this project please deploy the server.

and modify on RestService.kt the BASE_URL with the IP where server is running.

This project was developed on Kotlin. Using MVVM Architecture.

For network rest communication I used Retrofit and GSON to parse responses.

The application draw polygons when zoom is more than 10 (Based on google documentation that is the City zoom level.)
when the zoom level is below that value The polygons are replaced by Markers.

The markers are clickable and redirects the map to the city position.

On the MapActivity I also show the City information.

For decode Polylines, I used com.google.map library.



