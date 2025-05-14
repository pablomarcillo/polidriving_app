//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.menu;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para consultas en la base de datos
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.clases.usuario.UsuarioActivity;
import com.polidriving.mobile.clases.login.LoginActivity;
import com.polidriving.mobile.base_datos.DataBaseAccess;
import android.graphics.drawable.ColorDrawable;
import com.amplifyframework.core.Amplify;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.os.CountDownTimer;
import com.polidriving.mobile.R;
import android.widget.ImageView;
import java.text.MessageFormat;
import android.app.AlertDialog;
import android.widget.TextView;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

@SuppressLint("SetTextI18n")
@SuppressWarnings({"ConstantConditions", "RegExpRedundantEscape"})
public class FragmentosPerfilUsuario extends Fragment {
    TextView nombreApellidoUsuario;
    TextView telefonoUsuario;
    TextView licenciaUsuario;
    Button modificarUsuario;
    TextView medicasUsuario;
    Button eliminarUsuario;
    TextView nombreUsuario;
    TextView correoUsuario;
    TextView generoUsuario;
    TextView lentesUsuario;
    TextView puntosUsuario;
    TextView edadUsuario;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        final View view = inflater.inflate(R.layout.fragmento_lateral_usuario, container, false);
        //Inicializando las variables con los elementos de la actividad
        medicasUsuario = view.findViewById(R.id.respuestaCondicionesMedicas);
        nombreApellidoUsuario = view.findViewById(R.id.txtNombreyApellido);
        licenciaUsuario = view.findViewById(R.id.respuestaEstadoLicencia);
        puntosUsuario = view.findViewById(R.id.respuestaPuntosLicencia);
        nombreUsuario = view.findViewById(R.id.respuestaUsuarioUser);
        telefonoUsuario = view.findViewById(R.id.respuestaTelefono);
        modificarUsuario = view.findViewById(R.id.modificarUsuario);
        eliminarUsuario = view.findViewById(R.id.eliminarUsuario);
        correoUsuario = view.findViewById(R.id.respuestaCorreo);
        generoUsuario = view.findViewById(R.id.respuestaGenero);
        lentesUsuario = view.findViewById(R.id.respuestaLentes);
        edadUsuario = view.findViewById(R.id.respuestaEdad);

        //Segmento de código que permite obtener las credenciales del usuario y establecer conexión con la base de datos
        //Segmento de código utilizado para verificar si el usuario ya había iniciado sesión previamente
        Amplify.Auth.fetchAuthSession(result -> getActivity().runOnUiThread(new Runnable() {
            //Segmento de código que se ejecuta en un hilo secundario para verificar la conexión con los servicios de AWS
            public void run() {
                //Mensaje de consola que informa al usuario que se estableció conexión con los servicios de AWS
                Log.i("Información AWS: ", result.toString());
                try {
                    //Segmento de código que permite obtener de los servicios de AWS el nombre de usuario actualmente logueado
                    //Segmento de código que verifica si el usuario ya había iniciado sesión previamente
                    Amplify.Auth.getCurrentUser(result -> getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            //Mostrando en pantalla el nombre de usuario
                            nombreUsuario.setText(result.getUsername());
                            //Mensaje de consola que informa al usuario que se auténtico al usuario con los servicios de AWS
                            Log.i("Información AuthUser: ", result.toString());
                            // Iniciando hilo secundario para las consultas a la base de datos
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    try {
                                        // Llamando a una nueva instancia de la base de datos
                                        DataBaseAccess nuevaConsulta = new DataBaseAccess();
                                        // Método para obtener la lista de atributos desde la base de datos mediate la clave del UserName
                                        String obtenerDatos = (String) nuevaConsulta.obtenerUsuario(result.getUsername());
                                        // Se separa los atributos obtenidos mediante un split
                                        String[] separar = obtenerDatos.split(",");
                                        // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                        getActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                // Verificando el tipo de Usuario
                                                eliminarUsuario.setEnabled(!separar[2].trim().equals(getString(R.string.tipo_0)));
                                                // Se presenta los datos en pantalla en sus respectivos elementos
                                                licenciaUsuario.setText(separar[1].trim());
                                                medicasUsuario.setText(separar[6].trim());
                                                lentesUsuario.setText(separar[10].trim());
                                                correoUsuario.setText(separar[7].trim());
                                                generoUsuario.setText(separar[9].trim());
                                                if (separar[0].replaceAll("\\[", "").trim().equals("-1")) {
                                                    puntosUsuario.setText(getString(R.string.no_ingresado));
                                                } else {
                                                    puntosUsuario.setText(separar[0].replaceAll("\\[", "").trim());
                                                }
                                                if (separar[11].replaceAll("\\]", "").trim().equals("-1")) {
                                                    edadUsuario.setText(getString(R.string.no_ingresado));
                                                } else {
                                                    edadUsuario.setText(separar[11].replaceAll("\\]", "").trim());
                                                }

                                                if (separar[8].trim().equals(getString(R.string.no_ingresado)) || separar[3].trim().equals(getString(R.string.no_ingresado))) {
                                                    nombreApellidoUsuario.setText(getString(R.string.no_ingresado));
                                                } else {
                                                    nombreApellidoUsuario.setText(separar[8].trim() + " " + separar[3].trim());
                                                }

                                                if (separar[4].trim().equals("-1")) {
                                                    telefonoUsuario.setText(getString(R.string.no_ingresado));
                                                } else {
                                                    telefonoUsuario.setText(separar[4].trim());
                                                }
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
                    }), error -> getActivity().runOnUiThread(new Runnable() {
                        //Segmento de código que se ejecuta cuando no se pudo obtener la información del usuario
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión o al iniciar/regresar a la aplicación
                            Log.e("Error AuthUser: ", error.toString());
                        }
                    }));
                } catch (Exception error) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            //Mensaje de consola y emergente al fallar el inicio de sesión
                            Log.e("Error Nombre: ", error.toString());
                        }
                    });
                }
            }
        }), error -> getActivity().runOnUiThread(new Runnable() {
            public void run() {
                //Mensaje de consola que informa al usuario que no se pudo obtener el nombre de usuario
                Log.e("Error AWS: ", error.toString());
            }
        }));

        //Creando el evento click en un botón inicializado
        modificarUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Mensaje emergente de información de usuario
                String mensaje = getString(R.string.perfil_usuario);
                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de perfil de usuario
                Intent intent = new Intent(getActivity(), UsuarioActivity.class);
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón inicializado
        eliminarUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Mensaje especial de advertencia de información al usuario con duración de 5 segundos
                String mensaje = "¿Desea Eliminar el Usuario?" + "\n" + "Esta acción no se puede deshacer";
                //Invocando el mensaje de advertencia
                mostrarRojo(mensaje);
            }
        });

        //Retornando el estado de la actividad
        return view;
    }

    private void mostrarRojo(String texto) {
        //Método especial para crear un cuadro de diálogo personalizado
        //Permite mostrar un mensaje de advertencia en color rojo al usuario
        //Construcción de un cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = new View(getActivity());
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
                Amplify.Auth.fetchAuthSession(result -> getActivity().runOnUiThread(new Runnable() {
                    //Segmento de código que se ejecuta en un hilo secundario para verificar la conexión con los servicios de AWS
                    public void run() {
                        //Mensaje de consola que informa al usuario que se estableció conexión con los servicios de AWS
                        Log.i("Información AWS: ", result.toString());
                        try {
                            //Segmento de código que permite obtener de los servicios de AWS el nombre de usuario actualmente logueado
                            Amplify.Auth.getCurrentUser(result -> getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    //Mostrando en pantalla el nombre de usuario
                                    nombreUsuario.setText(result.getUsername());
                                    //Mensaje de consola que informa al usuario que se auténtico al usuario con los servicios de AWS
                                    Log.i("Información AuthUser: ", result.toString());
                                    // Iniciando hilo secundario para las consultas a la base de datos
                                    Runnable runnable = new Runnable() {
                                        public void run() {
                                            try {
                                                // Llamando a una nueva instancia de la base de datos
                                                DataBaseAccess nuevaConsulta_1 = new DataBaseAccess();
                                                // Método para obtener la lista de atributos desde ls base de datos mediate la clave del UserName
                                                String obtenerDatos = (String) nuevaConsulta_1.obtenerUsuario(result.getUsername());
                                                // Se separa los atributos obtenidos mediante un split
                                                String[] separar = obtenerDatos.split(",");
                                                // Llamando a una nueva instancia de la base de datos
                                                DataBaseAccess nuevaConsulta_2 = new DataBaseAccess();
                                                // Eliminando un usuario con sus credenciales y datos
                                                nuevaConsulta_2.eliminarUsuario(separar[5].trim(), separar[7].trim());
                                                try {
                                                    Amplify.Auth.deleteUser(() -> getActivity().runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    //Presentación de información al usuario por consola y mediante un mensaje emergente
                                                                    Log.i("Información", "Usuario eliminado con éxito");
                                                                    Toast.makeText(getActivity(), "Usuario eliminado con éxito", Toast.LENGTH_SHORT).show();
                                                                    //Redirigiendo al usuario a la actividad de login de usuario
                                                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                                    //Iniciando la actividad de login
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    }), error -> getActivity().runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    //Presentación de información al usuario por consola y mediante un mensaje emergente
                                                                    Log.e("Eliminación: ", "Falla al eliminar el usuario: " + error);
                                                                    Toast.makeText(getActivity(), "Falla al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }));
                                                } catch (Exception e) {
                                                    // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            //Presentación de información al usuario por consola y mediante un mensaje emergente
                                                            Log.e("Eliminación: ", "Falla al eliminar el usuario: " + e);
                                                            Toast.makeText(getActivity(), "Falla al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                // Se lleva los datos obtenidos al hilo principal para mostrarlos en pantalla
                                                getActivity().runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        //Presentación de información al usuario por consola y mediante un mensaje emergente
                                                        Log.e("Eliminación: ", "Falla al eliminar el usuario: " + e);
                                                        Toast.makeText(getActivity(), "Falla al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    };
                                    // Empezando a correr el hilo
                                    Thread mythread = new Thread(runnable);
                                    mythread.start();
                                }
                            }), error -> getActivity().runOnUiThread(new Runnable() {
                                //Segmento de código que se ejecuta cuando no se pudo obtener la información del usuario
                                public void run() {
                                    //Mensaje de consola y emergente al fallar el inicio de sesión o al iniciar/regresar a la aplicación
                                    Log.e("Error AuthUser: ", error.toString());
                                }
                            }));
                        } catch (Exception error) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    //Mensaje de consola y emergente al fallar el inicio de sesión
                                    Log.e("Error Nombre: ", error.toString());
                                }
                            });
                        }
                    }
                }), error -> getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //Mensaje de consola que informa al usuario que no se pudo obtener el nombre de usuario
                        Log.e("Error AWS: ", error.toString());
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

    public void onDestroyView() {
        super.onDestroyView();
        //TODO
    }
}