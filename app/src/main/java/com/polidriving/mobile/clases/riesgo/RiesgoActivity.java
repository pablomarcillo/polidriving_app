//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.riesgo;

//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
import androidx.appcompat.app.AppCompatActivity;
import com.polidriving.mobile.R;
import android.os.Bundle;

/**
 * @noinspection deprecation
 */
public class RiesgoActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riesgo);
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }
}