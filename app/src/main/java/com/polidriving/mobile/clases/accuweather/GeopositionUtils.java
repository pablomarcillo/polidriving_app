//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.accuweather;

//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import android.util.Log;
import android.net.Uri;
import java.net.URL;

public class GeopositionUtils {
    private final static String geoPosition = "https://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=";
    private final static String apiKey = "uLP123xtlw2SeVAqWZLc8wb0EnO6h3AX";
    private final static String parametroApiKey = "apikey";

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        // Obteniendo la respuesta de la URL
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            // Segmento de código para extraer la información
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL geoPositionData(String Latitud, String Longitud) {
        // Estableciendo la URL de consulta de la posición del usuario mediante las coordenadas
        String geoPositionComplete = geoPosition + apiKey + "&q=" + Latitud + "%2C%20" + Longitud + "&language=es-EC&details=true&toplevel=false";
        Uri builtUri = Uri.parse(geoPositionComplete).buildUpon().appendQueryParameter(apiKey, parametroApiKey).build();
        // Estableciendo la URL en null
        URL url = null;
        try {
            // Obteniendo la URL
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            // Mensaje de error
            Log.e("Error Geoposition AccuWeather", e.toString());
        }
        // Mensaje de información
        Log.i("Información Geoposition AccuWeather", "geoPositionData" + url);
        // Retornando la URL
        return url;
    }
}