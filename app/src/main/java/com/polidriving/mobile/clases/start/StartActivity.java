//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.start;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
import com.polidriving.mobile.clases.principal.PrincipalActivity;
import com.polidriving.mobile.clases.login.LoginActivity;
import com.polidriving.mobile.clases.nuevo.NuevoActivity;
import com.polidriving.mobile.base_datos.DataBaseAccess;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.amplifyframework.core.Amplify;
import com.polidriving.mobile.R;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

/**
 * @noinspection DataFlowIssue
 */
public class StartActivity extends AppCompatActivity {
    //Creación de variables para enviar información
    Button cambiarLoginStart;
    Button cambiarNuevoStart;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Desactivando el modo nocturno si esta activo en el dispositivo
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Inicializando las variables con los elementos de la actividad
        cambiarLoginStart = findViewById(R.id.btnIniciarSesionStart);
        cambiarNuevoStart = findViewById(R.id.btnNuevoUsuarioStart);

        //Segmento de código utilizado para verificar si el usuario ya había iniciado sesión previamente
        Amplify.Auth.fetchAuthSession(result -> StartActivity.this.runOnUiThread(new Runnable() {
            //Segmento de código que se ejecuta en un hilo secundario para verificar la conexión con los servicios de AWS
            public void run() {
                //Mensaje de consola que informa al usuario que se estableció conexión con los servicios de AWS
                Log.i("Información", result.toString());
                try {
                    //Segmento de código que verifica si el usuario ya había iniciado sesión previamente
                    Amplify.Auth.getCurrentUser(result -> StartActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Mensaje de consola que informa al usuario que se auténtico al usuario con los servicios de AWS
                            Log.i("Información", result.toString());
                            //Segmento de código para inhabilitar los botones de la actividad
                            cambiarLoginStart.setEnabled(false);
                            cambiarNuevoStart.setEnabled(false);
                            // Iniciando hilo secundario para las consultas a la base de datos
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    try {
                                        // Llamando a una nueva instancia de la base de datos
                                        DataBaseAccess nuevaConsulta = new DataBaseAccess();
                                        // Método para obtener la lista de atributos desde ls base de datos mediate la clave del UserName
                                        String obtenerDatos = (String) nuevaConsulta.obtenerUsuario(result.getUsername());
                                        // Se separa los atributos obtenidos mediante un split
                                        String[] separar = obtenerDatos.split(",");
                                        // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                        StartActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Mensaje emergente de saludo al usuario con el nombre de usuario
                                                if (separar[8].trim().equals(getString(R.string.no_ingresado)) || separar[3].trim().equals(getString(R.string.no_ingresado))) {
                                                    String informacion = getString(R.string.bienvenido);
                                                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    String informacion = getString(R.string.bienvenido) + " " + separar[8].trim() + " " + separar[3].trim();
                                                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                                                }
                                                //Redirigiendo al usuario a la actividad Principal de la aplicación enviando el parámetro de nombre de usuario
                                                Intent intent = new Intent(StartActivity.this, PrincipalActivity.class);
                                                intent.putExtra("ApellidoUsuario", separar[3].trim());
                                                intent.putExtra("NombreUsuario", separar[8].trim());
                                                intent.putExtra("Tipo", separar[2].trim());
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                StartActivity.this.startActivity(intent);
                                            }
                                        });
                                    } catch (Exception e) {
                                        StartActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                // Mensaje de error al no poder conectarse a la base de datos
                                                Log.e("Error de conexión a Internet: ", e.getMessage());
                                                String errorSesion = "Error al iniciar la App. \n Verifique su conexión a Internet \n Cierre y vuelva abrir la APP. \n Una vez solucionado su problema";
                                                Toast.makeText(getApplicationContext(), errorSesion, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            };
                            // Empezando a correr el hilo
                            Thread mythread = new Thread(runnable);
                            mythread.start();
                        }
                    }), error -> StartActivity.this.runOnUiThread(new Runnable() {
                        //Segmento de código que se ejecuta cuando no se pudo obtener la información del usuario
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión o al iniciar/regresar a la aplicación
                            Log.e("Error", error.toString());
                            String saludo = getString(R.string.bienvenido) + " " + getString(R.string.a) + " " + getString(R.string.nombre_app);
                            Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                        }
                    }));
                } catch (Exception error) {
                    StartActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión
                            Log.e("Error", error.toString());
                            String errorSesion = "Hubo un error al iniciar la App";
                            Toast.makeText(getApplicationContext(), errorSesion, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }), error -> StartActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                //Mensaje de consola que informa al usuario que no se pudo obtener el nombre de usuario
                Log.e("Error", error.toString());
            }
        }));

        //Creando el evento click en un botón inicializado
        cambiarLoginStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Redirigiendo al usuario a la actividad de creación de nuevo usuario
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón inicializado
        cambiarNuevoStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Redirigiendo al usuario a la actividad de creación de nuevo usuario
                Intent intent = new Intent(StartActivity.this, NuevoActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        //TODO
    }
}