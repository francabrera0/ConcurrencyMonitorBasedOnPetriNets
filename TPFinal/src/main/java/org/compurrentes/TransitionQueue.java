package org.compurrentes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *  Representa las colas de espera por las transiciones. Extiende de la clase Semaphore.
 */
public class TransitionQueue extends Semaphore {

    private final int transition;
    private final List<String> threads = new ArrayList<>();

    /**
     * Constructor de la clase.
     *
     * @param transition transición por la que se espera en esta cola
     * @param permits número de mutex permitidos
     */
    public TransitionQueue(int transition, int permits) {
        super(permits);
        this.transition = transition;
    }

    /**
     * Añade el nombre del hilo actual a un listado de hilos bloqueados y ejecuta el método acquire() de
     *  la superclase. Una vez que el hilo es despertado por la ejecución del método release() por parte de otro hilo
     *  su nombre es eliminado de la lista.
     * @throws InterruptedException excepción por interrupción
     */
    @Override
    public void acquire() throws InterruptedException {
        String currentThread = Thread.currentThread().getName();
        threads.add(currentThread);
        super.acquire();
        threads.remove(Thread.currentThread().getName());
    }

    /**
     * Ejecuta el método release de la superclase.
     */
    @Override
    public void release() {
        super.release();
    }

    /**
     * Retorna el listado de hilos esperando en la cola.
     *
     * @return threadList
     */
    @Override
    public String toString() {
        return  "T" + threads;
    }

    /**
     * Retorna la transición que es representada por esta cola.
     *
     * @return transition
     */
    public int getTransition() {
        return transition;
    }

}
