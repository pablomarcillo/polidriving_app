//Paquete que contiene un conjunto de clases relacionadas por finalidad, ámbito y herencia
package com.polidriving.mobile.clases.principal;

//Clases que permiten el control de vida de los Fragmentos de la App
//Clases usadas para reconocimiento de elementos en actividades
//Clases usadas para cambio entre actividades
//Clases usadas para la conexión interclases
//Clases usadas para el uso de fragmentos
//Clases usadas para el mapeo de cadenas
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class FragmentoModeloVista extends ViewModel {
    //Creación de variable para enviar, presentar y recibir información
    private static final MutableLiveData<Integer> indice = new MutableLiveData<>();

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> distance_travelled = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> throttle_position = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> accidents_onsite = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> accidents_onhour = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static final MutableLiveData<String> respuestaAgente = new MutableLiveData<>();

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> precipitacion = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> temperatura = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> inclinacion = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> ubicacion = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> vehiculos = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> ocupacion = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> direccion = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> velocidad = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> longitud = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> segmento = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> baterias = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> promedio = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> humedad = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> control = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> latitud = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> viento = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> lugar = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> motor = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> heart = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> rpm = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };

    //Creación de variable para enviar, presentar y recibir información
    private static LiveData<String> via = new LiveData<String>() {
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super String> observer) {
            super.observe(owner, observer);
        }
    };    
    //Creación del repositorio que permite presentar la información en las actividad principal riesgo
    public static void datosDataSet(String A, String B, String C, String D, String E, String F, String G, String H, String I, String J, String K, String L, String M, String N, String O, String P, String Q, String R, String S, String T) {
        new Thread(new Runnable() {
            public void run() {
                distance_travelled = Transformations.map(indice, input -> A);
                throttle_position = Transformations.map(indice, input -> B);
                accidents_onsite = Transformations.map(indice, input -> C);
                accidents_onhour = Transformations.map(indice, input -> D);
                precipitacion = Transformations.map(indice, input -> E);
                inclinacion = Transformations.map(indice, input -> F);
                temperatura = Transformations.map(indice, input -> G);
                velocidad = Transformations.map(indice, input -> H);
                ubicacion = Transformations.map(indice, input -> I);
                vehiculos = Transformations.map(indice, input -> J);
                ocupacion = Transformations.map(indice, input -> K);
                direccion = Transformations.map(indice, input -> L);
                baterias = Transformations.map(indice, input -> M);
                promedio = Transformations.map(indice, input -> N);
                humedad = Transformations.map(indice, input -> O);
                viento = Transformations.map(indice, input -> P);
                lugar = Transformations.map(indice, input -> Q);
                motor = Transformations.map(indice, input -> R);
                heart = Transformations.map(indice, input -> S);
                rpm = Transformations.map(indice, input -> T);
            }
        }).start();
    }

    //Creación del repositorio que permite presentar la información en las actividad principal riesgo, accidentes, clima
    public static void datosRouteOne(String A, String B, String C, String D, String E, String F) {
        new Thread(new Runnable() {
            public void run() {
                velocidad = Transformations.map(indice, input -> A);
                segmento = Transformations.map(indice, input -> B);
                longitud = Transformations.map(indice, input -> C);
                latitud = Transformations.map(indice, input -> D);
                control = Transformations.map(indice, input -> E);
                via = Transformations.map(indice, input -> F);
            }
        }).start();
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getDistance_travelled() {
        return distance_travelled;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getThrottle_position() {
        return throttle_position;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getAccidents_onsite() {
        return accidents_onsite;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getAccidents_onhour() {
        return accidents_onhour;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getRespuestaAgente() {
        return respuestaAgente;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getPrecipitacion() {
        return precipitacion;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getInclinacion() {
        return inclinacion;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getTemperatura() {
        return temperatura;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getVelocidad() {
        return velocidad;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getUbicacion() {
        return ubicacion;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getVehiculos() {
        return vehiculos;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getOcupacion() {
        return ocupacion;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getDireccion() {
        return direccion;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getBaterias() {
        return baterias;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getLongitud() {
        return longitud;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getPromedio() {
        return promedio;
        //TODO
    }

    //Creación del get que permite obtener la información a presentar
    public static void datosRespuesta(String RESPUESTA) {
        // Verificar si estamos en el hilo principal
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            // Estamos en el hilo principal, actualizar directamente
            respuestaAgente.setValue(RESPUESTA);
        } else {
            // Estamos en un hilo secundario, usar postValue
            respuestaAgente.postValue(RESPUESTA);
        }
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getSegmento() {
        return segmento;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getLatitud() {
        return latitud;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getHumedad() {
        return humedad;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getControl() {
        return control;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getViento() {
        return viento;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getLugar() {
        return lugar;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getMotor() {
        return motor;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getHeart() {
        return heart;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getRpm() {
        return rpm;
    }

    //Creación del get que permite obtener la información a presentar
    public static LiveData<String> getVia() {
        return via;
    }

    //Creación del get que permite obtener la información a presentar
    public static void setIndex(int index) {
        indice.setValue(index);
    }
}