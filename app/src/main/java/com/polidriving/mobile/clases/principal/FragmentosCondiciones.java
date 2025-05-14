//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.databinding.FragmentoPrincipalCondicionesBinding;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import androidx.lifecycle.Observer;
import com.polidriving.mobile.R;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;

public class FragmentosCondiciones extends Fragment {
    //Creación de variable permite obtener la ubicación del fragmento dentro de la actividad principal
    private static final String ARG_SECTION_NUMBER = "section_number";
    //Creación del concatenador de los fragmentos dentro de la actividad principal
    private FragmentoPrincipalCondicionesBinding vinculador_condiciones;
    //Creación del fragmento de condiciones
    private FragmentoModeloVista vista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Creación y presentación visual del fragment en la actividad principal
        vinculador_condiciones = FragmentoPrincipalCondicionesBinding.inflate(inflater, container, false);
        //Creación de un segundo hilo que permite presentar la información de los datos en tiempo real
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que obtiene los datos después de realizar la consulta POST
                presentarDatos();
                //Segmento de código que permite actualizar la información a presentar cada 0.5 segundos
                handler.postDelayed(this, 250);
            }
        }, 250);
        //Creación de un tercer hilo que permite presentar las alertas gráficas de los datos en tiempo real
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
        return vinculador_condiciones.getRoot();
    }

    public void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        vista = new ViewModelProvider(this).get(FragmentoModeloVista.class);
        //Variable que permite ubicar la posición del fragmento en la actividad principal
        int index = 3;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        //Regresando la ubicación actual a la actividad principal
        vista.setIndex(index);
    }

    private void presentarAlertas() {
        try {
            //Creación de variables para enviar, presentar y recibir información
            final ImageView resultadoBandera = vinculador_condiciones.imagenBanderaCondiciones;
            final TextView resultadoTexto = vinculador_condiciones.txtMedidaCondiciones;
            final TextView porcentaje = vinculador_condiciones.txtPorcentajeCondiciones;
            //Ocultando parámetro de información no usado
            porcentaje.setVisibility(View.VISIBLE);
            //Segmento de código que permite observar en tiempo real los cambios que se dan en la información
            FragmentoModeloVista.getRespuestaAgente().observe(getViewLifecycleOwner(), new Observer<String>() {
                @SuppressLint("SetTextI18n")
                public void onChanged(@Nullable String s) {
                    //Verificando que los parametros recibidos n sean vacíos
                    assert s != null;
                    //Comparando el valor recibido con el nivel 4
                    if (s.equals("{\"Output\": 4}")) {
                        //Presentando las alertas gráficas de nivel 4 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_muy_alta));
                        resultadoTexto.setTextSize(24);
                        resultadoBandera.setImageResource(R.drawable.rojo);
                        porcentaje.setText("25%");
                    }
                    //Comparando el valor recibido con el nivel 3
                    if (s.equals("{\"Output\": 3}")) {
                        //Presentando las alertas gráficas de nivel 3 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_alta));
                        resultadoBandera.setImageResource(R.drawable.naranja);
                        porcentaje.setText("50%");
                    }
                    //Comparando el valor recibido con el nivel 2
                    if (s.equals("{\"Output\": 2}")) {
                        //Presentando las alertas gráficas de nivel 2 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_media));
                        resultadoBandera.setImageResource(R.drawable.amarillo);
                        porcentaje.setText("75%");
                    }
                    //Comparando el valor recibido con el nivel 1
                    if (s.equals("{\"Output\": 1}")) {
                        //Presentando las alertas gráficas de nivel 1 al usuario
                        resultadoTexto.setText(getString(R.string.medida_datos_baja));
                        resultadoBandera.setImageResource(R.drawable.verde);
                        porcentaje.setText("100%");
                    }
                }
            });
        } catch (Exception e) {
            //TODO
        }
    }

    public void presentarDatos() {
        //Segmento de código que obtiene desde el agente y la actividad principal los datos a presentar en pantalla
        try {
            final TextView precipitacion = vinculador_condiciones.txtDatoPrecipitacion;
            FragmentoModeloVista.getPrecipitacion().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    precipitacion.setText(s);
                }
            });

            final TextView temperatura = vinculador_condiciones.txtDatoTemperaturaAmbiente;
            FragmentoModeloVista.getTemperatura().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    temperatura.setText(s);
                }
            });

            final TextView humedad = vinculador_condiciones.txtDatoHumedad;
            FragmentoModeloVista.getHumedad().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    humedad.setText(s);
                }
            });

            final TextView viento = vinculador_condiciones.txtDatoViento;
            FragmentoModeloVista.getViento().observe(getViewLifecycleOwner(), new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    viento.setText(s);
                }
            });

            final TextView lugar = vinculador_condiciones.txtDatoLugar;
            FragmentoModeloVista.getLugar().observe(getViewLifecycleOwner(), new Observer<String>() {
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