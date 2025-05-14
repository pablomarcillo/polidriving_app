//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.codigo;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para consultas en la base de datos
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.base_datos.DataBaseAccess;
import com.polidriving.mobile.clases.login.LoginActivity;
import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.core.Amplify;
import com.polidriving.mobile.R;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

/**
 * @noinspection DataFlowIssue
 */
public class CodigoActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    EditText codigoConfirmacion;
    TextView reenviar;
    Button confirmar;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo);

        //Segmento de código que permite recibir el nombre del usuario desde otra actividad
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("Nombre");
        String correo = intent.getStringExtra("Correo");

        //Inicializando las variables con los elementos de la actividad
        codigoConfirmacion = findViewById(R.id.txtConfirmarCorreo);
        confirmar = findViewById(R.id.botonConfirmarCorreo);
        reenviar = findViewById(R.id.botonReenviar);

        //Creando el evento click en un botón inicializado
        confirmar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //Verificando que no existan campos que se encuentre vacíos
                    if (!codigoConfirmacion.getText().toString().isEmpty()) {
                        //Obtención del código ingresado por el usuario
                        String codigo = codigoConfirmacion.getText().toString();
                        //Enviando a los servicios de AWS el nombre de usuario y el código de confirmación para su verificación
                        Amplify.Auth.confirmSignUp(nombre, codigo, result -> CodigoActivity.this.runOnUiThread(new Runnable() {
                            //Segmento de código que se ejecuta cuando la información enviada es correcta
                            public void run() {
                                // Iniciando hilo secundario para las consultas a la base de datos
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        try {
                                            // Llamando a una nueva instancia de la base de datos
                                            DataBaseAccess nuevaConsulta = new DataBaseAccess();
                                            // Agregando a un nuevo usuario con sus credenciales y datos por defecto
                                            nuevaConsulta.agregarUsuario(nombre, correo);
                                            // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                            CodigoActivity.this.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    //Presentación de información al usuario por consola y mediante un mensaje emergente
                                                    Log.i("Información", "Usuario creado con éxito");
                                                    Toast.makeText(getApplicationContext(), "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                                                    //Redirigiendo al usuario a la actividad de Login
                                                    Intent intent = new Intent(CodigoActivity.this, LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    CodigoActivity.this.startActivity(intent);
                                                }
                                            });
                                        } catch (Exception e) {
                                            // Mensaje de error al no poder conectarse a la base de datos
                                            Log.e("Error de creación: ", e.getMessage());
                                        }
                                    }
                                };
                                // Empezando a correr el hilo
                                Thread mythread = new Thread(runnable);
                                mythread.start();
                            }
                        }), error -> CodigoActivity.this.runOnUiThread(new Runnable() {
                            //Segmento de código que se ejecuta cuando la información enviada es incorrecta
                            public void run() {
                                //Presentación de información al usuario por consola y mediante un mensaje emergente
                                Log.e("Error", error.toString());
                                Toast.makeText(getApplicationContext(), "El Código Ingresado es Incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        }));
                    } else {
                        //Mensaje emergente de advertencia al usuario si se detectan campos vacíos
                        String error = getString(R.string.falta_codigo);
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //Mensaje emergente de error al usuario si el evento click falla
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Creando el evento click en un botón inicializado
        reenviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Segmento de código que solícita el reenvío del código de confirmación a los servicios de AWS
                //Enviando a los servicios de AWS el nombre de usuario para su verificación
                Amplify.Auth.resendSignUpCode(nombre, result -> CodigoActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //Mensaje emergente de código reenviado
                        Log.i("Información", "Código de Confirmación Reenviado");
                        Toast.makeText(getApplicationContext(), "Código de Confirmación Reenviado", Toast.LENGTH_SHORT).show();
                    }
                }), error -> CodigoActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //Mensaje emergente de error al usuario si el evento de reenvío de código falla
                        Log.e("Error", error.toString());
                        Toast.makeText(getApplicationContext(), "No se pudo Reenviar el Código", Toast.LENGTH_SHORT).show();
                    }
                }));
            }
        });
    }

    public void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        //TODO
    }
}