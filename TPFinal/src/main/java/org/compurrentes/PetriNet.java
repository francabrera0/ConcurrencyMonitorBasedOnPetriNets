package org.compurrentes;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.log4j.Logger;
import org.compurrentes.beans.SensitizedVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Esta clase modela la RdP del sistema a representar.
 */
public class PetriNet {

    private static final Logger LOG = Logger.getLogger(PetriNet.class);
    private final RealMatrix fluxMatrix;
    private RealVector tokens;
    private SensitizedVector sensitizedVector;

    /**
     * Constructor de la clase.
     *
     * @param fluxMatrixData matriz de flujo de incidencia de la RdP
     * @param initialTokens marcado inicial de la RdP
     */
    public PetriNet(double[][] fluxMatrixData, double[] initialTokens) {
        fluxMatrix = MatrixUtils.createRealMatrix(fluxMatrixData);
        tokens = MatrixUtils.createRealVector(initialTokens);
    }

    /**
     * Este método retorna un vector que representa a la transición pasada como parámetro. El vector
     *  contiene todos ceros y un uno en la posición correspondiente a la transición. El tamaño del vector se calcula
     *  en función de la cantidad total de transiciones (cantidad de columnas de la matriz).
     * @param i transición a representar
     * @return transitionData
     */
    private RealVector getTransition(int i) {
        double[] transitionData = new double[getTotalTransitions()];
        Arrays.fill(transitionData, 0);
        transitionData[i] = 1;
        return MatrixUtils.createRealVector(transitionData);
    }

    /**
     * Realiza el disparo de transiciones (en exclusión mutua, ya que se llama desde adentro del monitor).
     *  Primero obtiene las transiciones sensibilizadas y actualiza el vector de sensibilizado. Luego utiliza un método
     *  definido en dicho vector, para saber si la transición puede ser disparada o no.
     *  Si la transición a disparar está sensibilizada entonces la dispara, actualiza el vector de marcado con
     *  la ecuación fundamental e imprime un mensaje en pantalla (también lo guarda en el log).
     *  Finalmente, actualiza los tiempos relacionados con las transiciones.
     *
     * @param transition transición a disparar
     * @param finalShots disparos finales para ajustar la red
     * @return valor boolean de disparo
     */
    public boolean shoot(int transition, boolean finalShots) {
        sensitizedVector.setSensibilities(getSensibilities()); /*Actualiza las transiciones sensibilizadas*/

        if(sensitizedVector.isSensitized(transition, finalShots)) { /*Transición sensibilizada, se dispara*/
            tokens = tokens.add(fluxMatrix.operate(getTransition(transition))); /*Actualiza marcado*/
            String message = String.format("%s. Shooter %s shot T%s", /*log*/
                    System.nanoTime(), Thread.currentThread().getName(), (transition+1));
            LOG.info(message);
            sensitizedVector.updateTimeStamps(getSensibilities()); /*Actualiza timestamps*/
            return true;
        } else { /*Transición no sensibilizada, no se dispara*/
            return false;
        }
    }

    /**
     * Este método retorna una lista con todas las transiciones sensibilizadas al momento de la
     *  llamada al método. Para verificar si una transición está sensibilizada, verifica que la secuencia de disparo
     *  de dicha transición (vector con todos ceros y un uno en la transición, generado con el método getTransition)
     *  sea una secuencia válida. Utiliza la ecuación fundamental y comprueba que no haya tokens menores a cero.
     *
     * @return sensibilities
     */
    public List<Integer> getSensibilities() {
        List<Integer> sensibilities = new ArrayList<>();
        for(int transition = 0; transition < getTotalTransitions(); transition++) {
            RealVector nextTokens = tokens.add(fluxMatrix.operate(getTransition(transition)));
            if(!Arrays.stream(nextTokens.getData()).filter(val -> val < 0).findAny().isPresent()) {
                sensibilities.add(transition);
            }
        }
        return sensibilities;
    }

    /**
     * Retorna la cantidad total de transiciones de la RdP. Calculadas a partir de la cantidad
     *  de columnas de la matriz de flujo.
     *
     * @return totalTransitions
     */
    public int getTotalTransitions() {
        return fluxMatrix.getColumnDimension();
    }

    /**
     * Proporciona a la red una instancia del vector de sensibilizados.
     * @param sensitizedVector vector de sensibilizados
     */
    public void setSensitizedVector(SensitizedVector sensitizedVector) {
        this.sensitizedVector = sensitizedVector;
    }

    /**
     * Retorna el vector de marcado de la red.
     * @return vector de marcado de la red
     */
    public RealVector getTokens() {
        return tokens;
    }
}
