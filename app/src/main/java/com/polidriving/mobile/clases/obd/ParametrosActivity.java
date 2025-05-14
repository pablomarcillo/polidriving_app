//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.obd;

//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para la presentación de información al usuario
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el mapeo de cadenas
import androidx.appcompat.app.AppCompatActivity;
import com.polidriving.mobile.R;
import android.widget.CheckBox;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import android.os.Bundle;

@SuppressWarnings("unchecked")
public class ParametrosActivity extends AppCompatActivity {
    private ArrayList<String> chosenParams = new ArrayList<>();
    private CheckBox engineLoad, rpm, throttlePosition;
    private CheckBox permanentTroubleCodes;
    private int chosenParamsAmount = 0;
    private CheckBox coolantTemp;
    private CheckBox speed;

    private void removeParam(String paramName, CheckBox parentBox) {
        if (chosenParamsAmount > 1) {
            chosenParams.remove(paramName);
            chosenParamsAmount--;
            parentBox.setChecked(false);
        } else {
            Toast.makeText(ParametrosActivity.this, "There must be at least one parameter to display", Toast.LENGTH_LONG).show();
            parentBox.setChecked(true);
        }
    }

    private void addParam(String paramName, CheckBox parentBox) {
        if (chosenParamsAmount <= 5) {
            chosenParams.add(paramName);
            chosenParamsAmount++;
            parentBox.setChecked(true);
        } else {
            Toast.makeText(ParametrosActivity.this, "Can't add more than 5 parameters", Toast.LENGTH_LONG).show();
            parentBox.setChecked(false);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametros);

        Intent oncomingIntent = getIntent();
        chosenParams = (ArrayList<String>) oncomingIntent.getSerializableExtra("currentlySelectedParameters");
        chosenParamsAmount = oncomingIntent.getIntExtra("currentParametersAmount", 3);

        //Selección Checkboxes
        // Speed (1)
        speed = findViewById(R.id.cbSpeed);
        // Command (1)
        permanentTroubleCodes = findViewById(R.id.cbPermanentTroubleCodes);
        // Engine (3)
        engineLoad = findViewById(R.id.cbEngineLoad);
        rpm = findViewById(R.id.cbRpm);
        throttlePosition = findViewById(R.id.cbThrottlePosition);
        // Temperature (1)
        coolantTemp = findViewById(R.id.cbCoolantTemp);

        for (String paramName : chosenParams) {
            switch (paramName) {
                // Speed (1)
                case "Vehicle Speed":
                    speed.setChecked(true);
                    break;
                // Command (1)
                case "Permanent Trouble Codes":
                    permanentTroubleCodes.setChecked(true);
                    break;
                // Engine (3)
                case "Engine Load":
                    engineLoad.setChecked(true);
                    break;
                case "Engine RPM":
                    rpm.setChecked(true);
                    break;
                case "Throttle Position":
                    throttlePosition.setChecked(true);
                    break;
                // Temperature (3)
                case "Engine Coolant Temperature":
                    coolantTemp.setChecked(true);
                    break;
            }
        }

        // Speed (1)
        // Selecciona parámetro 'Velocidad'.
        speed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (speed.isChecked()) {
                addParam("Vehicle Speed", speed);
            } else {
                removeParam("Vehicle Speed", speed);
            }
        });

        // Command (1)
        // Selecciona parámetro 'Permanent Trouble Codes'.
        permanentTroubleCodes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (permanentTroubleCodes.isChecked()) {
                addParam("Permanent Trouble Codes", permanentTroubleCodes);
            } else {
                removeParam("Permanent Trouble Codes", permanentTroubleCodes);
            }
        });

        // Engine (3)
        // Selecciona parámetro 'Carga del Motor'.
        engineLoad.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (engineLoad.isChecked()) {
                addParam("Engine Load", engineLoad);
            } else {
                removeParam("Engine Load", engineLoad);
            }
        });

        // Selecciona parámetro 'RPM'.
        rpm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (rpm.isChecked()) {
                addParam("Engine RPM", rpm);
            } else {
                removeParam("Engine RPM", rpm);
            }
        });

        // Selecciona parámetro 'Posición del Acelerador' (Controla la entrada de aire del motor).
        throttlePosition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (throttlePosition.isChecked()) {
                addParam("Throttle Position", throttlePosition);
            } else {
                removeParam("Throttle Position", throttlePosition);
            }
        });

        // Temperature (1)
        // Selecciona parámetro 'Temperatura del Refrigerante'.
        coolantTemp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (coolantTemp.isChecked()) {
                addParam("Engine Coolant Temperature", coolantTemp);
            } else {
                removeParam("Engine Coolant Temperature", coolantTemp);
            }
        });
        Button bSave = findViewById(R.id.bDone);
        bSave.setOnClickListener(e -> finishAction());
    }

    public void finishAction() {
        Intent output = new Intent();
        output.putExtra("parametersAmount", chosenParamsAmount);
        output.putExtra("parameters", chosenParams);
        setResult(RESULT_OK, output);
        finish();
    }
}