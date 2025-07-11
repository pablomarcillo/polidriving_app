//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.databinding.FragmentoPrincipalCondicionesBinding;
import com.polidriving.mobile.BuildConfig;
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
    // Removed ViewModel instance as we're using static methods

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
                handler.postDelayed(this, 500);
            }
        }, 250);
        //Creación de un tercer hilo que permite presentar las alertas gráficas de los datos en tiempo real
        Handler handler_alertas = new Handler();
        handler_alertas.postDelayed(new Runnable() {
            public void run() {
                //Segmento de código que permite actualizar la información a presentar cada 10 segundos
                handler_alertas.postDelayed(this,10000);
            }
        }, 10000);
        //Presentando la información en el fragmento de vista
        return vinculador_condiciones.getRoot();
    }

    public void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        // Removed ViewModel instantiation
        //Variable que permite ubicar la posición del fragmento en la actividad principal
        int index = 3;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        //Regresando la ubicación actual a la actividad principal
        FragmentoModeloVista.setIndex(index);
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
