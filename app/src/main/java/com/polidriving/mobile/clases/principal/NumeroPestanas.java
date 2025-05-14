//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.graphics.drawable.Drawable;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import com.polidriving.mobile.R;
import android.content.Context;
import android.util.TypedValue;
import android.text.Spanned;
import java.util.Objects;

@SuppressWarnings({"deprecation", "unused", "NullableProblems"})
public class NumeroPestanas extends FragmentPagerAdapter {
    //Variable que permite nombrar y poner un icono a cada pestaña en la actividad principal
    private static final int[] iconosPestana = new int[]{R.drawable.riesgo, R.drawable.vehiculo, R.drawable.lluvia, R.drawable.auto};
    private static final int[] nombrePestana = new int[]{R.string.tab_riesgos, R.string.tab_vehiculo, R.string.tab_clima, R.string.tab_accidentes};
    //Variable que permite obtener el fragmento de actividad que se presenta actualmente
    private final Context contexto;

    public NumeroPestanas(Context recibirContexto, FragmentManager esteFragmento) {
        //Método que permite establecer y recibir el fragmento de actividad y presentarlo en la actividad principal
        super(esteFragmento);
        contexto = recibirContexto;
    }

    public CharSequence getPageTitle(int posicion) {
        //Método que permite obtener el nombre y el icono del fragmento
        // Obtención de un arreglo de iconos
        Drawable icono = ContextCompat.getDrawable(contexto, iconosPestana[posicion]);
        // Convertir dp a píxeles
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, contexto.getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, contexto.getResources().getDisplayMetrics());
        // Seteando el bound de los iconos con una ubicación y tamaño definido
        Objects.requireNonNull(icono).setBounds(0, 0, width, height);
        // Iniciando el texto de la imagen
        SpannableString spannableString = new SpannableString(" ");
        // Juntando la imagen con el texto
        ImageSpan imageSpan = new ImageSpan(icono, ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Crea un SpannableString para el texto.
        String title = contexto.getResources().getString(nombrePestana[posicion]);
        SpannableString textSpannable = new SpannableString("\n" + title);
        // Combinando el icono y el texto en un SpannableStringBuilder
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(spannableString);
        spannableStringBuilder.append(textSpannable);
        // Retorna spannable string
        return spannableStringBuilder;
    }

    public Fragment getItem(int posicionPestana) {
        //Método que permite obtener el indice del fragmento actual
        return PosicionFragmentoPestana.newInstance(posicionPestana + 1);
    }

    public int getCount() {
        //Número de fragmentos en la actividad principal
        //TODO
        return 4;
    }
}