//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases que permiten el control de vida de los Fragmentos de la App
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
//Clases usadas para el uso de voz
//Clases usadas para configuración centralizada
import com.polidriving.mobile.BuildConfig;
import com.polidriving.mobile.clases.accuweather.ParametrosAccuWeather;
import com.polidriving.mobile.clases.accuweather.ParametrosGeoposition;
import com.polidriving.mobile.clases.notificaciones.NotificacionesVoz;
import com.polidriving.mobile.clases.accuweather.GeopositionUtils;
import com.polidriving.mobile.clases.accuweather.NetworkUtils;
import android.graphics.drawable.ColorDrawable;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.content.ContextCompat;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import android.view.LayoutInflater;
import android.location.Location;
import android.os.CountDownTimer;
import com.polidriving.mobile.R;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import java.text.MessageFormat;
import org.json.JSONException;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import android.os.Handler;
import java.util.Calendar;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.net.Uri;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @noinspection ALL
 */
@SuppressLint({"StaticFieldLeak", "SetTextI18n", "DefaultLocale"})
public class FragmentosClima extends Fragment {
    //Creación de variables a usar e instancia a la clase de notificaciones de voz TTS
    static ArrayList<ParametrosGeoposition> detallesGeoposicionamiento = new ArrayList<>();
    static ArrayList<ParametrosAccuWeather> detallesCurrentCondintions = new ArrayList<>();
    private static final String ARG_SECTION_NUMBER = "section_number";
    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    //static TextView temperatura_real_feel_shade;
    private NotificacionesVoz conversor = null;
    //static TextView temperatura_real_feel;
    //static ImageView resultadoBandera;
    private FragmentoModeloVista vista;
    static TextView temperatura_actual;
    boolean locationPermissionGranted;
    LocationListener locationListener;
    LocationManager locationManager;
    Boolean get_set_MuyAlta = false;
    static LinearLayout grupoActual;
    static LinearLayout grupoNivel;
    static TextView weather_estado;
    static TextView resultadoTexto;
    Boolean get_set_Media = false;
    Boolean get_set_Alta = false;
    Boolean get_set_Baja = false;
    //static TextView porcentaje;
    static TextView obstruccion;
    static TextView visibilidad;
    static TextView presion;
    static TextView humedad;
    static TextView viento;
    static TextView lluvia;
    static TextView enlace;
    static TextView actual;
    static TextView rayos;
    
    // Variable para el TextView de última actualización de clima
    private TextView txtUltimaActualizacionClima;
    private static final String CLIMA_PREFS_NAME = "ClimaPrefs";
    private static final String CLIMA_LAST_UPDATE_KEY = "last_clima_update";
    
    // Claves para SharedPreferences para persistir valores de clima
    private static final String KEY_TEMPERATURA_ACTUAL = "ultimo_valor_temperatura_actual";
    private static final String KEY_WEATHER_ESTADO_CLIMA = "ultimo_valor_weather_estado_clima";
    private static final String KEY_OBSTRUCCION = "ultimo_valor_obstruccion";
    private static final String KEY_VISIBILIDAD_CLIMA = "ultimo_valor_visibilidad_clima";
    private static final String KEY_PRESION = "ultimo_valor_presion";
    private static final String KEY_HUMEDAD = "ultimo_valor_humedad";
    private static final String KEY_VIENTO = "ultimo_valor_viento";
    private static final String KEY_LLUVIA = "ultimo_valor_lluvia";
    private static final String KEY_RAYOS = "ultimo_valor_rayos";
    
    // Variable estática para acceder a la instancia del fragmento
    private static FragmentosClima instanciaFragmento;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Creando un vista de los elementos de la actividad
        View view = inflater.inflate(R.layout.fragmento_principal_clima, container, false);
        
        // Asignar la instancia actual
        instanciaFragmento = this;
        
        //Inicializando el TextView de última actualización
        txtUltimaActualizacionClima = view.findViewById(R.id.txtUltimaActualizacionClima);
        
        //Inicializando las variables con los elementos de la actividad
        //temperatura_real_feel_shade = view.findViewById(R.id.txtRespuestaGradosTemperaturaMedio);
        //temperatura_real_feel = view.findViewById(R.id.txtRespuestaGradosTemperaturaBajo);
        temperatura_actual = view.findViewById(R.id.txtRespuestaGradosTemperaturaAlto);
        obstruccion = view.findViewById(R.id.txtRespuestaObstruccionVisibilidad);
        //resultadoBandera = view.findViewById(R.id.imagenBanderaDatosClima);
        visibilidad = view.findViewById(R.id.txtRespuestaIndiceVisibilidad);
        weather_estado = view.findViewById(R.id.txtRespuestaEstadoClima);
        //porcentaje = view.findViewById(R.id.txtPorcentajeDatosClima);
        humedad = view.findViewById(R.id.txtRespuestaHumedadRelativa);
        resultadoTexto = view.findViewById(R.id.txtMedidaDatosClima);
        viento = view.findViewById(R.id.txtRespuestaVelocidadViento);
        presion = view.findViewById(R.id.txtRespuestaIndicePresion);
        actual = view.findViewById(R.id.txtUltimaUpdateFechaClima);
        lluvia = view.findViewById(R.id.txtRespuestaEstadoLluvia);
        enlace = view.findViewById(R.id.txtRespuestaEnlaceMobil);
        grupoActual = view.findViewById(R.id.grupoFechaClima);
        grupoNivel = view.findViewById(R.id.grupoNivelClima);
        rayos = view.findViewById(R.id.txtRespuestaIndiceUV);

        //Estableciendo la fecha para mostrar al usuario
        presentarFecha();
        actual.setText("Esperando…");

        // Cargar los últimos valores guardados antes de observar los datos
        cargarUltimosValoresClima();

        //Obteniendo la latitud, longitud del usuario y los permisos para su utilización
        getLocationPermission();
        // Administrador de la ubicación
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
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
                
                // Actualizar la fecha de presentación cuando se obtiene nueva ubicación
                presentarFecha();
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

        //Creación de un segundo hilo que permite actualizar la ubicación GPS
        Handler handler_gps = new Handler();
        handler_gps.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que permite presentar la ubicación gps
                startLocationUpdates();
                presentarFecha();
                //Segmento de código que permite actualizar la información a presentar cada 60 segundos
                handler_gps.postDelayed(this, 60000);
            }
        }, 5000);

        return view;
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
                    String textoTemperatura = String.format("%s °%s", valueTemperature, unitTemperature);
                    temperatura_actual.setText(textoTemperatura);
                    // Guardar valor y actualizar timestamp
                    if (instanciaFragmento != null) {
                        instanciaFragmento.guardarValorClima(KEY_TEMPERATURA_ACTUAL, textoTemperatura);
                        instanciaFragmento.actualizarTimestampClima();
                    }
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
                    //temperatura_real_feel.setText(String.format("%s °%s", valueRealFeelTemperature, unitRealFeelTemperature));
                    Log.i("RealFeelTemperature", "Valor: " + valueRealFeelTemperature + " " + "°" + unitRealFeelTemperature);
                } catch (Exception e) {
                    //temperatura_real_feel.setText("No Data");
                    Log.e("Error", "RealFeelTemperature");
                }

                try {
                    // Obteniendo el valor de RealFeelTemperatureShadeNo
                    JSONObject realFeelTemperatureShade = jsonObject.getJSONObject("RealFeelTemperatureShade");
                    JSONObject metricRealFeelTemperatureShade = realFeelTemperatureShade.getJSONObject("Metric");
                    double valueRealFeelTemperatureShade = metricRealFeelTemperatureShade.getDouble("Value");
                    String unitRealFeelTemperatureShade = metricRealFeelTemperatureShade.getString("Unit");
                    datosClima.setRealFeelTemperatureShade(valueRealFeelTemperatureShade + " " + "°" + unitRealFeelTemperatureShade);
                    //temperatura_real_feel_shade.setText(String.format("%s °%s", valueRealFeelTemperatureShade, unitRealFeelTemperatureShade));
                    Log.i("RealFeelTemperatureShade", "Valor: " + valueRealFeelTemperatureShade + " " + "°" + unitRealFeelTemperatureShade);
                } catch (Exception e) {
                    //temperatura_real_feel_shade.setText("No Data");
                    Log.e("Error", "RealFeelTemperatureShade");
                }

                try {
                    // Obteniendo el valor de WeatherText
                    String weatherText = jsonObject.getString("WeatherText");
                    datosClima.setEstado(weatherText);
                    weather_estado.setText(weatherText);
                    // Guardar valor
                    if (instanciaFragmento != null) {
                        instanciaFragmento.guardarValorClima(KEY_WEATHER_ESTADO_CLIMA, weatherText);
                    }
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
                    String textoLluvia = value + " " + unit + "³";
                    lluvia.setText(textoLluvia);
                    // Guardar valor
                    if (instanciaFragmento != null) {
                        instanciaFragmento.guardarValorClima(KEY_LLUVIA, textoLluvia);
                    }
                    Log.i("Precipitation", "Valor: " + value + " " + unit + "³");
                } catch (Exception e) {
                    lluvia.setText("No Data");
                    Log.e("Error", "Precipitation");
                }

                try {
                    // Obteniendo el valor de RelativeHumidity
                    int relativeHumidity = jsonObject.getInt("RelativeHumidity");
                    datosClima.setHumedad(String.valueOf(relativeHumidity));
                    String textoHumedad = String.valueOf(relativeHumidity + "%");
                    humedad.setText(textoHumedad);
                    // Guardar valor
                    if (instanciaFragmento != null) {
                        instanciaFragmento.guardarValorClima(KEY_HUMEDAD, textoHumedad);
                    }
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
                    String textoViento = String.format("%s %s %s° %s", valueWind, unitWind, grados, orientacion);
                    viento.setText(textoViento);
                    // Guardar valor
                    if (instanciaFragmento != null) {
                        instanciaFragmento.guardarValorClima(KEY_VIENTO, textoViento);
                    }
                    Log.i("Wind", "Valor: " + valueWind + " " + unitWind + " " + grados + "°" + " " + orientacion);
                } catch (Exception e) {
                    viento.setText("No Data");
                    Log.e("Error", "Wind");
                }

                try {
                    // Obteniendo el valor de UVIndex
                    int uvIndex = jsonObject.getInt("UVIndex");
                    datosClima.setRayosUV(String.valueOf(uvIndex));
                    String textoRayos = "";
                    if (uvIndex >= 0 && uvIndex <= 2) {
                        textoRayos = String.valueOf(uvIndex + " " + "Bajo");
                    }
                    if (uvIndex >= 3 && uvIndex <= 5) {
                        textoRayos = String.valueOf(uvIndex + " " + "Moderado");
                    }
                    if (uvIndex >= 6 && uvIndex <= 7) {
                        textoRayos = String.valueOf(uvIndex + " " + "Alto");
                    }
                    if (uvIndex >= 8 && uvIndex <= 10) {
                        textoRayos = String.valueOf(uvIndex + " " + "Muy Alto");
                    }
                    if (uvIndex >= 11) {
                        textoRayos = String.valueOf(uvIndex + " " + "Extremadamente Alto");
                    }
                    rayos.setText(textoRayos);
                    // Guardar valor
                    if (instanciaFragmento != null) {
                        instanciaFragmento.guardarValorClima(KEY_RAYOS, textoRayos);
                    }
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
                    // Obteniendo el valor de CloudCover
                    String cloudCover = jsonObject.getString("CloudCover");
                    datosClima.setObstruccion(cloudCover);
                    if (cloudCover.isEmpty()) {
                        obstruccion.setText("No Data");
                    } else {
                        obstruccion.setText(cloudCover + "%");
                    }
                    Log.i("CloudCover", "Valor: " + cloudCover);
                } catch (Exception e) {
                    obstruccion.setText("No Data");
                    Log.e("Error", "CloudCover");
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
                
                // Actualizar el timestamp de la última actualización de clima
                if (instanciaFragmento != null) {
                    instanciaFragmento.actualizarTimestampClima();
                }
                
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

    public void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);

        // Manejando la ubicación de la vista
        vista = new ViewModelProvider(this).get(FragmentoModeloVista.class);
        //Variable que permite ubicar la posición del fragmento en la actividad principal
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        //Regresando la ubicación actual a la actividad principal
        vista.setIndex(index);
        //Instanciando con la clase de notificación por voz
        conversor = new NotificacionesVoz();
        conversor.init(getActivity());
    }

    private void mostrarAmarillo(String texto) {
        //Mostrar amarillo
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Media
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = new View(getActivity());
        view = getLayoutInflater().inflate(R.layout.mensaje_amarillo, view.findViewById(R.id.contenedorAmarillo));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloAmarillo)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoAmarillo)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okAmarillo)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okAmarillo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void mostrarNaranja(String texto) {
        //Mostrar naranja
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Alta
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = new View(getActivity());
        view = getLayoutInflater().inflate(R.layout.mensaje_naranja, view.findViewById(R.id.contenedorNaranja));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloNaranja)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoNaranja)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okNaranja)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okNaranja).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void mostrarVerde(String texto) {
        //Mostrar verde
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Bajo
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = new View(getActivity());
        view = getLayoutInflater().inflate(R.layout.mensaje_verde, view.findViewById(R.id.contenedorVerde));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloVerde)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoVerde)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okVerde)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okVerde).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void mostrarRojo(String texto) {
        //Mostrar rojo
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Muy ALta
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        View view = new View(getActivity());
        view = getLayoutInflater().inflate(R.layout.mensaje_rojo, view.findViewById(R.id.contenedorRojo));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloRojo)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoRojo)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okRojo)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okRojo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, BuildConfig.INTERVALO_ENTRE_PETICIONES_ACCUWEATHER_API_MS, 0, locationListener);
    }

    
    private void presentarFecha() {
        //Estableciendo la fecha para mostrar al usuario usando la fecha del sistema
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String fechaActual = formatoFecha.format(new Date());
        actual.setText(fechaActual);
    }

    public void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        conversor.apagar();
        
        // Limpiar la referencia estática para evitar memory leaks
        if (instanciaFragmento == this) {
            instanciaFragmento = null;
        }
    }

    public void onResume() {
        //Reinicia la interacción con la actividad
        super.onResume();
        if (locationPermissionGranted) {
            startLocationUpdates();
        }
    }

    public void onStart() {
        //Inicia la interacción con la actividad
        super.onStart();
        // Cargar y mostrar la última actualización
        mostrarUltimaActualizacionClima();
        // Cargar los últimos valores guardados al regresar al fragmento
        cargarUltimosValoresClima();
    }
    
    public void onPause() {
        //Suspende la interacción con la actividad
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    public void onStop() {
        //Para la interacción con la actividad
        super.onStop();
    }
    /**
     * Actualiza la marca de tiempo de la última actualización del clima
     */
    public void actualizarTimestampClima() {
        if (getActivity() != null) {
            // Obtener el timestamp actual del sistema
            long timestampActual = System.currentTimeMillis();
            
            SharedPreferences prefs = getActivity().getSharedPreferences(CLIMA_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(CLIMA_LAST_UPDATE_KEY, timestampActual);
            editor.apply();
            
            // Actualizar inmediatamente la visualización
            mostrarUltimaActualizacionClima();
            
            Log.i("ClimaTimestamp", "Timestamp actualizado: " + timestampActual);
        }
    }
    
    /**
     * Carga los últimos valores guardados desde SharedPreferences
     */
    private void cargarUltimosValoresClima() {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(CLIMA_PREFS_NAME, Context.MODE_PRIVATE);
            
            // Cargar último valor de temperatura actual (por defecto "0 °C")
            String ultimaTemperatura = prefs.getString(KEY_TEMPERATURA_ACTUAL, "0 °C");
            if (temperatura_actual != null) temperatura_actual.setText(ultimaTemperatura);
            
            // Cargar último valor de weather estado (por defecto "Sin datos")
            String ultimoWeatherEstado = prefs.getString(KEY_WEATHER_ESTADO_CLIMA, "Sin datos");
            if (weather_estado != null) weather_estado.setText(ultimoWeatherEstado);
            
            // Cargar último valor de obstrucción (por defecto "Sin datos")
            String ultimaObstruccion = prefs.getString(KEY_OBSTRUCCION, "Sin datos");
            if (obstruccion != null) obstruccion.setText(ultimaObstruccion);
            
            // Cargar último valor de visibilidad (por defecto "0 km")
            String ultimaVisibilidad = prefs.getString(KEY_VISIBILIDAD_CLIMA, "0 km");
            if (visibilidad != null) visibilidad.setText(ultimaVisibilidad);
            
            // Cargar último valor de presión (por defecto "0 mb")
            String ultimaPresion = prefs.getString(KEY_PRESION, "0 mb");
            if (presion != null) presion.setText(ultimaPresion);
            
            // Cargar último valor de humedad (por defecto "0%")
            String ultimaHumedad = prefs.getString(KEY_HUMEDAD, "0%");
            if (humedad != null) humedad.setText(ultimaHumedad);
            
            // Cargar último valor de viento (por defecto "0 km/h")
            String ultimoViento = prefs.getString(KEY_VIENTO, "0 km/h");
            if (viento != null) viento.setText(ultimoViento);
            
            // Cargar último valor de lluvia (por defecto "0 mm³")
            String ultimaLluvia = prefs.getString(KEY_LLUVIA, "0 mm³");
            if (lluvia != null) lluvia.setText(ultimaLluvia);
            
            // Cargar último valor de rayos UV (por defecto "0 Bajo")
            String ultimosRayos = prefs.getString(KEY_RAYOS, "0 Bajo");
            if (rayos != null) rayos.setText(ultimosRayos);
        }
    }
    
    /**
     * Guarda un valor en SharedPreferences
     */
    private void guardarValorClima(String clave, String valor) {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(CLIMA_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(clave, valor);
            editor.apply();
        }
    }
    
    /**
     * Muestra la última actualización del clima en el TextView correspondiente
     */
    private void mostrarUltimaActualizacionClima() {
        if (getActivity() != null && txtUltimaActualizacionClima != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(CLIMA_PREFS_NAME, Context.MODE_PRIVATE);
            long lastUpdate = prefs.getLong(CLIMA_LAST_UPDATE_KEY, 0);
            
            if (lastUpdate == 0) {
                txtUltimaActualizacionClima.setText("Sin datos de clima");
            } else {
                // Usar el mismo formato que el resto de la aplicación
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String fechaFormateada = sdf.format(new Date(lastUpdate));
                txtUltimaActualizacionClima.setText("Última actualización: " + fechaFormateada);
                
                Log.i("ClimaDisplay", "Mostrando última actualización: " + fechaFormateada);
            }
        }
    }
}