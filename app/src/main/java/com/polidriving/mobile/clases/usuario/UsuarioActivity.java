//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.usuario;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para consultas en la base de datos
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.base_datos.DataBaseAccess;
import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.core.Amplify;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import com.polidriving.mobile.R;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

/**
 * @noinspection DataFlowIssue
 */
@SuppressWarnings("RegExpRedundantEscape")
public class UsuarioActivity extends AppCompatActivity {
    //Inicializando las variables con los elementos de la actividad
    String seleccionado_Genero;
    String seleccionado_Lentes;
    String seleccionado_Estado;
    String seleccionado_Puntos;
    String seleccionado_Medica;
    TextView telefonoUsuario;
    TextView apellidoUsuario;
    String seleccionado_Edad;
    Button modificarUsuario;
    TextView correoUsuario;
    TextView nombreUsuario;
    Spinner spinnerGenero;
    Spinner spinnerLentes;
    Spinner spinnerEstado;
    Spinner spinnerPuntos;
    Spinner spinnerMedica;
    TextView tipoUsuario;
    Spinner spinnerEdad;
    TextView userName;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        //Inicializando las variables con los elementos de la actividad
        spinnerMedica = findViewById(R.id.respuestaCondicionesMedicas_Perfil);
        spinnerEstado = findViewById(R.id.respuestaEstadoLicencia_Perfil);
        spinnerPuntos = findViewById(R.id.respuestaPuntosLicencia_Perfil);
        modificarUsuario = findViewById(R.id.modificarUsuario_Perfil);
        apellidoUsuario = findViewById(R.id.respuestaApellido_Perfil);
        telefonoUsuario = findViewById(R.id.respuestaTelefono_Perfil);
        userName = findViewById(R.id.respuestaUsuarioUser_Perfil);
        spinnerGenero = findViewById(R.id.respuestaGenero_Perfil);
        spinnerLentes = findViewById(R.id.respuestaLentes_Perfil);
        correoUsuario = findViewById(R.id.respuestaCorreo_Perfil);
        nombreUsuario = findViewById(R.id.respuestaNombre_Perfil);
        spinnerEdad = findViewById(R.id.respuestaEdad_Perfil);
        tipoUsuario = findViewById(R.id.respuestaTipo_Perfil);

        Amplify.Auth.fetchAuthSession(result -> UsuarioActivity.this.runOnUiThread(new Runnable() {
            //Segmento de código que se ejecuta en un hilo secundario para verificar la conexión con los servicios de AWS
            public void run() {
                //Mensaje de consola que informa al usuario que se estableció conexión con los servicios de AWS
                Log.i("Información AWS: ", result.toString());
                try {
                    //Segmento de código que permite obtener de los servicios de AWS el nombre de usuario actualmente logueado
                    //Segmento de código que verifica si el usuario ya había iniciado sesión previamente
                    Amplify.Auth.getCurrentUser(result -> UsuarioActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Mostrando en pantalla el nombre de usuario
                            userName.setText(result.getUsername());
                            //Mensaje de consola que informa al usuario que se auténtico al usuario con los servicios de AWS
                            Log.i("Información AuthUser: ", result.toString());
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
                                        UsuarioActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                // Se presenta los datos en pantalla en sus respectivos elementos
                                                // Se recorre los elementos del usuario y se carga el almacenado en la base de dats
                                                for (int i = 0; i < spinnerPuntos.getAdapter().getCount(); i++) {
                                                    //Verifica la existencia del elemento en el spinner
                                                    if (spinnerPuntos.getAdapter().getItem(i).toString().contains(separar[0].replaceAll("\\[", "").trim())) {
                                                        // Mostrando elemento en pantalla
                                                        spinnerPuntos.setSelection(i);
                                                        break;
                                                    } else {
                                                        spinnerPuntos.setSelection(0);
                                                    }
                                                }
                                                for (int i = 0; i < spinnerEdad.getAdapter().getCount(); i++) {
                                                    if (spinnerEdad.getAdapter().getItem(i).toString().equals(separar[11].replaceAll("\\]", "").trim())) {
                                                        spinnerEdad.setSelection(i);
                                                        break;
                                                    } else {
                                                        spinnerEdad.setSelection(0);
                                                    }
                                                }
                                                for (int i = 0; i < spinnerLentes.getAdapter().getCount(); i++) {
                                                    if (spinnerLentes.getAdapter().getItem(i).toString().equals(separar[10].trim())) {
                                                        spinnerLentes.setSelection(i);
                                                        break;
                                                    } else {
                                                        spinnerLentes.setSelection(0);
                                                    }
                                                }
                                                for (int i = 0; i < spinnerMedica.getAdapter().getCount(); i++) {
                                                    if (spinnerMedica.getAdapter().getItem(i).toString().equals(separar[6].trim())) {
                                                        spinnerMedica.setSelection(i);
                                                        break;
                                                    } else {
                                                        spinnerMedica.setSelection(0);
                                                    }
                                                }
                                                for (int i = 0; i < spinnerEstado.getAdapter().getCount(); i++) {
                                                    if (spinnerEstado.getAdapter().getItem(i).toString().equals(separar[1].trim())) {
                                                        spinnerEstado.setSelection(i);
                                                        break;
                                                    } else {
                                                        spinnerEstado.setSelection(0);
                                                    }
                                                }
                                                for (int i = 0; i < spinnerGenero.getAdapter().getCount(); i++) {
                                                    if (spinnerGenero.getAdapter().getItem(i).toString().equals(separar[9].trim())) {
                                                        spinnerGenero.setSelection(i);
                                                        break;
                                                    } else {
                                                        spinnerGenero.setSelection(0);
                                                    }
                                                }
                                                // Se muestra los elementos en pantalla
                                                if (separar[4].trim().equals("-1")) {
                                                    telefonoUsuario.setText(getString(R.string.no_ingresado));
                                                } else {
                                                    telefonoUsuario.setText(separar[4].trim());
                                                }
                                                apellidoUsuario.setText(separar[3].trim());
                                                nombreUsuario.setText(separar[8].trim());
                                                correoUsuario.setText(separar[7].trim());
                                                tipoUsuario.setText(separar[2].trim());
                                                // Mensaje de consola que informa que se realiza la consulta exitosa
                                                Log.i("Consulta Exitosa: ", obtenerDatos);
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
                    }), error -> UsuarioActivity.this.runOnUiThread(new Runnable() {
                        //Segmento de código que se ejecuta cuando no se pudo obtener la información del usuario
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión o al iniciar/regresar a la aplicación
                            Log.e("Error AuthUser: ", error.toString());
                        }
                    }));
                } catch (Exception error) {
                    UsuarioActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión
                            Log.e("Error Nombre: ", error.toString());
                        }
                    });
                }
            }
        }), error -> UsuarioActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                //Mensaje de consola que informa al usuario que no se pudo obtener el nombre de usuario
                Log.e("Error AWS: ", error.toString());
            }
        }));

        //Llamando a los métodos que permite leer los elementos en el spinner Medica
        spinnerMedica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obteniendo el valor seleccionado de spinner
                seleccionado_Medica = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });

        //Llamando a los métodos que permite leer los elementos en el spinner Estado
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obteniendo el valor seleccionado de spinner
                seleccionado_Estado = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });

        //Llamando a los métodos que permite leer los elementos en el spinner Puntos
        spinnerPuntos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obteniendo el valor seleccionado de spinner
                seleccionado_Puntos = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });

        //Llamando a los métodos que permite leer los elementos en el spinner Genero
        spinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obteniendo el valor seleccionado de spinner
                seleccionado_Genero = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });

        //Llamando a los métodos que permite leer los elementos en el spinner Lentes
        spinnerLentes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obteniendo el valor seleccionado de spinner
                seleccionado_Lentes = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });

        //Llamando a los métodos que permite leer los elementos en el spinner Edad
        spinnerEdad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obteniendo el valor seleccionado de spinner
                seleccionado_Edad = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //TODO
            }
        });

        //Creando el evento click en un botón inicializado
        modificarUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Iniciando hilo secundario para las consultas a la base de datos
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            if (spinnerPuntos.getSelectedItem().equals(getString(R.string.no_ingresado)) || spinnerEstado.getSelectedItem().equals(getString(R.string.no_ingresado)) || telefonoUsuario.getText().equals(getString(R.string.no_ingresado)) || telefonoUsuario.getText().toString().isEmpty() || spinnerEdad.getSelectedItem().equals(getString(R.string.no_ingresado)) || apellidoUsuario.getText().equals(getString(R.string.no_ingresado)) || apellidoUsuario.getText().toString().isEmpty() || spinnerMedica.getSelectedItem().equals(getString(R.string.no_ingresado)) || nombreUsuario.getText().equals(getString(R.string.no_ingresado)) || nombreUsuario.getText().toString().isEmpty() || spinnerGenero.getSelectedItem().equals(getString(R.string.no_ingresado)) || spinnerLentes.getSelectedItem().equals(getString(R.string.no_ingresado))) {
                                // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                UsuarioActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        //Presentación de información al usuario por consola y mediante un mensaje emergente
                                        String mensaje = getString(R.string.falta_informacion);
                                        Log.e("Error: ", mensaje);
                                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Llamando a una nueva instancia de la base de datos
                                DataBaseAccess nuevaConsulta = new DataBaseAccess();
                                // Actualizando un usuario con sus credenciales y datos por defecto
                                nuevaConsulta.actualizarUsuario((String) spinnerPuntos.getSelectedItem(), (String) spinnerEstado.getSelectedItem(), userName.getText().toString(), telefonoUsuario.getText().toString(), tipoUsuario.getText().toString(), correoUsuario.getText().toString(), (String) spinnerEdad.getSelectedItem(), apellidoUsuario.getText().toString(), (String) spinnerMedica.getSelectedItem(), nombreUsuario.getText().toString(), (String) spinnerGenero.getSelectedItem(), (String) spinnerLentes.getSelectedItem());
                                // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                UsuarioActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        //Presentación de información al usuario por consola y mediante un mensaje emergente
                                        String mensaje = getString(R.string.usuario_actualizado);
                                        Log.i("Información: ", mensaje);
                                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                                        //Redirigiendo al usuario a la actividad de Usuario
                                        finish();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            // Mensaje de error al no poder conectarse a la base de datos
                            Log.e("Error de actualización: ", e.getMessage());
                        }
                    }
                };
                // Empezando a correr el hilo
                Thread mythread = new Thread(runnable);
                mythread.start();
            }
        });
        //Llamando al método que carga los elementos en los spinners
        cargarCamposDefault();
    }

    private void cargarCamposDefault() {
        //Método que permite cargar los campos por default
        //Creación de un ArrayAdapter usando la matriz de cadenas y un diseño giratorio predeterminado
        ArrayAdapter<CharSequence> adapter_medica = ArrayAdapter.createFromResource(this, R.array.yes_no, android.R.layout.simple_spinner_item);
        //Se especifica el diseño que se usará cuando aparezca la lista de opciones
        adapter_medica.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Aplicar el adaptador a la ruleta
        spinnerMedica.setAdapter(adapter_medica);

        //Creación de un ArrayAdapter usando la matriz de cadenas y un diseño giratorio predeterminado
        ArrayAdapter<CharSequence> adapter_estado = ArrayAdapter.createFromResource(this, R.array.licencia, android.R.layout.simple_spinner_item);
        //Se especifica el diseño que se usará cuando aparezca la lista de opciones
        adapter_estado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Aplicar el adaptador a la ruleta
        spinnerEstado.setAdapter(adapter_estado);

        //Creación de un ArrayAdapter usando la matriz de cadenas y un diseño giratorio predeterminado
        ArrayAdapter<CharSequence> adapter_puntos = ArrayAdapter.createFromResource(this, R.array.puntos, android.R.layout.simple_spinner_item);
        //Se especifica el diseño que se usará cuando aparezca la lista de opciones
        adapter_puntos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Aplicar el adaptador a la ruleta
        spinnerPuntos.setAdapter(adapter_puntos);

        //Creación de un ArrayAdapter usando la matriz de cadenas y un diseño giratorio predeterminado
        ArrayAdapter<CharSequence> adapter_genero = ArrayAdapter.createFromResource(this, R.array.genero, android.R.layout.simple_spinner_item);
        //Se especifica el diseño que se usará cuando aparezca la lista de opciones
        adapter_genero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Aplicar el adaptador a la ruleta
        spinnerGenero.setAdapter(adapter_genero);

        //Creación de un ArrayAdapter usando la matriz de cadenas y un diseño giratorio predeterminado
        ArrayAdapter<CharSequence> adapter_lentes = ArrayAdapter.createFromResource(this, R.array.yes_no, android.R.layout.simple_spinner_item);
        //Se especifica el diseño que se usará cuando aparezca la lista de opciones
        adapter_lentes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Aplicar el adaptador a la ruleta
        spinnerLentes.setAdapter(adapter_lentes);

        //Creación de un ArrayAdapter usando la matriz de cadenas y un diseño giratorio predeterminado
        ArrayAdapter<CharSequence> adapter_edad = ArrayAdapter.createFromResource(this, R.array.edades, android.R.layout.simple_spinner_item);
        //Se especifica el diseño que se usará cuando aparezca la lista de opciones
        adapter_edad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Aplicar el adaptador a la ruleta
        spinnerEdad.setAdapter(adapter_edad);
    }

    public void onDestroy() {
        super.onDestroy();
        //TODO
    }
}