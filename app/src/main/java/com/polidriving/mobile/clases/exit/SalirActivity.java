//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.exit;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.polidriving.mobile.clases.start.StartActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import com.amplifyframework.core.Amplify;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import com.polidriving.mobile.R;
import android.widget.ImageView;
import java.text.MessageFormat;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

@SuppressWarnings({"deprecation"})
public class SalirActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    Button cerrarSesion;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salir);

        //Inicializando las variables con los elementos de la actividad
        cerrarSesion = findViewById(R.id.botonSignOutUsuario);

        //Creando el evento click en un botón inicializado
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Mensaje especial de advertencia de información al usuario con duración de 5 segundos
                String mensaje = "¿Desea Cerrar la Sesión?";
                //Invocando el mensaje de advertencia
                mostrarRojo(mensaje);
            }
        });
    }

    private void mostrarRojo(String texto) {
        //Método especial para crear un cuadro de diálogo personalizado
        //Permite mostrar un mensaje de advertencia en color rojo al usuario
        //Construcción de un cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(SalirActivity.this);
        View view = new View(SalirActivity.this);
        //Llamando al mensaje y al contenedor del cuadro de diálogo (Llamada a las actividades)
        view = getLayoutInflater().inflate(R.layout.mensaje_rojo, view.findViewById(R.id.contenedorRojo));
        builder.setView(view);
        //Insertando los texto dentro del cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloRojo)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoRojo)).setText(texto);
        //Insertando una imagen en el cuadro de diálogo para hacer al cuadro de diálogo más informativo y amigable con el usuario
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Creando el cuadro de diálogo en pantalla
        final AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        //Creando las propiedades del cuadro de diálogo
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            //Segmento de código para mostrar el mensaje de alerta personalizado
            public void onShow(DialogInterface dialog) {
                //Iniciando un contador con duración de 5 segundos  a 1 segundo de intervalo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        //Mostrando el botón OK en ele interior del cuadro de diálogo
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okRojo)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando el cuadro de diálogo si el usuario no realiza ninguna acción en un período de 5 segundos
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        //Segmento de código que se ejecuta si el usuario presiona el botón OK en el cuadro de diálogo en un período de 5 segundos
        view.findViewById(R.id.okRojo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Segmento de código que permite establecer conexión con los servicios de AWS para cerrar sesión del usuario
                AuthSignOutOptions options = AuthSignOutOptions.builder().globalSignOut(true).build();
                //Enviando la información a AWS con los parametros necesarios para cerrar sesión de usuario
                Amplify.Auth.signOut(options, signOutResult -> SalirActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                            //Mensaje emergente y de consola que informa al usuario que se logro cerrar la sesión de usuario con éxito
                            String mensaje = "Cerrando Sesión";
                            Log.i("Información", "Cierre de Sesión Éxitoso");
                            Toast.makeText(SalirActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                            //Redirigiendo al usuario a la actividad de login de usuario
                            Intent intent = new Intent(SalirActivity.this, StartActivity.class);
                            //Iniciando la actividad de login
                            startActivity(intent);
                        } else {
                            //Segmento de código que se ejecuta al producirse un error total o parcial al tratar de cerrar la sesión de usuario
                            if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                                //Mensaje emergente y de consola que informa al usuario que n se pudo cerrar la sesión de usuario
                                Log.e("Error", signOutResult.toString());
                                String mensaje = "No se pudo Cerrar la Sesión";
                                Toast.makeText(SalirActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                            } else {
                                if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                                    //Mensaje emergente y de consola que informa al usuario que n se pudo cerrar la sesión de usuario
                                    Log.e("Error", signOutResult.toString());
                                    String mensaje = "No se pudo Cerrar la Sesión";
                                    Toast.makeText(SalirActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }));
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia la mensaje de advertencia personalizado.
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Mostrar en pantalla el mensaje de advertencia
        mensajeMostrado.show();
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }

    public void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        //TODO
    }
}