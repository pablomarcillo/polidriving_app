//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
import androidx.fragment.app.Fragment;
import android.os.Bundle;

public class PosicionFragmentoPestana extends Fragment {
    // Instancia usado para la selección del Fragmento
    public static Fragment newInstance(int index) {
        Fragment fragmento = null;
        switch (index) {
            case 1:
                //Escogiendo el fragmento con indice 1
                fragmento = new FragmentosRiesgos();
                break;
            case 2:
                //Escogiendo el fragmento con indice 2
                fragmento = new FragmentosVehiculo();
                break;
            case 3:
                //Escogiendo el fragmento con indice 3
                fragmento = new FragmentosClima();
                break;
            case 4:
                //Escogiendo el fragmento con indice 4
                fragmento = new FragmentosAccidentes();
                break;
        }
        //Retorna el fragmento escogido
        return fragmento;
    }

    public void onCreate(Bundle crearInstancias) {
        //Creación y presentación visual del fragmento en la  actividad principal
        super.onCreate(crearInstancias);
        //TODO
    }

    public void onDestroyView() {
        //Finaliza la vista del fragmento escogido
        super.onDestroyView();
        //TODO
    }
}