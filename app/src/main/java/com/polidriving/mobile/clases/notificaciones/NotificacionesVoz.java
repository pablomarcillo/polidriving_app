//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.notificaciones;

//Clases usadas para la presentación de información al usuario
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
//Clases usadas para el uso de voz
import android.speech.tts.TextToSpeech;
import android.content.Context;
import android.util.Log;
import java.util.Locale;

/**
 * @noinspection CallToPrintStackTrace
 */
@SuppressWarnings("deprecation")
public class NotificacionesVoz {
    //Creación de variables para enviar, presentar y recibir información
    private TextToSpeech textoVoz = null;
    private boolean cargado = false;

    //Inicialización del TTS (Text‑to‑Speech)
    private final TextToSpeech.OnInitListener conversor = new TextToSpeech.OnInitListener() {
        public void onInit(int estado) {
            //Selección del idioma y región
            Locale espanol = new Locale("es", "EC");
            //Verificación si el TTS esta iniciado
            if (estado == TextToSpeech.SUCCESS) {
                //Seteando el lenguaje deseado al convertidor
                int resultado = textoVoz.setLanguage(espanol);
                cargado = true;
                if (resultado == TextToSpeech.LANG_MISSING_DATA || resultado == TextToSpeech.LANG_NOT_SUPPORTED) {
                    //Mensaje de consola que informa al usuario que el idioma o región n son soportados
                    Log.e("Error.", "Lenguaje no permitido.");
                }
            } else {
                //Mensaje de error que indica que el TTS no se pudo iniciar
                Log.e("Error.", "Falló al iniciar.");
            }
        }
    };

    public void init(Context contexto) {
        //Segmento de código que permite la comunicación con las diferentes actividades de la aplicación
        try {
            textoVoz = new TextToSpeech(contexto, conversor);
        } catch (Exception e) {
            //Mensaje de consola que informa al usuario que no se pudo establecer la comunicación con las diferentes actividades de la aplicación
            e.printStackTrace();
        }
    }

    public void palabra(String frase) {
        if (cargado) {
            //Permite la conversión de texto a voz mediante el TTS
            textoVoz.speak(frase, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            //Mensaje de consola que indica que el TTS no se ha iniciado
            Log.e("Error,", "TTS no inicializado.");
        }
    }

    public void apagar() {
        //Método que permite finalizar las notificaciones por voz
        textoVoz.shutdown();
        //TODO
    }
}