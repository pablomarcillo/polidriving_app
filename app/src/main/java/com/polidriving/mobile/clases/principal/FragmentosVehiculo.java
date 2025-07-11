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
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import android.widget.LinearLayout;
import androidx.lifecycle.Observer;
import android.view.LayoutInflater;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * @noinspection ALL
 */
@SuppressLint({"StaticFieldLeak", "SetTextI18n", "DefaultLocale"})
public class FragmentosVehiculo extends Fragment {
    //Creación de variables a usar e instancia a la clase de notificaciones de voz TTS
    private static final String ARG_SECTION_NUMBER = "section_number";
    private NotificacionesVoz conversor = null;
    //static ImageView resultadoBandera;
    // Removed ViewModel instance as we're using static methods
    Boolean get_set_MuyAlta = false;
    static LinearLayout grupoActual;
    static LinearLayout grupoNivel;
    static TextView resultadoTexto;
    Boolean get_set_Media = false;
    Boolean get_set_Alta = false;
    Boolean get_set_Baja = false;
    //static TextView porcentaje;
    static TextView temperatura;
    static TextView velocidad;
    static TextView distancia;
    static TextView posicion;
    static TextView voltaje;
    static TextView actual;
    static TextView carga;
    static TextView rpm;

    // Variable para el TextView de última actualización
    private TextView txtUltimaActualizacionVehiculo;
    private static final String PREFS_NAME = "VehiculoPrefs";
    private static final String LAST_UPDATE_KEY = "last_vehiculo_update";
    
    // Claves para SharedPreferences para persistir valores
    private static final String KEY_VELOCIDAD = "ultimo_valor_velocidad";
    private static final String KEY_RPM = "ultimo_valor_rpm";
    private static final String KEY_TEMPERATURA = "ultimo_valor_temperatura";
    private static final String KEY_VOLTAJE = "ultimo_valor_voltaje";
    private static final String KEY_POSICION = "ultimo_valor_posicion";
    private static final String KEY_CARGA = "ultimo_valor_carga";
    private static final String KEY_DISTANCIA = "ultimo_valor_distancia";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Creando un vista de los elementos de la actividad
        View view = inflater.inflate(R.layout.fragmento_principal_vehiculo, container, false);
        
        //Inicializando el TextView de última actualización
        txtUltimaActualizacionVehiculo = view.findViewById(R.id.txtUltimaActualizacionVehiculo);
        
        //Inicializando las variables con los elementos de la actividad
        distancia = view.findViewById(R.id.txtRespuestaDistanciaRecorridaVehiculo);
        //resultadoBandera = view.findViewById(R.id.imagenBanderaDatosVehiculo);
        temperatura = view.findViewById(R.id.txtRespuestaTemperaturaVehiculo);
        //porcentaje = view.findViewById(R.id.txtPorcentajeDatosVehiculo);
        posicion = view.findViewById(R.id.txtRespuestaAceleradorVehiculo);
        velocidad = view.findViewById(R.id.txtRespuestaVelocidadVehiculo);
        resultadoTexto = view.findViewById(R.id.txtMedidaDatosVehiculo);
        voltaje = view.findViewById(R.id.txtRespuestaVoltajeVehiculo);
        actual = view.findViewById(R.id.txtUltimaUpdateFechaVehiculo);
        carga = view.findViewById(R.id.txtRespuestaCargaVehiculo);
        grupoActual = view.findViewById(R.id.grupoFechaVehiculo);
        grupoNivel = view.findViewById(R.id.grupoNivelVehiculo);
        rpm = view.findViewById(R.id.txtRespuestaRpmVehiculo);

        //Estableciendo la fecha para mostrar al usuario
        //presentarFecha();
        actual.setText("Esperando…");
        
        // Cargar los últimos valores guardados antes de observar los datos
        cargarUltimosValores();

        //Creación de un segundo hilo que permite presentar la información de los datos en tiempo real
        Handler handler_datos = new Handler();
        handler_datos.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que obtiene los datos después de realizar la consulta POST
                presentarDatos();
                presentarFecha();
                //Segmento de código que permite actualizar la información a presentar cada VEHICULO_UPDATE_INTERVAL_MS milisegundos
                handler_datos.postDelayed(this, BuildConfig.VEHICULO_UPDATE_INTERVAL_MS);
            }
        }, BuildConfig.VEHICULO_UPDATE_INTERVAL_MS);
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        // Creando un vista de los elementos de la actividad
        super.onCreate(savedInstanceState);

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

    /**
     * Carga los últimos valores guardados desde SharedPreferences
     */
    private void cargarUltimosValores() {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            
            // Cargar último valor de velocidad (por defecto "0 Km/h")
            String ultimaVelocidad = prefs.getString(KEY_VELOCIDAD, "0 Km/h");
            if (velocidad != null) velocidad.setText(ultimaVelocidad);
            
            // Cargar último valor de RPM (por defecto "0 rpm")
            String ultimoRpm = prefs.getString(KEY_RPM, "0 rpm");
            if (rpm != null) rpm.setText(ultimoRpm);
            
            // Cargar último valor de temperatura (por defecto "0 °C")
            String ultimaTemperatura = prefs.getString(KEY_TEMPERATURA, "0 °C");
            if (temperatura != null) temperatura.setText(ultimaTemperatura);
            
            // Cargar último valor de voltaje (por defecto "0 V")
            String ultimoVoltaje = prefs.getString(KEY_VOLTAJE, "0 V");
            if (voltaje != null) voltaje.setText(ultimoVoltaje);
            
            // Cargar último valor de posición (por defecto "0°")
            String ultimaPosicion = prefs.getString(KEY_POSICION, "0°");
            if (posicion != null) posicion.setText(ultimaPosicion);
            
            // Cargar último valor de carga (por defecto "0 V")
            String ultimaCarga = prefs.getString(KEY_CARGA, "0 V");
            if (carga != null) carga.setText(ultimaCarga);
            
            // Cargar último valor de distancia (por defecto "0 m")
            String ultimaDistancia = prefs.getString(KEY_DISTANCIA, "0 m");
            if (distancia != null) distancia.setText(ultimaDistancia);
        }
    }
    
    /**
     * Guarda un valor en SharedPreferences
     */
    private void guardarValor(String clave, String valor) {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(clave, valor);
            editor.apply();
        }
    }


    private void presentarDatos() {
        //Segmento de código que obtiene desde el agente y la actividad principal los datos a presentar en pantalla
        try {
            // Variable para controlar si se han actualizado los datos
            boolean datosActualizados = false;
            
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getVelocidad().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + " Km/h";
                        velocidad.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValor(KEY_VELOCIDAD, textoCompleto);
                        // Actualizar timestamp cuando se reciben datos
                        actualizarTimestampVehiculo();
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getRpm().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + " rpm";
                        rpm.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValor(KEY_RPM, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido in la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getMotor().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        String textoCompleto = s + " °C";
                        temperatura.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValor(KEY_TEMPERATURA, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getBaterias().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + " V";
                        voltaje.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValor(KEY_VOLTAJE, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido in la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getThrottle_position().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + "°";
                        posicion.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValor(KEY_POSICION, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getBaterias().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + " V";
                        carga.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValor(KEY_CARGA, textoCompleto);
                    }
                }
            });

            //Llamando al método establecido in la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getDistance_travelled().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    if (s != null && !s.isEmpty()) {
                        //Estableciendo el texto de forma visual que se envió del Dataset
                        String textoCompleto = s + " m";
                        distancia.setText(textoCompleto);
                        // Guardar el valor en SharedPreferences
                        guardarValor(KEY_DISTANCIA, textoCompleto);
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
        mostrarUltimaActualizacion();
        // Cargar los últimos valores guardados al regresar al fragmento
        cargarUltimosValores();
    }
    
    /**
     * Actualiza la marca de tiempo de la última actualización de vehículo
     */
    public void actualizarTimestampVehiculo() {
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(LAST_UPDATE_KEY, System.currentTimeMillis());
            editor.apply();
            
            mostrarUltimaActualizacion();
        }
    }
    
    /**
     * Muestra la última actualización en el TextView correspondiente
     */
    private void mostrarUltimaActualizacion() {
        if (getActivity() != null && txtUltimaActualizacionVehiculo != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            long lastUpdate = prefs.getLong(LAST_UPDATE_KEY, 0);
            
            if (lastUpdate == 0) {
                txtUltimaActualizacionVehiculo.setText(getString(R.string.nunca_actualizado));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String fechaFormateada = sdf.format(new Date(lastUpdate));
                txtUltimaActualizacionVehiculo.setText(
                    String.format(getString(R.string.ultima_actualizacion_vehiculo), fechaFormateada)
                );
            }
        }
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
