//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.accuweather;
import com.polidriving.mobile.BuildConfig;

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
    private final static String CLASS_ACCUWEATHER_GEOPOSITION_UTILS_GEO_GEO_POSITION = BuildConfig.CLASS_ACCUWEATHER_GEOPOSITION_UTILS_GEO_GEO_POSITION;
    private final static String CLASS_ACCUWEATHER_GEOPOSITION_UTILS_GEO_APIKEY = BuildConfig.CLASS_ACCUWEATHER_GEOPOSITION_UTILS_GEO_APIKEY;
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
        String geoPositionComplete = CLASS_ACCUWEATHER_GEOPOSITION_UTILS_GEO_GEO_POSITION + CLASS_ACCUWEATHER_GEOPOSITION_UTILS_GEO_APIKEY + "&q=" + Latitud + "%2C%20" + Longitud + "&language=es-EC&details=true&toplevel=false";
        Uri builtUri = Uri.parse(geoPositionComplete).buildUpon().appendQueryParameter(CLASS_ACCUWEATHER_GEOPOSITION_UTILS_GEO_APIKEY, parametroApiKey).build();
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