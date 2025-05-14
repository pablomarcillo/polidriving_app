//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.nuevo;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
//Clases usadas para autenticación
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.polidriving.mobile.clases.codigo.CodigoActivity;
import com.polidriving.mobile.clases.login.LoginActivity;
import com.amplifyframework.auth.AuthUserAttributeKey;
import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.core.Amplify;
import com.polidriving.mobile.R;
import java.util.regex.Pattern;
import android.widget.EditText;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

public class NuevoActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    EditText confirmar;
    EditText password;
    EditText usuario;
    EditText correo;
    Button cancelar;
    Button limpiar;
    Button guardar;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo);

        //Inicializando las variables con los elementos de la actividad
        confirmar = findViewById(R.id.txtNuevoConfirmarContrasena);
        password = findViewById(R.id.txtNuevoContrasena);
        cancelar = findViewById(R.id.botonCancelarNuevo);
        limpiar = findViewById(R.id.botonLimpiarNuevo);
        guardar = findViewById(R.id.botonGuardarNuevo);
        usuario = findViewById(R.id.txtNuevoNombre);
        correo = findViewById(R.id.txtNuevoCorreo);

        //Bloquear elementos no usados
        cancelar.setVisibility(View.INVISIBLE);
        //limpiar.setVisibility(View.INVISIBLE);
        cancelar.setEnabled(false);
        //limpiar.setEnabled(false);

        //Segmento de código que permite la verificación de la longitud de la contraseña cuando este campo pierde el foco
        //Usado en el campo de confirmación de contraseña
        confirmar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Mensaje lateral secundario que informa al usuario que la contraseña debe tener una longitud mínima de 8 caracteres
                    if (confirmar.getText().toString().trim().length() < 8) {
                        confirmar.setError("El Password debe tener una longitud mínima de 8");
                    } else {
                        //Mensaje lateral secundario al usuario que informa que la contraseña no posee la longitud mínima
                        confirmar.setError(null);
                    }
                }
            }
        });

        //Segmento de código que permite la verificación de la longitud de la contraseña cuando este campo pierde el foco
        //Usado en el campo de contraseña
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Mensaje lateral secundario que informa al usuario que la contraseña debe tener una longitud mínima de 8 caracteres
                    if (password.getText().toString().trim().length() < 8) {
                        password.setError("El Password debe tener una longitud mínima de 8");
                    } else {
                        //Mensaje lateral secundario al usuario que informa que la contraseña no posee la longitud mínima
                        password.setError(null);
                    }
                }
            }
        });

        //Segmento de código que permite la verificación del nombre de usuario. El nombre de usuario no debe tener espacios en blanco
        usuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Mensaje lateral secundario que informa al usuario que nombre ingresado no debe tener espacios en blanco
                    usuario.setError("El Usuario no debe tener espacios en blanco");
                }
            }
        });

        //Creando el evento click en un botón inicializado
        cancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Redirigiendo al usuario a la actividad de Login y cancelar el registro de un nuevo usuario
                Intent intent = new Intent(NuevoActivity.this, LoginActivity.class);
                //Permite limpiar los campos de la actividad
                limpiarCampos();
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón inicializado
        limpiar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Permite limpiar los campos de la actividad
                limpiarCampos();
            }
        });

        //Creando el evento click en un botón inicializado
        guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //Verificando que no existan campos que se encuentre vacíos
                    if (!correo.getText().toString().isEmpty() && !usuario.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !confirmar.getText().toString().isEmpty()) {
                        //Verificando el formato de correo sea valido
                        if (validarEmail(correo.getText().toString())) {
                            //Verificando que el campo contraseña y verificar contraseña sean iguales
                            if (password.getText().toString().equals(confirmar.getText().toString())) {
                                //Verificando la longitud de la contraseña y verificar contraseña tengan un longitud mínima de 8
                                if (confirmar.getText().toString().trim().length() < 8 || password.getText().toString().trim().length() < 8) {
                                    Toast.makeText(getApplicationContext(), "El Password debe tener una longitud mínima de 8", Toast.LENGTH_SHORT).show();
                                    //Limpia los campos de la actividad
                                    limpiarCampos();
                                } else {
                                    try {
                                        //Obteniendo los valores de usuario, correo y password de los campos ingresados por el usuario
                                        String contrasena = password.getText().toString();
                                        String username = usuario.getText().toString();
                                        String email = correo.getText().toString();
                                        //Enviando a los servicios de AWS el nombre de usuario, correo y el contraseña para su verificación
                                        AuthSignUpOptions options = AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email).build();
                                        Amplify.Auth.signUp(username, contrasena, options, result -> NuevoActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Mensaje emergente que informa al usuario que se ha enviado un codigo de confirmación al correo ingresado
                                                String codigo = getString(R.string.codigo_enviado);
                                                Toast.makeText(getApplicationContext(), codigo, Toast.LENGTH_SHORT).show();
                                                Log.i("Información", result.toString());
                                                //Redirigiendo al usuario a la actividad de Verificación de Código
                                                Intent intent = new Intent(NuevoActivity.this, CodigoActivity.class);
                                                intent.putExtra("Correo", email);
                                                intent.putExtra("Nombre", username);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                NuevoActivity.this.startActivity(intent);
                                            }
                                        }), error -> NuevoActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                //Segmento de código que permite verificar la existencia del nombre de usuario
                                                String mensaje = "UsernameExistsException(message=User already exists)";
                                                Log.e("Error", String.valueOf(error.getCause()));
                                                if (String.valueOf(error.getCause()).equals(mensaje)) {
                                                    Toast.makeText(getApplicationContext(), "Error al crear el usuario. El Nombre de Usuario ya existe", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Error al crear el usuario", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }));
                                        //Limpia los campos de la actividad
                                        limpiarCampos();
                                    } catch (Exception e) {
                                        //Mensaje emergente de advertencia al usuario que indica que no se pudo crear el usuario
                                        String error = getString(R.string.error_crear_usuario);
                                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                //Mensaje emergente de advertencia al usuario que indica que el campo contraseña y el campo verificar contraseña no son iguales
                                String error = getString(R.string.password_no_conicide);
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Mensaje emergente de advertencia al usuario que indica que el correo ingresado con tiene un formato valido
                            String error = getString(R.string.correo_invalido);
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

    private boolean validarEmail(String email) {
        //Método que permite la validación en formato del correo ingresado por el usuario
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void limpiarCampos() {
        //Método que permite limpiar los campos de registro de usuario
        confirmar.getText().clear();
        password.getText().clear();
        usuario.getText().clear();
        correo.getText().clear();
    }

    protected void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        //TODO
    }
}