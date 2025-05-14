//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.menu;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import com.polidriving.mobile.databinding.ActivityMenuBinding;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.polidriving.mobile.R;
import android.os.Bundle;

public class MenuLateralActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    private AppBarConfiguration configuracionLateral;
    //Creación del concatenador del menú lateral con las actividades del menú lateral
    private ActivityMenuBinding vinculador;

    protected void onCreate(Bundle savedInstanceState) {
        //Creación y presentación visual de la actividad
        super.onCreate(savedInstanceState);
        //Mostrando en pantalla el menú lateral
        vinculador = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(vinculador.getRoot());
        //Mostrando en pantalla el encabezado del menú lateral
        setSupportActionBar(vinculador.appBarMenuLateral.toolbar);
        //Instanciando las diferentes actividades vinculadas al menú lateral
        DrawerLayout fragmento = vinculador.drawerLayout;
        NavigationView vista = vinculador.navView;
        //Vinculando las instancias al menú lateral
        configuracionLateral = new AppBarConfiguration.Builder(R.id.perfil_de_usuario, R.id.configuracion_usuario, R.id.condiciones_usuario, R.id.politicas_usuario, R.id.ayuda_comentarios, R.id.cerrar_sesion).setOpenableLayout(fragmento).build();
        NavController navController = Navigation.findNavController(this, R.id.contenedor_menu_lateral);
        NavigationUI.setupActionBarWithNavController(this, navController, configuracionLateral);
        NavigationUI.setupWithNavController(vista, navController);
    }

    public boolean onSupportNavigateUp() {
        //Iniciando el contenedor del menú lateral
        NavController navController = Navigation.findNavController(this, R.id.contenedor_menu_lateral);
        //Mostrando en pantalla el contenedor del menú lateral
        return NavigationUI.navigateUp(navController, configuracionLateral) || super.onSupportNavigateUp();
    }

    protected void onDestroy() {
        //Finaliza la interacción con la actividad
        super.onDestroy();
        vinculador = null;
    }
}