//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clase para conexión con servicio REST mediante petición POST
//Clases usadas para la presentación de información al usuario
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
//Clases para el uso de URL
import com.polidriving.mobile.base_datos.DataBaseAccess;
import com.polidriving.mobile.BuildConfig;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.auth.AuthUserAttribute;
import java.nio.charset.StandardCharsets;
import android.annotation.SuppressLint;
import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.util.Iterator;
import android.util.Log;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;


// Interfaz para callback del correo de usuario
interface CorreoUsuarioCallback {
    void onCorreoObtenido(String correo);
    void onError(String error);
}

/**
 * @noinspection DataFlowIssue
 */
@SuppressWarnings({"deprecation", "LoopStatementThatDoesntLoop", "RegExpRedundantEscape"})
@SuppressLint("StaticFieldLeak")
public class Agente extends AsyncTask<String, String, String> {
    //Creación de variables para enviar, presentar y recibir información
    public String distance_travelled;
    public String engine_temperature;
    public String throttle_position;
    public String resultadoAPI = "";
    public String accidents_onsite;
    public String current_weather;
    public String steering_angle_;
    public String system_voltage;
    public String linkRequestAPI;
    public String acceleration;
    public Context httContext;
    public String heart_rate;
    public String longitude;
    public String latitude;
    public String speed;
    public String rpm_;
    public String timestamp; // Variable para almacenar el timestamp de la predicción
    public String correoUsuario; // Variable para almacenar el correo del usuario logueado


    public Agente(Context contexto, String linkAPI, String steering_angle_R, String speed_R, String rpm_R, String acceleration_R, String throttle_position_R, String engine_temperature_R, String system_voltage_R, String heart_rate_R, String distance_travelled_R, String latitude_R, String longitude_R, String current_weather_R, String accidents_onsite_R) {
        //Creación del constructor que permite enviar y recibir información a partir de esta clase
        //Se inicializa las variables creadas con la información recibida por parte del usuario
        this.engine_temperature = engine_temperature_R;
        this.distance_travelled = distance_travelled_R;
        this.throttle_position = throttle_position_R;
        this.accidents_onsite = accidents_onsite_R;
        this.current_weather = current_weather_R;
        this.steering_angle_ = steering_angle_R;
        this.system_voltage = system_voltage_R;
        this.acceleration = acceleration_R;
        this.heart_rate = heart_rate_R;
        this.linkRequestAPI = linkAPI;
        this.longitude = longitude_R;
        this.latitude = latitude_R;
        this.httContext = contexto;
        this.speed = speed_R;
        this.rpm_ = rpm_R;
        // Generando timestamp para esta predicción
        this.timestamp = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
    }

    public String getPostDataString(JSONObject params) throws Exception {
        //Segmento de código que permite dar formato POST a la cadena de consulta al servicio REST
        StringBuilder result = new StringBuilder();
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            //Instanciando la palabra clave a la consulta
            String key = itr.next();
            Object value = params.get(key);
            //Agregando parametros claves al formato de la consulta
            result.append("{").append("\"").append(key).append("\"").append(": ").append(value).append("}");
        }
        //Devolviendo al usuario la consulta en el formato POST para su uso
        return result.toString();
    }

    protected String doInBackground(String... strings) {
        //Segmento de código que se ejecuta en segundo plano que permite realizar la consulta POST al servicio REST
        //Instanciando la dirección URL del servicio REST
        String wsURL = linkRequestAPI;
        URL url;
        try {
            url = new URL(wsURL);
            //Abriendo una nueva conexión con el servicio REST de AWS
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //Creando una nueva consulta POST
            JSONObject parametrosPost = new JSONObject();
            //Agregando a la consulta POST los datos de consulta
            parametrosPost.put("Input", "[[" + steering_angle_ + ", " + speed + ", " + rpm_ + ", " + acceleration + ", " + throttle_position + ", " + engine_temperature + ", " + system_voltage + ", " + heart_rate + ", " + distance_travelled + ", " + latitude + ", " + longitude + ", " + current_weather + ", " + accidents_onsite + "]]");
            //Estableciendo las características de consulta POST con timeout configurado desde BuildConfig
            urlConnection.setReadTimeout(BuildConfig.TIMEOUT_API_PREDICTOR_MS);
            urlConnection.setConnectTimeout(BuildConfig.TIMEOUT_API_PREDICTOR_MS);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            //Enviando al servicio REST la cadena de consulta con el format adecuado
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(getPostDataString(parametrosPost));
            writer.flush();
            writer.close();
            os.close();
            //Verificando el estado de conexión con el servicio REST
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //Estado de conexión exitoso con una respuesta de 200
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                //Creación de una nueva variable para recibir la respuesta del servicio REST
                String linea;
                while ((linea = in.readLine()) != null) {
                    sb.append(linea);
                    break;
                }
                //Cerrando la conexión con el servicio REST
                in.close();
                //Instanciando la respuesta del servicio REST con una variable local para su posterior uso
                resultadoAPI = sb.toString();
            } else {
                //Mensaje de consola que informa al usuario que no se pudo establecer conexión con el servicio REST
                Log.e("ERROR API: ", "Código: " + responseCode);
            }
        } catch (Exception e) {
            //Mensaje de consola que informa al usuario que se produjo un error al ejecutar la consulta POST
            Log.e("EXCEPCIÓN API: ", e.getMessage());
        }
        //Retornando el valor de la variable local con la respuesta del servicio REST al usuario
        return resultadoAPI;
    }

    protected void onPostExecute(String s) {
        //Segmento de código que permite obtener la respuesta del servicio REST para que esta sea presentada en las actividades
        super.onPostExecute(s);
        // Removed instance creation as we're using static methods now
        // Se separa los atributos obtenidos mediante un split
        // Extraer el valor del Output desde la estructura JSON anidada
        String[] idDataBase = s.replaceAll(".*\\\"body\\\":\\s*\\\"\\{\\\\\\\"Output\\\\\\\":\\s*", "").trim().replaceAll("\\}\\\".*", "").trim().split(",");
        
        // Guardar la predicción en DynamoDB
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Primero obtener el correo del usuario logueado
                    obtenerCorreoUsuarioLogueado(new CorreoUsuarioCallback() {
                        @Override
                        public void onCorreoObtenido(String correo) {
                            try {
                                DataBaseAccess dbAccess = new DataBaseAccess();
                                // Obtener la placa del vehículo actual
                                String placaVehiculo = dbAccess.obtenerPlacaVehiculoActual(correo);
                                String resultadoGuardado = (String) dbAccess.guardarPrediccionML(
                                    steering_angle_, speed, rpm_, acceleration, throttle_position, 
                                    engine_temperature, system_voltage, heart_rate, distance_travelled, 
                                    latitude, longitude, current_weather, accidents_onsite, correo, placaVehiculo, idDataBase[0], timestamp
                                );
                                Log.i("PREDICCION_GUARDADA: ", resultadoGuardado.toString());
                            } catch (Exception e) {
                                Log.e("ERROR_GUARDAR_PREDICCION: ", e.getMessage());
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e("ERROR_OBTENER_CORREO: ", error);
                            // Guardar con correo por defecto en caso de error
                            try {
                                DataBaseAccess dbAccess = new DataBaseAccess();
                                String resultadoGuardado = (String) dbAccess.guardarPrediccionML(
                                    steering_angle_, speed, rpm_, acceleration, throttle_position, 
                                    engine_temperature, system_voltage, heart_rate, distance_travelled, 
                                    latitude, longitude, current_weather, accidents_onsite, "correo_no_disponible", "SIN_VEHICULO", idDataBase[0], timestamp
                                );
                                Log.i("PREDICCION_GUARDADA_SIN_CORREO: ", resultadoGuardado.toString());
                            } catch (Exception e) {
                                Log.e("ERROR_GUARDAR_PREDICCION_FALLBACK: ", e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("ERROR_OBTENER_USUARIO: ", e.getMessage());
                }
            }
        }).start();
        
        //Segmento de código que permite leer el Dataset con los datos generados por el vehículo
        new Thread(new Runnable() {
            //Para la lectura se genera un hilo secundario, con el fin de estar leyendo el data set continuamente
            public void run() {
                try {
                    // Llamando a una nueva instancia de la base de datos
                    DataBaseAccess consulta = new DataBaseAccess();
                    try {
                        // Método para obtener la lista de atributos desde la base de datos de los Datos del Vehiculo
                        String datosAlarma = (String) consulta.obtenerAlarma(Integer.parseInt(idDataBase[0]));
                        // Se separa los atributos obtenidos mediante un split
                        Log.i("datosAlarma API: ", datosAlarma);
                        String[] token = datosAlarma.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().split(",");
                        
                        if (token.length > 0 && !token[0].trim().isEmpty()) {
                            // Mapear la alarma textual a número para la interfaz gráfica
                            String alarmaTexto = token[0].trim();
                            String nivelRiesgo = "1"; // Por defecto nivel bajo
                            
                            // Mapear alarmas textuales a niveles numéricos
                            switch (alarmaTexto.toLowerCase()) {
                                case "muy alta":
                                case "muy alto":
                                case "critica":
                                case "crítica":
                                    nivelRiesgo = "4";
                                    break;
                                case "alta":
                                case "alto":
                                    nivelRiesgo = "3";
                                    break;
                                case "media":
                                case "medio":
                                case "moderada":
                                case "moderado":
                                    nivelRiesgo = "2";
                                    break;
                                case "baja":
                                case "bajo":
                                case "minima":
                                case "mínima":
                                default:
                                    nivelRiesgo = "1";
                                    break;
                            }
                            
                            // Crear la respuesta con el formato esperado por la interfaz gráfica
                            String respuestaAlarma = "{\"Output\": " + nivelRiesgo + "}";
                            
                            // Actualizar la interfaz con la alarma obtenida en el hilo principal
                            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    FragmentoModeloVista.datosRespuesta(respuestaAlarma);
                                }
                            });
                            
                            //Mensaje de consola que permite mostrar el resultado del servicio REST
                            Log.i("RESULTADO API DYNAMODB: ", alarmaTexto);
                            Log.i("NIVEL_RIESGO_MAPEADO: ", nivelRiesgo);
                            Log.i("RESPUESTA_ALARMA_ENVIADA: ", respuestaAlarma);
                        } else {
                            // Si no hay alarma específica, usar la respuesta original de la API
                            android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    FragmentoModeloVista.datosRespuesta(s);
                                }
                            });
                            Log.i("USANDO_RESPUESTA_ORIGINAL: ", s);
                        }
                    } catch (Exception e) {
                        // Mensaje de error al no poder conectarse a la base de datos
                        Log.e("Error de conexión: ", e.getMessage());
                        // En caso de error, usar la respuesta original de la API
                        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                FragmentoModeloVista.datosRespuesta(s);
                            }
                        });
                    }
                } catch (Exception e) {
                    //Mensaje de consola que informa al usuario que n se pudo leer el Dataset
                    Log.e("ERROR DATASET: ", e.toString());
                    // En caso de error, usar la respuesta original de la API
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            FragmentoModeloVista.datosRespuesta(s);
                        }
                    });
                }
            }
        }).start();
        //Mensaje de consola que permite mostrar el resultado del servicio REST
        Log.i("RESULTADO API: ", s);
        resultadoAPI = s;
    }

    protected void onPreExecute() {
        //Segmento de código que se ejecuta después del obtener la respuesta del servicio REST
        super.onPreExecute();
        //TODO
    }
    
    /**
     * Método para obtener el correo del usuario logueado mediante Amplify Auth
     */
    private void obtenerCorreoUsuarioLogueado(CorreoUsuarioCallback callback) {
        try {
            Amplify.Auth.getCurrentUser(
                result -> {
                    // Obtener los atributos del usuario para obtener el correo real
                    Amplify.Auth.fetchUserAttributes(
                        attributes -> {
                            String correoUsuario = null;
                            for (AuthUserAttribute attribute : attributes) {
                                if (attribute.getKey().getKeyString().equals("email")) {
                                    correoUsuario = attribute.getValue();
                                    Log.i("CORREO_OBTENIDO_DIRECTO: ", correoUsuario);
                                    break;
                                }
                            }
                            
                            if (correoUsuario != null && !correoUsuario.isEmpty()) {
                                callback.onCorreoObtenido(correoUsuario);
                            } else {
                                // Fallback: usar username si no se encuentra email
                                String username = result.getUsername();
                                Log.i("FALLBACK_USERNAME: ", username);
                                callback.onCorreoObtenido(username);
                            }
                        },
                        error -> {
                            Log.e("ERROR_FETCH_ATTRIBUTES: ", error.toString());
                            // Fallback: usar username en caso de error
                            String username = result.getUsername();
                            Log.i("FALLBACK_USERNAME_ERROR: ", username);
                            callback.onCorreoObtenido(username);
                        }
                    );
                },
                error -> {
                    Log.e("ERROR_AUTH_USER: ", error.toString());
                    callback.onError("Error al obtener el usuario autenticado: " + error.toString());
                }
            );
        } catch (Exception e) {
            Log.e("ERROR_AMPLIFY_AUTH: ", e.getMessage());
            callback.onError("Error en Amplify Auth: " + e.getMessage());
        }
    }
}
