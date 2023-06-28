package org.compurrentes.beans;

import java.util.Iterator;

/**
 * Representa los segmentos definidos de la red de Petri. Estos segmentos
 *  son sectores específicos de la RdP que fijan las responsabilidades de los hilos. Es decir
 *  cada hilo puede disparar las transiciones que corresponden a su segmento asignado.
 */
public class Segment {

    private final String name;
    private final int threadNumber;
    private final Iterator<Integer> transitions;

    /**
     * Constructor de la clase.
     *
     * @param threadNumber Número máximo de hilos del segmento
     * @param transitions Transiciones que pertenecen al segmento
     * @param name Nombre del segmento, representado con una letra mayúscula, "A","B",...
     */
    public Segment(int threadNumber, Iterator<Integer> transitions, String name) {
        this.threadNumber = threadNumber;
        this.transitions = transitions;
        this.name = name;
    }

    /**
     * Retorna la cantidad máxima de hilos del segmento.
     *
     * @return threadNumber
     */
    public int getThreadNumber() {
        return threadNumber;
    }

    /**
     * Retorna las transiciones pertenecientes al segmento.
     *
     * @return transitions
     */
    public Iterator<Integer> getTransitions() {
        return transitions;
    }

    /**
     * Retorna el nombre del segmento.
     *
     * @return name
     */
    @Override
    public String toString() {
        return name;
    }

}
