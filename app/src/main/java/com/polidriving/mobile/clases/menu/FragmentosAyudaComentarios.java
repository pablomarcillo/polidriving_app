//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.menu;
import com.polidriving.mobile.BuildConfig;

//Clases
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
//

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import com.polidriving.mobile.R;
import android.widget.EditText;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;

public class FragmentosAyudaComentarios extends Fragment {
    //Creación de variables para enviar, presentar y recibir información
    String CLASS_COMENTARIOS_ENLACE_WEB = BuildConfig.CLASS_COMENTARIOS_ENLACE_WEB;
    String regresar = "";
    String enviado = "";
    Button comentario;
    EditText mensaje;
    EditText titulo;
    Button web;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        final View view = inflater.inflate(R.layout.fragmento_lateral_ayuda_comentarios, container, false);
        //Inicializando las variables con los elementos de la actividad
        mensaje = view.findViewById(R.id.txtComentario_Ayuda);
        comentario = view.findViewById(R.id.botonComentario);
        titulo = view.findViewById(R.id.txtTituloAyuda);
        web = view.findViewById(R.id.botonAyudas);

        //Creando el evento click en un botón inicializado
        comentario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //Verificando que no existan campos que se encuentre vacíos
                    if (!titulo.getText().toString().isEmpty() && !mensaje.getText().toString().isEmpty()) {
                        //Cadena de texto para verificar el estado del mensaje
                        enviado = "No Enviado";
                        //Obteniendo el cuerpo del mensaje a ser enviado
                        String comentario = mensaje.getText().toString();
                        String titulo_m = titulo.getText().toString();
                        String correo = "polidriving@epn.edu.ec";
                        //Iniciando la aplicación de correos definida en el celular
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{correo});
                        intent.putExtra(Intent.EXTRA_SUBJECT, titulo_m);
                        intent.putExtra(Intent.EXTRA_TEXT, comentario);
                        //Indica que el cuerpo contiene un mensaje encapsulado con la sintaxis de un mensaje RFC 822
                        intent.setType("message/rfc822");
                        startActivity(intent);
                        //Cadena de texto para verificar el estado del mensaje
                        enviado = "Enviado";
                    } else {
                        //Mensaje emergente de advertencia al usuario si se detectan campos vacíos
                        String error = getString(R.string.falta_informacion);
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //Mensaje emergente de error al usuario si el evento click falla
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Cadena de texto para verificar el estado de la actividad
                regresar = "No Regresar";
                //Obteniendo el enlace de la página web a redirigir
                Uri enlace = Uri.parse(CLASS_COMENTARIOS_ENLACE_WEB);
                //Abriendo el enlace web en el navegador de preferencia
                Intent pagina = new Intent(Intent.ACTION_VIEW, enlace);
                startActivity(pagina);
                //Cadena de texto para verificar el estado de la actividad
                regresar = "Regresar";
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

    public void onResume() {
        super.onResume();
        //Segmento de código para verificar el estado del mensaje
        if (enviado.equals("Enviado")) {
            //Mensaje emergente que informa al usuario que el mensaje se ha enviado
            String mensaje = getString(R.string.mensaje_enviado);
            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            //Redirigiendo al usuario al menú lateral
            Intent cambiar = new Intent(getActivity(), MenuLateralActivity.class);
            //Cadena de texto para verificar el estado del mensaje
            enviado = "No Enviado";
            //Iniciando la actividad
            startActivity(cambiar);
        }
        if (regresar.equals("Regresar")) {
            //Redirigiendo al usuario a la actividad menú lateral
            Intent cambiar = new Intent(getActivity(), MenuLateralActivity.class);
            regresar = "No Regresar";
            //Iniciando la actividad
            startActivity(cambiar);
        }
    }
}