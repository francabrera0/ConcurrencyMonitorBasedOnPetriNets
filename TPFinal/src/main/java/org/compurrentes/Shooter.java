package org.compurrentes;


import java.util.Iterator;


/**
 * Representa a los trabajadores de la línea de producción, cada shooter tendrá un segmento asociado.
 * Implementa la interfaz Runnable Estos objetos serán utilizados para crear los hilos de ejecución.
 */
public class Shooter implements Runnable {

    private final MonitorManager monitor;
    private final Iterator<Integer> transitions;
    private final ModelledProcess modelledProcess;
    private boolean isInterrupted = false;

    /**
     * Constructor de la clase.
     *
     * @param monitor Monitor de concurrencia de la RdP
     * @param transitions Transiciones de la RdP
     * @param modelledProcess Proceso modelado de la RdP
     */
    public Shooter(MonitorManager monitor, Iterator<Integer> transitions, ModelledProcess modelledProcess) {
        this.monitor = monitor;
        this.transitions = transitions;
        this.modelledProcess = modelledProcess;
    }

    /**
     * Método que ejecuta el disparo de transiciones y la realización de tareas. Es importante destacar que
     *  el disparo de la transición ocurre en exclusión mutua debido a que este método pertenece al monitor y la
     *  realización de las tareas puede ocurrir de manera concurrente, ya que los recursos ya fueron asignados.
     *  Los hilos se encontrarán en ejecución hasta que sean interrumpidos y se complete un ciclo completo de
     *  producción (no pueden quedar ciclos incompletos al final).
     */
    @Override
    public void run() {
        int currentTransition = transitions.next();
        while(!(isInterrupted)) {
            try {
                monitor.shootTransition(currentTransition);
                modelledProcess.realizeTask(currentTransition);
                currentTransition = transitions.next();
            } catch (RuntimeException e) {
                isInterrupted = true;
            }
        }
    }

}
