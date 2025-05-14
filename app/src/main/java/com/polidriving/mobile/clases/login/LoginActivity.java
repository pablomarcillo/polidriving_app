//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.login;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para consultas en la base de datos
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.clases.principal.PrincipalActivity;
import com.polidriving.mobile.clases.recuperar.RecuperarActivity;
import com.polidriving.mobile.clases.nuevo.NuevoActivity;
import com.polidriving.mobile.base_datos.DataBaseAccess;

import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.core.Amplify;
import com.polidriving.mobile.R;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

/**
 * @noinspection DataFlowIssue
 */
public class LoginActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    TextView cambiarRecuperar;
    Button cambiarInicio;
    Button cambiarNuevo;
    TextView password;
    TextView usuario;

    public void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializando las variables con los elementos de la actividad
        cambiarRecuperar = findViewById(R.id.btnRecobrarPassword);
        cambiarInicio = findViewById(R.id.btnIniciarSesion);
        cambiarNuevo = findViewById(R.id.btnNuevoUsuario);
        password = findViewById(R.id.txtPassword);
        usuario = findViewById(R.id.txtUsuario);

        //Bloquear elementos no usados
        cambiarNuevo.setEnabled(false);
        cambiarNuevo.setVisibility(View.INVISIBLE);

        //Segmento de código utilizado para verificar si el usuario ya había iniciado sesión previamente
        Amplify.Auth.fetchAuthSession(result -> LoginActivity.this.runOnUiThread(new Runnable() {
            //Segmento de código que se ejecuta en un hilo secundario para verificar la conexión con los servicios de AWS
            public void run() {
                //Mensaje de consola que informa al usuario que se estableció conexión con los servicios de AWS
                Log.i("Información", result.toString());
                try {
                    //Segmento de código que verifica si el usuario ya había iniciado sesión previamente
                    Amplify.Auth.getCurrentUser(result -> LoginActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Mensaje de consola que informa al usuario que se auténtico al usuario con los servicios de AWS
                            Log.i("Información", result.toString());
                            //Segmento de código para inhabilitar los botones de la actividad
                            cambiarRecuperar.setEnabled(false);
                            cambiarInicio.setEnabled(false);
                            cambiarNuevo.setEnabled(false);
                            password.setEnabled(false);
                            usuario.setEnabled(false);
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
                                        LoginActivity.this.runOnUiThread(new Runnable() {
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
                                                Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                                intent.putExtra("ApellidoUsuario", separar[3].trim());
                                                intent.putExtra("NombreUsuario", separar[8].trim());
                                                intent.putExtra("Tipo", separar[2].trim());
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                LoginActivity.this.startActivity(intent);
                                            }
                                        });
                                    } catch (Exception e) {
                                        // Mensaje de error al no poder conectarse a la base de datos
                                        Log.e("Error de conexión: ", e.getMessage());
                                    }
                                }
                            };
                            // Empezando a correr el hilo
                            Thread mythread = new Thread(runnable);
                            mythread.start();
                        }
                    }), error -> LoginActivity.this.runOnUiThread(new Runnable() {
                        //Segmento de código que se ejecuta cuando no se pudo obtener la información del usuario
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión o al iniciar/regresar a la aplicación
                            Log.e("Error", error.toString());
                            String saludo = getString(R.string.iniciar_sesion);
                            // String saludo = getString(R.string.bienvenido) + " " + getString(R.string.a) + " " + getString(R.string.nombre_app);
                            Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                        }
                    }));
                } catch (Exception error) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión
                            Log.e("Error", error.toString());
                            String errorSesion = "Hubo un error al iniciar la App";
                            Toast.makeText(getApplicationContext(), errorSesion, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }), error -> LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                //Mensaje de consola que informa al usuario que no se pudo obtener el nombre de usuario
                Log.e("Error", error.toString());
            }
        }));

        //Creando el evento click en un botón inicializado
        cambiarRecuperar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Redirigiendo al usuario a la actividad de recuperación de cuenta
                Intent intent = new Intent(LoginActivity.this, RecuperarActivity.class);
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón inicializado
        cambiarInicio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //Verificando que no existan campos que se encuentre vacíos
                    if (!usuario.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                        //Obtención de los campos de contraseña y nombre de usuario para el inicio de sesión
                        String contrasena = password.getText().toString();
                        String username = usuario.getText().toString();
                        //Segmento de código para inhabilitar los botones de la actividad
                        cambiarRecuperar.setEnabled(false);
                        cambiarInicio.setEnabled(false);
                        cambiarNuevo.setEnabled(false);
                        password.setEnabled(false);
                        usuario.setEnabled(false);
                        //Enviando a los servicios de AWS el nombre de usuario y la contraseña para su validación
                        Amplify.Auth.signIn(username, contrasena, result -> LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Mensaje de consola de bienvenida al usuario al verificar correctamente las credenciales enviadas
                                Log.i("Información", "Inicio de Sesión Éxitoso");
                                // Iniciando hilo secundario para las consultas a la base de datos
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        try {
                                            // Llamando a una nueva instancia de la base de datos
                                            DataBaseAccess nuevaConsulta = new DataBaseAccess();
                                            // Método para obtener la lista de atributos desde ls base de datos mediate la clave del UserName
                                            String obtenerDatos = (String) nuevaConsulta.obtenerUsuario(username);
                                            // Se separa los atributos obtenidos mediante un split
                                            String[] separar = obtenerDatos.split(",");
                                            // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                            LoginActivity.this.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    //Mensaje emergente de saludo al usuario con el nombre de usuario
                                                    //Mensaje emergente de saludo al usuario con el nombre de usuario
                                                    if (separar[8].trim().equals(getString(R.string.no_ingresado)) || separar[3].trim().equals(getString(R.string.no_ingresado))) {
                                                        String informacion = getString(R.string.bienvenido);
                                                        Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        String informacion = getString(R.string.bienvenido) + " " + separar[8].trim() + " " + separar[3].trim();
                                                        Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                                                    }
                                                    //Redirigiendo al usuario a la actividad Principal de la aplicación enviando el parámetro de nombre de usuario
                                                    Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                                    intent.putExtra("ApellidoUsuario", separar[3].trim());
                                                    intent.putExtra("NombreUsuario", separar[8].trim());
                                                    intent.putExtra("Tipo", separar[2].trim());
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    LoginActivity.this.startActivity(intent);
                                                }
                                            });
                                        } catch (Exception e) {
                                            // Mensaje de error al no poder conectarse a la base de datos
                                            Log.e("Error de conexión: ", e.getMessage());
                                        }
                                    }
                                };
                                // Empezando a correr el hilo
                                Thread mythread = new Thread(runnable);
                                mythread.start();
                            }
                        }), error -> LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Mensaje emergente y de consola al enviar credenciales incorrectas a los servicios de AWS
                                Log.e("Error", error.toString());
                                String informacionError = getString(R.string.inicio_sesion_fallida) + " " + getString(R.string.usuario_incorrecto);
                                Toast.makeText(getApplicationContext(), informacionError, Toast.LENGTH_SHORT).show();
                                //Segmento de código para habilitar los botones de la actividad
                                cambiarRecuperar.setEnabled(true);
                                cambiarInicio.setEnabled(true);
                                password.setEnabled(true);
                                usuario.setEnabled(true);
                            }
                        }));
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

        //Creando el evento click en un botón inicializado
        cambiarNuevo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Redirigiendo al usuario a la actividad de creación de nuevo usuario
                Intent intent = new Intent(LoginActivity.this, NuevoActivity.class);
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