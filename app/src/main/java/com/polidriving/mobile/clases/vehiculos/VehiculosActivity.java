//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.vehiculos;

//Clases usadas para la presentación de información al usuario
//Clases usadas para consultas en la base de datos
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases

import com.polidriving.mobile.base_datos.DataBaseAccess;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.ColorDrawable;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.app.AlertDialog;
import com.polidriving.mobile.R;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.content.Intent;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.auth.AuthUserAttribute;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Actividad para la gestión de vehículos del usuario.
 * 
 * MEJORAS IMPLEMENTADAS:
 * - Gestión correcta del ciclo de vida de la actividad
 * - Sincronización adecuada entre operaciones asíncronas
 * - Prevención de memory leaks y crashes por operaciones en actividades destruidas
 * - Carga de datos después de obtener el correo del usuario autenticado
 * - Validación de estado de actividad antes de actualizar UI
 */

@SuppressLint("SetTextI18n")
@SuppressWarnings({"ConstantConditions", "RegExpRedundantEscape", "deprecation"})
public class VehiculosActivity extends AppCompatActivity {
    //Creación de variables para enviar, presentar y recibir información
    private RecyclerView recyclerViewVehiculos;
    private VehiculosAdapter vehiculosAdapter;
    private List<Vehiculo> listaVehiculos;
    private DataBaseAccess dataBaseAccess;
    private Button btnAgregarVehiculo;
    private String correoUsuario;
    private boolean isActivityDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);

        // Configurar la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().setIcon(R.mipmap.ic_menu_header);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("Configuración vehículo");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }

        // Inicializar variables
        initializeViews();
        
        // Obtener el correo del usuario autenticado
        obtenerCorreoUsuario();
        
        // Inicializar base de datos
        dataBaseAccess = new DataBaseAccess();
        
        // Configurar RecyclerView
        setupRecyclerView();
        
        // Configurar botón agregar
        setupAddButton();
        
        // NO cargar vehículos aquí - se cargará después de obtener el correo
        // cargarVehiculos(); // REMOVIDO - se llama desde obtenerCorreoUsuario()
    }

    private void initializeViews() {
        recyclerViewVehiculos = findViewById(R.id.recyclerViewVehiculos);
        btnAgregarVehiculo = findViewById(R.id.btnAgregarVehiculo);
        listaVehiculos = new ArrayList<>();
    }

    private void obtenerCorreoUsuario() {
        Amplify.Auth.getCurrentUser(
            user -> {
                if (isActivityDestroyed) return; // Evitar operaciones si la actividad fue destruida
                
                // Obtener el correo electrónico real del usuario
                Amplify.Auth.fetchUserAttributes(
                    attributes -> {
                        if (isActivityDestroyed) return; // Verificar nuevamente
                        
                        for (AuthUserAttribute attribute : attributes) {
                            if (attribute.getKey().getKeyString().equals("email")) {
                                correoUsuario = attribute.getValue();
                                Log.d("VehiculosActivity", "Correo del usuario: " + correoUsuario);
                                break;
                            }
                        }
                        // Si no se encuentra email, usar username como fallback
                        if (correoUsuario == null || correoUsuario.isEmpty()) {
                            correoUsuario = user.getUsername();
                            Log.d("VehiculosActivity", "Usando username como fallback: " + correoUsuario);
                        }
                        
                        // IMPORTANTE: Cargar vehículos DESPUÉS de obtener el correo
                        cargarVehiculos();
                    },
                    error -> {
                        if (isActivityDestroyed) return;
                        
                        Log.e("VehiculosActivity", "Error obteniendo atributos: " + error.toString());
                        // Usar username como fallback en caso de error
                        correoUsuario = user.getUsername();
                        Log.d("VehiculosActivity", "Usando username por error: " + correoUsuario);
                        
                        // Cargar vehículos incluso con error de atributos
                        cargarVehiculos();
                    }
                );
            },
            error -> {
                if (isActivityDestroyed) return;
                
                Log.e("VehiculosActivity", "Error obteniendo usuario: " + error.toString());
                runOnUiThread(() -> {
                    if (!isActivityDestroyed) {
                        Toast.makeText(this, "Error obteniendo información del usuario", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        );
    }

    private void setupRecyclerView() {
        vehiculosAdapter = new VehiculosAdapter(listaVehiculos, new VehiculosAdapter.OnVehiculoListener() {
            @Override
            public void onEditClick(Vehiculo vehiculo) {
                mostrarDialogoEditarVehiculo(vehiculo);
            }

            @Override
            public void onDeleteClick(Vehiculo vehiculo) {
                confirmarEliminacionVehiculo(vehiculo);
            }

            @Override
            public void onSelectClick(Vehiculo vehiculo) {
                establecerVehiculoActual(vehiculo);
            }
        });
        
        recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVehiculos.setAdapter(vehiculosAdapter);
    }

    private void setupAddButton() {
        btnAgregarVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAgregarVehiculo();
            }
        });
    }

    private void cargarVehiculos() {
        if (correoUsuario != null && isActivityActive()) {
            new Thread(() -> {
                try {
                    // Verificar nuevamente si la actividad sigue activa antes de procesar
                    if (!isActivityActive()) {
                        Log.d("VehiculosActivity", "Actividad no activa, cancelando carga de vehículos");
                        return;
                    }
                    
                    List<Vehiculo> vehiculos = dataBaseAccess.obtenerVehiculosUsuarioLista(correoUsuario);
                    
                    // Verificar si la actividad sigue activa antes de actualizar UI
                    runOnUiThreadSafely(() -> {
                        if (vehiculosAdapter != null && listaVehiculos != null) {
                            listaVehiculos.clear();
                            listaVehiculos.addAll(vehiculos);
                            vehiculosAdapter.notifyDataSetChanged();
                            Log.d("VehiculosActivity", "Vehículos cargados: " + vehiculos.size());
                        }
                    });
                } catch (Exception e) {
                    Log.e("VehiculosActivity", "Error cargando vehículos", e);
                    runOnUiThreadSafely(() -> {
                        if (isActivityActive()) {
                            Toast.makeText(VehiculosActivity.this, "Error cargando vehículos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        } else {
            Log.d("VehiculosActivity", "No se puede cargar vehículos - correoUsuario: " + correoUsuario + ", isDestroyed: " + isActivityDestroyed);
        }
    }

    private void mostrarDialogoAgregarVehiculo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vehiculo, null);
        builder.setView(dialogView);
        
        // No establecer título aquí ya que está en el layout
        
        // Obtener referencias a los EditText
        AutoCompleteTextView etMarca = dialogView.findViewById(R.id.etMarca);
        EditText etModelo = dialogView.findViewById(R.id.etModelo);
        EditText etAno = dialogView.findViewById(R.id.etAno);
        AutoCompleteTextView etTipoCombustible = dialogView.findViewById(R.id.etTipoCombustible);
        EditText etCilindraje = dialogView.findViewById(R.id.etCilindraje);
        AutoCompleteTextView etTipoCarroceria = dialogView.findViewById(R.id.etTipoCarroceria);
        AutoCompleteTextView etTransmision = dialogView.findViewById(R.id.etTransmision);
        AutoCompleteTextView etTraccion = dialogView.findViewById(R.id.etTraccion);
        AutoCompleteTextView etColor = dialogView.findViewById(R.id.etColor);
        EditText etPlaca = dialogView.findViewById(R.id.etPlaca);

        // Configurar listas desplegables
        configurarListasFormulario(dialogView);

        builder.setPositiveButton(getString(R.string.guardar), null); // Usar null para manejar manualmente
        
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Validar campos obligatorios
                        if (validarCampos(etMarca, etModelo, etAno, etPlaca) && 
                            validarCamposOpcionales(etCilindraje, etTipoCombustible, etTipoCarroceria, etTransmision, etTraccion, etColor)) {
                            
                            Vehiculo vehiculo = new Vehiculo();
                            vehiculo.setCorreoUsuario(correoUsuario);
                            vehiculo.setMarca(etMarca.getText().toString().trim());
                            vehiculo.setModelo(etModelo.getText().toString().trim());
                            vehiculo.setAnoFabricacion(etAno.getText().toString().trim());
                            vehiculo.setTipoCombustible(etTipoCombustible.getText().toString().trim());
                            vehiculo.setCilindraje(etCilindraje.getText().toString().trim());
                            vehiculo.setTipoCarroceria(etTipoCarroceria.getText().toString().trim());
                            vehiculo.setTransmision(etTransmision.getText().toString().trim());
                            vehiculo.setTraccion(etTraccion.getText().toString().trim());
                            vehiculo.setColor(etColor.getText().toString().trim());
                            vehiculo.setPlaca(etPlaca.getText().toString().trim().toUpperCase()); // Normalizar a mayúsculas
                            vehiculo.setActual(false); // Por defecto no es el actual

                            guardarVehiculo(vehiculo);
                            dialog.dismiss();
                        }
                        // Si hay errores, no cerrar el diálogo
                    }
                });
            }
        });

        builder.setNegativeButton(getString(R.string.cancelar), null);
        dialog.show();
    }

    private void mostrarDialogoEditarVehiculo(Vehiculo vehiculo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vehiculo, null);
        builder.setView(dialogView);

        // Obtener referencias a los EditText y llenarlos con datos existentes
        AutoCompleteTextView etMarca = dialogView.findViewById(R.id.etMarca);
        EditText etModelo = dialogView.findViewById(R.id.etModelo);
        EditText etAno = dialogView.findViewById(R.id.etAno);
        AutoCompleteTextView etTipoCombustible = dialogView.findViewById(R.id.etTipoCombustible);
        EditText etCilindraje = dialogView.findViewById(R.id.etCilindraje);
        AutoCompleteTextView etTipoCarroceria = dialogView.findViewById(R.id.etTipoCarroceria);
        AutoCompleteTextView etTransmision = dialogView.findViewById(R.id.etTransmision);
        AutoCompleteTextView etTraccion = dialogView.findViewById(R.id.etTraccion);
        AutoCompleteTextView etColor = dialogView.findViewById(R.id.etColor);
        EditText etPlaca = dialogView.findViewById(R.id.etPlaca);

        // Llenar campos con datos actuales
        etMarca.setText(vehiculo.getMarca());
        etModelo.setText(vehiculo.getModelo());
        etAno.setText(vehiculo.getAnoFabricacion());
        etTipoCombustible.setText(vehiculo.getTipoCombustible());
        etCilindraje.setText(vehiculo.getCilindraje());
        etTipoCarroceria.setText(vehiculo.getTipoCarroceria());
        etTransmision.setText(vehiculo.getTransmision());
        etTraccion.setText(vehiculo.getTraccion());
        etColor.setText(vehiculo.getColor());
        etPlaca.setText(vehiculo.getPlaca());

        // Configurar listas desplegables
        configurarListasFormulario(dialogView);

        builder.setPositiveButton(getString(R.string.guardar), null); // Usar null para manejar manualmente
        
        AlertDialog dialogEdit = builder.create();
        dialogEdit.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialogEdit.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validarCampos(etMarca, etModelo, etAno, etPlaca) && 
                            validarCamposOpcionales(etCilindraje, etTipoCombustible, etTipoCarroceria, etTransmision, etTraccion, etColor)) {
                            
                            vehiculo.setMarca(etMarca.getText().toString().trim());
                            vehiculo.setModelo(etModelo.getText().toString().trim());
                            vehiculo.setAnoFabricacion(etAno.getText().toString().trim());
                            vehiculo.setTipoCombustible(etTipoCombustible.getText().toString().trim());
                            vehiculo.setCilindraje(etCilindraje.getText().toString().trim());
                            vehiculo.setTipoCarroceria(etTipoCarroceria.getText().toString().trim());
                            vehiculo.setTransmision(etTransmision.getText().toString().trim());
                            vehiculo.setTraccion(etTraccion.getText().toString().trim());
                            vehiculo.setColor(etColor.getText().toString().trim());
                            vehiculo.setPlaca(etPlaca.getText().toString().trim().toUpperCase()); // Normalizar a mayúsculas

                            actualizarVehiculo(vehiculo);
                            dialogEdit.dismiss();
                        }
                        // Si hay errores, no cerrar el diálogo
                    }
                });
            }
        });

        builder.setNegativeButton(getString(R.string.cancelar), null);
        dialogEdit.show();
    }

    private boolean validarCampos(EditText etMarca, EditText etModelo, EditText etAno, EditText etPlaca) {
        // Validar marca
        String marca = etMarca.getText().toString().trim();
        if (marca.isEmpty()) {
            etMarca.setError(getString(R.string.falta_informacion));
            return false;
        }
        if (marca.length() < 2 || marca.length() > 50) {
            etMarca.setError("La marca debe tener entre 2 y 50 caracteres");
            return false;
        }
        if (!marca.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s\\-]+$")) {
            etMarca.setError("La marca solo puede contener letras, espacios y guiones");
            return false;
        }

        // Validar modelo
        String modelo = etModelo.getText().toString().trim();
        if (modelo.isEmpty()) {
            etModelo.setError(getString(R.string.falta_informacion));
            return false;
        }
        if (modelo.length() < 1 || modelo.length() > 50) {
            etModelo.setError("El modelo debe tener entre 1 y 50 caracteres");
            return false;
        }
        if (!modelo.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\-\\.]+$")) {
            etModelo.setError("El modelo solo puede contener letras, números, espacios, guiones y puntos");
            return false;
        }

        // Validar año
        String ano = etAno.getText().toString().trim();
        if (ano.isEmpty()) {
            etAno.setError(getString(R.string.falta_informacion));
            return false;
        }
        try {
            int anoInt = Integer.parseInt(ano);
            int anoActual = Calendar.getInstance().get(Calendar.YEAR);
            if (anoInt < 1950 || anoInt > anoActual + 1) {
                etAno.setError("El año debe estar entre 1950 y " + (anoActual + 1));
                return false;
            }
        } catch (NumberFormatException e) {
            etAno.setError("Ingrese un año válido");
            return false;
        }

        // Validar placa
        String placa = etPlaca.getText().toString().trim().toUpperCase();
        if (placa.isEmpty()) {
            etPlaca.setError(getString(R.string.falta_informacion));
            return false;
        }
        // Formato ecuatoriano: ABC-1234 o ABC1234
        if (!placa.matches("^[A-Z]{3}-?[0-9]{3,4}$")) {
            etPlaca.setError("Formato de placa inválido (ej: ABC-1234 o ABC1234)");
            return false;
        }

        return true;
    }

    private boolean validarCamposOpcionales(EditText etCilindraje, EditText etTipoCombustible, 
                                          EditText etTipoCarroceria, EditText etTransmision, 
                                          EditText etTraccion, EditText etColor) {
        // Validar cilindraje si no está vacío
        String cilindraje = etCilindraje.getText().toString().trim();
        if (!cilindraje.isEmpty()) {
            try {
                int cilindrajeInt = Integer.parseInt(cilindraje);
                if (cilindrajeInt < 100 || cilindrajeInt > 8000) {
                    etCilindraje.setError("El cilindraje debe estar entre 100cc y 8000cc");
                    return false;
                }
            } catch (NumberFormatException e) {
                etCilindraje.setError("Ingrese un cilindraje válido");
                return false;
            }
        }

        // Validar que los campos de lista desplegable tengan valores válidos si no están vacíos
        String tipoCombustible = etTipoCombustible.getText().toString().trim();
        if (!tipoCombustible.isEmpty()) {
            String[] tiposValidos = {"Gasolina", "Diésel", "Súper", "Eléctrico", "Híbrido"};
            boolean esValido = false;
            for (String tipo : tiposValidos) {
                if (tipo.equalsIgnoreCase(tipoCombustible)) {
                    esValido = true;
                    break;
                }
            }
            if (!esValido) {
                etTipoCombustible.setError("Seleccione un tipo de combustible válido");
                return false;
            }
        }

        return true;
    }

    private void guardarVehiculo(Vehiculo vehiculo) {
        new Thread(() -> {
            try {
                CharSequence resultado = dataBaseAccess.agregarVehiculo(
                    vehiculo.getPlaca(),
                    vehiculo.getMarca(),
                    vehiculo.getModelo(),
                    vehiculo.getAnoFabricacion(),
                    vehiculo.getTipoCombustible(),
                    vehiculo.getCilindraje(),
                    vehiculo.getTipoCarroceria(),
                    vehiculo.getTransmision(),
                    vehiculo.getTraccion(),
                    vehiculo.getColor(),
                    vehiculo.getCorreoUsuario()
                );
                
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, getString(R.string.vehiculo_guardado), Toast.LENGTH_SHORT).show();
                            cargarVehiculos();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("VehiculosActivity", "Error guardando vehículo", e);
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, getString(R.string.error_guardar_vehiculo), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void actualizarVehiculo(Vehiculo vehiculo) {
        new Thread(() -> {
            try {
                CharSequence resultado = dataBaseAccess.actualizarVehiculo(
                    vehiculo.getPlaca(),
                    vehiculo.getMarca(),
                    vehiculo.getModelo(),
                    vehiculo.getAnoFabricacion(),
                    vehiculo.getTipoCombustible(),
                    vehiculo.getCilindraje(),
                    vehiculo.getTipoCarroceria(),
                    vehiculo.getTransmision(),
                    vehiculo.getTraccion(),
                    vehiculo.getColor(),
                    correoUsuario  // Usar el correo del usuario autenticado, no del vehículo
                );
                
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, getString(R.string.vehiculo_actualizado), Toast.LENGTH_SHORT).show();
                            cargarVehiculos();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("VehiculosActivity", "Error actualizando vehículo", e);
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, getString(R.string.error_guardar_vehiculo), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void confirmarEliminacionVehiculo(Vehiculo vehiculo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.eliminar_vehiculo));
        builder.setMessage(getString(R.string.confirmar_eliminar));
        
        builder.setPositiveButton(getString(R.string.eliminar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarVehiculo(vehiculo);
            }
        });
        
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }

    private void eliminarVehiculo(Vehiculo vehiculo) {
        new Thread(() -> {
            try {
                CharSequence resultado = dataBaseAccess.eliminarVehiculo(vehiculo.getPlaca(), correoUsuario);
                
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, getString(R.string.vehiculo_eliminado), Toast.LENGTH_SHORT).show();
                            cargarVehiculos();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("VehiculosActivity", "Error eliminando vehículo", e);
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, "Error eliminando vehículo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void establecerVehiculoActual(Vehiculo vehiculo) {
        new Thread(() -> {
            try {
                CharSequence resultado = dataBaseAccess.establecerVehiculoActual(vehiculo.getPlaca(), correoUsuario);
                
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, getString(R.string.vehiculo_seleccionado), Toast.LENGTH_SHORT).show();
                            cargarVehiculos();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("VehiculosActivity", "Error estableciendo vehículo actual", e);
                if (!isActivityDestroyed && !isFinishing()) {
                    runOnUiThread(() -> {
                        if (!isActivityDestroyed) {
                            Toast.makeText(VehiculosActivity.this, "Error seleccionando vehículo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    // Métodos para configurar las listas desplegables
    private void configurarListasFormulario(View dialogView) {
        configurarMarcasAutoComplete(dialogView);
        configurarTipoCombustible(dialogView);
        configurarTipoCarroceria(dialogView);
        configurarTransmision(dialogView);
        configurarTraccion(dialogView);
        configurarColores(dialogView);
    }

    private void configurarMarcasAutoComplete(View dialogView) {
        AutoCompleteTextView etMarca = dialogView.findViewById(R.id.etMarca);
        String[] marcas = {
            "Chevrolet", "Kia", "Toyota", "Hyundai", "Nissan", "Honda", "Ford", 
            "Suzuki", "Mitsubishi", "Mazda", "Volkswagen", "Renault", "Peugeot",
            "BMW", "Mercedes-Benz", "Audi", "Subaru", "Isuzu", "Great Wall",
            "Jeep", "Dodge", "Chrysler", "Fiat", "Alfa Romeo", "Volvo",
            "Chery", "JAC", "BYD", "Geely", "MG", "DFSK"
        };
        ArrayAdapter<String> adapterMarcas = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, marcas);
        etMarca.setAdapter(adapterMarcas);
        etMarca.setThreshold(1); // Mostrar sugerencias desde el primer carácter
    }

    private void configurarTipoCombustible(View dialogView) {
        AutoCompleteTextView etTipoCombustible = dialogView.findViewById(R.id.etTipoCombustible);
        String[] tiposCombustible = {"Gasolina", "Diésel", "Súper", "Eléctrico", "Híbrido"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposCombustible);
        etTipoCombustible.setAdapter(adapter);
        etTipoCombustible.setThreshold(1);
        etTipoCombustible.setOnClickListener(v -> etTipoCombustible.showDropDown());
        etTipoCombustible.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etTipoCombustible.showDropDown();
            }
        });
    }

    private void configurarTipoCarroceria(View dialogView) {
        AutoCompleteTextView etTipoCarroceria = dialogView.findViewById(R.id.etTipoCarroceria);
        String[] tiposCarroceria = {
            "Sedán", "Hatchback", "SUV", "Camioneta", "Furgoneta", "Coupe", 
            "Convertible", "Pick-Up", "Station Wagon", "Bus", "Camión"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposCarroceria);
        etTipoCarroceria.setAdapter(adapter);
        etTipoCarroceria.setOnClickListener(v -> etTipoCarroceria.showDropDown());
    }

    private void configurarTransmision(View dialogView) {
        AutoCompleteTextView etTransmision = dialogView.findViewById(R.id.etTransmision);
        String[] transmisiones = {"Manual", "Automática", "Automatizada", "CVT", "Doble embrague"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, transmisiones);
        etTransmision.setAdapter(adapter);
        etTransmision.setOnClickListener(v -> etTransmision.showDropDown());
    }

    private void configurarTraccion(View dialogView) {
        AutoCompleteTextView etTraccion = dialogView.findViewById(R.id.etTraccion);
        String[] tracciones = {"4x2 (delantera)", "4x2 (trasera)", "4x4", "AWD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tracciones);
        etTraccion.setAdapter(adapter);
        etTraccion.setOnClickListener(v -> etTraccion.showDropDown());
    }

    private void configurarColores(View dialogView) {
        AutoCompleteTextView etColor = dialogView.findViewById(R.id.etColor);
        String[] colores = {
            "Blanco", "Negro", "Gris", "Rojo", "Azul", "Plateado", 
            "Verde", "Amarillo", "Café", "Beige", "Naranja"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, colores);
        etColor.setAdapter(adapter);
        etColor.setOnClickListener(v -> etColor.showDropDown());
    }

    /**
     * Método para verificar si es seguro ejecutar operaciones de UI
     * @return true si la actividad está activa y puede actualizar la UI
     */
    private boolean isActivityActive() {
        return !isActivityDestroyed && !isFinishing();
    }

    /**
     * Método para ejecutar operaciones en el hilo principal de forma segura
     * @param runnable operación a ejecutar
     */
    private void runOnUiThreadSafely(Runnable runnable) {
        if (isActivityActive()) {
            runOnUiThread(() -> {
                if (isActivityActive()) {
                    runnable.run();
                }
            });
        }
    }

    /**
     * Método para ejecutar operaciones de base de datos con timeout
     * @param operation operación a ejecutar
     * @param timeoutMillis tiempo máximo de espera en milisegundos
     */
    private void ejecutarOperacionBDConTimeout(Runnable operation, long timeoutMillis) {
        Thread dbThread = new Thread(() -> {
            try {
                if (isActivityActive()) {
                    operation.run();
                }
            } catch (Exception e) {
                Log.e("VehiculosActivity", "Error en operación de BD", e);
            }
        });
        
        dbThread.start();
        
        // Crear un timeout thread
        Thread timeoutThread = new Thread(() -> {
            try {
                Thread.sleep(timeoutMillis);
                if (dbThread.isAlive()) {
                    Log.w("VehiculosActivity", "Operación de BD excedió timeout de " + timeoutMillis + "ms");
                    dbThread.interrupt();
                }
            } catch (InterruptedException e) {
                // El timeout thread fue interrumpido, esto es normal
            }
        });
        
        timeoutThread.start();
    }

    @Override
    protected void onDestroy() {
        // Marcar como destruida primero para evitar operaciones asíncronas
        isActivityDestroyed = true;
        
        // Llamar a super.onDestroy() ANTES de limpiar referencias
        super.onDestroy();
        
        // Limpiar referencias para prevenir memory leaks
        try {
            if (vehiculosAdapter != null) {
                vehiculosAdapter = null;
            }
            if (listaVehiculos != null) {
                listaVehiculos.clear();
                listaVehiculos = null;
            }
            dataBaseAccess = null;
            correoUsuario = null;
            
            Log.d("VehiculosActivity", "Activity destruida y limpiada");
        } catch (Exception e) {
            Log.e("VehiculosActivity", "Error limpiando referencias en onDestroy", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("VehiculosActivity", "Activity pausada");
        
        // Pausar operaciones asíncronas para evitar problemas
        // No marcamos como destruida aquí porque puede reanudarse
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("VehiculosActivity", "Activity detenida");
        
        // Detener operaciones no críticas cuando la actividad no es visible
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("VehiculosActivity", "Activity reanudada");
        
        // Recargar vehículos si ya tenemos el correo del usuario
        if (correoUsuario != null && !correoUsuario.isEmpty()) {
            cargarVehiculos();
        }
    }

    @Override
    public void onBackPressed() {
        // Asegurar una navegación correcta al salir
        try {
            // Marcar actividad como destruida para detener operaciones asíncronas
            isActivityDestroyed = true;
            
            // Finalizar la actividad de manera controlada
            super.onBackPressed();
        } catch (Exception e) {
            Log.e("VehiculosActivity", "Error en onBackPressed", e);
            // En caso de error, forzar el finish de manera segura
            try {
                finish();
            } catch (Exception ex) {
                Log.e("VehiculosActivity", "Error crítico en onBackPressed", ex);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Manejar la navegación hacia atrás desde la barra de acción
        try {
            isActivityDestroyed = true;
            onBackPressed();
            return true;
        } catch (Exception e) {
            Log.e("VehiculosActivity", "Error en onSupportNavigateUp", e);
            try {
                finish();
                return true;
            } catch (Exception ex) {
                Log.e("VehiculosActivity", "Error crítico en onSupportNavigateUp", ex);
                return super.onSupportNavigateUp();
            }
        }
    }
}
