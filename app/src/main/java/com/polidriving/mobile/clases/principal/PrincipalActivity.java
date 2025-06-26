//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.polidriving.mobile.BuildConfig;
import com.polidriving.mobile.clases.configuracion.ConfiguracionActivity;
import com.polidriving.mobile.clases.notificaciones.NotificacionesVoz;
import com.polidriving.mobile.clases.accuweather.AccuWeatherActivity;
import com.polidriving.mobile.clases.comentarios.ComentariosActivity;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.polidriving.mobile.databinding.ActivityPrincipalBinding;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.polidriving.mobile.clases.privacidad.PrivacyActivity;
import com.polidriving.mobile.clases.vehiculos.VehiculosActivity;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.polidriving.mobile.clases.cuenta.AccountActivity;
import com.polidriving.mobile.clases.riesgo.RiesgoActivity;
import com.polidriving.mobile.clases.mapas.MapasActivity;
import com.polidriving.mobile.base_datos.DataBaseAccess;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.polidriving.mobile.clases.obd.ObdActivity;
import com.google.android.material.tabs.TabLayout;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.ObdCommand;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import com.github.pires.obd.enums.ObdProtocols;
import com.polidriving.mobile.dataset.DataSet;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import com.amplifyframework.core.Amplify;
import java.nio.charset.StandardCharsets;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import android.widget.LinearLayout;
import android.os.CountDownTimer;
import java.io.InputStreamReader;
import com.polidriving.mobile.R;
import android.widget.ImageView;
import android.content.Context;
import android.widget.TextView;
import java.text.MessageFormat;
import android.content.Intent;
import java.io.BufferedReader;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.IOException;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;
import android.view.Menu;
import android.Manifest;
import android.util.Log;
import java.util.Arrays;
import java.util.Timer;
import java.util.Date;
import java.util.UUID;

/**
 * @noinspection DataFlowIssue
 */
@SuppressWarnings({"BusyWait", "unused", "RegExpRedundantEscape", "deprecation"})
@SuppressLint({"MissingSuperCall", "SetTextI18n", "DefaultLocale"})
public class PrincipalActivity extends AppCompatActivity {
    //Creación del concatenador de los fragmentos dentro de la actividad principal
    //Creación de variables para enviar, presentar y recibir información
    ObdCommand command_EngineCoolantTemperature = new EngineCoolantTemperatureCommand();
    ObdCommand command_ThrottlePosition = new ThrottlePositionCommand();
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    ObdCommand command_Speed = new SpeedCommand();
    ObdCommand command_Load = new LoadCommand();
    private final String URL_API_PREDICTOR = BuildConfig.URL_API_PREDICTOR;
    private ActivityPrincipalBinding vinculante;
    private NotificacionesVoz conversor = null;
    ObdCommand command_RPM = new RPMCommand();
    Boolean get_set_MuyAlta = false;
    TextView nombreApellidoUsuario;
    Boolean get_set_Media = false;
    Boolean get_set_Alta = false;
    Boolean get_set_Baja = false;
    String chosenDeviceAddress;
    LinearLayout grupoActual;
    BluetoothSocket btSocket;
    LinearLayout grupoNivel;
    String chosenDeviceName;
    TextView resultadoTexto;
    String apellidoUsuario;
    String nombreUsuario;
    String tipoUsuario;
    TextView actual;
    Timer myTimer; // Timer para la lectura OBD

    int TIMEOUT_API_PREDICTOR = BuildConfig.TIMEOUT_API_PREDICTOR; // Tiempo de espera para la API del predictor, en milisegundos
    int INTERVALO_ENTRE_PETICIONES = BuildConfig.INTERVALO_ENTRE_PETICIONES; // Intervalo entre peticiones al modelo ML, en milisegundos

    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneja los clics del elemento de la barra de acción.
        // La barra de acción se maneja automáticamente los clics en el botón Inicio
        int id = item.getItemId();

        //Obteniendo el tipo del usuario que es enviado desde la actividad login al hacer logueo con éxito
        Intent tipo = getIntent();
        tipoUsuario = tipo.getStringExtra("Tipo");
        // Bloqueando elementos dependiendo del tipo de usuario
        if (tipoUsuario.equals(getString(R.string.tipo_0))) {
            if (id == R.id.accion_usuario) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Información de Usuario
                String saludo = getString(R.string.informacion_usuario);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Información de Usuario
                Intent intent = new Intent(PrincipalActivity.this, AccountActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
            if (id == R.id.accion_escaner) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a escaner
                String saludo = getString(R.string.no_permitido);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
            }
            if (id == R.id.accion_alertas) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a alertas
                String saludo = getString(R.string.no_permitido);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
            }
            if (id == R.id.accion_privacidad) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a privacidad
                String saludo = getString(R.string.no_permitido);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
            }
            if (id == R.id.accion_vehiculos) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a vehículos
                String saludo = getString(R.string.no_permitido);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
            }
            if (id == R.id.accion_correo) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a correo
                String saludo = getString(R.string.no_permitido);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
            }
            if (id == R.id.accion_trafico) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Mapas
                String saludo = getString(R.string.no_permitido);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (id == R.id.accion_usuario) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Información de Usuario
                String saludo = getString(R.string.informacion_usuario);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Información de Usuario
                Intent intent = new Intent(PrincipalActivity.this, AccountActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
            if (id == R.id.accion_escaner) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a escaner
                String saludo = getString(R.string.informacion_obd);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de OBD-II
                Intent intent = new Intent(PrincipalActivity.this, ObdActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
            if (id == R.id.accion_alertas) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a alertas
                String saludo = getString(R.string.configuracion_alertas);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Configuración
                Intent intent = new Intent(PrincipalActivity.this, ConfiguracionActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
            if (id == R.id.accion_privacidad) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a privacidad
                String saludo = getString(R.string.politicas_privacidad);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Privacidad
                Intent intent = new Intent(PrincipalActivity.this, PrivacyActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
            if (id == R.id.accion_vehiculos) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a vehículos
                String saludo = getString(R.string.vehiculos);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Vehículos
                Intent intent = new Intent(PrincipalActivity.this, VehiculosActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
            if (id == R.id.accion_correo) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a correo
                String saludo = getString(R.string.ayuda_comentarios);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Comentarios
                Intent intent = new Intent(PrincipalActivity.this, ComentariosActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
            if (id == R.id.accion_trafico) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Mapas
                String saludo = getString(R.string.informacion_trafico);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Mapas
                Intent intent = new Intent(PrincipalActivity.this, MapasActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        vinculante = ActivityPrincipalBinding.inflate(getLayoutInflater());
        //Presentación de los elementos visuales
        setContentView(vinculante.getRoot());

        // Verifica si la barra de acción está disponible y luego establece el icono
        if (getSupportActionBar() != null) {
            getSupportActionBar().setIcon(R.mipmap.ic_menu_header);
            // Otras opciones disponibles para la barra de acción
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        //Inicializando las variables con los elementos de la actividad
        FloatingActionButton informacionUsuario = vinculante.botonInformacionUsuario;
        FloatingActionButton riesgoFlotante = vinculante.botonInformacionRiesgo;
        FloatingActionButton accuweatherFlotante = vinculante.botonAccuWeather;
        FloatingActionButton correoFlotante = vinculante.botonCorreoPrincipal;
        FloatingActionButton mapasFlotante = vinculante.botonMapasPrincipal;
        FloatingActionButton obdFlotante = vinculante.botonOBD;

        //Bloquear elementos no usados
        accuweatherFlotante.setVisibility(View.INVISIBLE);
        informacionUsuario.setVisibility(View.INVISIBLE);
        correoFlotante.setVisibility(View.INVISIBLE);
        riesgoFlotante.setVisibility(View.INVISIBLE);
        mapasFlotante.setVisibility(View.INVISIBLE);
        obdFlotante.setVisibility(View.INVISIBLE);

        //Elementos de las alerta visuales
        resultadoTexto = vinculante.txtMedidaDatosRiesgo;
        actual = vinculante.txtUltimaUpdateFechaRiesgo;
        grupoActual = vinculante.grupoFechaRiesgo;
        grupoNivel = vinculante.grupoNivelRiesgo;

        //Instanciando con la clase de notificación por voz
        conversor = new NotificacionesVoz();
        conversor.init(this);

        //Estableciendo la fecha para mostrar al usuario
        actual.setText("Esperando…");

        //Llamando a la clase que Número de Pestañas que le informa a la actividad principal el número de fragmentos con los que se trabaja
        NumeroPestanas crearPaginas = new NumeroPestanas(this, getSupportFragmentManager());
        ViewPager viewPager = vinculante.visorMultiPagina;
        //Vinculando los fragmentos de actividades al fragmento principal
        viewPager.setAdapter(crearPaginas);
        // Segmento de código que permite ver las pestañas tipo tab
        TabLayout paginas = vinculante.botonesPestanas;
        paginas.setupWithViewPager(viewPager);
        //paginas.setVisibility(View.INVISIBLE);

        //Creando el evento click en un botón tipo flotante inicializado
        informacionUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Información de Usuario
                String saludo = getString(R.string.informacion_usuario);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Información de Usuario
                Intent intent = new Intent(PrincipalActivity.this, AccountActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
        });

        riesgoFlotante.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Información de Usuario
                String saludo = getString(R.string.riesgo);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Información de Usuario
                Intent intent = new Intent(PrincipalActivity.this, RiesgoActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón tipo flotante inicializado
        accuweatherFlotante.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Información de Usuario
                String saludo = getString(R.string.informacion_clima);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Información de Usuario
                Intent intent = new Intent(PrincipalActivity.this, AccuWeatherActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón tipo flotante inicializado
        correoFlotante.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Comentarios
                String saludo = "Envianos tus Comentarios";
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Comentarios
                Intent intent = new Intent(PrincipalActivity.this, ComentariosActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón tipo flotante inicializado
        mapasFlotante.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Mapas
                String saludo = getString(R.string.informacion_trafico);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de Mapas
                Intent intent = new Intent(PrincipalActivity.this, MapasActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
        });

        //Creando el evento click en un botón tipo flotante inicializado
        obdFlotante.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Mensaje emergente que informa al usuario que se va a cambiar de actividad a OBD
                String saludo = getString(R.string.informacion_obd);
                Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
                //Redirigiendo al usuario a la actividad de OBD
                Intent intent = new Intent(PrincipalActivity.this, ObdActivity.class);
                //Iniciando la actividad
                startActivity(intent);
            }
        });

        //Carga la configuración automática del OBD
        cargarConfiguracionOBD();

        //Creación de un tercer hilo que permite presentar las alertas gráficas y sonoras de los datos en tiempo real
        Handler handler_alertas = new Handler();
        handler_alertas.postDelayed(new Runnable() {
            public void run() {
                //Llamando al método que permite presentar las alertas según su nivel: Muy Alto, Alto, Media y Baja
                presentarAlertas();
                //Segmento de código que permite actualizar la información a presentar cada 10 segundos
                handler_alertas.postDelayed(this, 5000);
            }
        }, 5000);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Esto agrega elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        
        // Debug: Verificar que el menú de vehículos está configurado correctamente
        MenuItem vehiculosItem = menu.findItem(R.id.accion_vehiculos);
        if (vehiculosItem != null) {
            Log.d("PrincipalActivity", "Menú vehículos encontrado. Título: " + vehiculosItem.getTitle());
            Log.d("PrincipalActivity", "Es visible: " + vehiculosItem.isVisible());
            Log.d("PrincipalActivity", "Es habilitado: " + vehiculosItem.isEnabled());
        } else {
            Log.e("PrincipalActivity", "Menú vehículos NO encontrado");
        }
        
        return true;
    }

    private void mostrarAmarillo(String texto) {
        //Mostrar amarillo
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Media
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = new View(this);
        view = getLayoutInflater().inflate(R.layout.mensaje_amarillo, view.findViewById(R.id.contenedorAmarillo));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloAmarillo)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoAmarillo)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okAmarillo)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okAmarillo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void mostrarNaranja(String texto) {
        //Mostrar naranja
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Alta
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = new View(this);
        view = getLayoutInflater().inflate(R.layout.mensaje_naranja, view.findViewById(R.id.contenedorNaranja));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloNaranja)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoNaranja)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okNaranja)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okNaranja).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void mostrarVerde(String texto) {
        //Mostrar verde
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Bajo
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = new View(this);
        view = getLayoutInflater().inflate(R.layout.mensaje_verde, view.findViewById(R.id.contenedorVerde));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloVerde)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoVerde)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okVerde)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okVerde).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void mostrarRojo(String texto) {
        //Mostrar rojo
        //Método que permiten presentar un cuadro de diálogo personalizado
        //El cuadro de diálogo muestra información referente al estado de alerta Muy ALta
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = new View(this);
        view = getLayoutInflater().inflate(R.layout.mensaje_rojo, view.findViewById(R.id.contenedorRojo));
        builder.setView(view);
        //Estableciendo el mensaje estandar a mostrar en el cuadro de diálogo
        ((TextView) view.findViewById(R.id.tituloRojo)).setText(getResources().getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoRojo)).setText(texto);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        //Llamando al cuadro de diálogo personalizado
        final android.app.AlertDialog mensajeMostrado = builder.create();
        View finalView = view;
        mensajeMostrado.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                //Estableciendo un contador de 5 segundo en el cuadro de diálogo con intervalo de 1 segundo
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okRojo)).setText(MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")"));
                    }

                    public void onFinish() {
                        //Descartando mensaje de alerta
                        mensajeMostrado.dismiss();
                    }
                }.start();
            }
        });
        view.findViewById(R.id.okRojo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Descartando mensaje de alerta
                mensajeMostrado.dismiss();
            }
        });
        if (mensajeMostrado.getWindow() != null) {
            //Dando transparencia al mensaje de alerta
            mensajeMostrado.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        //Presentando mensaje al usuario
        mensajeMostrado.show();
    }

    private void cargarConfiguracionOBD() {
        //Verificación o Creación del archivo local de configuraciones de Usuario
        SharedPreferences dispositivoGuardado = getSharedPreferences("PreferenciasOBD", Context.MODE_PRIVATE);
        //Obteniendo los datos
        boolean obdGuardado = dispositivoGuardado.getBoolean("Dispositivo OBD Guardado", false);
        String address = dispositivoGuardado.getString("Address", "No Address");
        String nombre = dispositivoGuardado.getString("Nombre", "No Name");
        //Verificación Alerta Activada o Desactivada
        if (String.valueOf(obdGuardado).equals("true")) {
            if (address.equals("No Address") || nombre.equals("No Name")) {
                Toast.makeText(getApplicationContext(), "No existe un dispositivo OBD guardado para conexión automática.", Toast.LENGTH_SHORT).show();
            } else {
                autoConectarOBD();
            }
        }
    }

    public void leerRouteOneDynamoDB() {
        //Segmento de código que permite leer el Dataset con los datos generados por el vehículo
        new Thread(new Runnable() {
            //Para la lectura se genera un hilo secundario, con el fin de estar leyendo el data set continuamente
            public void run() {
                //Se llama al constructor para las variables obtenidas
                FragmentoModeloVista cambio = new FragmentoModeloVista();
                try {
                    // Llamando a una nueva instancia de la base de datos
                    DataBaseAccess consulta = new DataBaseAccess();
                    // Método para obtener la lista de atributos desde la base de datos de la tabla Route 1
                    String obtenerPoint = (String) consulta.obtenerPoint();
                    // Mostrando mensaje de consulta exitosa en consola
                    Log.i("Consulta Exitosa Point: ", obtenerPoint);
                    // Llamando al método replaceAll() y al método split() en cadena
                    // Eliminando los corchetes de la cadena de consulta
                    String[] cadena = obtenerPoint.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().replaceAll(" ", "").trim().split(",");
                    Arrays.sort(cadena);
                    // Declarando el inicio del bucle while
                    int i = 0;
                    // Inicializando el bucle while
                    while (i < cadena.length) {
                        try {
                            // Método para obtener la lista de atributos desde la base de datos de los Datos del Vehiculo
                            String datosDataSetRouteOneDynamoDB = (String) consulta.datosRouteOneDynamoDB(cadena[i]);
                            // Se separa los atributos obtenidos mediante un split
                            String[] token = datosDataSetRouteOneDynamoDB.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().split(",");
                            //Mensaje de consola que presenta la información obtenida
                            Log.d("DATOS ROUTE 1_leerRouteOneDynamoDB", Arrays.toString(token));
                            // Retornando al hilo principal
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //Enviando la información a presentar en pantalla con los datos del Dataset
                                    cambio.datosRouteOne(token[5].replaceAll(" ", "").trim(), token[1].replaceAll(" ", "").trim(), token[4].replaceAll(" ", "").trim(), token[3].replaceAll(" ", "").trim(), token[0].replaceAll(" ", "").trim(), token[2].replaceAll(" ", "").trim());
                                }
                            });
                            //Se establece un tiempo entre la lectura de cada línea
                            Thread.sleep(INTERVALO_ENTRE_PETICIONES); // Usando variable específica para intervalo entre peticiones
                        } catch (Exception e) {
                            // Mensaje de error al no poder conectarse a la base de datos
                            Log.e("Error de conexión: ", e.getMessage());
                        }
                        i++;
                    }
                } catch (Exception e) {
                    //Mensaje de consola que informa al usuario que n se pudo leer el Dataset
                    Log.e("ERROR DATASET: ", e.toString());
                }
            }
        }).start();
    }

    public void leerDataSetDynamoDB() {
        //Segmento de código que permite leer el Dataset con los datos generados por el vehículo
        new Thread(new Runnable() {
            //Para la lectura se genera un hilo secundario, con el fin de estar leyendo el data set continuamente
            public void run() {
                //Se llama al constructor para las variables obtenidas
                FragmentoModeloVista cambio = new FragmentoModeloVista();
                try {
                    // Llamando a una nueva instancia de la base de datos
                    DataBaseAccess consulta = new DataBaseAccess();
                    // Método para obtener la lista de atributos desde la base de datos mediate la clave del UserName
                    String obtenerId = (String) consulta.obtenerId();
                    // Mostrando mensaje de consulta exitosa en consola
                    Log.i("Consulta Exitosa Id: ", obtenerId);
                    // Llamando al método replaceAll() y al método split() en cadena
                    // Eliminando los corchetes de la cadena de consulta
                    String[] cadena = obtenerId.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().split(",");
                    // Declarando el inicio del bucle while
                    int i = 0;
                    // Inicializando el bucle while
                    while (i < cadena.length) {
                        try {
                            // Método para obtener la lista de atributos desde la base de datos de los Datos del Vehiculo
                            String datosDataSetDynamoDB = (String) consulta.datosDataSetDynamoDB(i);
                            // Se separa los atributos obtenidos mediante un split
                            String[] token = datosDataSetDynamoDB.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().split(",");
                            //Mensaje de consola que presenta la información obtenida
                            Log.d("DATASET_LINEA_leerDataSetDynamoDB", Arrays.toString(token));
                            // Retornando al hilo principal
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //Enviando la información al servicio REST de AWS
                                    Agente agenteRespuesta = new Agente(getApplicationContext(), URL_API_PREDICTOR, String.valueOf(Double.parseDouble(token[39].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[3].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[41].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[5].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[6].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[7].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[8].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[17].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[10].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[13].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[14].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[20].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[38].replaceAll(" ", "").trim())));
                                    //Ejecutando el servicio de AWS
                                    agenteRespuesta.execute();
                                    //Enviando la información a presentar en pantalla with los datos del Dataset
                                    cambio.datosDataSet(String.valueOf(Double.parseDouble(token[10].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[6].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[38].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[45].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[37].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[39].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[26].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[3].replaceAll(" ", "").trim())), "La: " + Double.parseDouble(token[13].replaceAll(" ", "").trim()) + "\n" + "Lo: " + Double.parseDouble(token[14].replaceAll(" ", "").trim()), String.valueOf(Integer.parseInt(token[38].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[43].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[29].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[8].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[3].replaceAll(" ", "").trim())), String.valueOf(Integer.parseInt(token[30].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[28].replaceAll(" ", "").trim())), token[21].replaceAll(" ", "").trim(), String.valueOf(Integer.parseInt(token[7].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[17].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[41].replaceAll(" ", "").trim())));
                                }
                            });
                            //Se establece un tiempo entre la lectura de cada línea
                            Thread.sleep(INTERVALO_ENTRE_PETICIONES); // Usando variable específica para intervalo entre peticiones - LÍNEA PRINCIPAL PARA LAS PREDICCIONES ML
                        } catch (Exception e) {
                            // Mensaje de error al no poder conectarse a la base de datos
                            Log.e("Error de conexión: ", e.getMessage());
                        }
                        i++;
                    }
                } catch (Exception e) {
                    //Mensaje de consola que informa al usuario que n se pudo leer el Dataset
                    Log.e("ERROR DATASET: ", e.toString());
                }
            }
        }).start();
    }

    public void finalizarLecturaOBD() {
        try {
            if (myTimer != null) {
                myTimer.cancel();
                myTimer = null;
            }
            if (btSocket != null && btSocket.isConnected()) {
                btSocket.close();
            }
        } catch (Exception e) {
            Log.e("PrincipalActivity", "Error finalizando OBD: " + e.getMessage());
        }
    }

    public void iniciarLecturaOBD() {
        // Método que inicia la lectura desde la interfaz OBD-II.
        long frecuencia = 5000;
        myTimer = new Timer();
        // myTimer.scheduleAtFixedRate(new TimerTask() {
        myTimer.schedule(new TimerTask() {
            public void run() {
                // Mensaje en consola.
                Log.i("Lectura realizada", "Fecha de la lectura: " + new Date());
                try {
                    // Lectura de datos de 'Velocidad'.
                    command_Speed.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command_Load.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command_RPM.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command_ThrottlePosition.run(btSocket.getInputStream(), btSocket.getOutputStream());
                    command_EngineCoolantTemperature.run(btSocket.getInputStream(), btSocket.getOutputStream());
                } catch (RuntimeException runtimeException) {
                    Toast.makeText(getApplicationContext(), "RuntimeException: Could not initialize Timer" + runtimeException.getMessage(), Toast.LENGTH_LONG).show();
                } catch (IOException ioException) {
                    Toast.makeText(getApplicationContext(), "IOException: Could not initialize Timer" + ioException.getMessage(), Toast.LENGTH_LONG).show();
                } catch (InterruptedException interruptedException) {
                    Toast.makeText(getApplicationContext(), "InterruptedException: Could not initialize Timer" + interruptedException.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, 0, frecuencia);
    }

    private void presentarAlertas() {
        try {
            //Ocultando parámetro de información no usado
            //porcentaje.setVisibility(View.INVISIBLE);
            //resultadoBandera.setVisibility(View.INVISIBLE);
            //Segmento de código que permite observar en tiempo real los cambios que se dan en la información
            FragmentoModeloVista.getRespuestaAgente().observe(this, new Observer<String>() {
                public void onChanged(@Nullable String s) {
                    //Verificando que los parametros recibidos no sean vacíos
                    assert s != null;
                    //Verificación o Creación del archivo local de configuraciones de Usuario
                    SharedPreferences configuracionUsuario = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                    //Obteniendo los datos a presentar
                    boolean alertaMuyAlta = configuracionUsuario.getBoolean("Alerta Muy Alta", false);
                    boolean alertaMedia = configuracionUsuario.getBoolean("Alerta Media", false);
                    boolean alertaAlta = configuracionUsuario.getBoolean("Alerta Alta", false);
                    boolean alertaBaja = configuracionUsuario.getBoolean("Alerta Baja", false);
                    //Cambiando la información visual de los elementos
                    get_set_MuyAlta = alertaMuyAlta;
                    get_set_Media = alertaMedia;
                    get_set_Alta = alertaAlta;
                    get_set_Baja = alertaBaja;
                    //Comparando el valor recibido con el nivel 4
                    if (s.equals("{\"Output\": 4}") && get_set_MuyAlta) {
                        //Presentando las alertas gráficas de nivel 4 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        resultadoTexto.setText(getString(R.string.medida_datos_muy_alta));
                        //Iniciando alerta sonora de nivel 4
                        String mensaje = "Nivel de Riesgo Muy Alta";
                        conversor.palabra(mensaje);
                        mostrarRojo(mensaje);
                        presentarFecha();
                    }
                    if (s.equals("{\"Output\": 4}") && !get_set_MuyAlta) {
                        //Presentando las alertas gráficas de nivel 4 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_rojo));
                        resultadoTexto.setText(getString(R.string.medida_datos_muy_alta));
                        presentarFecha();
                    }
                    //Comparando el valor recibido con el nivel 3
                    if (s.equals("{\"Output\": 3}") && get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 3 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        resultadoTexto.setText(getString(R.string.medida_datos_alta));
                        //Iniciando alerta sonora de nivel 3
                        String mensaje = "Nivel de Riesgo Alta";
                        conversor.palabra(mensaje);
                        mostrarNaranja(mensaje);
                        presentarFecha();
                    }
                    if (s.equals("{\"Output\": 3}") && !get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 3 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_naranja));
                        resultadoTexto.setText(getString(R.string.medida_datos_alta));
                        presentarFecha();
                    }
                    //Comparando el valor recibido con el nivel 2
                    if (s.equals("{\"Output\": 2}") && get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 2 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        resultadoTexto.setText(getString(R.string.medida_datos_media));
                        //Iniciando alerta sonora de nivel 2
                        String mensaje = "Nivel de Riesgo Media";
                        conversor.palabra(mensaje);
                        mostrarAmarillo(mensaje);
                        presentarFecha();
                    }
                    if (s.equals("{\"Output\": 2}") && !get_set_Alta) {
                        //Presentando las alertas gráficas de nivel 2 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_amarillo));
                        resultadoTexto.setText(getString(R.string.medida_datos_media));
                        presentarFecha();
                    }
                    //Comparando el valor recibido con el nivel 1
                    if (s.equals("{\"Output\": 1}") && get_set_Baja) {
                        //Presentando las alertas gráficas de nivel 1 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        resultadoTexto.setText(getString(R.string.medida_datos_baja));
                        //Iniciando alerta sonora de nivel 1
                        String mensaje = "Nivel de Riesgo Baja";
                        conversor.palabra(mensaje);
                        mostrarVerde(mensaje);
                        presentarFecha();
                    }
                    if (s.equals("{\"Output\": 1}") && !get_set_Baja) {
                        //Presentando las alertas gráficas de nivel 1 al usuario
                        grupoActual.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        grupoNivel.setBackgroundColor(getResources().getColor(R.color.color_verde));
                        resultadoTexto.setText(getString(R.string.medida_datos_baja));
                        presentarFecha();

                    }
                }
            });
        } catch (Exception e) {
            Log.e("Error Alertas", e.toString());
        }
    }

    public void autoConectarOBD() {
        // Método para conectar el dispositivo Bluetooth seleccionado.
        try {
            //Verificación o Creación del archivo local de configuraciones de Usuario
            SharedPreferences dispositivoGuardado = getSharedPreferences("PreferenciasOBD", Context.MODE_PRIVATE);
            //Obteniendo los datos
            boolean obdGuardado = dispositivoGuardado.getBoolean("Dispositivo OBD Guardado", false);
            String address = dispositivoGuardado.getString("Address", "No Address");
            String nombre = dispositivoGuardado.getString("Nombre", "No Name");
            //Iniciando la conexión Bluetooth
            chosenDeviceAddress = address;
            chosenDeviceName = nombre;
            BluetoothDevice device = btAdapter.getRemoteDevice(chosenDeviceAddress);
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                btSocket = device.createRfcommSocketToServiceRecord(uuid);
                btSocket.connect();
                new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
                new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());
                Toast.makeText(PrincipalActivity.this, "Conectado al dispositivo OBD.", Toast.LENGTH_SHORT).show();
                iniciarLecturaOBD();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(PrincipalActivity.this, "No existe un dispositivo guardado para conexión automática.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(PrincipalActivity.this, "No se puede establecer conexión.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(PrincipalActivity.this, "Se produjo un error en la conexión OBD." + e, Toast.LENGTH_LONG).show();
        }
    }

    private void presentarFecha() {
        //Estableciendo la fecha para mostrar al usuario
        Calendar fecha = Calendar.getInstance();
        int day = fecha.get(Calendar.DAY_OF_MONTH);
        // Se suma 1 porque Calendar.MONTH devuelve un valor basado en cero
        int month = fecha.get(Calendar.MONTH) + 1;
        int year = fecha.get(Calendar.YEAR);
        int hour = fecha.get(Calendar.HOUR_OF_DAY);
        int minute = fecha.get(Calendar.MINUTE);
        int seconds = fecha.get(Calendar.SECOND);
        String amPm = (fecha.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        actual.setText(String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + String.format("%02d", year) + "  " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds) + " " + amPm);
    }

    public void onBackPressed() {
        //Cuadro de diálogo que se presenta al usuario al momento de presionar el botón atrás en el celular
        AlertDialog.Builder constructor = new AlertDialog.Builder(this);
        //Mensaje pregunta al usuario si desea salir de la aplicación
        constructor.setTitle("ALERTA").setMessage("¿Desea salir de la aplicación?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Si el usuario acepta, la aplicación cerrar y presentará la pantalla principal del celular
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finalizarLecturaOBD();
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Descarta el cuadro de diálogo
                dialog.dismiss();
            }
        }).create().show();
    }

    protected void onDestroy() {
        //Finalizando completamente la actividad Principal
        try {
            // Finalizar lectura OBD de manera segura
            finalizarLecturaOBD();
            
            // Apagar conversor de voz
            if (conversor != null) {
                conversor.apagar();
                conversor = null;
            }
        } catch (Exception e) {
            Log.e("PrincipalActivity", "Error en onDestroy: " + e.getMessage());
        } finally {
            super.onDestroy();
        }
    }

    public void leerDataSet() {
        //Segmento de código que permite leer el Dataset con los datos generados por el vehículo
        new Thread(new Runnable() {
            //Para la lectura se genera un hilo secundario, con el fin de estar leyendo el data set continuamente
            public void run() {
                //Se obtiene los recursos desde la carpeta RAW, donde previamente se cargo el Dataset
                InputStream data = getResources().openRawResource(R.raw.agente);
                //Esta clase lee texto desde un flujo de entrada de caracteres, almacenando los caracteres para proporcionar una lectura eficiente de caracteres, arreglos y líneas.
                BufferedReader lector = new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8));
                //Se llama al constructor para las variables obtenidas
                FragmentoModeloVista cambio = new FragmentoModeloVista();
                String linea = "";
                try {
                     //Se va leyendo el Dataset línea por línea
                    lector.readLine();
                    while ((linea = lector.readLine()) != null) {
                        //Se establece un tiempo entre la lectura de cada línea
                        Thread.sleep(INTERVALO_ENTRE_PETICIONES); // Usando variable específica para intervalo entre peticiones
                        //Se establece un carácter que divide la cadena para extraer lo necesitado
                        String[] token = linea.split(",");
                        //Se llama al constructor del Dataset para presentar la información en el formato adecuado
                        DataSet obtenerDatos = new DataSet();
                        //Se envia los datos solicitados por la aplicación
                        obtenerDatos.setDistance_travelled(Double.parseDouble(token[10]));
                        obtenerDatos.setThrottle_position(Double.parseDouble(token[6]));
                        obtenerDatos.setEngine_temperature(Integer.parseInt(token[7]));
                        obtenerDatos.setSteering_angle_(Double.parseDouble(token[39]));
                        obtenerDatos.setAccidents_onsite(Integer.parseInt(token[38]));
                        obtenerDatos.setCurrent_weather(Integer.parseInt(token[20]));
                        obtenerDatos.setSystem_voltage(Double.parseDouble(token[8]));
                        obtenerDatos.setAcceleration(Double.parseDouble(token[5]));
                        obtenerDatos.setLongitude(Double.parseDouble(token[14]));
                        obtenerDatos.setLatitude(Double.parseDouble(token[13]));
                        obtenerDatos.setHeart_rate(Integer.parseInt(token[17]));
                        obtenerDatos.setRpm_(Double.parseDouble(token[41]));
                        obtenerDatos.setSpeed(Integer.parseInt(token[3]));

                        /*obtenerDatos.setDistance_travelled_total(Double.parseDouble(token[11]));
                        obtenerDatos.setSpeed_vs_accidents_onsite(Integer.parseInt(token[43]));
                        obtenerDatos.setSpeed_vs_steering_angle_(Integer.parseInt(token[40]));
                        obtenerDatos.setReal_feel_temperature(Double.parseDouble(token[27]));
                        obtenerDatos.setSpeed_vs_precipitation(Integer.parseInt(token[44]));
                        obtenerDatos.setBarometric_pressure(Double.parseDouble(token[9]));
                        obtenerDatos.setHas_precipitation(Integer.parseInt(token[23]));
                        obtenerDatos.setRelative_humidity(Integer.parseInt(token[30]));
                        obtenerDatos.setSteering_angle(Double.parseDouble(token[2]));
                        obtenerDatos.setPrecipitation(Double.parseDouble(token[37]));
                        obtenerDatos.setWind_direction(Integer.parseInt(token[29]));
                        obtenerDatos.setAccident_rate(Integer.parseInt(token[45]));
                        obtenerDatos.setSpeed_vs_rpm_(Integer.parseInt(token[42]));
                        obtenerDatos.setTemperature(Double.parseDouble(token[26]));
                        obtenerDatos.setWind_speed(Double.parseDouble(token[28]));
                        obtenerDatos.setBody_battery(Integer.parseInt(token[19]));
                        obtenerDatos.setVisibility(Double.parseDouble(token[31]));
                        obtenerDatos.setCloud_cover(Integer.parseInt(token[34]));
                        obtenerDatos.setIs_day_time(Integer.parseInt(token[25]));
                        obtenerDatos.setId_vehicle(Integer.parseInt(token[12]));
                        obtenerDatos.setPressure(Double.parseDouble(token[36]));
                        obtenerDatos.setId_driver(Integer.parseInt(token[16]));
                        obtenerDatos.setHas_precipitation_category(token[22]);
                        obtenerDatos.setUv_index(Integer.parseInt(token[32]));
                        obtenerDatos.setAltitude(Integer.parseInt(token[15]));
                        obtenerDatos.setCeiling(Integer.parseInt(token[35]));
                        obtenerDatos.setCurrent_weather_category(token[21]);
                        obtenerDatos.setStress(Integer.parseInt(token[18]));
                        obtenerDatos.setIndice(Integer.parseInt(token[0]));
                        obtenerDatos.setIs_day_time_category(token[24]);
                        obtenerDatos.setRpm(Integer.parseInt(token[4]));
                        obtenerDatos.setUv_index_text(token[33]);
                        obtenerDatos.setTime(token[1]);*/

                        //Mensaje de consola que presenta la información obtenida
                        Log.d("DATASET_LINEA", String.valueOf(obtenerDatos));
                        // Retornando al hilo principal
                            // Retornando al hilo principal
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //Enviando la información al servicio REST de AWS
                                Agente agenteRespuesta = new Agente(getApplicationContext(), "https://wsouotfzr3.execute-api.us-east-1.amazonaws.com/FaseBeta/Recursos_Predictor_Accidentes", String.valueOf(Double.parseDouble(token[39])), String.valueOf(Integer.parseInt(token[3])), String.valueOf(Double.parseDouble(token[41])), String.valueOf(Double.parseDouble(token[5])), String.valueOf(Double.parseDouble(token[6])), String.valueOf(Integer.parseInt(token[7])), String.valueOf(Double.parseDouble(token[8])), String.valueOf(Integer.parseInt(token[17])), String.valueOf(Double.parseDouble(token[10])), String.valueOf(Double.parseDouble(token[13])), String.valueOf(Double.parseDouble(token[14])), String.valueOf(Integer.parseInt(token[20])), String.valueOf(Integer.parseInt(token[38])));
                                //Ejecutando el servicio de AWS
                                agenteRespuesta.execute();
                                //Enviando la información a presentar en pantalla con los datos del Dataset
                                cambio.datosDataSet(String.valueOf(Double.parseDouble(token[10].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[6].replaceAll(" ", "").trim())), String.valueOf(Double.parseDouble(token[38])), String.valueOf(Double.parseDouble(token[45])), String.valueOf(Double.parseDouble(token[37])), String.valueOf(Double.parseDouble(token[39])), String.valueOf(Double.parseDouble(token[26])), String.valueOf(Integer.parseInt(token[3])), "La: " + Double.parseDouble(token[13]) + "\n" + "Lo: " + Double.parseDouble(token[14]), String.valueOf(Integer.parseInt(token[38])), String.valueOf(Integer.parseInt(token[43])), String.valueOf(Integer.parseInt(token[29])), String.valueOf(Double.parseDouble(token[8])), String.valueOf(Integer.parseInt(token[3])), String.valueOf(Integer.parseInt(token[30])), String.valueOf(Double.parseDouble(token[28])), String.valueOf(token[21]), String.valueOf(Integer.parseInt(token[7])), String.valueOf(Integer.parseInt(token[17])), String.valueOf(Double.parseDouble(token[41])));
                                 }
                            });
                    }
                } catch (Exception e) {
                    //Mensaje de consola que informa al usuario que n se pudo leer el Dataset
                    Log.e("ERROR DATASET: ", linea + e);
                }
            }
        }).start();
    }

    public void onResume() {
        super.onResume();
        //Inicializando las variables con los elementos de la actividad
        FloatingActionButton informacionUsuario = vinculante.botonInformacionUsuario;
        FloatingActionButton riesgoFlotante = vinculante.botonInformacionRiesgo;
        FloatingActionButton accuweatherFlotante = vinculante.botonAccuWeather;
        FloatingActionButton correoFlotante = vinculante.botonCorreoPrincipal;
        FloatingActionButton mapasFlotante = vinculante.botonMapasPrincipal;
        FloatingActionButton obdFlotante = vinculante.botonOBD;
        // Código que permite obtener una serie de datos del usuario
        try {
            // Adquiriendo información del usuario
            nombreApellidoUsuario = findViewById(R.id.txtSingInNombre);
            Intent intent = getIntent();
            //Obteniendo el nombre del usuario que es enviado desde la actividad login al hacer logueo con éxito
            apellidoUsuario = intent.getStringExtra("ApellidoUsuario");
            nombreUsuario = intent.getStringExtra("NombreUsuario");
            tipoUsuario = intent.getStringExtra("Tipo");

            //Seteando el nombre en pantalla
            if (nombreUsuario.equals(getString(R.string.no_ingresado)) || apellidoUsuario.equals(getString(R.string.no_ingresado))) {
                nombreApellidoUsuario.setText(String.format("%s %s %s", getString(R.string.bienvenido), getString(R.string.a), getString(R.string.nombre_app)));
            } else {
                nombreApellidoUsuario.setText(String.format("%s %s %s", getString(R.string.bienvenido), nombreUsuario, apellidoUsuario));
            }

            // Bloqueando elementos dependiendo del tipo de usuario
            if (tipoUsuario.equals(getString(R.string.tipo_0))) {
                accuweatherFlotante.setEnabled(false);
                informacionUsuario.setEnabled(false);
                riesgoFlotante.setEnabled(false);
                correoFlotante.setEnabled(false);
                mapasFlotante.setEnabled(false);
                obdFlotante.setEnabled(false);
            } else {
                accuweatherFlotante.setEnabled(true);
                informacionUsuario.setEnabled(true);
                riesgoFlotante.setEnabled(true);
                correoFlotante.setEnabled(true);
                mapasFlotante.setEnabled(true);
                obdFlotante.setEnabled(true);
            }

            //Solicitando a los servicios de AWS el nombre de usuario si el usuario ya había iniciado sesión con anterioridad
            Amplify.Auth.getCurrentUser(result -> PrincipalActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //Mensaje de consola que permite verificar que el nombre de usuario sea correcto
                    Log.i("Información", result.toString());
                    //Seteando el nombre en pantalla
                    if (nombreUsuario.equals(getString(R.string.no_ingresado)) || apellidoUsuario.equals(getString(R.string.no_ingresado))) {
                        nombreApellidoUsuario.setText(String.format("%s %s %s", getString(R.string.bienvenido), getString(R.string.a), getString(R.string.nombre_app)));
                    } else {
                        nombreApellidoUsuario.setText(String.format("%s %s %s", getString(R.string.bienvenido), nombreUsuario, apellidoUsuario));
                    }
                }
            }), error -> PrincipalActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //Mensaje de consola que informa que no se pudo obtener el nombre de usuario
                    Log.e("Error", error.toString());
                }
            }));

            // Método para leer el Dataset
            leerRouteOneDynamoDB();
            leerDataSetDynamoDB();
            //leerDataSet();
        } catch (Exception e) {
            //TODO
        }
    }
}