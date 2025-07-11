package com.polidriving.mobile.clases.principal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.polidriving.mobile.R;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Gestor centralizado de alertas que controla la presentación de diálogos de alerta
 * para evitar múltiples alertas simultáneas y mantener un delay de 10 segundos
 * entre presentaciones.
 */
public class AlertManager {
    private static AlertManager instance;
    private boolean isAlertShowing = false;
    private long lastAlertDismissedTime = 0;
    private static final long ALERT_DELAY_MS = 10000; // 10 segundos
    private Queue<AlertRequest> pendingAlerts = new LinkedList<>();
    private AlertDialog currentDialog = null;
    
    /**
     * Clase interna que representa una solicitud de alerta
     */
    private static class AlertRequest {
        Context context;
        String tipo;
        String mensaje;
        
        AlertRequest(Context context, String tipo, String mensaje) {
            this.context = context;
            this.tipo = tipo;
            this.mensaje = mensaje;
        }
    }
    
    /**
     * Obtiene la instancia singleton del AlertManager
     */
    public static synchronized AlertManager getInstance() {
        if (instance == null) {
            instance = new AlertManager();
        }
        return instance;
    }
    
    private AlertManager() {
        // Constructor privado para patrón singleton
    }
    
    /**
     * Solicita mostrar una alerta. Si hay una alerta en curso o no ha pasado
     * el tiempo requerido, la alerta se añade a la cola.
     * 
     * @param context Contexto para crear el diálogo
     * @param tipo Tipo de alerta ("verde", "amarillo", "naranja", "rojo")
     * @param mensaje Mensaje a mostrar en la alerta
     */
    public void mostrarAlerta(Context context, String tipo, String mensaje) {
        if (!isAlertShowing && canShowAlert()) {
            showAlertImmediately(context, tipo, mensaje);
        } else {
            // Añadir a la cola, pero primero verificar si ya existe una alerta del mismo tipo
            // para evitar duplicados innecesarios
            boolean alreadyQueued = false;
            for (AlertRequest request : pendingAlerts) {
                if (request.tipo.equals(tipo)) {
                    alreadyQueued = true;
                    break;
                }
            }
            
            if (!alreadyQueued) {
                pendingAlerts.offer(new AlertRequest(context, tipo, mensaje));
            }
        }
    }
    
    /**
     * Verifica si puede mostrarse una alerta basado en el tiempo transcurrido
     */
    private boolean canShowAlert() {
        return (System.currentTimeMillis() - lastAlertDismissedTime) >= ALERT_DELAY_MS;
    }
    
    /**
     * Muestra una alerta inmediatamente
     */
    private void showAlertImmediately(Context context, String tipo, String mensaje) {
        isAlertShowing = true;
        
        switch (tipo.toLowerCase()) {
            case "verde":
                currentDialog = createVerdeDialog(context, mensaje);
                break;
            case "amarillo":
                currentDialog = createAmarilloDialog(context, mensaje);
                break;
            case "naranja":
                currentDialog = createNaranjaDialog(context, mensaje);
                break;
            case "rojo":
                currentDialog = createRojoDialog(context, mensaje);
                break;
            default:
                isAlertShowing = false;
                return;
        }
        
        if (currentDialog != null) {
            currentDialog.show();
        }
    }
    
    /**
     * Método llamado cuando se cierra una alerta
     */
    private void onAlertDismissed() {
        isAlertShowing = false;
        lastAlertDismissedTime = System.currentTimeMillis();
        currentDialog = null;
        
        // Programar la verificación de alertas pendientes después del delay
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                processNextAlert();
            }
        }, ALERT_DELAY_MS);
    }
    
    /**
     * Procesa la siguiente alerta en la cola si es posible
     */
    private void processNextAlert() {
        if (!pendingAlerts.isEmpty() && !isAlertShowing && canShowAlert()) {
            AlertRequest nextAlert = pendingAlerts.poll();
            if (nextAlert != null) {
                showAlertImmediately(nextAlert.context, nextAlert.tipo, nextAlert.mensaje);
            }
        }
    }
    
    /**
     * Crea un diálogo de alerta verde
     */
    private AlertDialog createVerdeDialog(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.mensaje_verde, null);
        builder.setView(view);
        
        ((TextView) view.findViewById(R.id.tituloVerde)).setText(context.getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoVerde)).setText(mensaje);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        
        final AlertDialog dialog = builder.create();
        View finalView = view;
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialogInterface) {
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okVerde)).setText(
                            MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")")
                        );
                    }

                    public void onFinish() {
                        dialog.dismiss();
                    }
                }.start();
            }
        });
        
        view.findViewById(R.id.okVerde).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onAlertDismissed();
            }
        });
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        
        return dialog;
    }
    
    /**
     * Crea un diálogo de alerta amarillo
     */
    private AlertDialog createAmarilloDialog(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.mensaje_amarillo, null);
        builder.setView(view);
        
        ((TextView) view.findViewById(R.id.tituloAmarillo)).setText(context.getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoAmarillo)).setText(mensaje);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        
        final AlertDialog dialog = builder.create();
        View finalView = view;
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialogInterface) {
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okAmarillo)).setText(
                            MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")")
                        );
                    }

                    public void onFinish() {
                        dialog.dismiss();
                    }
                }.start();
            }
        });
        
        view.findViewById(R.id.okAmarillo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onAlertDismissed();
            }
        });
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        
        return dialog;
    }
    
    /**
     * Crea un diálogo de alerta naranja
     */
    private AlertDialog createNaranjaDialog(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.mensaje_naranja, null);
        builder.setView(view);
        
        ((TextView) view.findViewById(R.id.tituloNaranja)).setText(context.getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoNaranja)).setText(mensaje);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        
        final AlertDialog dialog = builder.create();
        View finalView = view;
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialogInterface) {
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okNaranja)).setText(
                            MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")")
                        );
                    }

                    public void onFinish() {
                        dialog.dismiss();
                    }
                }.start();
            }
        });
        
        view.findViewById(R.id.okNaranja).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onAlertDismissed();
            }
        });
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        
        return dialog;
    }
    
    /**
     * Crea un diálogo de alerta rojo
     */
    private AlertDialog createRojoDialog(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.mensaje_rojo, null);
        builder.setView(view);
        
        ((TextView) view.findViewById(R.id.tituloRojo)).setText(context.getString(R.string.mensaje_alerta));
        ((TextView) view.findViewById(R.id.textoRojo)).setText(mensaje);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.precaucion);
        
        final AlertDialog dialog = builder.create();
        View finalView = view;
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialogInterface) {
                new CountDownTimer(5000, 1000) {
                    public void onTick(long seg) {
                        String ok = "OK";
                        ((Button) finalView.findViewById(R.id.okRojo)).setText(
                            MessageFormat.format("{0}{1}{2}{3}{4}", ok, " ", "(", ((seg / 1000) + 1), ")")
                        );
                    }

                    public void onFinish() {
                        dialog.dismiss();
                    }
                }.start();
            }
        });
        
        view.findViewById(R.id.okRojo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onAlertDismissed();
            }
        });
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        
        return dialog;
    }
    
    /**
     * Limpia la cola de alertas pendientes
     */
    public void clearPendingAlerts() {
        pendingAlerts.clear();
    }
    
    /**
     * Verifica si hay una alerta mostrándose actualmente
     */
    public boolean isShowingAlert() {
        return isAlertShowing;
    }
    
    /**
     * Fuerza el cierre de la alerta actual si existe
     */
    public void dismissCurrentAlert() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
    }
}
