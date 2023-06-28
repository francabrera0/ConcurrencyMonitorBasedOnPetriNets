package org.compurrentes;


import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Esta clase modela la actividad que se realiza en las plazas de la RdP. Además, cuenta con
 *  un contador de disparos por transición que será util para las estadísticas de la red.
 */
public class ModelledProcess {

    private final PetriNet petriNet;
    private final List<List<Integer>> piecesTransitions;
    private final Politics politics;
    private final int[] transitionActionCounter; /*Contador de disparos por transición*/

    /**
     * Constructor de la clase.
     *
     * @param petriNet RdP del sistema.
     * @param piecesTransitions Listado de lista de transiciones que representan cada camino productivo de la RdP.
     */
    public ModelledProcess(PetriNet petriNet, List<List<Integer>> piecesTransitions) {
        this.petriNet = petriNet;
        this.piecesTransitions = piecesTransitions;
        this.politics = new Politics(this);
        transitionActionCounter = new int[petriNet.getTotalTransitions()];
    }

    /**
     * Simula la realización de una tarea en una plaza de la RdP, simplemente realiza un sleep para
     *  dormir al hilo durante un tiempo definido y actualiza el contador de disparos de la transición correspondiente.
     *
     * @param transition Transición de entrada a la plaza a simular.
     * @throws RuntimeException Excepción manejada en Shooter
     */
    public void realizeTask(int transition) throws RuntimeException {
        transitionActionCounter[transition]++;
        /*En caso de transiciones no temporizdas hay que simular el tiempo de tarea*/
        /*if(transition == 7 || transition == 11){
            return;
        }
        try {
            int minTime = 1;
            int maxTime = 10;
            int time = new Random().nextInt(maxTime - minTime + 1) + minTime;
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
    }

    /**
     * Retorna la RdP asociada.
     *
     * @return petriNet
     */
    public PetriNet getPetriNet() {
        return petriNet;
    }

    /**
     * Retorna las transiciones de los caminos de producción de la RdP.
     *
     * @return piecesTransitions
     */
    public List<List<Integer>> getPiecesTransitions() {
        return piecesTransitions;
    }

    /**
     * Retorna las políticas aplicadas.
     *
     * @return politics
     */
    public Politics getPolitics() {
        return politics;
    }

    /**
     * Retorna el contador de disparos de transiciones
     * @return transitionActionCounter
     */
    public int[] getTransitionActionCounter() {
        return transitionActionCounter;
    }

}
