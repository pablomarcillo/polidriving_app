//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.recuperar;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
//Clases usadas para autenticación
import com.polidriving.mobile.clases.login.LoginActivity;
import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.core.Amplify;
import com.polidriving.mobile.R;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

public class RecuperarActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    EditText verificarNuevoPassword;
    EditText usuarioRecuperacion;
    Button restablecerPassword;
    EditText verificarCodigo;
    EditText nuevoPassword;
    Button enviarCodigo;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        //Inicializando las variables con los elementos de la actividad
        verificarNuevoPassword = findViewById(R.id.txtConfirmarPasswordRestablecer);
        restablecerPassword = findViewById(R.id.botonRestablecerPassword);
        usuarioRecuperacion = findViewById(R.id.txtUsuarioRecuperacion);
        verificarCodigo = findViewById(R.id.txtCodigoRecuperacion);
        nuevoPassword = findViewById(R.id.txtPasswordRestablecer);
        enviarCodigo = findViewById(R.id.botonVerificarUsuario);

        //Método que permite verificar la longitud de la contraseña en el campo de verificar contraseña
        verificarNuevoPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (verificarNuevoPassword.getText().toString().trim().length() < 8) {
                        verificarNuevoPassword.setError("El Password debe tener una longitud mínima de 8");
                    } else {
                        verificarNuevoPassword.setError(null);
                    }
                }
            }
        });

        //Método que permite verificar la longitud de la contraseña
        nuevoPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (nuevoPassword.getText().toString().trim().length() < 8) {
                        nuevoPassword.setError("El Password debe tener una longitud mínima de 8");
                    } else {
                        nuevoPassword.setError(null);
                    }
                }
            }
        });

        //Método que permite enviar el código de confirmación a los servicios de AWS
        enviarCodigo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //Verificando que no existan campos que se encuentre vacíos
                    if (!usuarioRecuperacion.getText().toString().isEmpty()) {
                        //Método que permite a los servicios de AWS reestablecer la contraseña de usuario
                        Amplify.Auth.resetPassword(usuarioRecuperacion.getText().toString(), result -> RecuperarActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Mensaje emergente y de consola que informa al usuario que se ha enviado un código al correo del usuario
                                String codigo = getString(R.string.codigo_enviado);
                                Toast.makeText(getApplicationContext(), codigo, Toast.LENGTH_SHORT).show();
                                Log.i("Información", result.toString());
                                //Se habilita/inhabilita una serie de campos para el reestablecimiento de la contraseña
                                verificarNuevoPassword.setEnabled(true);
                                usuarioRecuperacion.setEnabled(false);
                                restablecerPassword.setEnabled(true);
                                verificarCodigo.setEnabled(true);
                                nuevoPassword.setEnabled(true);
                                enviarCodigo.setEnabled(false);
                            }
                        }), error -> RecuperarActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Mensaje emergente que informa al usuario que no se pudo reestablecer la contraseña
                                String mensaje = "UserNotFoundException(message=Username/client id combination not found.)";
                                Log.e("Error", String.valueOf(error.getCause()));
                                if (String.valueOf(error.getCause()).equals(mensaje)) {
                                    //Mensaje emergente que informa que el nombre de usuario no existe
                                    usuarioRecuperacion.getText().clear();
                                    Toast.makeText(getApplicationContext(), "El Nombre de Usuario no existe. Ingrese un usuario valido", Toast.LENGTH_SHORT).show();
                                } else {
                                    //Mensaje emergente que informa que no se pudo leer el nombre de usuario
                                    Toast.makeText(getApplicationContext(), "Error al leer el Usuario", Toast.LENGTH_SHORT).show();
                                    usuarioRecuperacion.getText().clear();
                                }
                            }
                        }));
                    } else {
                        //Mensaje emergente de advertencia al usuario si se detectan campos vacíos
                        String error = getString(R.string.ingrese_usuario);
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    //Mensaje emergente de error al usuario si el evento click falla
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Método que permite verificar si el código ingresado es el correcto
        restablecerPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //Verificando que no existan campos que se encuentre vacíos
                    if (!verificarCodigo.getText().toString().isEmpty() && !nuevoPassword.getText().toString().isEmpty() && !verificarNuevoPassword.getText().toString().isEmpty()) {
                        //Verificando que el campo contraseña y verificar contraseña sean iguales
                        if (nuevoPassword.getText().toString().equals(verificarNuevoPassword.getText().toString())) {
                            //Verificando que los campos contraseña y verificar contraseña sean mayores o iguales a 8 en longitud
                            if (nuevoPassword.getText().toString().trim().length() < 8 || verificarNuevoPassword.getText().toString().trim().length() < 8) {
                                Toast.makeText(getApplicationContext(), "El Password debe tener una longitud mínima de 8", Toast.LENGTH_SHORT).show();
                                verificarNuevoPassword.getText().clear();
                                nuevoPassword.getText().clear();
                            } else {
                                //Método que permite al usuario establecer una nueva contraseña en los servicios de AWS
                                Amplify.Auth.confirmResetPassword(usuarioRecuperacion.getText().toString(), nuevoPassword.getText().toString(), verificarCodigo.getText().toString(), () -> RecuperarActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        //Mensaje que informa al usuario que se cambio la contraseña con éxito
                                        Log.i("Información", "Contraseña Restablecida con éxito");
                                        Toast.makeText(getApplicationContext(), "Contraseña Restablecida con éxito", Toast.LENGTH_SHORT).show();
                                        //Redirigiendo al usuario ala actividad de login
                                        Intent intent = new Intent(RecuperarActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        RecuperarActivity.this.startActivity(intent);
                                    }
                                }), error -> RecuperarActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        //Mensaje que informa al usuario que no se pudo cambiar la contraseña
                                        String error = "No se pudo Restablecer la Contraseña";
                                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                        Log.e("Error", error);
                                    }
                                }));
                            }
                        } else {
                            //Mensaje que informa que los campos contraseña y verificar contraseña no son iguales
                            String error = getString(R.string.password_no_conicide);
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        }
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
    }

    protected void onDestroy() {
        //Finaliza completamente la actividad
        super.onDestroy();
        //TODO
    }
}