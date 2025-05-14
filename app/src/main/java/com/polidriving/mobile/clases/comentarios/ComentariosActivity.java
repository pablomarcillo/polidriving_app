//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.comentarios;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import androidx.appcompat.app.AppCompatActivity;
import com.polidriving.mobile.R;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;
import android.net.Uri;

@SuppressWarnings("deprecation")
public class ComentariosActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    String enlace_web = "https://www.epn.edu.ec/";
    EditText textoComentario;
    Button cambiarPrincipal;
    EditText textoTitulo;
    String enviado = "";
    Button web;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Inicializando las variables con los elementos de la actividad
        cambiarPrincipal = findViewById(R.id.botonComentario);
        textoComentario = findViewById(R.id.txtComentario);
        textoTitulo = findViewById(R.id.txtTitulo);
        web = findViewById(R.id.botonAyudas);

        //Creando el evento click en un botón inicializado
        cambiarPrincipal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //Verificando que no existan campos que se encuentre vacíos
                    if (!textoTitulo.getText().toString().isEmpty() && !textoComentario.getText().toString().isEmpty()) {
                        //Cadena de texto para verificar el estado del mensaje
                        enviado = "No Enviado";
                        //Obteniendo el cuerpo del mensaje a ser enviado
                        String comentario = textoComentario.getText().toString();
                        String titulo = textoTitulo.getText().toString();
                        String correo = "polidriving@epn.edu.ec";
                        //Iniciando la aplicación de correos definida en el celular
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        //Enviando la información a la aplicación de correos del celular
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{correo});
                        intent.putExtra(Intent.EXTRA_SUBJECT, titulo);
                        intent.putExtra(Intent.EXTRA_TEXT, comentario);
                        //Indica que el cuerpo contiene un mensaje encapsulado con la sintaxis de un mensaje RFC 822
                        intent.setType("message/rfc822");
                        startActivity(intent);
                        //Cadena de texto para verificar el estado del mensaje
                        enviado = "Enviado";
                    } else {
                        //Mensaje emergente de advertencia al usuario si se detectan campos vacíos
                        String error = getString(R.string.falta_informacion);
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //Mensaje emergente de error al usuario si el evento click falla
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Obteniendo el enlace de la página web a redirigir
                Uri enlace = Uri.parse(enlace_web);
                //Abriendo el enlace web en el navegador de preferencia
                Intent pagina = new Intent(Intent.ACTION_VIEW, enlace);
                startActivity(pagina);
            }
        });
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }

    protected void onResume() {
        super.onResume();
        //Segmento de código para verificar el estado del mensaje
        if (enviado.equals("Enviado")) {
            //Mensaje emergente que informa al usuario que el mensaje se ha enviado
            String mensaje = getString(R.string.mensaje_enviado);
            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            //Redirigiendo al usuario a la actividad principal y Cadena de texto para verificar el estado del mensaje
            enviado = "No Enviado";
            onSupportNavigateUp();
        }
    }

    public void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        //TODO
    }
}