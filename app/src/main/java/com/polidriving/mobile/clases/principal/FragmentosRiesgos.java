//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases que permiten el control de vida de los Fragmentos de la App
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
//Clases usadas para el uso de voz
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
import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.content.Context;
import android.widget.TextView;
import java.text.MessageFormat;
import org.json.JSONException;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Button;
import android.os.AsyncTask;
import java.util.ArrayList;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Calendar;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import java.net.URL;

/**
 * @noinspection ALL
 */
@SuppressLint({"StaticFieldLeak", "SetTextI18n", "DefaultLocale"})
public class FragmentosRiesgos extends Fragment {
    //Creación de variables a usar e instancia a la clase de notificaciones de voz TTS
    static ArrayList<ParametrosGeoposition> detallesGeoposicionamiento = new ArrayList<>();
    static ArrayList<ParametrosAccuWeather> detallesCurrentCondintions = new ArrayList<>();
    private static final String ARG_SECTION_NUMBER = "section_number";
    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private NotificacionesVoz conversor = null;
    static TextView precipitacionTitulo;
    //static ImageView resultadoBandera;
    static TextView precipitacionLevel;
    private FragmentoModeloVista vista;
    boolean locationPermissionGranted;
    LocationListener locationListener;
    static TextView visibilidadTitulo;
    static TextView visibilidadLevel;
    static TextView velocidadTitulo;
    LocationManager locationManager;
    Boolean get_set_MuyAlta = false;
    static LinearLayout grupoActual;
    static LinearLayout grupoNivel;
    static TextView weather_estado;
    static TextView resultadoTexto;
    static TextView velocidadLevel;
    static TextView cardiacoTitulo;
    static TextView cardiacoLevel;
    static TextView precipitacion;
    Boolean get_set_Media = false;
    //static TextView porcentaje;
    Boolean get_set_Alta = false;
    Boolean get_set_Baja = false;
    static TextView onSiteTitulo;
    static TextView onHourTitulo;
    static TextView onSiteLevel;
    static TextView visibilidad;
    static TextView onHourLevel;
    static TextView motorTitulo;
    static TextView motorLevel;
    static TextView velocidad;
    static TextView rpmTitulo;
    static TextView rpmLevel;
    static TextView cardiaco;
    static TextView actual;
    static TextView onSite;
    static TextView onHour;
    static TextView motor;
    static TextView rpm;

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
        View view = inflater.inflate(R.layout.fragmento_principal_riesgos, container, false);
        //Inicializando las variables con los elementos de la actividad
        precipitacionLevel = view.findViewById(R.id.txtNivelPrecipitacion);
        //resultadoBandera = view.findViewById(R.id.imagenBanderaDatos);
        //porcentaje = view.findViewById(R.id.txtPorcentajeDatosRiesgo);
        visibilidadLevel = view.findViewById(R.id.txtNivelVisibilidad);
        precipitacionTitulo = view.findViewById(R.id.txtPrecipitacion);
        resultadoTexto = view.findViewById(R.id.txtMedidaDatosRiesgo);
        precipitacion = view.findViewById(R.id.txtDatoPrecipitacion);
        actual = view.findViewById(R.id.txtUltimaUpdateFechaRiesgo);
        visibilidadTitulo = view.findViewById(R.id.txtVisibilidad);
        velocidadLevel = view.findViewById(R.id.txtNivelVelocidad);
        visibilidad = view.findViewById(R.id.txtDatoVisibilidad);
        cardiacoLevel = view.findViewById(R.id.txtNivelCardiaco);
        grupoActual = view.findViewById(R.id.grupoFechaRiesgo);
        velocidadTitulo = view.findViewById(R.id.txtVelocidad);
        weather_estado = view.findViewById(R.id.txtDatoClima);
        grupoNivel = view.findViewById(R.id.grupoNivelRiesgo);
        velocidad = view.findViewById(R.id.txtDatoVelocidad);
        onSiteLevel = view.findViewById(R.id.txtNivelOnSite);
        onHourLevel = view.findViewById(R.id.txtNivelOnHour);
        motorTitulo = view.findViewById(R.id.txtTemperatura);
        cardiacoTitulo = view.findViewById(R.id.txtCardiaco);
        motorLevel = view.findViewById(R.id.txtNivelMotor);
        cardiaco = view.findViewById(R.id.txtDatoCardiaco);
        onHourTitulo = view.findViewById(R.id.txtOnHour);
        onSiteTitulo = view.findViewById(R.id.txtOnSite);
        onSite = view.findViewById(R.id.txtDatoOnSite);
        onHour = view.findViewById(R.id.txtDatoOnHour);
        rpmLevel = view.findViewById(R.id.txtNivelRPM);
        motor = view.findViewById(R.id.txtDatoMotor);
        rpmTitulo = view.findViewById(R.id.txtRPM);
        rpm = view.findViewById(R.id.txtDatoRPM);

        //Estableciendo la fecha para mostrar al usuario
        //presentarFecha();
        actual.setText("Esperando…");

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
            }
        };
        if (locationPermissionGranted) {
            // Iniciando la actualización de la localización GPS del usuario
            startLocationUpdates();
        }

        //Creación de un segundo hilo que permite presentar la información de los datos en tiempo real
        Handler handler_datos = new Handler();
        handler_datos.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que obtiene los datos después de realizar la consulta POST
                startLocationUpdates();
                presentarDatos();
                presentarFecha();

                //Segmento de código que permite actualizar la información a presentar cada 5 segundos
                handler_datos.postDelayed(this, 5000);
            }
        }, 5000);

        //Creación de un tercer hilo que permite presentar las alertas gráficas y sonoras de los datos en tiempo real
        /*Handler handler_alertas = new Handler();
        handler_alertas.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que permite presentar las alertas según su nivel: Muy Alto, Alto, Media y Baja
                presentarAlertas();
                //Segmento de código que permite actualizar la información a presentar cada 5 segundos
                handler_alertas.postDelayed(this, 10000);
            }
        }, 10000);*/

        //Presentando la información en el fragmento de vista
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
                    // Obteniendo el valor del clima
                    weather_estado.setTextColor(Color.BLACK);
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
                    precipitacionTitulo.setTextColor(Color.BLACK);
                    precipitacionLevel.setTextColor(Color.BLACK);
                    precipitacion.setTextColor(Color.BLACK);
                    JSONObject precipitationSummary = jsonObject.getJSONObject("PrecipitationSummary");
                    JSONObject precipitation = precipitationSummary.getJSONObject("Precipitation");
                    JSONObject metric = precipitation.getJSONObject("Metric");
                    int value = metric.getInt("Value");
                    String unit = metric.getString("Unit");
                    datosClima.setLluvia(value + " " + unit + "³");
                    precipitacion.setText(value + " " + unit + "³");
                    if (value == 0) {
                        precipitacionTitulo.setTextColor(Color.BLACK);
                        precipitacionLevel.setTextColor(Color.BLACK);
                        precipitacion.setTextColor(Color.BLACK);
                        precipitacionLevel.setText("Ninguna");
                    }
                    if (value >= 0.1 && value <= 2.4) {
                        precipitacionTitulo.setTextColor(Color.BLACK);
                        precipitacionLevel.setTextColor(Color.BLACK);
                        precipitacion.setTextColor(Color.BLACK);
                        precipitacionLevel.setText("Ligera");
                    }
                    if (value >= 2.5 && value <= 10) {
                        //precipitacionTitulo.setTextColor(0xFFABAB10);
                        //precipitacionLevel.setTextColor(0xFFABAB10);
                        //precipitacion.setTextColor(0xFFABAB10);
                        precipitacionTitulo.setTextColor(Color.BLACK);
                        precipitacionLevel.setTextColor(Color.BLACK);
                        precipitacion.setTextColor(Color.BLACK);
                        precipitacionLevel.setText("Moderada");
                    }
                    if (value > 10 && value <= 50) {
                        //precipitacionTitulo.setTextColor(0xFFFF8000);
                        //precipitacionLevel.setTextColor(0xFFFF8000);
                        //precipitacion.setTextColor(0xFFFF8000);
                        precipitacionTitulo.setTextColor(Color.RED);
                        precipitacionLevel.setTextColor(Color.RED);
                        precipitacion.setTextColor(Color.RED);
                        precipitacionLevel.setText("Fuertes");
                    }
                    if (value > 50) {
                        precipitacionTitulo.setTextColor(Color.RED);
                        precipitacionLevel.setTextColor(Color.RED);
                        precipitacion.setTextColor(Color.RED);
                        precipitacionLevel.setText("Muy Fuertes");
                    }

                    Log.i("Precipitation", "Valor: " + value + " " + unit + "³");
                } catch (Exception e) {
                    precipitacion.setText("No Data");
                    Log.e("Error", "Precipitation");
                }

                try {
                    // Obteniendo el valor de Visibility
                    visibilidadTitulo.setTextColor(Color.BLACK);
                    visibilidadLevel.setTextColor(Color.BLACK);
                    visibilidad.setTextColor(Color.BLACK);
                    JSONObject visibility = jsonObject.getJSONObject("Visibility");
                    JSONObject metricVisibility = visibility.getJSONObject("Metric");
                    double valueVisibility = metricVisibility.getDouble("Value");
                    String unitVisibility = metricVisibility.getString("Unit");
                    datosClima.setVisibilidad(valueVisibility + " " + unitVisibility);
                    visibilidad.setText(String.format("%s %s", valueVisibility, unitVisibility));
                    if (valueVisibility >= 50) {
                        visibilidadTitulo.setTextColor(Color.BLACK);
                        visibilidadLevel.setTextColor(Color.BLACK);
                        visibilidad.setTextColor(Color.BLACK);
                        visibilidadLevel.setText("Excelente");
                    }
                    if (valueVisibility >= 10 && valueVisibility < 50) {
                        visibilidadTitulo.setTextColor(Color.BLACK);
                        visibilidadLevel.setTextColor(Color.BLACK);
                        visibilidad.setTextColor(Color.BLACK);
                        visibilidadLevel.setText("Buena");
                    }
                    if (valueVisibility >= 2.5 && valueVisibility < 10) {
                        //visibilidadTitulo.setTextColor(0xFFABAB10);
                        //visibilidadLevel.setTextColor(0xFFABAB10);
                        //visibilidad.setTextColor(0xFFABAB10);
                        visibilidadTitulo.setTextColor(Color.BLACK);
                        visibilidadLevel.setTextColor(Color.BLACK);
                        visibilidad.setTextColor(Color.BLACK);
                        visibilidadLevel.setText("Moderada");
                    }
                    if (valueVisibility >= 0.1 && valueVisibility < 2.5) {
                        //visibilidadTitulo.setTextColor(0xFFFF8000);
                        //visibilidadLevel.setTextColor(0xFFFF8000);
                        //visibilidad.setTextColor(0xFFFF8000);
                        visibilidadTitulo.setTextColor(Color.RED);
                        visibilidadLevel.setTextColor(Color.RED);
                        visibilidad.setTextColor(Color.RED);
                        visibilidadLevel.setText("Pobre");
                    }
                    if (valueVisibility == 0) {
                        visibilidadTitulo.setTextColor(Color.RED);
                        visibilidadLevel.setTextColor(Color.RED);
                        visibilidad.setTextColor(Color.RED);
                        visibilidadLevel.setText("Mala");
                    }

                    Log.i("Visibility", "Valor: " + valueVisibility + " " + unitVisibility);
                } catch (Exception e) {
                    visibilidad.setText("No Data");
                    Log.e("Error", "Visibility");
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

    public void onCreate(Bundle savedInstanceState) {
        // Creando un vista de los elementos de la actividad
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void presentarAlertas() {
        try {
            //Ocultando parámetro de información no usado
            //porcentaje.setVisibility(View.INVISIBLE);
            //resultadoBandera.setVisibility(View.INVISIBLE);
            //Segmento de código que permite observar en tiempo real los cambios que se dan en la información
            FragmentoModeloVista.getRespuestaAgente().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Verificando que los parametros recibidos no sean vacíos
                    assert s != null;
                    //Verificación o Creación del archivo local de configuraciones de Usuario
                    SharedPreferences configuracionUsuario = requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                    //Obteniendo los datos a presentar
                    boolean alertaMuyAlta = configuracionUsuario.getBoolean("Alerta Muy Alta", false);
                    boolean alertaMedia = configuracionUsuario.getBoolean("Alerta Media", false);
                    boolean alertaAlta = configuracionUsuario.getBoolean("Alerta Alta", false);
                    boolean alertaBaja = configuracionUsuario.getBoolean("Alerta Baja", false);
                    //Cambiando la información visual de los elementos
                    get_set_MuyAlta = alertaMuyAlta;
                    get_set_Media = alertaMedia;
                    get_set_Alta = alertaAlta;
                    get_set_Baja = alertaBaja;
                    //Comparando el valor recibido con el nivel 4
                    if (s.equals("{\"Output\": 4}") && get_set_MuyAlta) {
                        //Presentando las alertas gráficas de nivel 4 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        resultadoTexto.setText(getString(R.string.medida_datos_muy_alta));
                        //resultadoBandera.setImageResource(R.drawable.rojo);
                        //porcentaje.setText("25%");
                        //Iniciando alerta sonora de nivel 4
                        String mensaje = "Nivel de Riesgo Muy Alta";
                        conversor.palabra(mensaje);
                        mostrarRojo(mensaje);
                    }
                    if (s.equals("{\"Output\": 4}") && !get_set_MuyAlta) {
                        //Presentando las alertas gráficas de nivel 4 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        resultadoTexto.setText(getString(R.string.medida_datos_muy_alta));
                        //resultadoBandera.setImageResource(R.drawable.rojo);
                        //porcentaje.setText("25%");
                    }
                    //Comparando el valor recibido con el nivel 3
                    if (s.equals("{\"Output\": 3}") && get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 3 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        resultadoTexto.setText(getString(R.string.medida_datos_alta));
                        //resultadoBandera.setImageResource(R.drawable.naranja);
                        //porcentaje.setText("50%");
                        //Iniciando alerta sonora de nivel 3
                        String mensaje = "Nivel de Riesgo Alta";
                        conversor.palabra(mensaje);
                        mostrarNaranja(mensaje);
                    }
                    if (s.equals("{\"Output\": 3}") && !get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 3 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        resultadoTexto.setText(getString(R.string.medida_datos_alta));
                        //resultadoBandera.setImageResource(R.drawable.naranja);
                        //porcentaje.setText("50%");
                    }
                    //Comparando el valor recibido con el nivel 2
                    if (s.equals("{\"Output\": 2}") && get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 2 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        resultadoTexto.setText(getString(R.string.medida_datos_media));
                        //resultadoBandera.setImageResource(R.drawable.amarillo);
                        //porcentaje.setText("75%");
                        //Iniciando alerta sonora de nivel 2
                        String mensaje = "Nivel de Riesgo Media";
                        conversor.palabra(mensaje);
                        mostrarAmarillo(mensaje);
                    }
                    if (s.equals("{\"Output\": 2}") && !get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 2 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        resultadoTexto.setText(getString(R.string.medida_datos_media));
                        //resultadoBandera.setImageResource(R.drawable.amarillo);
                        //porcentaje.setText("75%");
                    }
                    //Comparando el valor recibido con el nivel 1
                    if (s.equals("{\"Output\": 1}") && get_set_Baja) {
                        //Presentando las alertas gráficas de nivel 1 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        resultadoTexto.setText(getString(R.string.medida_datos_baja));
                        //resultadoBandera.setImageResource(R.drawable.verde);
                        //porcentaje.setText("100%");
                        //Iniciando alerta sonora de nivel 1
                        String mensaje = "Nivel de Riesgo Baja";
                        conversor.palabra(mensaje);
                        mostrarVerde(mensaje);
                    }
                    if (s.equals("{\"Output\": 1}") && !get_set_Baja) {
                        //Presentando las alertas gráficas de nivel 1 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        resultadoTexto.setText(getString(R.string.medida_datos_baja));
                        //resultadoBandera.setImageResource(R.drawable.verde);
                        //porcentaje.setText("100%");

                    }
                }
            });
        } catch (Exception e) {
            Log.e("Error Alertas", e.toString());
        }
    }

    private void presentarDatos() {
        //Segmento de código que obtiene desde el agente y la actividad principal los datos a presentar en pantalla
        try {
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getAccidents_onsite().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    onSiteTitulo.setTextColor(Color.BLACK);
                    onSiteLevel.setTextColor(Color.BLACK);
                    onSite.setTextColor(Color.BLACK);
                    onSite.setText(s);
                    if (Double.parseDouble(s) == 0) {
                        onSiteTitulo.setTextColor(Color.BLACK);
                        onSiteLevel.setTextColor(Color.BLACK);
                        onSite.setTextColor(Color.BLACK);
                        onSiteLevel.setText("Ninguno");
                    }
                    if (Double.parseDouble(s) >= 1 && Double.parseDouble(s) <= 8) {
                        onSiteTitulo.setTextColor(Color.BLACK);
                        onSiteLevel.setTextColor(Color.BLACK);
                        onSite.setTextColor(Color.BLACK);
                        onSiteLevel.setText("Bajo");
                    }
                    if (Double.parseDouble(s) >= 9 && Double.parseDouble(s) <= 30) {
                        //onSiteTitulo.setTextColor(0xFFC3C32B);
                        //onSiteLevel.setTextColor(0xFFC3C32B);
                        //onSite.setTextColor(0xFFC3C32B);
                        onSiteTitulo.setTextColor(Color.BLACK);
                        onSiteLevel.setTextColor(Color.BLACK);
                        onSite.setTextColor(Color.BLACK);
                        onSiteLevel.setText("Moderado");
                    }
                    if (Double.parseDouble(s) >= 31 && Double.parseDouble(s) <= 132) {
                        //onSiteTitulo.setTextColor(0xFFFF8000);
                        //onSiteLevel.setTextColor(0xFFFF8000);
                        //onSite.setTextColor(0xFFFF8000);
                        onSiteTitulo.setTextColor(Color.RED);
                        onSiteLevel.setTextColor(Color.RED);
                        onSite.setTextColor(Color.RED);
                        onSiteLevel.setText("Alto");
                    }
                    if (Double.parseDouble(s) >= 133) {
                        onSiteTitulo.setTextColor(Color.RED);
                        onSiteLevel.setTextColor(Color.RED);
                        onSite.setTextColor(Color.RED);
                        onSiteLevel.setText("Muy Alto");
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getAccidents_onhour().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    onHourTitulo.setTextColor(Color.BLACK);
                    onHourLevel.setTextColor(Color.BLACK);
                    onHour.setTextColor(Color.BLACK);
                    onHour.setText(s);
                    if (Double.parseDouble(s) == 0) {
                        onHourTitulo.setTextColor(Color.BLACK);
                        onHourLevel.setTextColor(Color.BLACK);
                        onHour.setTextColor(Color.BLACK);
                        onHourLevel.setText("Ninguno");
                    }
                    if (Double.parseDouble(s) >= 1 && Double.parseDouble(s) <= 2) {
                        //onHourTitulo.setTextColor(0xFFABAB10);
                        //onHourLevel.setTextColor(0xFFABAB10);
                        //onHour.setTextColor(0xFFABAB10);
                        onHourTitulo.setTextColor(Color.BLACK);
                        onHourLevel.setTextColor(Color.BLACK);
                        onHour.setTextColor(Color.BLACK);
                        onHourLevel.setText("Bajo");
                    }
                    if (Double.parseDouble(s) >= 3 && Double.parseDouble(s) <= 9) {
                        //onHourTitulo.setTextColor(0xFFFF8000);
                        //onHourLevel.setTextColor(0xFFFF8000);
                        //onHour.setTextColor(0xFFFF8000);
                        onHourTitulo.setTextColor(Color.RED);
                        onHourLevel.setTextColor(Color.RED);
                        onHour.setTextColor(Color.RED);
                        onHourLevel.setText("Moderado");
                    }
                    if (Double.parseDouble(s) >= 10) {
                        onHourTitulo.setTextColor(Color.RED);
                        onHourLevel.setTextColor(Color.RED);
                        onHour.setTextColor(Color.RED);
                        onHourLevel.setText("Alto");
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getVelocidad().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    velocidadTitulo.setTextColor(Color.BLACK);
                    velocidadLevel.setTextColor(Color.BLACK);
                    velocidad.setTextColor(Color.BLACK);
                    velocidad.setText(s + " Km/h");
                    if (Double.parseDouble(s) == 0) {
                        velocidadTitulo.setTextColor(Color.BLACK);
                        velocidadLevel.setTextColor(Color.BLACK);
                        velocidad.setTextColor(Color.BLACK);
                        velocidadLevel.setText("Normal");
                    }
                    if (Double.parseDouble(s) >= 1 && Double.parseDouble(s) <= 10) {
                        velocidadTitulo.setTextColor(Color.BLACK);
                        velocidadLevel.setTextColor(Color.BLACK);
                        velocidad.setTextColor(Color.BLACK);
                        velocidadLevel.setText("Ligero");
                    }
                    if (Double.parseDouble(s) >= 11 && Double.parseDouble(s) <= 20) {
                        //velocidadTitulo.setTextColor(0xFFABAB10);
                        //velocidadLevel.setTextColor(0xFFABAB10);
                        //velocidad.setTextColor(0xFFABAB10);
                        velocidadTitulo.setTextColor(Color.BLACK);
                        velocidadLevel.setTextColor(Color.BLACK);
                        velocidad.setTextColor(Color.BLACK);
                        velocidadLevel.setText("Moderado");
                    }
                    if (Double.parseDouble(s) >= 21 && Double.parseDouble(s) <= 40) {
                        //velocidadTitulo.setTextColor(0xFFFF8000);
                        //velocidadLevel.setTextColor(0xFFFF8000);
                        //velocidad.setTextColor(0xFFFF8000);
                        velocidadTitulo.setTextColor(Color.RED);
                        velocidadLevel.setTextColor(Color.RED);
                        velocidad.setTextColor(Color.RED);
                        velocidadLevel.setText("Serio");
                    }
                    if (Double.parseDouble(s) >= 41) {
                        velocidadTitulo.setTextColor(Color.RED);
                        velocidadLevel.setTextColor(Color.RED);
                        velocidad.setTextColor(Color.RED);
                        velocidadLevel.setText("Muy Serio");
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getMotor().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    motorTitulo.setTextColor(Color.BLACK);
                    motorLevel.setTextColor(Color.BLACK);
                    motor.setTextColor(Color.BLACK);
                    motor.setText(s + " °C");
                    if (Double.parseDouble(s) >= 0 && Double.parseDouble(s) <= 82) {
                        motorTitulo.setTextColor(Color.BLACK);
                        motorLevel.setTextColor(Color.BLACK);
                        motor.setTextColor(Color.BLACK);
                        motorLevel.setText("Bajo");
                    }
                    if (Double.parseDouble(s) >= 83 && Double.parseDouble(s) <= 94) {
                        //motorTitulo.setTextColor(0xFFABAB10);
                        //motorLevel.setTextColor(0xFFABAB10);
                        //motor.setTextColor(0xFFABAB10);
                        motorTitulo.setTextColor(Color.BLACK);
                        motorLevel.setTextColor(Color.BLACK);
                        motor.setTextColor(Color.BLACK);
                        motorLevel.setText("Normal");
                    }
                    if (Double.parseDouble(s) >= 95 && Double.parseDouble(s) <= 104) {
                        //motorTitulo.setTextColor(0xFFFF8000);
                        //motorLevel.setTextColor(0xFFFF8000);
                        //motor.setTextColor(0xFFFF8000);
                        motorTitulo.setTextColor(Color.RED);
                        motorLevel.setTextColor(Color.RED);
                        motor.setTextColor(Color.RED);
                        motorLevel.setText("Alto");
                    }
                    if (Double.parseDouble(s) >= 105) {
                        motorTitulo.setTextColor(Color.RED);
                        motorLevel.setTextColor(Color.RED);
                        motor.setTextColor(Color.RED);
                        motorLevel.setText("Overheating");
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getHeart().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    cardiacoTitulo.setTextColor(Color.BLACK);
                    cardiacoLevel.setTextColor(Color.BLACK);
                    cardiaco.setTextColor(Color.BLACK);
                    cardiaco.setText(s + " ppm");

                    if (Double.parseDouble(s) >= 0 && Double.parseDouble(s) <= 59) {
                        cardiacoTitulo.setTextColor(Color.BLACK);
                        cardiacoLevel.setTextColor(Color.BLACK);
                        cardiaco.setTextColor(Color.BLACK);
                        cardiacoLevel.setText("Bradicardia");
                    }
                    if (Double.parseDouble(s) >= 60 && Double.parseDouble(s) <= 80) {
                        cardiacoTitulo.setTextColor(Color.BLACK);
                        cardiacoLevel.setTextColor(Color.BLACK);
                        cardiaco.setTextColor(Color.BLACK);
                        cardiacoLevel.setText("Sinus zona a");
                    }
                    if (Double.parseDouble(s) >= 81 && Double.parseDouble(s) <= 100) {
                        //cardiacoTitulo.setTextColor(0xFFABAB10);
                        //cardiacoLevel.setTextColor(0xFFABAB10);
                        //cardiaco.setTextColor(0xFFABAB10);
                        cardiacoTitulo.setTextColor(Color.BLACK);
                        cardiacoLevel.setTextColor(Color.BLACK);
                        cardiaco.setTextColor(Color.BLACK);
                        cardiacoLevel.setText("Sinus zona b");
                    }
                    if (Double.parseDouble(s) >= 101 && Double.parseDouble(s) <= 120) {
                        //cardiacoTitulo.setTextColor(0xFFFF8000);
                        //cardiacoLevel.setTextColor(0xFFFF8000);
                        //cardiaco.setTextColor(0xFFFF8000);
                        cardiacoTitulo.setTextColor(Color.RED);
                        cardiacoLevel.setTextColor(Color.RED);
                        cardiaco.setTextColor(Color.RED);
                        cardiacoLevel.setText("Taquicardia Ligera");
                    }
                    if (Double.parseDouble(s) >= 121) {
                        cardiacoTitulo.setTextColor(Color.RED);
                        cardiacoLevel.setTextColor(Color.RED);
                        cardiaco.setTextColor(Color.RED);
                        cardiacoLevel.setText("Taquicardia Severa");
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getRpm().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    rpmTitulo.setTextColor(Color.BLACK);
                    rpmLevel.setTextColor(Color.BLACK);
                    rpm.setTextColor(Color.BLACK);
                    rpm.setText(s + " rpm");
                    if (Double.parseDouble(s) >= 0 && Double.parseDouble(s) <= 1500) {
                        rpmTitulo.setTextColor(Color.BLACK);
                        rpmLevel.setTextColor(Color.BLACK);
                        rpm.setTextColor(Color.BLACK);
                        rpmLevel.setText("Bajo");
                    }
                    if (Double.parseDouble(s) >= 1501 && Double.parseDouble(s) <= 3000) {
                        rpmTitulo.setTextColor(Color.BLACK);
                        rpmLevel.setTextColor(Color.BLACK);
                        rpm.setTextColor(Color.BLACK);
                        //rpmTitulo.setTextColor(0xFFABAB10);
                        //rpmLevel.setTextColor(0xFFABAB10);
                        //rpm.setTextColor(0xFFABAB10);
                        rpmLevel.setText("Normal");
                    }
                    if (Double.parseDouble(s) >= 3001 && Double.parseDouble(s) <= 5000) {
                        //rpmTitulo.setTextColor(0xFFFF8000);
                        //rpmLevel.setTextColor(0xFFFF8000);
                        //rpm.setTextColor(0xFFFF8000);
                        rpmTitulo.setTextColor(Color.RED);
                        rpmLevel.setTextColor(Color.RED);
                        rpm.setTextColor(Color.RED);
                        rpmLevel.setText("Alto");
                    }
                    if (Double.parseDouble(s) >= 5001) {
                        rpmTitulo.setTextColor(Color.RED);
                        rpmLevel.setTextColor(Color.RED);
                        rpm.setTextColor(Color.RED);
                        rpmLevel.setText("Muy Alto");
                    }
                }
            });
        } catch (Exception e) {
            //TODO
        }
    }

    private void presentarFecha() {
        //Estableciendo la fecha para mostrar al usuario
        Calendar fecha = Calendar.getInstance();
        int day = fecha.get(Calendar.DAY_OF_MONTH);
        // Se suma 1 porque Calendar.MONTH devuelve un valor basado en cero
        int month = fecha.get(Calendar.MONTH) + 1;
        int year = fecha.get(Calendar.YEAR);
        int hour = fecha.get(Calendar.HOUR_OF_DAY);
        int minute = fecha.get(Calendar.MINUTE);
        int seconds = fecha.get(Calendar.SECOND);
        String amPm = (fecha.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        actual.setText(String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + String.format("%02d", year) + "  " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds) + " " + amPm);
    }

    public void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        conversor.apagar();
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
}