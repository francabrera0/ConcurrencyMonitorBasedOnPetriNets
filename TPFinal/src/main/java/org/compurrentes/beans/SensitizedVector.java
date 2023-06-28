package org.compurrentes.beans;

import org.compurrentes.MonitorManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Vector de sensibilizados, utilizado para el manejo de la temporalidad de las transiciones temporizadas.
 */
public class SensitizedVector {
    List<Integer> sensibilities; //Transiciones sensibilizadas
    private final long[] alpha = {0,10,10,10,10,10,10,10,0,10,10,10}; //Límite inferior de la ventana
    private final long[] beta = new long[alpha.length]; //Límite superior de la ventana
    private final long[] timeStamp = new long[alpha.length]; //Tiempo en el que se sensibilizó una transición (por tokens).
    private final boolean[] waitingFor = new boolean[alpha.length]; //Banderas de espera por transición.
    MonitorManager monitorManager;


    /**
     * Constructor de la clase
     * @param monitorManager monitor de concurrencia de la RdP
     */
    public SensitizedVector(MonitorManager monitorManager) {
        sensibilities = new ArrayList<>();
        Arrays.fill(timeStamp,System.currentTimeMillis());
        Arrays.fill(waitingFor, false);
        Arrays.fill(beta,0xFFFFFFF);
        this.monitorManager = monitorManager;
    }

    /**
     * Retorna un valor boolean que representa si la transición está sensibilizada por tokens y por tiempo.
     * @param transition transición consultada
     * @param finalShoots bandera de finalización del programa
     * @return true si está sensibilizada, false si no lo está
     */
    public boolean isSensitized(int transition, boolean finalShoots) {
        if(sensibilities.contains(transition)) { /*Sensibilizada por tokens*/
            if(finalShoots) return true; /*Si es de finalización NO tiene en cuenta temporalidades*/
            return isSensitizedByTime(transition); /*Verifica ventana temporal*/
        }
        else return false; /*Si no está sensibilizada por tokens simplemente retorna false*/
    }

    /**
     * Retorna un valor booleano que representa si la transición está temporizada temporalmente o no. A demás
     *  maneja los diferentes casos de llegada del hilo a la ventana temporal.
     * @param transition transición consultada
     * @return true si está sensibilizada temporalmente, false si no
     * @throws RuntimeException si el hilo es interrumpido
     */
    public boolean isSensitizedByTime(int transition) throws RuntimeException {
        if (alpha[transition] == 0){ /*Alpha = 0 representa una transición no temporizada, por lo tanto sólo importan los tokens*/
            System.out.printf("Thread %s, transition T%d not timed\n",Thread.currentThread().getName(),transition+1);
            return true; /*Se dispara*/
        }

        if (waitingFor[transition]){ /*Hay un hilo durmiendo a la espera de esta transición*/
            System.out.printf("Thread %s, transition T%d has a thread waiting\n",Thread.currentThread().getName(),transition+1);
            return false; /*El hilo se coloca en la cola de la transición*/
        }

        /*Cota inferior de la ventana = tiempo en que la transición fue sensibilizada por tokens + alpha*/
        /*Cota superior de la ventana = tiempo en que la transición fue sensibilizada por tokens + beta*/
        long actualTime = System.currentTimeMillis();
        boolean inWindow = actualTime >= timeStamp[transition]+alpha[transition] && actualTime <= timeStamp[transition]+beta[transition];
        if (inWindow){ /*Dentro de la ventana temporal*/
            System.out.printf("Thread %s, transition T%d inside window\n",Thread.currentThread().getName(),transition+1);
            return true; /*Se dispara*/
        }

        boolean beforeWindow = actualTime < timeStamp[transition]+alpha[transition];
        if (!beforeWindow){ /*Si no está antes de la ventana ni adentro, entonces se pasó*/
            System.out.printf("Thread %s, transition T%d after window\n",Thread.currentThread().getName(),transition+1);
            return false; //Ver la posibilidad de lanzar excepción para que no entre a la cola
        }

        /*En este punto, el hilo está antes de la ventana temporal*/
        waitingFor[transition] = true; /*Actualiza el vector de espera*/
        long timeToSleep = timeStamp[transition]+alpha[transition] - actualTime; /*Tiempo que le falta para llegar a la cota inferior*/
        monitorManager.getMutex().release(); /*Abandona el mutex*/
        System.out.printf("Thread %s, transition T%d before window, to sleep\n",Thread.currentThread().getName(),transition+1);

        try {
            TimeUnit.MILLISECONDS.sleep(timeToSleep); /*Duerme el tiempo que le falta para llegar*/
            monitorManager.getMutex().acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        /*En este punto ya tomó el mutex del monitor*/
        waitingFor[transition] = false; /*Actualiza el vector de espera*/
        setSensibilities(monitorManager.getPetriNet().getSensibilities()); /*Actualiza el vector de sensibilizadas, ya que pudo cambiar*/
        System.out.printf("Thread %s, transition T%d wakeUp\n",Thread.currentThread().getName(),transition+1);
        return isSensitized(transition, false); /*Verifica si sigue estando sensibilizada*/
    }

    /**
     * Actualiza el vector de tiempos de las transiciones. Solo actualiza el de las que no estaban sensibilizadas
     *  anteriormente pero luego sí.
     * @param newSensibilities nuevas transiciones sensibilizadas
     */
    public void updateTimeStamps(List<Integer> newSensibilities) {
        for (Integer i : newSensibilities) {
            if(!sensibilities.contains(i))
                timeStamp[i] = System.currentTimeMillis();
        }
    }

    /**
     * Actualiza el listado de transiciones sensibilizadas.
     * @param sensibilities nuevas transiciones sensibilizadas
     */
    public void setSensibilities(List<Integer> sensibilities) {
        this.sensibilities = sensibilities;
    }
}
