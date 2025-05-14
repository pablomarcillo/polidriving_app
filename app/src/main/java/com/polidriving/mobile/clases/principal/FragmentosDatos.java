//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
//Clases usadas para el uso de voz
import com.polidriving.mobile.databinding.FragmentoPrincipalDatosBinding;
import com.polidriving.mobile.clases.notificaciones.NotificacionesVoz;
import android.graphics.drawable.ColorDrawable;
import androidx.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import androidx.lifecycle.Observer;
import android.os.CountDownTimer;
import com.polidriving.mobile.R;
import android.widget.ImageView;
import android.content.Context;
import android.widget.TextView;
import java.text.MessageFormat;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.Calendar;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;

@SuppressWarnings("ConstantConditions")
public class FragmentosDatos extends Fragment {
    //Creación de variable permite obtener la ubicación del fragmento dentro de la actividad principal
    private static final String ARG_SECTION_NUMBER = "section_number";
    //Creación del concatenador de los fragmentos dentro de la actividad principal
    private FragmentoPrincipalDatosBinding vinculador;
    //Creación de un instancia a la clase de notificaciones de voz TTS
    private NotificacionesVoz conversor = null;
    //Creación del fragmento de datos
    private FragmentoModeloVista vista;
    //Creación de variables para presentar información
    Boolean get_set_MuyAlta = false;
    Boolean get_set_Media = false;
    Boolean get_set_Alta = false;
    Boolean get_set_Baja = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Creación y presentación visual del fragment en la actividad principal
        vinculador = FragmentoPrincipalDatosBinding.inflate(inflater, container, false);
        //Creación de un segundo hilo que permite presentar la información de los datos en tiempo real
        Handler handler_datos = new Handler();
        handler_datos.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que obtiene los datos después de realizar la consulta POST
                presentarDatos();
                //Segmento de código que permite actualizar la información a presentar cada 0.5 segundos
                handler_datos.postDelayed(this, 250);
            }
        }, 250);
        //Creación de un tercer hilo que permite presentar las alertas gráficas y sonoras de los datos en tiempo real
        Handler handler_alertas = new Handler();
        handler_alertas.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que permite presentar las alertas según su nivel: Muy Alto, Alto, Media y Baja
                presentarAlertas();
                //Segmento de código que permite actualizar la información a presentar cada 10 segundos
                handler_alertas.postDelayed(this, 10000);
            }
        }, 10000);
        //Presentando la información en el fragmento de vista
        return vinculador.getRoot();
    }

    public void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
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
            //Creación de variables para enviar, presentar y recibir información
            final ImageView resultadoBandera = vinculador.imagenBanderaDatos;
            final TextView resultadoTexto = vinculador.txtMedidaDatos;
            final TextView porcentaje = vinculador.txtPorcentajeDatos;
            //Ocultando parámetro de información no usado
            porcentaje.setVisibility(View.VISIBLE);
            //Segmento de código que permite observar en tiempo real los cambios que se dan en la información
            FragmentoModeloVista.getRespuestaAgente().observe(getViewLifecycleOwner(), new Observer<String>() {
                @SuppressLint("SetTextI18n")
                public void onChanged(@Nullable String s) {
                    //Verificando que los parametros recibidos no sean vacíos
                    assert s != null;
                    //Verificación o Creación del archivo local de configuraciones de Usuario
                    SharedPreferences configuracionUsuario = getActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
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
                    // s = "{\"Output\": 4}"; // Para Pruebas
                    //Comparando el valor recibido con el nivel 4
                    if (s.equals("{\"Output\": 4}") && get_set_MuyAlta) {
                        //Presentando las alertas gráficas de nivel 4 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_muy_alta));
                        resultadoBandera.setImageResource(R.drawable.rojo);
                        porcentaje.setText("25%");
                        //Iniciando alerta sonora de nivel 4
                        String mensaje = "Nivel de Riesgo Muy Alta";
                        conversor.palabra(mensaje);
                        mostrarRojo(mensaje);
                    }
                    //Comparando el valor recibido con el nivel 3
                    if (s.equals("{\"Output\": 3}") && get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 3 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_alta));
                        resultadoBandera.setImageResource(R.drawable.naranja);
                        porcentaje.setText("50%");
                        //Iniciando alerta sonora de nivel 3
                        String mensaje = "Nivel de Riesgo Alta";
                        conversor.palabra(mensaje);
                        mostrarNaranja(mensaje);
                    }
                    //Comparando el valor recibido con el nivel 2
                    if (s.equals("{\"Output\": 2}") && get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 2 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_media));
                        resultadoBandera.setImageResource(R.drawable.amarillo);
                        porcentaje.setText("75%");
                        //Iniciando alerta sonora de nivel 2
                        String mensaje = "Nivel de Riesgo Media";
                        conversor.palabra(mensaje);
                        mostrarAmarillo(mensaje);
                    }
                    //Comparando el valor recibido con el nivel 1
                    if (s.equals("{\"Output\": 1}") && get_set_Baja) {
                        //Presentando las alertas gráficas de nivel 1 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_baja));
                        resultadoBandera.setImageResource(R.drawable.verde);
                        porcentaje.setText("100%");
                        //Iniciando alerta sonora de nivel 1
                        String mensaje = "Nivel de Riesgo Baja";
                        conversor.palabra(mensaje);
                        mostrarVerde(mensaje);
                    }
                }
            });
        } catch (Exception e) {
            //TODO
        }
    }

    private void presentarDatos() {
        //Segmento de código que obtiene desde el agente y la actividad principal los datos a presentar en pantalla
        try {
            final TextView actualizar = vinculador.txtUltimaUpdateFecha;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getInclinacion().observe(getViewLifecycleOwner(), new Observer<String>() {
                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                public void onChanged(@Nullable String s) {
                    //Estableciendo la fecha para mostrar al usuario
                    Calendar fecha = Calendar.getInstance();
                    int day = fecha.get(Calendar.DAY_OF_MONTH);
                    // Se suma 1 porque Calendar.MONTH devuelve un valor basado en cero
                    int month = fecha.get(Calendar.MONTH) + 1;
                    int year = fecha.get(Calendar.YEAR);
                    int hour = fecha.get(Calendar.HOUR_OF_DAY);
                    int minute = fecha.get(Calendar.MINUTE);
                    String amPm = (fecha.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
                    actualizar.setText(day + "/" + month + "/" + year + "  " + hour + ":" + String.format("%02d", minute) + " " + amPm);
                }
            });

            final TextView inclinacion = vinculador.txtDatoAngulo;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getInclinacion().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    inclinacion.setText(s);
                }
            });

            final TextView velocidad = vinculador.txtDatoVelocidad;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getVelocidad().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    velocidad.setText(s);
                }
            });

            final TextView gasolina = vinculador.txtDatoBaterias;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getBaterias().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    gasolina.setText(s);
                }
            });

            final TextView motor = vinculador.txtDatoMotor;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getMotor().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    motor.setText(s);
                }
            });

            final TextView rpm = vinculador.txtDatoRPM;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getRpm().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Estableciendo el texto de forma visual que se envió del Dataset
                    rpm.setText(s);
                }
            });

            final TextView ubicacion = vinculador.txtDatoUbicacion;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getUbicacion().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    ubicacion.setText(s);
                }
            });

            final TextView vehiculos = vinculador.txtDatoNumeroVehiculos;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getVehiculos().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    vehiculos.setText(s);
                }
            });

            final TextView ocupacion = vinculador.txtDatoOcupacion;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getOcupacion().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    ocupacion.setText(s);
                }
            });

            final TextView direccion = vinculador.txtDatoDireccionFlujo;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getDireccion().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    direccion.setText(s);
                }
            });

            final TextView promedio = vinculador.txtDatoVelocidadPromedio;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getPromedio().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    promedio.setText(s);
                }
            });

            final TextView precipitacion = vinculador.txtDatoPrecipitacion;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getPrecipitacion().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    precipitacion.setText(s);
                }
            });

            final TextView temperatura = vinculador.txtDatoTemperaturaAmbiente;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getTemperatura().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    temperatura.setText(s);
                }
            });

            final TextView humedad = vinculador.txtDatoHumedad;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getHumedad().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    humedad.setText(s);
                }
            });

            final TextView viento = vinculador.txtDatoViento;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getViento().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    viento.setText(s);
                }
            });

            final TextView lugar = vinculador.txtDatoLugar;
            //Llamando al método establecido en la clase Modelo Vista para obtener la información del Dataset
            FragmentoModeloVista.getLugar().observe(getViewLifecycleOwner(), new Observer<String>() {
                //Estableciendo el texto de forma visual que se envió del Dataset
                public void onChanged(@Nullable String s) {
                    lugar.setText(s);
                }
            });
        } catch (Exception e) {
            //TODO
        }
    }

    public void onDestroy() {
        //Finaliza la actividad
        super.onDestroy();
        conversor.apagar();
        //TODO
    }

    public void onResume() {
        //Reinicia la actividad
        super.onResume();
        //TODO
    }

    public void onStart() {
        //Inicia la actividad
        super.onStart();
        //TODO
    }

    public void onPause() {
        //Pausa la actividad
        super.onPause();
        //TODO
    }

    public void onStop() {
        //Detiene la actividad
        super.onStop();
        //TODO
    }
}