//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.privacidad;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
import androidx.appcompat.app.AppCompatActivity;
import com.polidriving.mobile.R;
import android.os.Bundle;

/**
 * @noinspection deprecation
 */
public class PrivacyActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
    }

    public boolean onSupportNavigateUp() {
        // Segmento de código que permite volver a la actividad anterior sim perder información
        onBackPressed();
        return false;
    }
}