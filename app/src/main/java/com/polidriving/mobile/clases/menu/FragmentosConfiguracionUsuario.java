//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.menu;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.clases.configuracion.ConfiguracionActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import com.polidriving.mobile.R;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;

public class FragmentosConfiguracionUsuario extends Fragment {
    //Creación de variables para enviar, presentar y recibir información
    Button configuracion;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        final View view = inflater.inflate(R.layout.fragmento_lateral_configuracion, container, false);
        //Inicializando las variables con los elementos de la actividad
        configuracion = view.findViewById(R.id.botonIrConfiguracion);

        //Creando el evento click en un botón inicializado
        configuracion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Mensaje emergente de información del usuario
                String mensaje = getString(R.string.configuraciones_usuario);
                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de configuraciones de usuario
                Intent intent = new Intent(getActivity(), ConfiguracionActivity.class);
                startActivity(intent);
            }
        });
        //Retornando el estado de la actividad
        return view;
    }

    public void onDestroyView() {
        //Finaliza la interacción con la actividad
        super.onDestroyView();
        //TODO
    }
}