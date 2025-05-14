//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.obd;

//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import androidx.activity.result.contract.ActivityResultContracts;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import com.github.pires.obd.commands.SpeedCommand;
import androidx.appcompat.app.AppCompatActivity;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.enums.ObdProtocols;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.amplifyframework.core.Amplify;
import androidx.core.app.ActivityCompat;
import android.annotation.SuppressLint;
import androidx.annotation.RequiresApi;
import java.text.SimpleDateFormat;
import com.polidriving.mobile.R;
import android.content.Context;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Switch;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.TimerTask;
import java.io.IOException;
import java.io.FileWriter;
import android.os.Bundle;
import android.view.View;
import android.os.Build;
import android.util.Log;
import android.Manifest;
import java.util.Timer;
import java.util.Date;
import java.util.UUID;
import java.util.Set;
import java.io.File;

/**
 * @noinspection DataFlowIssue
 */
@SuppressWarnings({"unchecked", "UnnecessaryToStringCall", "deprecation"})
@SuppressLint({"SimpleDateFormat", "ObsoleteSdkInt", "UseSwitchCompatOrMaterialCode"})
@RequiresApi(api = Build.VERSION_CODES.ECLAIR)
public class ObdActivity extends AppCompatActivity {
    //Región Instanciación Commands
    private ObdCommand command_EngineCoolantTemperature = new EngineCoolantTemperatureCommand();
    private ObdCommand command_ThrottlePosition = new ThrottlePositionCommand();
    private ObdCommand command_Speed = new SpeedCommand();
    private ObdCommand command_Load = new LoadCommand();
    private ObdCommand command_RPM = new RPMCommand();

    //Región Declaraciones Results
    private TextView command_LoadResult, command_RPMResult, command_ThrottlePositionResult;
    private TextView command_EngineCoolantTemperatureResult;
    private TextView command_SpeedResult;

    //Región Declaraciones Labels
    private TextView command_LoadLabel, command_RPMLabel, command_ThrottlePositionLabel;
    private TextView command_EngineCoolantTemperatureLabel;
    private TextView command_SpeedLabel;

    // Declaraciones de Bluetooth
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private String chosenDeviceName, chosenDeviceAddress;
    private BluetoothSocket btSocket;

    // Se construye el nombre del archivo con el formato "yyyyMMdd_HHmmss".
    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Date date = new Date();
    String nombreArchivo = fmt.format(date) + ".csv";

    // Se definen variables para File y FileWriter.
    private FileWriter fileWriter;
    private File file;

    //Región Declaraciones Buttons
    private Button bChooseDevice;
    private Button bExitApp;
    private Button bConnect;
    private Button bStart;
    private Button bStop;

    // Variable de tiempo
    private Timer myTimer;

    // Switch Guardar
    Button guardarConfiguracionesOBD;
    Boolean get_set_Guardar = false;
    Switch guardar;

    ActivityResultLauncher<Intent> getResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        // Método para registrar resultados de actividades.
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                assert intent != null;
                String data = intent.getStringExtra("requestCode");
                if (data.equals("ENABLE_BT_REQUEST")) {
                    continueBluetooth();
                }
            }
            if (result.getResultCode() == RESULT_CANCELED) {
                Intent intent = result.getData();
                assert intent != null;
                String data = intent.getStringExtra("requestCode");
                if (data.equals("ENABLE_BT_REQUEST")) {
                    Toast.makeText(ObdActivity.this, "M1 Application requires Bluetooth enabled", Toast.LENGTH_LONG).show();
                }
            }
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent != null) {
                    ArrayList<String> newParametersNames = (ArrayList<String>) intent.getSerializableExtra("parameters");
                    chosenParameters.clear();
                    //Región Options
                    for (String paramName : newParametersNames) {
                        switch (paramName) {
                            //Región Parameters
                            case "Vehicle Speed":
                                chosenParameters.add(new SpeedCommand());
                                break;
                            case "Engine Load":
                                chosenParameters.add(new LoadCommand());
                                break;
                            case "Engine RPM":
                                chosenParameters.add(new RPMCommand());
                                break;
                            case "Throttle Position":
                                chosenParameters.add(new ThrottlePositionCommand());
                                break;
                            case "Engine Coolant Temperature":
                                chosenParameters.add(new EngineCoolantTemperatureCommand());
                                break;
                        }
                    }
                    try {
                        command_SpeedLabel.setText(String.format("%s:", chosenParameters.get(1).getName()));
                        command_Speed = chosenParameters.get(1);
                    } catch (IndexOutOfBoundsException ex) {
                        command_SpeedLabel.setText("");
                        command_Speed = null;
                    }
                    try {
                        command_LoadLabel.setText(String.format("%s:", chosenParameters.get(2).getName()));
                        command_Load = chosenParameters.get(2);
                    } catch (IndexOutOfBoundsException ex) {
                        command_LoadLabel.setText("");
                        command_Load = null;
                    }
                    try {
                        command_RPMLabel.setText(String.format("%s:", chosenParameters.get(3).getName()));
                        command_RPM = chosenParameters.get(3);
                    } catch (IndexOutOfBoundsException ex) {
                        command_RPMLabel.setText("");
                        command_RPM = null;
                    }
                    try {
                        command_ThrottlePositionLabel.setText(String.format("%s:", chosenParameters.get(4).getName()));
                        command_ThrottlePosition = chosenParameters.get(4);
                    } catch (IndexOutOfBoundsException ex) {
                        command_ThrottlePositionLabel.setText("");
                        command_ThrottlePosition = null;
                    }
                    try {
                        command_EngineCoolantTemperatureLabel.setText(String.format("%s:", chosenParameters.get(5).getName()));
                        command_EngineCoolantTemperature = chosenParameters.get(5);
                    } catch (IndexOutOfBoundsException ex) {
                        command_EngineCoolantTemperatureLabel.setText("");
                        command_EngineCoolantTemperature = null;
                    }
                } else {
                    Toast.makeText(ObdActivity.this, "M2 Preferred parameters not saved correctly", Toast.LENGTH_LONG).show();
                }
            }
        }
    });

    private void writeCsvData(float velocity, float engine_load, float rpm, float throttle_position, float engine_coolant_temperature, FileWriter fileWriter) {
        // Método para escribir filas en el archivo csv.
        try {
            @SuppressLint("DefaultLocale") String line = String.format("%.2f,%.2f,%.2f,%.2f,%.2f\n", velocity, engine_load, rpm, throttle_position, engine_coolant_temperature);
            fileWriter.write(line);
        } catch (IOException ioException) {
            Toast.makeText(getApplicationContext(), "IOException: Could not write Csv Data" + ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private final ArrayList<ObdCommand> chosenParameters = new ArrayList<ObdCommand>() {
        {
            add(command_Speed);
            add(command_Load);
            add(command_RPM);
            add(command_ThrottlePosition);
            add(command_EngineCoolantTemperature);
        }
    };

    private void uploadFile(String nombreArchivo, File file) {
        // Método para subir archivo a repositorio en AWS.
        try {
            Amplify.Storage.uploadFile(nombreArchivo, file, result -> Toast.makeText(getApplicationContext(), "Archivo subido a Amazon S3 con éxito", Toast.LENGTH_LONG).show(), storageFailure -> Log.i("MyAmplifyApp", "Upload failed", storageFailure));
        } catch (RuntimeException runtimeException) {
            Toast.makeText(getApplicationContext(), "RuntimeException: Could not upload file to AWS" + runtimeException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeCsvHeaderUnits(FileWriter fileWriter) {
        // Método para escribir las unidades de la fila de cabecera del archivo csv.
        try {
            // Lectura de las unidades para 'Velocidad'.
            command_Speed.run(btSocket.getInputStream(), btSocket.getOutputStream());
            String strSpeedUnit = command_Speed.getResultUnit();
            // Lectura de las unidades para 'Carga del motor'.
            command_Load.run(btSocket.getInputStream(), btSocket.getOutputStream());
            String strLoadUnit = command_Load.getResultUnit();
            // Lectura de las unidades para 'RPM'.
            command_RPM.run(btSocket.getInputStream(), btSocket.getOutputStream());
            String strRPMUnit = command_RPM.getResultUnit();
            // Lectura de las unidades para 'Posición del acelerador'.
            command_ThrottlePosition.run(btSocket.getInputStream(), btSocket.getOutputStream());
            String strThrottlePositionUnits = command_ThrottlePosition.getResultUnit();
            // Lectura de las unidades para 'Temperatura del refrigerante'.
            command_EngineCoolantTemperature.run(btSocket.getInputStream(), btSocket.getOutputStream());
            String strEngineCoolantTemperature = command_EngineCoolantTemperature.getResultUnit();
            String line = String.format("%s, %s, %s, %s, %s\n", strSpeedUnit, strLoadUnit, strRPMUnit, strThrottlePositionUnits, strEngineCoolantTemperature);
            fileWriter.write(line);
        } catch (IOException ioException) {
            Toast.makeText(getApplicationContext(), "IOException: Could not write Csv Header" + ioException.getMessage(), Toast.LENGTH_LONG).show();
        } catch (InterruptedException interruptedException) {
            Toast.makeText(getApplicationContext(), "InterruptedException: Could not write Csv Header" + interruptedException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        // Método principal de la actividad.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obd);

        //Región Asociaciones Command Labels
        command_SpeedLabel = findViewById(R.id.command_SpeedLabel);
        command_LoadLabel = findViewById(R.id.command_EngineLoadLabel);
        command_RPMLabel = findViewById(R.id.command_RPMLabel);
        command_ThrottlePositionLabel = findViewById(R.id.command_ThrottlePositionLabel);
        command_EngineCoolantTemperatureLabel = findViewById(R.id.command_EngineCoolantTemperatureLabel);

        //Región Asociaciones Command Results
        command_SpeedResult = findViewById(R.id.command_SpeedResult);
        command_LoadResult = findViewById(R.id.command_EngineLoadResult);
        command_RPMResult = findViewById(R.id.command_RPMResult);
        command_ThrottlePositionResult = findViewById(R.id.command_ThrottlePositionResult);
        command_EngineCoolantTemperatureResult = findViewById(R.id.command_EngineCoolantTemperatureResult);

        //Región Asociaciones Buttons
        bChooseDevice = findViewById(R.id.bChooseDevice);
        bConnect = findViewById(R.id.bConnect);
        bStart = findViewById(R.id.bStart);
        bStop = findViewById(R.id.bStop);
        bExitApp = findViewById(R.id.bExitApp);

        // Región Guardar
        guardarConfiguracionesOBD = findViewById(R.id.btnGuardarConfiguracion);
        guardar = findViewById(R.id.swConectar);
        cargarConfiguracion();

        //Creando el evento click en un botón inicializado
        guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Verificando si el elemento esta activado o desactivado
                if (guardar.isChecked()) {
                    //Cambiando la información visual del elemento
                    guardar.setText(getString(R.string.activada));
                    //Cambiando la información a almacenar
                    get_set_Guardar = true;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.activada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                } else {
                    //Cambiando la información visual del elemento
                    guardar.setText(getString(R.string.desactivada));
                    //Cambiando la información a almacenar
                    get_set_Guardar = false;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.desactivada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // /Creando el evento click del botón inicializado para guardar
        guardarConfiguracionesOBD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Llamando al método para guardar la configuración
                guardarConfiguracion();
                //Redirigiendo al usuario a la actividad de principal de cuenta
                onBackPressed();
            }
        });

        //Región Buttons Click Events
        try {
            bChooseDevice.setOnClickListener(e -> chooseBluetoothDevice());
        } catch (Exception e) {
            Toast.makeText(this, "No se encontró ningún dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
        }

        try {
            bConnect.setOnClickListener(e -> connectOBD());
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo establecer conexión con el  dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
        }

        bStart.setOnClickListener(e -> {
            // Se inician plugins de AWS.
            try {
                // Amplify.addPlugin(new AWSCognitoAuthPlugin());
                // Amplify.addPlugin(new AWSS3StoragePlugin());
                // Amplify.configure(getApplicationContext());
                // Se muestran mensajes de notificación de inicio de plugins AWS.
                // Log.i("MyAmplifyApp", "Initialized Amplify");
                // Toast.makeText(this, "Servicio AWS inicializado", Toast.LENGTH_SHORT).show();
                // Se inicia lectura de datos.
                Toast.makeText(this, "Conexión con AWS inicializada", Toast.LENGTH_SHORT).show();
                startOBD();
            }
            /*catch (AmplifyException amplifyException) {
                Log.e("MyAmplifyApp", "Could not initialize Amplify", amplifyException);
                Toast.makeText(getApplicationContext(), "AmplifyException: Could not initialize Amplify" + amplifyException.getMessage(), Toast.LENGTH_SHORT).show();
            } */ catch (RuntimeException runtimeException) {
                Toast.makeText(getApplicationContext(), "RuntimeException: Could not initialize Amplify" + runtimeException.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        bStop.setOnClickListener(e -> {
            try {
                stopOBD();
            } catch (RuntimeException runtimeException) {
                Toast.makeText(getApplicationContext(), "RuntimeException: Could not initialize Amplify" + runtimeException.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        bExitApp.setOnClickListener(e -> exitApp());
        //Región Button Properties
        bConnect.setEnabled(false);
        bStart.setEnabled(false);
        bStop.setEnabled(false);
    }

    private void writeCsvHeader(FileWriter fileWriter) {
        // Método para escribir la cabecera del archivo csv
        try {
            String line = String.format("%s, %s, %s, %s, %s\n", "Velocity", "Engine_Load", "RPM", "Throttle_Position", "Engine_Coolant_Temperature");
            fileWriter.write(line);
        } catch (IOException ioException) {
            Toast.makeText(getApplicationContext(), "IOException: Could not write Csv Header" + ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }

    private void chooseBluetoothDevice() {
        try {
            // Método para seleccionar dispositivo Bluetooth.
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter == null) {
                Toast.makeText(this, "M3 Device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
            }
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.putExtra("requestCode", "ENABLE_BT_REQUEST");
                getResult.launch(enableBtIntent);
            } else {
                continueBluetooth();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error Bluetooth" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Error Bluetooth: ", e.getMessage());
        }
    }

    private void guardarConfiguracion() {
        //Verificación o Creación del archivo local de configuraciones de Usuario
        SharedPreferences configuracionDispositivoOBD = getSharedPreferences("PreferenciasOBD", Context.MODE_PRIVATE);
        //Obteniendo los datos a almacenar
        Boolean guardarOBD = get_set_Guardar;
        String nombre = chosenDeviceAddress;
        String address = chosenDeviceName;
        //Creando un objeto tipo editor que permite modificar el archivo Preferencias creado
        SharedPreferences.Editor editar = configuracionDispositivoOBD.edit();
        editar.putBoolean("Dispositivo OBD Guardado", guardarOBD);
        editar.putString("Address", address);
        editar.putString("Nombre", nombre);
        editar.apply();
        //Mensaje emergente de información al usuario
        String informacion = getString(R.string.configuracion_guardar);
        Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
    }

    private void cargarConfiguracion() {
        //Verificación o Creación del archivo local de configuraciones de Usuario
        SharedPreferences dispositivoGuardado = getSharedPreferences("PreferenciasOBD", Context.MODE_PRIVATE);
        //Obteniendo los datos a presentar
        boolean obdGuardado = dispositivoGuardado.getBoolean("Dispositivo OBD Guardado", false);
        //Cambiando la información visual de los elementos
        guardar.setChecked(obdGuardado);
        get_set_Guardar = obdGuardado;
        // Toast.makeText(getApplicationContext(), String.valueOf(obdGuardado) + " " + address + " " + nombre, Toast.LENGTH_SHORT).show();
        //Verificación texto a presentar Alerta Activada o Desactivada
        if (obdGuardado) {
            guardar.setText(getString(R.string.activada));
        } else {
            guardar.setText(getString(R.string.desactivada));
        }
    }

    private void continueBluetooth() {
        // Método complementario en la selección del dispositivo Bluetooth.
        final ArrayList<String> pairedDevicesNames = new ArrayList<>();
        final ArrayList<String> pairedDevicesAddresses = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            // if (pairedDevices.size() > 0) {
            if (!pairedDevices.isEmpty()) {
                for (BluetoothDevice device : pairedDevices) {
                    pairedDevicesNames.add(device.getName());
                    pairedDevicesAddresses.add(device.getAddress());
                }
                final String[] devicesString = pairedDevicesNames.toArray(new String[0]);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ObdActivity.this);
                mBuilder.setTitle("Seleccionar dispositivo OBD:");
                mBuilder.setSingleChoiceItems(devicesString, -1, (dialog, i) -> {
                    dialog.dismiss();
                    int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    chosenDeviceAddress = pairedDevicesAddresses.get(position);
                    chosenDeviceName = pairedDevicesNames.get(position);
                    Toast.makeText(ObdActivity.this, "Dispositivo elegido: " + chosenDeviceName, Toast.LENGTH_SHORT).show();
                    TextView info = findViewById(R.id.info);
                    info.setText(String.format("Dispositivo: %s\tDirección: %s", chosenDeviceName, chosenDeviceAddress));
                    bConnect.setEnabled(true);
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron dispositivos emparejados.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        //TODO
    }

    protected void onResume() {
        super.onResume();
        // TODO
    }

    private void connectOBD() {
        // Método para conectar el dispositivo Bluetooth seleccionado.
        try {
            BluetoothDevice device = btAdapter.getRemoteDevice(chosenDeviceAddress);
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                btSocket = device.createRfcommSocketToServiceRecord(uuid);
                btSocket.connect();
                new EchoOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
                new LineFeedOffCommand().run(btSocket.getInputStream(), btSocket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(btSocket.getInputStream(), btSocket.getOutputStream());
                Toast.makeText(ObdActivity.this, "Conectado al dispositivo OBD.", Toast.LENGTH_SHORT).show();
                bStart.setEnabled(true);
                bConnect.setEnabled(false);
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(ObdActivity.this, "Seleccione primero el dispositvo Bluetooth. ", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(ObdActivity.this, "No se puede establecer conexión.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(ObdActivity.this, "An error occurred in OBD connection." + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void startOBD() {
        // Método que inicia la lectura desde la interfaz OBD-II.
        try {
            command_SpeedResult.setText("");
            command_LoadResult.setText("");
            command_RPMResult.setText("");
            command_ThrottlePositionResult.setText("");
            command_EngineCoolantTemperatureResult.setText("");
            bStart.setEnabled(false);
            bConnect.setEnabled(false);
            bChooseDevice.setEnabled(false);
            bStop.setEnabled(true);
            // Se crea archivo para almacenar los datos leídos.
            file = new File(getFilesDir(), nombreArchivo);
            fileWriter = new FileWriter(file);
            writeCsvHeader(fileWriter);
            writeCsvHeaderUnits(fileWriter);
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
                        command_SpeedResult.setText(command_Speed.getCalculatedResult());
                        float velocidad = Float.parseFloat(command_SpeedResult.getText().toString());
                        // Lectura de datos de 'Carga del motor'.
                        command_Load.run(btSocket.getInputStream(), btSocket.getOutputStream());
                        command_LoadResult.setText(command_Load.getCalculatedResult());
                        float engineLoad = Float.parseFloat(command_LoadResult.getText().toString());
                        // Lectura de datos de 'RPM'.
                        command_RPM.run(btSocket.getInputStream(), btSocket.getOutputStream());
                        command_RPMResult.setText(command_RPM.getCalculatedResult());
                        float rpmResult = Float.parseFloat(command_RPMResult.getText().toString());
                        // Lectura de datos de 'Posición del acelerador'.
                        command_ThrottlePosition.run(btSocket.getInputStream(), btSocket.getOutputStream());
                        command_ThrottlePositionResult.setText(command_ThrottlePosition.getCalculatedResult());
                        float throttlePosition = Float.parseFloat(command_ThrottlePositionResult.getText().toString());
                        // Lectura de datos de 'Temperatura del refrigerante'.
                        command_EngineCoolantTemperature.run(btSocket.getInputStream(), btSocket.getOutputStream());
                        command_EngineCoolantTemperatureResult.setText(command_EngineCoolantTemperature.getCalculatedResult());
                        float engineCoolantTemperature = Float.parseFloat(command_EngineCoolantTemperatureResult.getText().toString());
                        // Se escriben los datos de la lectura en una nueva fila del archivo.
                        writeCsvData(velocidad, engineLoad, rpmResult, throttlePosition, engineCoolantTemperature, fileWriter);
                    } catch (RuntimeException runtimeException) {
                        Toast.makeText(getApplicationContext(), "RuntimeException: Could not initialize Timer" + runtimeException.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException ioException) {
                        Toast.makeText(getApplicationContext(), "IOException: Could not initialize Timer" + ioException.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (InterruptedException interruptedException) {
                        Toast.makeText(getApplicationContext(), "InterruptedException: Could not initialize Timer" + interruptedException.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }, 0, frecuencia);
        } catch (IOException ioException) {
            Toast.makeText(getApplicationContext(), "IOException: Could not start data reading" + ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopOBD() {
        // Método que detiene la lectura desde la interfaz OBD-II.
        // Se finaliza el objeto 'Timer'.
        myTimer.cancel();
        // Se limpian los campos de las lecturas.
        command_SpeedResult.setText("");
        command_LoadResult.setText("");
        command_RPMResult.setText("");
        command_ThrottlePositionResult.setText("");
        command_EngineCoolantTemperatureResult.setText("");
        // Se muestra un mensaje de confirmación del archivo guardado.
        Toast.makeText(getApplicationContext(), "Archivo " + file + " guardado con éxito.", Toast.LENGTH_LONG).show();
        try {
            bStart.setEnabled(false);
            bStop.setEnabled(false);
            bConnect.setEnabled(false);
            bChooseDevice.setEnabled(false);
            // Se sube el archivo generado a AWS.
            uploadFile(nombreArchivo, file);
            // Se liberan los recursos del archivo generado.
            assert fileWriter != null;
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ioException) {
            Toast.makeText(getApplicationContext(), "IOException: Could not stop data reading" + ioException.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException runtimeException) {
            Toast.makeText(getApplicationContext(), "RuntimeException: Could not stop data reading" + runtimeException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void exitApp() {
        // Método para salir de la aplicación.
        try {
            //Mensaje emergente que informa al usuario que se va a cambiar de actividad a Información de Usuario
            String saludo = getString(R.string.informacion_usuario);
            Toast.makeText(getApplicationContext(), saludo, Toast.LENGTH_SHORT).show();
            ObdActivity.this.finish();
            System.exit(0);
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), "An error occurred when trying to return to menu principal" + exception.toString(), Toast.LENGTH_LONG).show();
        }
    }
}