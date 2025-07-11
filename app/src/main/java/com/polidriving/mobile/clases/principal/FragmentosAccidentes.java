//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases que permiten el control de vida de los Fragmentos de la App
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
//Clases usadas para el uso de voz
import com.polidriving.mobile.clases.notificaciones.NotificacionesVoz;
import com.polidriving.mobile.BuildConfig;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.location.LocationListener;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.location.LocationManager;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.widget.LinearLayout;
import androidx.lifecycle.Observer;
import android.view.LayoutInflater;
import android.location.Location;
import android.os.CountDownTimer;
import com.polidriving.mobile.R;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import java.text.MessageFormat;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Handler;
import java.util.Calendar;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @noinspection ALL
 */
@SuppressLint({"StaticFieldLeak", "SetTextI18n", "DefaultLocale"})
public class FragmentosAccidentes extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private NotificacionesVoz conversor = null;
    boolean locationPermissionGranted;
    LocationListener locationListener;
    LocationManager locationManager;
    Boolean get_set_MuyAlta = false;
    static LinearLayout grupoActual;
    static LinearLayout grupoNivel;
    static TextView resultadoTexto;
    Boolean get_set_Media = false;
    Boolean get_set_Alta = false;
    Boolean get_set_Baja = false;
    static TextView velocidad;
    static TextView segmento;
    static TextView longitud;
    static TextView latitud;
    static TextView control;
    static TextView onSite;
    static TextView onHour;
    static TextView actual;
    static TextView via;
    static TextView longitudReal;
    static TextView latitudReal;
    private TextView txtUltimaActualizacionAccidentes;
    private static final String ACCIDENTES_PREFS_NAME = "AccidentesPrefs";
    private static final String ACCIDENTES_LAST_UPDATE_KEY = "last_accidentes_update";
    private static final String KEY_VELOCIDAD_ACC = "ultimo_valor_velocidad_acc";
    private static final String KEY_SEGMENTO = "ultimo_valor_segmento";
    private static final String KEY_LONGITUD = "ultimo_valor_longitud";
    private static final String KEY_LATITUD = "ultimo_valor_latitud";
    private static final String KEY_CONTROL = "ultimo_valor_control";
    private static final String KEY_ONSITE_ACC = "ultimo_valor_onsite_acc";
    private static final String KEY_ONHOUR_ACC = "ultimo_valor_onhour_acc";
    private static final String KEY_VIA = "ultimo_valor_via";
    private static final String KEY_LONGITUD_REAL = "ultimo_valor_longitud_real";
    private static final String KEY_LATITUD_REAL = "ultimo_valor_latitud_real";

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
        View view = inflater.inflate(R.layout.fragmento_principal_accidentes, container, false);
        
        //Inicializando el TextView de última actualización
        txtUltimaActualizacionAccidentes = view.findViewById(R.id.txtUltimaActualizacionAccidentes);
        
        //Inicializando las variables con los elementos de la actividad
        //resultadoBandera = view.findViewById(R.id.imagenBanderaDatosAccidentes);
        //porcentaje = view.findViewById(R.id.txtPorcentajeDatosAccidentes);
        resultadoTexto = view.findViewById(R.id.txtMedidaDatosAccidentes);
        longitud = view.findViewById(R.id.txtRespuestaLongitudAccidente);
        actual = view.findViewById(R.id.txtUltimaUpdateFechaAccidentes);
        latitud = view.findViewById(R.id.txtRespuestaLatitudAccidente);
        onSite = view.findViewById(R.id.txtRespuestaAccidentesSitio);
        velocidad = view.findViewById(R.id.txtRespuestaVelocidadVia);
        onHour = view.findViewById(R.id.txtRespuestaAccidentesHora);
        control = view.findViewById(R.id.txtRespuestaPuntoControl);
        grupoActual = view.findViewById(R.id.grupoFechaAccidentes);
        grupoNivel = view.findViewById(R.id.grupoNivelAccidentes);
        segmento = view.findViewById(R.id.txtRespuestaSegmento);
        via = view.findViewById(R.id.txtRespuestaIdVia);

        longitudReal = view.findViewById(R.id.txtRespuestaLongitudAccidenteReal);
        latitudReal = view.findViewById(R.id.txtRespuestaLatitudAccidenteReal);

        //Estableciendo la fecha para mostrar al usuario
        //presentarFecha();
        actual.setText("Esperando…");

        // Cargar los últimos valores guardados antes de observar los datos
        cargarUltimosValoresAccidentes();

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
                Log.i("Coordenadas:", "Lat: " + latitude + ", Lon: " + longitude);

                longitudReal.setText(Double.toString(longitude));
                latitudReal.setText(Double.toString(latitude));

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
                //presentarFecha();
                //Segmento de código que permite actualizar la información a presentar según ACCIDENTES_UPDATE_INTERVAL_MS
                handler_datos.postDelayed(this, BuildConfig.ACCIDENTES_UPDATE_INTERVAL_MS);
            }
        }, BuildConfig.ACCIDENTES_UPDATE_INTERVAL_MS);

        //Creación de un tercer hilo que permite presentar las alertas gráficas y sonoras de los datos en tiempo real
        /*Handler handler_alertas = new Handler();
        handler_alertas.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que permite presentar las alertas según su nivel: Muy Alto, Alto, Media y Baja
                //Segmento de código que permite actualizar la información a presentar cada 5 segundos
                handler_alertas.postDelayed(this, BuildConfig.ACCIDENTES_UPDATE_INTERVAL_SECONDS);
            }
        }, BuildConfig.ACCIDENTES_UPDATE_INTERVAL_SECONDS);*/

        //Presentando la información en el fragmento de vista
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        // Creando un vista de los elementos de la actividad
        super.onCreate(savedInstanceState);

        // Manejando la ubicación de la vista
        // Removed ViewModel instantiation
        //Variable que permite ubicar la posición del fragmento en la actividad principal
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        //Regresando la ubicación actual a la actividad principal
        FragmentoModeloVista.setIndex(index);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, BuildConfig.INTERVALO_ENTRE_PETICIONES_API_PREDICTOR_MS, 0, locationListener);
    }

    /**
     * Carga los últimos valores guardados desde SharedPreferences
     */
    private void cargarUltimosValoresAccidentes() {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCIDENTES_PREFS_NAME, Context.MODE_PRIVATE);
            
            // Cargar último valor de velocidad (por defecto "0 Km/h")
            String ultimaVelocidad = prefs.getString(KEY_VELOCIDAD_ACC, "0 Km/h");
            if (velocidad != null) velocidad.setText(ultimaVelocidad);
            
            // Cargar último valor de segmento (por defecto "0")
            String ultimoSegmento = prefs.getString(KEY_SEGMENTO, "0");
            if (segmento != null) segmento.setText(ultimoSegmento);
            
            // Cargar último valor de longitud (por defecto "0.0")
            String ultimaLongitud = prefs.getString(KEY_LONGITUD, "0.0");
            if (longitud != null) longitud.setText(ultimaLongitud);
            
            // Cargar último valor de latitud (por defecto "0.0")
            String ultimaLatitud = prefs.getString(KEY_LATITUD, "0.0");
            if (latitud != null) latitud.setText(ultimaLatitud);
            
            // Cargar último valor de control (por defecto "0")
            String ultimoControl = prefs.getString(KEY_CONTROL, "0");
            if (control != null) control.setText(ultimoControl);
            
            // Cargar último valor de onsite (por defecto "0")
            String ultimoOnsite = prefs.getString(KEY_ONSITE_ACC, "0");
            if (onSite != null) onSite.setText(ultimoOnsite);
            
            // Cargar último valor de onhour (por defecto "0")
            String ultimoOnhour = prefs.getString(KEY_ONHOUR_ACC, "0");
            if (onHour != null) onHour.setText(ultimoOnhour);
            
            // Cargar último valor de via (por defecto "0")
            String ultimaVia = prefs.getString(KEY_VIA, "0");
            if (via != null) via.setText(ultimaVia);
            
            // Cargar último valor de longitud real (por defecto "0.0")
            String ultimaLongitudReal = prefs.getString(KEY_LONGITUD_REAL, "0.0");
            if (longitudReal != null) longitudReal.setText(ultimaLongitudReal);
            
            // Cargar último valor de latitud real (por defecto "0.0")
            String ultimaLatitudReal = prefs.getString(KEY_LATITUD_REAL, "0.0");
            if (latitudReal != null) latitudReal.setText(ultimaLatitudReal);
        }
    }
    
    /**
     * Guarda un valor en SharedPreferences
     */
    private void guardarValorAccidentes(String clave, String valor) {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCIDENTES_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(clave, valor);
            editor.apply();
        }
    }

    /**
     * Actualiza la marca de tiempo de la última actualización de accidentes
     */
    public void actualizarTimestampAccidentes() {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCIDENTES_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(ACCIDENTES_LAST_UPDATE_KEY, System.currentTimeMillis());
            editor.apply();
            
            mostrarUltimaActualizacionAccidentes();
        }
    }
    
    /**
     * Muestra la última actualización en el TextView correspondiente
     */
    private void mostrarUltimaActualizacionAccidentes() {
        if (getActivity() != null && txtUltimaActualizacionAccidentes != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(ACCIDENTES_PREFS_NAME, Context.MODE_PRIVATE);
            long lastUpdate = prefs.getLong(ACCIDENTES_LAST_UPDATE_KEY, 0);
            
            if (lastUpdate == 0) {
                txtUltimaActualizacionAccidentes.setText("Nunca actualizado");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String fechaFormateada = sdf.format(new Date(lastUpdate));
                txtUltimaActualizacionAccidentes.setText("Última actualización: " + fechaFormateada);
            }
        }
    }

    private void presentarDatos() {
        //Segmento de código que obtiene desde el agente y la actividad principal los datos a presentar en pantalla
        try {
            // Variable para controlar si se han actualizado los datos
            boolean datosActualizados = false;
            
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getAccidents_onsite().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        onSite.setText(s);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_ONSITE_ACC, s);
                        // Actualizar timestamp cuando se reciben datos
                        actualizarTimestampAccidentes();
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getAccidents_onhour().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        onHour.setText(s);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_ONHOUR_ACC, s);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getVelocidad().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + " Km/h";
                        velocidad.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_VELOCIDAD_ACC, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getSegmento().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        segmento.setText(s);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_SEGMENTO, s);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getLongitud().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + "°";
                        longitud.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_LONGITUD, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getControl().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        control.setText(s);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_CONTROL, s);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getLatitud().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + "°";
                        latitud.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_LATITUD, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getVia().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        via.setText(s);
                        // Guardar el valor en SharedPreferences
                        guardarValorAccidentes(KEY_VIA, s);
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
    }

    public void onStart() {
        //Inicia la interacción con la actividad
        super.onStart();
        // Cargar y mostrar la última actualización
        mostrarUltimaActualizacionAccidentes();
        // Cargar los últimos valores guardados al regresar al fragmento
        cargarUltimosValoresAccidentes();
    }
    
    public void onPause() {
        //Suspende la interacción con la actividad
        super.onPause();
    }

    public void onStop() {
        //Para la interacción con la actividad
        super.onStop();
    }
}
