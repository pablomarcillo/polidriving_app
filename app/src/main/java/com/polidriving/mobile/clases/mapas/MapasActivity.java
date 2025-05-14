//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.mapas;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clase de verificación de permisos concedidos a la aplicación
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de Google Maps
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.databinding.ActivityMapasBinding;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import android.location.Location;
import com.polidriving.mobile.R;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import java.util.Objects;
import android.Manifest;

@SuppressWarnings("deprecation")
public class MapasActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Creación de variables para enviar, presentar y recibir información
    private ActivityMapasBinding secuenciador;
    private GoogleMap misMapas;
    private MapView vistaMapa;

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //Creación y presentación visual de los fragmentos en la actividad (objeto mapa)
        super.onSaveInstanceState(outState);
        vistaMapa.onSaveInstanceState(outState);
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            //Creación de variables para crear y cargar los mapas en pantalla con la ubicación del usuario
            misMapas = googleMap;
            misMapas.getUiSettings().setMyLocationButtonEnabled(true);
            misMapas.getUiSettings().setZoomControlsEnabled(true);

            //Verificación de permisos de acceso a la ubicación
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //Segmento de código que permite mostrar la ubicación actual del usuario en la pantalla
            misMapas.setMyLocationEnabled(true);
            //Segmento de código que permite llamar a los servicios GPS
            LocationManager locationManager = (LocationManager) MapasActivity.this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    //Obteniendo las coordenadas del usuario (latitud y longitud)
                    LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                    //Seteando la ubicación del usuario en pantalla
                    Objects.requireNonNull(misMapas.addMarker(new MarkerOptions().position(miUbicacion).title("Mi Ubicación"))).showInfoWindow();
                    misMapas.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                    //ParametrosActivity que permiten mejorar la interacción con el usuario
                    //Permite hacer zoom en la pantalla y cambiar la posición de la camara
                    CameraPosition posicionCamara = new CameraPosition.Builder().target(miUbicacion).zoom(18).bearing(0).tilt(45).build();
                    misMapas.animateCamera(CameraUpdateFactory.newCameraPosition(posicionCamara));
                }
            };
            //Segmento de código que permite actualizar la ubicación del usuario si el usuario cambia de ubicación
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (Exception e) {
            //Mensaje emergente que informa al usuario que no se pudo obtener las coordenadas
            Toast.makeText(getApplicationContext(), "No se pudo obtener las coordenadas", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCreate(Bundle instancia) {
        //Creación y presentación visual de la actividad
        super.onCreate(instancia);
        secuenciador = ActivityMapasBinding.inflate(getLayoutInflater());
        setContentView(secuenciador.getRoot());

        //Inicializando las variables con los elementos de la actividad
        vistaMapa = findViewById(R.id.presentarMapa);
        vistaMapa.getMapAsync(this);
        vistaMapa.onCreate(instancia);

        //Inicializando el botón que permite cambiar la forma en la cual se visualiza los mapas a vista satelital
        Button cambiarSatelite = findViewById(R.id.botonSatelital);
        cambiarSatelite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                misMapas.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        //Inicializando el botón que permite cambiar la forma en la cual se visualiza los mapas a vista en capas
        Button cambiarCapas = findViewById(R.id.botonCapas);
        cambiarCapas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                misMapas.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        //Segmento de código que permite llamar al método para solicitar o verificar los permisos de GPS
        solicitudPermisos();
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }

    private void solicitudPermisos() {
        try {
            //Segmento de código que verifica y de ser necesario solícita permisos de ubicación GPS al usuario para el uso de los mapas
            int permiso = ContextCompat.checkSelfPermission(MapasActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //Verificación de permisos no concedidos
            if (permiso == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Mensaje emergente de búsqueda de la posición actual del usuario
                    Toast.makeText(getApplicationContext(), "Buscando ubicación actual", Toast.LENGTH_SHORT).show();
                } else {
                    //Mensaje emergente de búsqueda de la posición actual del usuario
                    //Mensaje emergente de permisos concedidos con éxito
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Toast.makeText(getApplicationContext(), "Permisos de ubicación concedidos", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Buscando ubicación actual", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            //Mensaje emergente que informa al usuario que no se han concedido los permisos necesarios
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onLowMemory() {
        //Control de la interacción con la actividad y los mapas respecto a la memoria
        super.onLowMemory();
        vistaMapa.onLowMemory();
    }

    protected void onResume() {
        //Reinicia la interacción con la actividad y los mapas
        super.onResume();
        vistaMapa.onResume();
    }

    protected void onStart() {
        //Inicia la interacción con la actividad y los mapas
        super.onStart();
        vistaMapa.onStart();
    }

    protected void onPause() {
        //Suspende la interacción con la actividad y los mapas
        super.onPause();
        vistaMapa.onPause();
    }

    protected void onStop() {
        //Para la interacción con la actividad y los mapas
        super.onStop();
        vistaMapa.onStop();
    }

    public void onDestroy() {
        //Finaliza la interacción con la actividad y los mapas
        super.onDestroy();
        vistaMapa.onDestroy();
        //TODO
    }
}