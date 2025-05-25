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

public class NetworkUtils {
    // Declaración de variables a utilizar
    private final static String CLASS_ACCUWEATHER_NETWORK_UTILS_CURRENT_CONDITIONS = BuildConfig.CLASS_ACCUWEATHER_NETWORK_UTILS_CURRENT_CONDITIONS;
    private final static String CLASS_ACCUWEATHER_NETWORK_UTILS_APIKEY = BuildConfig.CLASS_ACCUWEATHER_NETWORK_UTILS_APIKEY;
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

    public static URL buildUrlForWeather(String value) {
        // Estableciendo la URL de consulta de la posición del usuario mediante las coordenadas
        String currentConditionsComplete = CLASS_ACCUWEATHER_NETWORK_UTILS_CURRENT_CONDITIONS + value + "?apikey=" + CLASS_ACCUWEATHER_NETWORK_UTILS_APIKEY + "&language=es-EC&details=true";
        Uri builtUri = Uri.parse(currentConditionsComplete).buildUpon().appendQueryParameter(CLASS_ACCUWEATHER_NETWORK_UTILS_APIKEY, parametroApiKey).build();
        // Estableciendo la URL en null
        URL url = null;
        try {
            // Obteniendo la URL
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            // Mensaje de error
            Log.e("Error URL AccuWeather", e.toString());
        }
        // Mensaje de información
        Log.i("Información URL AccuWeather", "buildUrlForWeather" + url);
        // Retornando la URL
        return url;
    }
}