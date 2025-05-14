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
import android.graphics.drawable.ColorDrawable;
import androidx.lifecycle.ViewModelProvider;
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

/**
 * @noinspection ALL
 */
@SuppressLint({"StaticFieldLeak", "SetTextI18n", "DefaultLocale"})
public class FragmentosVehiculo extends Fragment {
    //Creación de variables a usar e instancia a la clase de notificaciones de voz TTS
    private static final String ARG_SECTION_NUMBER = "section_number";
    private NotificacionesVoz conversor = null;
    //static ImageView resultadoBandera;
    private FragmentoModeloVista vista;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Creando un vista de los elementos de la actividad
        View view = inflater.inflate(R.layout.fragmento_principal_vehiculo, container, false);
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

        //Creación de un segundo hilo que permite presentar la información de los datos en tiempo real
        Handler handler_datos = new Handler();
        handler_datos.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que obtiene los datos después de realizar la consulta POST
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
            FragmentoModeloVista.getVelocidad().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    velocidad.setText(s + " Km/h");
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getRpm().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    rpm.setText(s + " rpm");
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getMotor().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    temperatura.setText(s + " °C");
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getBaterias().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    voltaje.setText(s + " V");
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getThrottle_position().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    posicion.setText(s + "°");
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getBaterias().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    carga.setText(s + " V");
                }
            });

            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getDistance_travelled().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    distancia.setText(s + " m");
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