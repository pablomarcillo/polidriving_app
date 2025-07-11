//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.base_datos;
import com.polidriving.mobile.BuildConfig;
import com.polidriving.mobile.clases.vehiculos.Vehiculo;

//Clases usadas para el CRUD de usuarios en la base de datos DYNAMODB
//Clases usadas para la conexión con AWS mediante Amplify y Cognito
//Clases usadas para la presentación de información al usuario
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.auth.BasicAWSCredentials;
import android.annotation.SuppressLint;
import com.amazonaws.regions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
@SuppressLint("StaticFieldLeak")
// Clase que permite la gestión de los perfiles de usuario
public class DataBaseAccess {
    // Definición de las variables a usar en la conexión con la base de datos
    private final String BASE_DATOS_ACCESS_SECRET_KEY = BuildConfig.BASE_DATOS_ACCESS_SECRET_KEY;
    private final String BASE_DATOS_ACCESS_KEY = BuildConfig.BASE_DATOS_ACCESS_KEY;
    private final String DYNAMODB_TABLE_ALERTAS = "DataSet_Alertas";
    private final String DYNAMODB_TABLE_DATASET = "DataSet";
    private final String DYNAMODB_TABLE_ROUTE_1 = "Route_1";
    private final String DYNAMODB_TABLE_DATA = "UserData";
    private final String DYNAMODB_TABLE_PREDICCIONES = "ML_Predicciones";
    private final String DYNAMODB_TABLE_VEHICLES = "Vehicle";
    private AmazonDynamoDBClient dbClient_1;

    public CharSequence actualizarUsuario(String puntos, String estado, String user, String telefono, String tipo, String correo, String edad, String apellido, String medicas, String nombre, String genero, String lentes) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Agregando la lista de atributos a la base de datos
        // La lista de atributos son para actualizar un usuario
        Map<String, AttributeValue> nuevoUsuario = new HashMap<>();
        nuevoUsuario.put("PuntosLicencia", new AttributeValue().withN(puntos));
        nuevoUsuario.put("EstadoLicencia", new AttributeValue(estado));
        nuevoUsuario.put("UserName", new AttributeValue(user));
        nuevoUsuario.put("Telefono", new AttributeValue().withN(telefono));
        nuevoUsuario.put("TipoUsuario", new AttributeValue(tipo));
        nuevoUsuario.put("Correo", new AttributeValue(correo));
        nuevoUsuario.put("Edad", new AttributeValue().withN(edad));
        nuevoUsuario.put("Apellido", new AttributeValue(apellido));
        nuevoUsuario.put("Medicas", new AttributeValue(medicas));
        nuevoUsuario.put("Nombre", new AttributeValue(nombre));
        nuevoUsuario.put("Genero", new AttributeValue(genero));
        nuevoUsuario.put("Lentes", new AttributeValue(lentes));

        // Realizando la petición a la base de datos para actualizar un elemento
        PutItemRequest peticion = new PutItemRequest();
        // Enviando el nombre de la Tabla de la base de datos
        peticion.withTableName(DYNAMODB_TABLE_DATA).withItem(nuevoUsuario);
        // Se adquiere la respuesta desde la base de datos
        PutItemResult respuesta = dbClient_1.putItem(peticion);

        // Enviando al usuario la respuesta de la base de datos
        return respuesta.toString();
    }

    public CharSequence agregarUsuario(String usernameRecibido, String correoRecibido) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Agregando la lista de atributos a la base de datos
        // La lista de atributos son por default al crear un nuevo usuario
        Map<String, AttributeValue> nuevoUsuario = new HashMap<>();
        nuevoUsuario.put("PuntosLicencia", new AttributeValue().withN("-1"));
        nuevoUsuario.put("EstadoLicencia", new AttributeValue("No Ingresado"));
        nuevoUsuario.put("UserName", new AttributeValue(usernameRecibido));
        nuevoUsuario.put("Telefono", new AttributeValue().withN("-1"));
        nuevoUsuario.put("TipoUsuario", new AttributeValue("Cliente"));
        nuevoUsuario.put("Correo", new AttributeValue(correoRecibido));
        nuevoUsuario.put("Edad", new AttributeValue().withN("-1"));
        nuevoUsuario.put("Apellido", new AttributeValue("No Ingresado"));
        nuevoUsuario.put("Medicas", new AttributeValue("No Ingresado"));
        nuevoUsuario.put("Nombre", new AttributeValue("No Ingresado"));
        nuevoUsuario.put("Genero", new AttributeValue("No Ingresado"));
        nuevoUsuario.put("Lentes", new AttributeValue("No Ingresado"));

        // Realizando la petición a la base de datos para agregar un nuevo elemento
        PutItemRequest peticion = new PutItemRequest();
        // Enviando el nombre de la Tabla de la base de datos
        peticion.withTableName(DYNAMODB_TABLE_DATA).withItem(nuevoUsuario);
        // Se adquiere la respuesta desde la base de datos
        PutItemResult respuesta = dbClient_1.putItem(peticion);

        // Enviando al usuario la respuesta de la base de datos
        return respuesta.toString();
    }

    public CharSequence eliminarUsuario(String usernameRecibido, String correoRecibido) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Agregando la lista de atributos a la base de datos
        // La lista de atributos son para actualizar un usuario
        Map<String, AttributeValue> borrarUsuario = new HashMap<>();
        borrarUsuario.put("UserName", new AttributeValue().withS(usernameRecibido));
        borrarUsuario.put("Correo", new AttributeValue().withS(correoRecibido));

        // Realizando la petición a la base de datos para actualizar un elemento
        DeleteItemRequest peticion = new DeleteItemRequest();
        // Enviando el nombre de la Tabla de la base de datos
        peticion.withTableName(DYNAMODB_TABLE_DATA).withKey(borrarUsuario);
        // Se adquiere la respuesta desde la base de datos
        DeleteItemResult respuesta = dbClient_1.deleteItem(peticion);

        // Enviando al usuario la respuesta de la base de datos
        return respuesta.toString();
    }

    public CharSequence datosRouteOneDynamoDB(String identificador) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos para adquirir los datos del usuario
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_ROUTE_1);
        // Obteniendo la respuesta desde la base de datos
        ScanResult respuesta = dbClient_1.scan(peticion);
        // Agregando los atributos adquiridos desde la base de datos a una lista
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        // Creando una nueva lista para enviar los atributos al usuario
        List<String> atributos = new ArrayList<>();
        // Recorriendo la lista obtenida de la base de datos
        for (Map<String, AttributeValue> map : elementos) {
            // Creando variables para agregar a la lista de elementos
            String Point = Objects.requireNonNull(map.get("point")).getS();
            String Segmento = Objects.requireNonNull(map.get("segment")).getS();
            String Id_Road = Objects.requireNonNull(map.get("id_road")).getS();
            String Latitud = Objects.requireNonNull(map.get("latitude")).getS();
            String Longitud = Objects.requireNonNull(map.get("longitude")).getS();
            String Speed = Objects.requireNonNull(map.get("speed")).getS();

            // Agregando los elementos a la lista
            if (Point.equals(identificador)) {
                atributos.add(Point);
                atributos.add(Segmento);
                atributos.add(Id_Road);
                atributos.add(Latitud);
                atributos.add(Longitud);
                atributos.add(Speed);
            }
        }

        // Enviando al usuario los atributos
        return atributos.toString();
    }

    public CharSequence obtenerUsuario(String usernameRecibido) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos para adquirir los datos del usuario
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_DATA);
        // Obteniendo la respuesta desde la base de datos
        ScanResult respuesta = dbClient_1.scan(peticion);
        // Agregando los atributos adquiridos desde la base de datos a una lista
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        // Creando una nueva lista para enviar los atributos al usuario
        List<String> atributos = new ArrayList<>();
        // Recorriendo la lista obtenida de la base de datos
        for (Map<String, AttributeValue> map : elementos) {
            // Creando variables para agregar a la lista de elementos
            String PuntosLicencia = Objects.requireNonNull(map.get("PuntosLicencia")).getN(); //0
            String EstadoLicencia = Objects.requireNonNull(map.get("EstadoLicencia")).getS(); //1
            String TipoUsuario = Objects.requireNonNull(map.get("TipoUsuario")).getS(); //2
            String Apellido = Objects.requireNonNull(map.get("Apellido")).getS(); //3
            String Telefono = Objects.requireNonNull(map.get("Telefono")).getN(); //4
            String UserName = Objects.requireNonNull(map.get("UserName")).getS(); //5
            String Medicas = Objects.requireNonNull(map.get("Medicas")).getS(); //6
            String Correo = Objects.requireNonNull(map.get("Correo")).getS(); //7
            String Nombre = Objects.requireNonNull(map.get("Nombre")).getS(); //8
            String Genero = Objects.requireNonNull(map.get("Genero")).getS(); //9
            String Lentes = Objects.requireNonNull(map.get("Lentes")).getS(); //10
            String Edad = Objects.requireNonNull(map.get("Edad")).getN();//11
            // Agregando los elementos a la lista
            if (UserName.equals(usernameRecibido)) {
                atributos.add(PuntosLicencia);
                atributos.add(EstadoLicencia);
                atributos.add(TipoUsuario);
                atributos.add(Apellido);
                atributos.add(Telefono);
                atributos.add(UserName);
                atributos.add(Medicas);
                atributos.add(Correo);
                atributos.add(Nombre);
                atributos.add(Genero);
                atributos.add(Lentes);
                atributos.add(Edad);
            }
        }

        // Enviando al usuario sus atributos
        return atributos.toString();
    }

    public CharSequence datosDataSetDynamoDB(int identificador) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos para adquirir los datos del usuario
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_DATASET);
        // Obteniendo la respuesta desde la base de datos
        ScanResult respuesta = dbClient_1.scan(peticion);
        // Agregando los atributos adquiridos desde la base de datos a una lista
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        // Creando una nueva lista para enviar los atributos al usuario
        List<String> atributos = new ArrayList<>();
        // Recorriendo la lista obtenida de la base de datos
        for (Map<String, AttributeValue> map : elementos) {
            // Creando variables para agregar a la lista de elementos
            String Id = Objects.requireNonNull(map.get("Id")).getN();
            String time = Objects.requireNonNull(map.get("time")).getS();
            String steering_angle = Objects.requireNonNull(map.get("steering_angle")).getN();
            String speed = Objects.requireNonNull(map.get("speed")).getN();
            String rpm = Objects.requireNonNull(map.get("rpm")).getN();
            String acceleration = Objects.requireNonNull(map.get("acceleration")).getN();
            String throttle_position = Objects.requireNonNull(map.get("throttle_position")).getN();
            String engine_temperature = Objects.requireNonNull(map.get("engine_temperature")).getN();
            String system_voltage = Objects.requireNonNull(map.get("system_voltage")).getN();
            String barometric_pressure = Objects.requireNonNull(map.get("barometric_pressure")).getN();
            String distance_travelled = Objects.requireNonNull(map.get("distance_travelled")).getN();
            String distance_travelled_total = Objects.requireNonNull(map.get("distance_travelled_total")).getN();
            String id_vehicle = Objects.requireNonNull(map.get("id_vehicle")).getN();
            String latitude = Objects.requireNonNull(map.get("latitude")).getN();
            String longitude = Objects.requireNonNull(map.get("longitude")).getN();
            String altitude = Objects.requireNonNull(map.get("altitude")).getN();
            String id_driver = Objects.requireNonNull(map.get("id_driver")).getN();
            String heart_rate = Objects.requireNonNull(map.get("heart_rate")).getN();
            String stress = Objects.requireNonNull(map.get("stress")).getN();
            String body_battery = Objects.requireNonNull(map.get("body_battery")).getN();
            String current_weather = Objects.requireNonNull(map.get("current_weather")).getN();
            String current_weather_category = Objects.requireNonNull(map.get("current_weather_category")).getS();
            String has_precipitation_category = Objects.requireNonNull(map.get("has_precipitation_category")).getS();
            String has_precipitation = Objects.requireNonNull(map.get("has_precipitation")).getN();
            String is_day_time_category = Objects.requireNonNull(map.get("is_day_time_category")).getS();
            String is_day_time = Objects.requireNonNull(map.get("is_day_time")).getN();
            String temperature = Objects.requireNonNull(map.get("temperature")).getN();
            String real_feel_temperature = Objects.requireNonNull(map.get("real_feel_temperature")).getN();
            String wind_speed = Objects.requireNonNull(map.get("wind_speed")).getN();
            String wind_direction = Objects.requireNonNull(map.get("wind_direction")).getN();
            String relative_humidity = Objects.requireNonNull(map.get("relative_humidity")).getN();
            String visibility = Objects.requireNonNull(map.get("visibility")).getN();
            String uv_index = Objects.requireNonNull(map.get("uv_index")).getN();
            String uv_index_text = Objects.requireNonNull(map.get("uv_index_text")).getS();
            String cloud_cover = Objects.requireNonNull(map.get("cloud_cover")).getN();
            String ceiling = Objects.requireNonNull(map.get("ceiling")).getN();
            String pressure = Objects.requireNonNull(map.get("pressure")).getN();
            String precipitation = Objects.requireNonNull(map.get("precipitation")).getN();
            String accidents_onsite = Objects.requireNonNull(map.get("accidents_onsite")).getN();
            String steering_angle_ = Objects.requireNonNull(map.get("steering_angle_")).getN();
            String speed_vs_steering_angle_ = Objects.requireNonNull(map.get("speed_vs_steering_angle_")).getN();
            String rpm_ = Objects.requireNonNull(map.get("rpm_")).getN();
            String speed_vs_rpm_ = Objects.requireNonNull(map.get("speed_vs_rpm_")).getN();
            String speed_vs_accidents_onsite = Objects.requireNonNull(map.get("speed_vs_accidents_onsite")).getN();
            String speed_vs_precipitation = Objects.requireNonNull(map.get("speed_vs_precipitation")).getN();
            String accident_rate = Objects.requireNonNull(map.get("accident_rate")).getN();

            // Agregando los elementos a la lista
            if (Integer.parseInt(Id) == identificador) {
                atributos.add(Id);
                atributos.add(time);
                atributos.add(steering_angle);
                atributos.add(speed);
                atributos.add(rpm);
                atributos.add(acceleration);
                atributos.add(throttle_position);
                atributos.add(engine_temperature);
                atributos.add(system_voltage);
                atributos.add(barometric_pressure);
                atributos.add(distance_travelled);
                atributos.add(distance_travelled_total);
                atributos.add(id_vehicle);
                atributos.add(latitude);
                atributos.add(longitude);
                atributos.add(altitude);
                atributos.add(id_driver);
                atributos.add(heart_rate);
                atributos.add(stress);
                atributos.add(body_battery);
                atributos.add(current_weather);
                atributos.add(current_weather_category);
                atributos.add(has_precipitation_category);
                atributos.add(has_precipitation);
                atributos.add(is_day_time_category);
                atributos.add(is_day_time);
                atributos.add(temperature);
                atributos.add(real_feel_temperature);
                atributos.add(wind_speed);
                atributos.add(wind_direction);
                atributos.add(relative_humidity);
                atributos.add(visibility);
                atributos.add(uv_index);
                atributos.add(uv_index_text);
                atributos.add(cloud_cover);
                atributos.add(ceiling);
                atributos.add(pressure);
                atributos.add(precipitation);
                atributos.add(accidents_onsite);
                atributos.add(steering_angle_);
                atributos.add(speed_vs_steering_angle_);
                atributos.add(rpm_);
                atributos.add(speed_vs_rpm_);
                atributos.add(speed_vs_accidents_onsite);
                atributos.add(speed_vs_precipitation);
                atributos.add(accident_rate);
            }
        }

        // Enviando al usuario los atributos
        return atributos.toString();
    }

    public CharSequence obtenerAlarma(int identificador) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos para adquirir los datos del usuario
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_ALERTAS);
        // Obteniendo la respuesta desde la base de datos
        ScanResult respuesta = dbClient_1.scan(peticion);
        // Agregando los atributos adquiridos desde la base de datos a una lista
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        // Creando una nueva lista para enviar los atributos al usuario
        List<String> atributos = new ArrayList<>();
        // Recorriendo la lista obtenida de la base de datos
        for (Map<String, AttributeValue> map : elementos) {
            // Creando variables para agregar a la lista de elemento
            String Alerta = Objects.requireNonNull(map.get("Alerta")).getS();
            String Id = Objects.requireNonNull(map.get("Id")).getN();

            // Agregando los elementos a la lista
            if (Integer.parseInt(Id) == identificador) {
                atributos.add(Alerta);
            }
        }
        // Enviando al usuario los atributos
        return atributos.toString();
    }

    public CharSequence obtenerPoint() {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos para adquirir los datos del usuario
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_ROUTE_1);
        // Obteniendo la respuesta desde la base de datos
        ScanResult respuesta = dbClient_1.scan(peticion);
        // Agregando los atributos adquiridos desde la base de datos a una lista
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        // Creando una nueva lista para enviar los atributos al usuario
        List<String> atributos = new ArrayList<>();
        // Recorriendo la lista obtenida de la base de datos
        for (Map<String, AttributeValue> map : elementos) {
            // Creando variables para agregar a la lista de elemento
            String Point = Objects.requireNonNull(map.get("point")).getS();
            // Agregando los elementos a la lista
            atributos.add(Point);
        }

        // Enviando al usuario los atributos
        return atributos.toString();
    }

    public CharSequence obtenerId() {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos para adquirir los datos del usuario
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_DATASET);
        // Obteniendo la respuesta desde la base de datos
        ScanResult respuesta = dbClient_1.scan(peticion);
        // Agregando los atributos adquiridos desde la base de datos a una lista
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        // Creando una nueva lista para enviar los atributos al usuario
        List<String> atributos = new ArrayList<>();
        // Recorriendo la lista obtenida de la base de datos
        for (Map<String, AttributeValue> map : elementos) {
            // Creando variables para agregar a la lista de elemento
            String Id = Objects.requireNonNull(map.get("Id")).getN();
            // Agregando los elementos a la lista
            atributos.add(Id);
        }

        // Enviando al usuario los atributos
        return atributos.toString();
    }

    public CharSequence guardarPrediccionML(String steering_angle,   String speed,              String rpm, 
                                            String acceleration,     String throttle_position,  String engine_temperature, 
                                            String system_voltage,   String heart_rate,         String distance_travelled, 
                                            String latitude,         String longitude,          String current_weather, 
                                            String accidents_onsite, String correoUsuario,      String placaVehiculo,
                                            String respuestaModelo,  String timestamp) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Agregando la lista de atributos a la base de datos para la predicción del modelo ML
        Map<String, AttributeValue> nuevaPrediccion = new HashMap<>();

        // Generando un ID único basado en timestamp y datos
        String predictionId = String.valueOf(System.currentTimeMillis());

        // Datos de entrada al modelo
        nuevaPrediccion.put("PredictionId", new AttributeValue(predictionId));
        nuevaPrediccion.put("CorreoUsuario", new AttributeValue(correoUsuario));
        nuevaPrediccion.put("PlacaVehiculo", new AttributeValue(placaVehiculo));
        nuevaPrediccion.put("SteeringAngle", new AttributeValue().withN(steering_angle));
        nuevaPrediccion.put("Speed", new AttributeValue().withN(speed));
        nuevaPrediccion.put("RPM", new AttributeValue().withN(rpm));
        nuevaPrediccion.put("Acceleration", new AttributeValue().withN(acceleration));
        nuevaPrediccion.put("ThrottlePosition", new AttributeValue().withN(throttle_position));
        nuevaPrediccion.put("EngineTemperature", new AttributeValue().withN(engine_temperature));
        nuevaPrediccion.put("SystemVoltage", new AttributeValue().withN(system_voltage));
        nuevaPrediccion.put("HeartRate", new AttributeValue().withN(heart_rate));
        nuevaPrediccion.put("DistanceTravelled", new AttributeValue().withN(distance_travelled));
        nuevaPrediccion.put("Latitude", new AttributeValue().withN(latitude));
        nuevaPrediccion.put("Longitude", new AttributeValue().withN(longitude));
        nuevaPrediccion.put("CurrentWeather", new AttributeValue().withN(current_weather));
        nuevaPrediccion.put("AccidentsOnsite", new AttributeValue().withN(accidents_onsite));

        // Timestamp de la predicción
        nuevaPrediccion.put("Timestamp", new AttributeValue(timestamp));
        // Extraer solo el valor numérico de la respuesta del modelo
        nuevaPrediccion.put("ModelResponse", new AttributeValue(respuestaModelo));

        // Realizando la petición a la base de datos para agregar un nuevo elemento
        PutItemRequest peticion = new PutItemRequest();
        // Enviando el nombre de la Tabla de la base de datos
        peticion.withTableName(DYNAMODB_TABLE_PREDICCIONES).withItem(nuevaPrediccion);
        // Se adquiere la respuesta desde la base de datos
        PutItemResult respuesta = dbClient_1.putItem(peticion);

        // Enviando al usuario la respuesta de la base de datos
        return respuesta.toString();
    }

    // ===== MÉTODOS CRUD PARA VEHÍCULOS =====

    /**
     * Método para agregar un nuevo vehículo
     */
    public CharSequence agregarVehiculo(String placa, String marca, String modelo, String anoFabricacion, 
                                       String tipoCombustible, String cilindraje, String tipoCarroceria, 
                                       String transmision, String traccion, String numeroAirbags, String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Agregando la lista de atributos a la base de datos
        Map<String, AttributeValue> nuevoVehiculo = new HashMap<>();
        nuevoVehiculo.put("Placa", new AttributeValue(placa));
        nuevoVehiculo.put("Marca", new AttributeValue(marca));
        nuevoVehiculo.put("Modelo", new AttributeValue(modelo));
        nuevoVehiculo.put("AnoFabricacion", new AttributeValue().withN(anoFabricacion));
        nuevoVehiculo.put("TipoCombustible", new AttributeValue(tipoCombustible));
        nuevoVehiculo.put("Cilindraje", new AttributeValue().withN(cilindraje));
        nuevoVehiculo.put("TipoCarroceria", new AttributeValue(tipoCarroceria));
        nuevoVehiculo.put("Transmision", new AttributeValue(transmision));
        nuevoVehiculo.put("Traccion", new AttributeValue(traccion));
        // Validar numeroAirbags antes de guardarlo
        String airbagsValue = (numeroAirbags == null || numeroAirbags.trim().isEmpty()) ? "0" : numeroAirbags.trim();
        nuevoVehiculo.put("NumeroAirbags", new AttributeValue().withN(airbagsValue));
        nuevoVehiculo.put("CorreoUsuario", new AttributeValue(correoUsuario));
        nuevoVehiculo.put("EsVehiculoActual", new AttributeValue().withBOOL(false));

        // Realizando la petición a la base de datos para agregar un nuevo elemento
        PutItemRequest peticion = new PutItemRequest();
        peticion.withTableName(DYNAMODB_TABLE_VEHICLES).withItem(nuevoVehiculo);
        PutItemResult respuesta = dbClient_1.putItem(peticion);

        return respuesta.toString();
    }

    /**
     * Método para actualizar un vehículo existente
     */
    public CharSequence actualizarVehiculo(String placa, String marca, String modelo, String anoFabricacion, 
                                          String tipoCombustible, String cilindraje, String tipoCarroceria, 
                                          String transmision, String traccion, String numeroAirbags, String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Agregando la lista de atributos a la base de datos
        Map<String, AttributeValue> vehiculoActualizado = new HashMap<>();
        vehiculoActualizado.put("Placa", new AttributeValue(placa));
        vehiculoActualizado.put("Marca", new AttributeValue(marca));
        vehiculoActualizado.put("Modelo", new AttributeValue(modelo));
        vehiculoActualizado.put("AnoFabricacion", new AttributeValue().withN(anoFabricacion));
        vehiculoActualizado.put("TipoCombustible", new AttributeValue(tipoCombustible));
        vehiculoActualizado.put("Cilindraje", new AttributeValue().withN(cilindraje));
        vehiculoActualizado.put("TipoCarroceria", new AttributeValue(tipoCarroceria));
        vehiculoActualizado.put("Transmision", new AttributeValue(transmision));
        vehiculoActualizado.put("Traccion", new AttributeValue(traccion));
        // Validar numeroAirbags antes de guardarlo
        String airbagsValue = (numeroAirbags == null || numeroAirbags.trim().isEmpty()) ? "0" : numeroAirbags.trim();
        vehiculoActualizado.put("NumeroAirbags", new AttributeValue().withN(airbagsValue));
        vehiculoActualizado.put("CorreoUsuario", new AttributeValue(correoUsuario));

        // Obtener el estado actual del vehículo para mantener EsVehiculoActual
        String vehiculoExistente = (String) obtenerVehiculo(placa, correoUsuario);
        boolean esActual = false;
        if (!vehiculoExistente.equals("[]")) {
            // Parsear para obtener el estado actual
            String[] datos = vehiculoExistente.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().split(",");
            if (datos.length > 11) {
                esActual = Boolean.parseBoolean(datos[11].trim());
            }
        }
        vehiculoActualizado.put("EsVehiculoActual", new AttributeValue().withBOOL(esActual));

        // Realizando la petición a la base de datos para actualizar
        PutItemRequest peticion = new PutItemRequest();
        peticion.withTableName(DYNAMODB_TABLE_VEHICLES).withItem(vehiculoActualizado);
        PutItemResult respuesta = dbClient_1.putItem(peticion);

        return respuesta.toString();
    }

    /**
     * Método para eliminar un vehículo
     */
    public CharSequence eliminarVehiculo(String placa, String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Crear la clave para eliminar
        Map<String, AttributeValue> clave = new HashMap<>();
        clave.put("Placa", new AttributeValue(placa));
        clave.put("CorreoUsuario", new AttributeValue(correoUsuario));

        // Realizando la petición a la base de datos para eliminar
        DeleteItemRequest peticion = new DeleteItemRequest();
        peticion.withTableName(DYNAMODB_TABLE_VEHICLES).withKey(clave);
        DeleteItemResult respuesta = dbClient_1.deleteItem(peticion);

        return respuesta.toString();
    }

    /**
     * Método para obtener un vehículo específico
     */
    public CharSequence obtenerVehiculo(String placa, String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_VEHICLES);
        ScanResult respuesta = dbClient_1.scan(peticion);
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        List<String> atributos = new ArrayList<>();
        for (Map<String, AttributeValue> map : elementos) {
            String placaVehiculo = Objects.requireNonNull(map.get("Placa")).getS();
            String correoVehiculo = Objects.requireNonNull(map.get("CorreoUsuario")).getS();

            if (placaVehiculo.equals(placa) && correoVehiculo.equals(correoUsuario)) {
                atributos.add(Objects.requireNonNull(map.get("Placa")).getS()); //0
                atributos.add(Objects.requireNonNull(map.get("Marca")).getS()); //1
                atributos.add(Objects.requireNonNull(map.get("Modelo")).getS()); //2
                atributos.add(Objects.requireNonNull(map.get("AnoFabricacion")).getN()); //3
                atributos.add(Objects.requireNonNull(map.get("TipoCombustible")).getS()); //4
                atributos.add(Objects.requireNonNull(map.get("Cilindraje")).getN()); //5
                atributos.add(Objects.requireNonNull(map.get("TipoCarroceria")).getS()); //6
                atributos.add(Objects.requireNonNull(map.get("Transmision")).getS()); //7
                atributos.add(Objects.requireNonNull(map.get("Traccion")).getS()); //8
                // Manejo seguro de NumeroAirbags
                AttributeValue airbagsValue = map.get("NumeroAirbags");
                if (airbagsValue != null && airbagsValue.getN() != null) {
                    atributos.add(airbagsValue.getN()); //9
                } else {
                    atributos.add("0"); //9 - Valor por defecto si es null
                }
                atributos.add(Objects.requireNonNull(map.get("CorreoUsuario")).getS()); //10
                atributos.add(String.valueOf(Objects.requireNonNull(map.get("EsVehiculoActual")).getBOOL())); //11
                break;
            }
        }

        return atributos.toString();
    }

    /**
     * Método para obtener todos los vehículos de un usuario
     */
    public CharSequence obtenerVehiculosUsuario(String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_VEHICLES);
        ScanResult respuesta = dbClient_1.scan(peticion);
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        List<String> vehiculos = new ArrayList<>();
        for (Map<String, AttributeValue> map : elementos) {
            String correoVehiculo = Objects.requireNonNull(map.get("CorreoUsuario")).getS();

            if (correoVehiculo.equals(correoUsuario)) {
                // Manejo seguro de NumeroAirbags
                AttributeValue airbagsValue = map.get("NumeroAirbags");
                String numeroAirbags = "0"; // Valor por defecto
                if (airbagsValue != null && airbagsValue.getN() != null) {
                    numeroAirbags = airbagsValue.getN();
                }
                
                // Crear una cadena con los datos del vehículo separados por |
                String vehiculoInfo = Objects.requireNonNull(map.get("Placa")).getS() + "|" +
                                    Objects.requireNonNull(map.get("Marca")).getS() + "|" +
                                    Objects.requireNonNull(map.get("Modelo")).getS() + "|" +
                                    Objects.requireNonNull(map.get("AnoFabricacion")).getN() + "|" +
                                    Objects.requireNonNull(map.get("TipoCombustible")).getS() + "|" +
                                    Objects.requireNonNull(map.get("Cilindraje")).getN() + "|" +
                                    Objects.requireNonNull(map.get("TipoCarroceria")).getS() + "|" +
                                    Objects.requireNonNull(map.get("Transmision")).getS() + "|" +
                                    Objects.requireNonNull(map.get("Traccion")).getS() + "|" +
                                    numeroAirbags + "|" +
                                    Objects.requireNonNull(map.get("CorreoUsuario")).getS() + "|" +
                                    String.valueOf(Objects.requireNonNull(map.get("EsVehiculoActual")).getBOOL());
                vehiculos.add(vehiculoInfo);
            }
        }

        return vehiculos.toString();
    }

    /**
     * Método para establecer un vehículo como actual
     */
    public CharSequence establecerVehiculoActual(String placa, String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Primero, desmarcar todos los vehículos del usuario como actuales
        String vehiculosUsuario = (String) obtenerVehiculosUsuario(correoUsuario);
        if (!vehiculosUsuario.equals("[]")) {
            String[] vehiculosArray = vehiculosUsuario.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().split(",");
            for (String vehiculoInfo : vehiculosArray) {
                String[] datos = vehiculoInfo.trim().split("\\|");
                if (datos.length > 0) {
                    String placaVehiculo = datos[0].trim();
                    // Actualizar para que no sea vehículo actual
                    actualizarEstadoVehiculoActual(placaVehiculo, correoUsuario, false);
                }
            }
        }

        // Ahora marcar el vehículo seleccionado como actual
        String resultado = (String) actualizarEstadoVehiculoActual(placa, correoUsuario, true);
        return resultado;
    }

    /**
     * Método auxiliar para actualizar el estado de vehículo actual
     */
    private CharSequence actualizarEstadoVehiculoActual(String placa, String correoUsuario, boolean esActual) {
        // Obtener el vehículo completo
        String vehiculoExistente = (String) obtenerVehiculo(placa, correoUsuario);
        if (vehiculoExistente.equals("[]")) {
            return "Vehículo no encontrado";
        }

        // Parsear los datos del vehículo
        String[] datos = vehiculoExistente.replaceAll("\\[", "").trim().replaceAll("\\]", "").trim().split(",");
        if (datos.length < 11) {
            return "Datos del vehículo incompletos";
        }

        // Crear el vehículo actualizado
        Map<String, AttributeValue> vehiculoActualizado = new HashMap<>();
        vehiculoActualizado.put("Placa", new AttributeValue(datos[0].trim()));
        vehiculoActualizado.put("Marca", new AttributeValue(datos[1].trim()));
        vehiculoActualizado.put("Modelo", new AttributeValue(datos[2].trim()));
        vehiculoActualizado.put("AnoFabricacion", new AttributeValue().withN(datos[3].trim()));
        vehiculoActualizado.put("TipoCombustible", new AttributeValue(datos[4].trim()));
        vehiculoActualizado.put("Cilindraje", new AttributeValue().withN(datos[5].trim()));
        vehiculoActualizado.put("TipoCarroceria", new AttributeValue(datos[6].trim()));
        vehiculoActualizado.put("Transmision", new AttributeValue(datos[7].trim()));
        vehiculoActualizado.put("Traccion", new AttributeValue(datos[8].trim()));
        // Validar numeroAirbags antes de guardarlo
        String airbagsValue = (datos[9] == null || datos[9].trim().isEmpty()) ? "0" : datos[9].trim();
        vehiculoActualizado.put("NumeroAirbags", new AttributeValue().withN(airbagsValue));
        vehiculoActualizado.put("CorreoUsuario", new AttributeValue(datos[10].trim()));
        vehiculoActualizado.put("EsVehiculoActual", new AttributeValue().withBOOL(esActual));

        // Realizando la petición a la base de datos para actualizar
        PutItemRequest peticion = new PutItemRequest();
        peticion.withTableName(DYNAMODB_TABLE_VEHICLES).withItem(vehiculoActualizado);
        PutItemResult respuesta = dbClient_1.putItem(peticion);

        return respuesta.toString();
    }

    /**
     * Método para obtener el vehículo actual del usuario
     */
    public CharSequence obtenerVehiculoActual(String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_VEHICLES);
        ScanResult respuesta = dbClient_1.scan(peticion);
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        List<String> atributos = new ArrayList<>();
        for (Map<String, AttributeValue> map : elementos) {
            String correoVehiculo = Objects.requireNonNull(map.get("CorreoUsuario")).getS();
            boolean esVehiculoActual = Objects.requireNonNull(map.get("EsVehiculoActual")).getBOOL();

            if (correoVehiculo.equals(correoUsuario) && esVehiculoActual) {
                atributos.add(Objects.requireNonNull(map.get("Placa")).getS()); //0
                atributos.add(Objects.requireNonNull(map.get("Marca")).getS()); //1
                atributos.add(Objects.requireNonNull(map.get("Modelo")).getS()); //2
                atributos.add(Objects.requireNonNull(map.get("AnoFabricacion")).getN()); //3
                atributos.add(Objects.requireNonNull(map.get("TipoCombustible")).getS()); //4
                atributos.add(Objects.requireNonNull(map.get("Cilindraje")).getN()); //5
                atributos.add(Objects.requireNonNull(map.get("TipoCarroceria")).getS()); //6
                atributos.add(Objects.requireNonNull(map.get("Transmision")).getS()); //7
                atributos.add(Objects.requireNonNull(map.get("Traccion")).getS()); //8
                // Manejo seguro de NumeroAirbags
                AttributeValue airbagsValue = map.get("NumeroAirbags");
                if (airbagsValue != null && airbagsValue.getN() != null) {
                    atributos.add(airbagsValue.getN()); //9
                } else {
                    atributos.add("0"); //9 - Valor por defecto si es null
                }
                atributos.add(Objects.requireNonNull(map.get("CorreoUsuario")).getS()); //10
                atributos.add(String.valueOf(Objects.requireNonNull(map.get("EsVehiculoActual")).getBOOL())); //11
                break;
            }
        }

        return atributos.toString();
    }

    /**
     * Método para obtener todos los vehículos de un usuario como lista de objetos Vehiculo
     */
    public List<Vehiculo> obtenerVehiculosUsuarioLista(String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_VEHICLES);
        ScanResult respuesta = dbClient_1.scan(peticion);
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        List<Vehiculo> vehiculos = new ArrayList<>();
        for (Map<String, AttributeValue> map : elementos) {
            String correoVehiculo = Objects.requireNonNull(map.get("CorreoUsuario")).getS();

            if (correoVehiculo.equals(correoUsuario)) {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setCorreoUsuario(correoVehiculo);
                vehiculo.setPlaca(Objects.requireNonNull(map.get("Placa")).getS());
                vehiculo.setMarca(Objects.requireNonNull(map.get("Marca")).getS());
                vehiculo.setModelo(Objects.requireNonNull(map.get("Modelo")).getS());
                vehiculo.setAnoFabricacion(Objects.requireNonNull(map.get("AnoFabricacion")).getN());
                vehiculo.setTipoCombustible(Objects.requireNonNull(map.get("TipoCombustible")).getS());
                vehiculo.setCilindraje(Objects.requireNonNull(map.get("Cilindraje")).getN());
                vehiculo.setTipoCarroceria(Objects.requireNonNull(map.get("TipoCarroceria")).getS());
                vehiculo.setTransmision(Objects.requireNonNull(map.get("Transmision")).getS());
                vehiculo.setTraccion(Objects.requireNonNull(map.get("Traccion")).getS());
                // Manejo seguro de NumeroAirbags con validación de nulos
                AttributeValue airbagsValue = map.get("NumeroAirbags");
                if (airbagsValue != null && airbagsValue.getN() != null) {
                    try {
                        vehiculo.setNumeroAirbags(Integer.parseInt(airbagsValue.getN()));
                    } catch (NumberFormatException e) {
                        vehiculo.setNumeroAirbags(0); // Valor por defecto si hay error de formato
                    }
                } else {
                    vehiculo.setNumeroAirbags(0); // Valor por defecto si es null
                }
                vehiculo.setActual(Objects.requireNonNull(map.get("EsVehiculoActual")).getBOOL());
                
                vehiculos.add(vehiculo);
            }
        }

        return vehiculos;
    }

    /**
     * Método para obtener la placa del vehículo actual del usuario
     */
    public String obtenerPlacaVehiculoActual(String correoUsuario) {
        //Crear un nuevo proveedor de credenciales
        dbClient_1 = new AmazonDynamoDBClient(new BasicAWSCredentials(BASE_DATOS_ACCESS_KEY, BASE_DATOS_ACCESS_SECRET_KEY));
        dbClient_1.setRegion(Region.getRegion(Regions.US_EAST_1));

        // Realizando una petición a la base de datos
        ScanRequest peticion = new ScanRequest(DYNAMODB_TABLE_VEHICLES);
        ScanResult respuesta = dbClient_1.scan(peticion);
        List<Map<String, AttributeValue>> elementos = respuesta.getItems();

        for (Map<String, AttributeValue> map : elementos) {
            String correoVehiculo = Objects.requireNonNull(map.get("CorreoUsuario")).getS();
            Boolean esVehiculoActual = Objects.requireNonNull(map.get("EsVehiculoActual")).getBOOL();

            if (correoVehiculo.equals(correoUsuario) && esVehiculoActual) {
                return Objects.requireNonNull(map.get("Placa")).getS();
            }
        }

        return "SIN_VEHICULO"; // Retorna un valor por defecto si no hay vehículo actual
    }
}