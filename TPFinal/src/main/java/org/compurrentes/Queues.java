package org.compurrentes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Esta clase representa la cola de espera en la cual los hilos se estarán bloqueados a la espera
 *  de una señal que les indique que el recurso fue liberado y pueden disparar la transición. Esta cola tendrá un
 *  listado de colas que representan cada transición.
 */
public class Queues {

    private final List<TransitionQueue> transitionsSemaphoreList = new ArrayList<>();

    /**
     * Constructor de la clase. Crea las listas de transiciones particulares.
     *
     * @param totalTransition número total de transiciones
     */
    public Queues(int totalTransition) {
        /*Permits=0, para que siempre se bloquee al intentar hacer un acquire*/
        IntStream.range(0, totalTransition)
                .forEach(i -> transitionsSemaphoreList.add(new TransitionQueue(i, 0)));
    }

    /**
     * Ejecuta el acquire de una lista de transiciones particular que es pasada como parámetro.
     *
     * @param transition indica en que cola se debe colocar el hilo en espera
     */
    public void acquire(int transition) {
        try {
            transitionsSemaphoreList.get(transition).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ejecuta el release de una lista de transiciones particular que es pasada como parámetro.
     *
     * @param transition indica de que cola se debe despertar un hilo
     */
    public void release(int transition) {
        transitionsSemaphoreList.get(transition).release();
    }

    /**
     * Devuelve un listado con las transiciones que tienen hilos esperando por ella. Es decir
     *  hilos que realizaron el acquire de la lista y fueron bloqueados.
     *
     * @return waitingTransitions
     */
    public List<Integer> getWaitingTransitions() {
        return transitionsSemaphoreList.stream()
                .filter(Semaphore::hasQueuedThreads)
                .map(TransitionQueue::getTransition)
                .collect(Collectors.toList());
    }

}
