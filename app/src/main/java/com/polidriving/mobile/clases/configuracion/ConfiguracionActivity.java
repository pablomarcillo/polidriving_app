//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.configuracion;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import com.polidriving.mobile.R;
import android.content.Context;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.view.View;
import android.os.Bundle;

/**
 * @noinspection deprecation
 */
@SuppressLint({"UseSwitchCompatOrMaterialCode", "CommitPrefEdits"})
public class ConfiguracionActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    Button guardarConfiguracionesSet;
    Boolean get_set_MuyAlta = false;
    Boolean get_set_Media = false;
    Boolean get_set_Alta = false;
    Boolean get_set_Baja = false;
    Switch muyAlta;
    Switch media;
    Switch alta;
    Switch baja;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        //Inicializando las variables con los elementos de la actividad
        guardarConfiguracionesSet = findViewById(R.id.botonIrConfiguracionSet);
        muyAlta = findViewById(R.id.switchMuyAlto);
        media = findViewById(R.id.switchMedio);
        alta = findViewById(R.id.switchAlto);
        baja = findViewById(R.id.switchBajo);
        cargarConfiguracion();

        //Creando el evento click en un botón inicializado
        guardarConfiguracionesSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Llamando al método para guardar la configuración
                guardarConfiguracion();
                //Redirigiendo al usuario a la actividad de principal de cuenta
                onBackPressed();
            }
        });

        //Creando el evento click en un botón inicializado
        muyAlta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Verificando si el elemento esta activado o desactivado
                if (muyAlta.isChecked()) {
                    //Cambiando la información visual del elemento
                    muyAlta.setText(getString(R.string.activada));
                    //Cambiando la información a almacenar
                    get_set_MuyAlta = true;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_muy_altas) + " " + getString(R.string.activada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                } else {
                    //Cambiando la información visual del elemento
                    muyAlta.setText(getString(R.string.desactivada));
                    //Cambiando la información a almacenar
                    get_set_MuyAlta = false;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_muy_altas) + " " + getString(R.string.desactivada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Creando el evento click en un botón inicializado
        media.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Verificando si el elemento esta activado o desactivado
                if (media.isChecked()) {
                    //Cambiando la información visual del elemento
                    media.setText(getString(R.string.activada));
                    //Cambiando la información a almacenar
                    get_set_Media = true;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_medias) + " " + getString(R.string.activada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                } else {
                    //Cambiando la información visual del elemento
                    media.setText(getString(R.string.desactivada));
                    //Cambiando la información a almacenar
                    get_set_Media = false;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_medias) + " " + getString(R.string.desactivada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Creando el evento click en un botón inicializado
        alta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Verificando si el elemento esta activado o desactivado
                if (alta.isChecked()) {
                    //Cambiando la información visual del elemento
                    alta.setText(getString(R.string.activada));
                    //Cambiando la información a almacenar
                    get_set_Alta = true;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_altas) + " " + getString(R.string.activada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                } else {
                    //Cambiando la información visual del elemento
                    alta.setText(getString(R.string.desactivada));
                    //Cambiando la información a almacenar
                    get_set_Alta = false;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_altas) + " " + getString(R.string.desactivada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Creando el evento click en un botón inicializado
        baja.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Verificando si el elemento esta activado o desactivado
                if (baja.isChecked()) {
                    //Cambiando la información visual del elemento
                    baja.setText(getString(R.string.activada));
                    //Cambiando la información a almacenar
                    get_set_Baja = true;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_bajas) + " " + getString(R.string.activada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                } else {
                    //Cambiando la información visual del elemento
                    baja.setText(getString(R.string.desactivada));
                    //Cambiando la información a almacenar
                    get_set_Baja = false;
                    //Mensaje emergente de información al usuario
                    String informacion = getString(R.string.alertas_bajas) + " " + getString(R.string.desactivada);
                    Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }

    private void guardarConfiguracion() {
        //Verificación o Creación del archivo local de configuraciones de Usuario
        SharedPreferences configuracionUsuario = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        //Obteniendo los datos a almacenar
        Boolean alertaMuyAlta = get_set_MuyAlta;
        Boolean alertaMedia = get_set_Media;
        Boolean alertaAlta = get_set_Alta;
        Boolean alertaBaja = get_set_Baja;
        //Creando un objeto tipo editor que permite modificar el archivo Preferencias creado
        SharedPreferences.Editor editar = configuracionUsuario.edit();
        editar.putBoolean("Alerta Muy Alta", alertaMuyAlta);
        editar.putBoolean("Alerta Media", alertaMedia);
        editar.putBoolean("Alerta Alta", alertaAlta);
        editar.putBoolean("Alerta Baja", alertaBaja);
        editar.apply();
        //Mensaje emergente de información al usuario
        String informacion = getString(R.string.configuracion_guardar);
        Toast.makeText(getApplicationContext(), informacion, Toast.LENGTH_SHORT).show();
    }

    private void cargarConfiguracion() {
        //Verificación o Creación del archivo local de configuraciones de Usuario
        SharedPreferences configuracionUsuario = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        //Obteniendo los datos a presentar
        boolean alertaMuyAlta = configuracionUsuario.getBoolean("Alerta Muy Alta", false);
        boolean alertaMedia = configuracionUsuario.getBoolean("Alerta Media", false);
        boolean alertaAlta = configuracionUsuario.getBoolean("Alerta Alta", false);
        boolean alertaBaja = configuracionUsuario.getBoolean("Alerta Baja", false);
        //Cambiando la información visual de los elementos
        muyAlta.setChecked(alertaMuyAlta);
        get_set_MuyAlta = alertaMuyAlta;
        media.setChecked(alertaMedia);
        get_set_Media = alertaMedia;
        alta.setChecked(alertaAlta);
        baja.setChecked(alertaBaja);
        get_set_Alta = alertaAlta;
        get_set_Baja = alertaBaja;
        //Verificación texto a presentar Alerta Activada o Desactivada
        if (alertaMuyAlta) {
            muyAlta.setText(getString(R.string.activada));
        } else {
            muyAlta.setText(getString(R.string.desactivada));
        }
        if (alertaMedia) {
            media.setText(getString(R.string.activada));
        } else {
            media.setText(getString(R.string.desactivada));
        }
        if (alertaAlta) {
            alta.setText(getString(R.string.activada));
        } else {
            alta.setText(getString(R.string.desactivada));
        }
        if (alertaBaja) {
            baja.setText(getString(R.string.activada));
        } else {
            baja.setText(getString(R.string.desactivada));
        }
    }

    protected void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        //TODO
    }
}