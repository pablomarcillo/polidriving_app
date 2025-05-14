//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.accuweather;

//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.location.Location;
import com.polidriving.mobile.R;
import android.widget.TextView;
import org.json.JSONException;
import android.content.Intent;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import android.view.View;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.net.Uri;
import java.net.URL;

/**
 * @noinspection deprecation
 */
@SuppressLint({"StaticFieldLeak", "SetTextI18n", "DefaultLocale"})
public class AccuWeatherActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    private static ArrayList<ParametrosGeoposition> detallesGeoposicionamiento = new ArrayList<>();
    private static ArrayList<ParametrosAccuWeather> detallesCurrentCondintions = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static TextView temperatura_real_feel_shade;
    private boolean locationPermissionGranted;
    static TextView temperatura_real_feel;
    static TextView temperatura_actual;
    LocationListener locationListener;
    LocationManager locationManager;
    static TextView weather_estado;
    static TextView obstruccion;
    static TextView visibilidad;
    static TextView presion;
    static TextView humedad;
    static TextView viento;
    static TextView lluvia;
    static TextView enlace;
    static TextView rayos;
    TextView fechaActual;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Verificando los permisos para obtener la latitud y longitud del usuario mediante GPS
        locationPermissionGranted = false;
        // Verificando si los permisos fueron concedidos
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        // Enviando los códigos de verificación
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static ArrayList<ParametrosAccuWeather> parseJSONCurrent(String resultadoCondiciones) {
        // Verificando si la lista esta vacia
        if (detallesCurrentCondintions != null) {
            // Vaciando la lista
            detallesCurrentCondintions.clear();
        }
        if (resultadoCondiciones != null) {
            try {
                // Creando objeto JSON
                JSONObject jsonObject = new JSONObject(resultadoCondiciones.replace("[", "").replace("]", ""));

                // Llamada a la clase de condiciones actuales
                ParametrosAccuWeather datosClima = new ParametrosAccuWeather();

                try {
                    // Obteniendo el valor de Temperature
                    JSONObject temperature = jsonObject.getJSONObject("Temperature");
                    JSONObject metricTemperature = temperature.getJSONObject("Metric");
                    double valueTemperature = metricTemperature.getDouble("Value");
                    String unitTemperature = metricTemperature.getString("Unit");
                    datosClima.setTemperatura(valueTemperature + " " + "°" + unitTemperature);
                    temperatura_actual.setText(String.format("%s °%s", valueTemperature, unitTemperature));
                    Log.i("Temperature", "Valor: " + valueTemperature + " " + "°" + unitTemperature);
                } catch (Exception e) {
                    temperatura_actual.setText("No Data");
                    Log.e("Error", "Temperature");
                }

                try {
                    // Obteniendo el valor de RealFeelTemperature
                    JSONObject realFeelTemperature = jsonObject.getJSONObject("RealFeelTemperature");
                    JSONObject metricRealFeelTemperature = realFeelTemperature.getJSONObject("Metric");
                    double valueRealFeelTemperature = metricRealFeelTemperature.getDouble("Value");
                    String unitRealFeelTemperature = metricRealFeelTemperature.getString("Unit");
                    datosClima.setRealFeelTemperature(valueRealFeelTemperature + " " + "°" + unitRealFeelTemperature);
                    temperatura_real_feel.setText(String.format("%s °%s", valueRealFeelTemperature, unitRealFeelTemperature));
                    Log.i("RealFeelTemperature", "Valor: " + valueRealFeelTemperature + " " + "°" + unitRealFeelTemperature);
                } catch (Exception e) {
                    temperatura_real_feel.setText("No Data");
                    Log.e("Error", "RealFeelTemperature");
                }

                try {
                    // Obteniendo el valor de RealFeelTemperatureShade
                    JSONObject realFeelTemperatureShade = jsonObject.getJSONObject("RealFeelTemperatureShade");
                    JSONObject metricRealFeelTemperatureShade = realFeelTemperatureShade.getJSONObject("Metric");
                    double valueRealFeelTemperatureShade = metricRealFeelTemperatureShade.getDouble("Value");
                    String unitRealFeelTemperatureShade = metricRealFeelTemperatureShade.getString("Unit");
                    datosClima.setRealFeelTemperatureShade(valueRealFeelTemperatureShade + " " + "°" + unitRealFeelTemperatureShade);
                    temperatura_real_feel_shade.setText(String.format("%s °%s", valueRealFeelTemperatureShade, unitRealFeelTemperatureShade));
                    Log.i("RealFeelTemperatureShade", "Valor: " + valueRealFeelTemperatureShade + " " + "°" + unitRealFeelTemperatureShade);
                } catch (Exception e) {
                    temperatura_real_feel_shade.setText("No Data");
                    Log.e("Error", "RealFeelTemperatureShade");
                }

                try {
                    // Obteniendo el valor de WeatherText
                    String weatherText = jsonObject.getString("WeatherText");
                    datosClima.setEstado(weatherText);
                    weather_estado.setText(weatherText);
                    Log.i("WeatherText", "Valor: " + weatherText);
                } catch (Exception e) {
                    weather_estado.setText("No Data");
                    Log.e("Error", "WeatherText");
                }

                try {
                    // Obteniendo el valor de Lluvia
                    JSONObject precipitationSummary = jsonObject.getJSONObject("PrecipitationSummary");
                    JSONObject precipitation = precipitationSummary.getJSONObject("Precipitation");
                    JSONObject metric = precipitation.getJSONObject("Metric");
                    int value = metric.getInt("Value");
                    String unit = metric.getString("Unit");
                    datosClima.setLluvia(value + " " + unit + "³");
                    lluvia.setText(value + " " + unit + "³");
                    Log.i("Precipitation", "Valor: " + value + " " + unit + "³");
                } catch (Exception e) {
                    lluvia.setText("No Data");
                    Log.e("Error", "Precipitation");
                }

                try {
                    // Obteniendo el valor de RelativeHumidity
                    int relativeHumidity = jsonObject.getInt("RelativeHumidity");
                    datosClima.setHumedad(String.valueOf(relativeHumidity));
                    humedad.setText(String.valueOf(relativeHumidity));
                    Log.i("RelativeHumidity", "Valor: " + relativeHumidity);
                } catch (Exception e) {
                    humedad.setText("No Data");
                    Log.e("Error", "RelativeHumidity");
                }

                try {
                    // Obteniendo el valor de Wind
                    JSONObject wind = jsonObject.getJSONObject("Wind");
                    JSONObject directionWind = wind.getJSONObject("Direction");
                    int grados = directionWind.getInt("Degrees");
                    String orientacion = directionWind.getString("Localized");
                    JSONObject velocidadWind = wind.getJSONObject("Speed");
                    JSONObject metricWind = velocidadWind.getJSONObject("Metric");
                    double valueWind = metricWind.getDouble("Value");
                    String unitWind = metricWind.getString("Unit");
                    datosClima.setViento(valueWind + " " + unitWind + " " + grados + "°" + " " + orientacion);
                    viento.setText(String.format("%s %s %s° %s", valueWind, unitWind, grados, orientacion));
                    Log.i("Wind", "Valor: " + valueWind + " " + unitWind + " " + grados + "°" + " " + orientacion);
                } catch (Exception e) {
                    viento.setText("No Data");
                    Log.e("Error", "Wind");
                }

                try {
                    // Obteniendo el valor de UVIndex
                    int uvIndex = jsonObject.getInt("UVIndex");
                    datosClima.setRayosUV(String.valueOf(uvIndex));
                    rayos.setText(String.valueOf(uvIndex));
                    Log.i("UVIndex", "Valor: " + uvIndex);
                } catch (Exception e) {
                    rayos.setText("No Data");
                    Log.e("Error", "UVIndex");
                }

                try {
                    // Obteniendo el valor de Visibility
                    JSONObject visibility = jsonObject.getJSONObject("Visibility");
                    JSONObject metricVisibility = visibility.getJSONObject("Metric");
                    double valueVisibility = metricVisibility.getDouble("Value");
                    String unitVisibility = metricVisibility.getString("Unit");
                    datosClima.setVisibilidad(valueVisibility + " " + unitVisibility);
                    visibilidad.setText(String.format("%s %s", valueVisibility, unitVisibility));
                    Log.i("Visibility", "Valor: " + valueVisibility + " " + unitVisibility);
                } catch (Exception e) {
                    visibilidad.setText("No Data");
                    Log.e("Error", "Visibility");
                }

                try {
                    // Obteniendo el valor de Pressure
                    JSONObject pressure = jsonObject.getJSONObject("Pressure");
                    JSONObject metricPressure = pressure.getJSONObject("Metric");
                    double valuePressure = metricPressure.getDouble("Value");
                    String unitPressure = metricPressure.getString("Unit");
                    datosClima.setPresion(valuePressure + " " + unitPressure);
                    presion.setText(String.format("%s %s", valuePressure, unitPressure));
                    Log.i("Pressure", "Valor: " + valuePressure + " " + unitPressure);
                } catch (Exception e) {
                    presion.setText("No Data");
                    Log.e("Error", "Pressure");
                }

                try {
                    // Obteniendo el valor de ObstructionsToVisibility
                    String obstructionsToVisibility = jsonObject.getString("ObstructionsToVisibility");
                    datosClima.setObstruccion(obstructionsToVisibility);
                    obstruccion.setText(obstructionsToVisibility);
                    Log.i("ObstructionsToVisibility", "Valor: " + obstructionsToVisibility);
                } catch (Exception e) {
                    obstruccion.setText("No Data");
                    Log.e("Error", "ObstructionsToVisibility");
                }

                try {
                    // Obteniendo el valor de MobileLink
                    String mobileLink = jsonObject.getString("MobileLink");
                    datosClima.setEnlace(mobileLink);
                    enlace.setText(mobileLink);
                    enlace.setEnabled(true);
                    Log.i("MobileLink", "Valor: " + mobileLink);

                } catch (Exception e) {
                    enlace.setText("No Data");
                    enlace.setEnabled(false);
                    Log.e("Error", "MobileLink");
                }

                Log.i("Condiciones", String.valueOf(jsonObject));
                return detallesCurrentCondintions;
            } catch (Exception e) {
                Log.e("Error Extracción Condiciones", "resultadoCondiciones: " + e);
            }
        }
        return null;
    }

    private static class FetchCurrentConditionsDetails extends AsyncTask<URL, Void, String> {
        // Sección que se ejecuta al iniciar
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO
        }

        protected String doInBackground(URL... urls) {
            //Sección que se ejecuta por detrás de la aplicación para obtener la URL de las condiciones
            URL condintionsURL = urls[0];
            // Estableciendo los resultados en null
            String condicionesResultados = null;
            try {
                // Obteniendo la respuesta desde el servidor de AccuWeather
                condicionesResultados = NetworkUtils.getResponseFromHttpUrl(condintionsURL);
            } catch (IOException e) {
                // Mensaje de error
                Log.i("Error Condiciones URL", "condicionesResultados: " + e);
            }
            // Mensaje de información
            Log.i("Información Condiciones URL", "condicionesResultados: " + condicionesResultados);
            // Retornando los resultados
            return condicionesResultados;
        }

        protected void onPostExecute(String resultadoCurrent) {
            // Sección que se ejecuta tras obtener la información
            if (resultadoCurrent != null && !resultadoCurrent.isEmpty()) {
                detallesCurrentCondintions = parseJSONCurrent(resultadoCurrent);
            }
            super.onPostExecute(resultadoCurrent);
        }
    }

    private static ArrayList<ParametrosGeoposition> parseJSONGeo(String resultadoKey) {
        // Verificando si la lista esta vacia
        if (detallesGeoposicionamiento != null) {
            // Vaciando la lista
            detallesGeoposicionamiento.clear();
        }
        if (resultadoKey != null) {
            try {
                // Creando objeto JSON
                JSONObject jsonObject = new JSONObject(resultadoKey);
                int value = jsonObject.getInt("Key");
                // Procesar el valor individual
                ParametrosGeoposition geoposicion = new ParametrosGeoposition();
                geoposicion.setKey(String.valueOf(value));
                Log.i("LLave Geoposicionamiento", "Key: " + value);
                // Establecimiento de la conexión URL con AccuWeather para obtener la condiciones del clima actual
                URL weatherURL = NetworkUtils.buildUrlForWeather(String.valueOf(value));
                new FetchCurrentConditionsDetails().execute(weatherURL);
                Log.i("Conexión URL AccuWeather", "onCreate: weatherURL: " + weatherURL);
                return detallesGeoposicionamiento;
            } catch (JSONException e) {
                Log.e("Error Extracción Geoposicionamiento", "resultadoKey: " + e);
            }
        }
        return null;
    }

    private static class FetchGeopositioDetails extends AsyncTask<URL, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO
        }

        protected String doInBackground(URL... urls) {
            //Sección que se ejecuta por detrás de la aplicación para obtener la URL de la posición GPS
            URL geoURL = urls[0];
            // Estableciendo los resultados en null
            String geoResultados = null;
            try {
                // Obteniendo la respuesta desde el servidor de AccuWeather
                geoResultados = GeopositionUtils.getResponseFromHttpUrl(geoURL);
            } catch (IOException e) {
                // Mensaje de error
                Log.e("Error Geoposicionamiento URL", "geoResultados: " + e);
            }
            // Mensaje de información
            Log.i("Información Geoposicionamiento URL", "geoResultados: " + geoResultados);
            // Retornando los resultados
            return geoResultados;
        }

        protected void onPostExecute(String resultadoKey) {
            // Sección que se ejecuta tras obtener la información
            if (resultadoKey != null && !resultadoKey.isEmpty()) {
                detallesGeoposicionamiento = parseJSONGeo(resultadoKey);
            }
            super.onPostExecute(resultadoKey);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accu_weather);

        //Inicializando las variables con los elementos de la actividad
        temperatura_real_feel_shade = findViewById(R.id.txtRespuestaGradosTemperaturaMedio);
        temperatura_real_feel = findViewById(R.id.txtRespuestaGradosTemperaturaBajo);
        temperatura_actual = findViewById(R.id.txtRespuestaGradosTemperaturaAlto);
        obstruccion = findViewById(R.id.txtRespuestaObstruccionVisibilidad);
        visibilidad = findViewById(R.id.txtRespuestaIndiceVisibilidad);
        weather_estado = findViewById(R.id.txtRespuestaEstadoClima);
        fechaActual = findViewById(R.id.txtRespuestaFechaActual);
        humedad = findViewById(R.id.txtRespuestaHumedadRelativa);
        viento = findViewById(R.id.txtRespuestaVelocidadViento);
        presion = findViewById(R.id.txtRespuestaIndicePresion);
        lluvia = findViewById(R.id.txtRespuestaEstadoLluvia);
        enlace = findViewById(R.id.txtRespuestaEnlaceMobil);
        rayos = findViewById(R.id.txtRespuestaIndiceUV);

        //Estableciendo la fecha para mostrar al usuario
        Calendar fecha = Calendar.getInstance();
        int day = fecha.get(Calendar.DAY_OF_MONTH);
        // Se suma 1 porque Calendar.MONTH devuelve un valor basado en cero
        int month = fecha.get(Calendar.MONTH) + 1;
        int year = fecha.get(Calendar.YEAR);
        int hour = fecha.get(Calendar.HOUR_OF_DAY);
        int minute = fecha.get(Calendar.MINUTE);
        String amPm = (fecha.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        fechaActual.setText(day + "/" + month + "/" + year + "  " + hour + ":" + String.format("%02d", minute) + " " + amPm);

        //Obteniendo la latitud, longitud del usuario y los permisos para su utilización
        getLocationPermission();
        // Administrador de la ubicación
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Administrador de escucha
        locationListener = new LocationListener() {
            public void onLocationChanged(@NonNull Location location) {
                // Aquí se obtiene la ubicación
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Mostrando la información de latitud y longitud del usuario
                Log.i("Coordenadas AccuWeather", "Lat: " + latitude + ", Lon: " + longitude);
                URL geoURL = GeopositionUtils.geoPositionData(Double.toString(latitude), Double.toString(longitude));
                // Obteniendo las claves de ubicación del usuario mediante AccuWeather
                new FetchGeopositioDetails().execute(geoURL);
                // Mostrando la información del usuario de AccuWeather
                Log.i("Conexión Geo AccuWeather", "onCreate: geoURL: " + geoURL);
            }
        };
        if (locationPermissionGranted) {
            // Iniciando la actualización de la localización GPS del usuario
            startLocationUpdates();
        }

        // Abrir Movil Link
        enlace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri link = Uri.parse(enlace.getText().toString());
                Intent abrir = new Intent(Intent.ACTION_VIEW, link);
                startActivity(abrir);
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
    }

    protected void onResume() {
        //Reinicia la interacción con la actividad
        super.onResume();
        if (locationPermissionGranted) {
            startLocationUpdates();
        }
    }

    protected void onStart() {
        //Inicia la interacción con la actividad
        super.onStart();
    }

    protected void onPause() {
        //Suspende la interacción con la actividad
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    protected void onStop() {
        //Para la interacción con la actividad
        super.onStop();
    }

    public void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
    }
}