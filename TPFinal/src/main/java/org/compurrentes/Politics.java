package org.compurrentes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Esta clase es encargada de tomar las decisiones que darán mayor o menor prioridad de disparos
 *  a las transiciones. Se busca que todas las transiciones sean disparadas equitativamente.
 */
public class Politics {

    private final ModelledProcess modelledProcess;

    /**
     * Constructor de la clase.
     *
     * @param modelledProcess proceso modelado por la RdP
     */
    public Politics(ModelledProcess modelledProcess) {
        this.modelledProcess = modelledProcess;
    }

    /**
     * Devuelve una y solo una transición para ser disparada. El método de decisión es, a partir de todas las
     *  transiciones disponibles para disparar (availableTransitions) se elige la que tenga menor cantidad de disparos
     *  históricos (valor representado en el contador de modelledProcess). En caso de haber más de una con la misma
     *  cantidad de disparos, se elige la primera.
     *
     * @param availableTransitions transiciones disponibles para disparar
     * @return privilegedAction
     */
    public int getPriorityShooter(List<Integer> availableTransitions) {
        int[] actions = modelledProcess.getTransitionActionCounter();

        List<List<Integer>> base = modelledProcess.getPiecesTransitions();

        List<Integer> privilegedActions = base.stream()
                .sorted(Comparator.comparingInt(pieceTransitions -> actions[pieceTransitions.get(1)]))
                .flatMap(Collection::stream)
                .filter(availableTransitions::contains)
                .collect(Collectors.toList());

        System.out.println("Actions: " + Arrays.toString(actions));
        System.out.println("Available: " + availableTransitions);
        System.out.println("Result " + privilegedActions);
        return privilegedActions.get(0);
    }
}
