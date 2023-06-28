package org.compurrentes;

import com.google.common.collect.Iterables;
import org.compurrentes.beans.Segment;
import org.compurrentes.beans.SensitizedVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Aquí se definen los objetos y variables que representan el sistema.
 *  Luego se da comienzo a la ejecución del programa.
 */
public class Main {

    private static final int TIME = 50; /*Tiempo de ejecución*/
    private static final double[] INITIAL_TOKENS = /*Marcado inicial de la red*/
            new double[]{0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 4, 2, 2, 3, 1, 2, 1};
    private static final double[][] FLUX_DATA_MATRIX = { /*Matriz de flujo de incidencia*/
            //1  2  3  4  5  6  7  8  9 10 11 12
            { 1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //P1
            { 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0, 0}, //P2
            { 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0, 0}, //P3
            { 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0, 0}, //P4
            { 0, 0, 0, 0, 1, 0,-1, 0, 0, 0, 0, 0}, //P5
            { 0, 0, 0, 0, 0, 1, 1,-1, 0, 0, 0, 0}, //P6
            {-1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}, //P7
            { 0, 0, 0, 0, 0, 0, 0, 0, 1,-1, 0, 0}, //P8
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,-1, 0}, //P9
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,-1}, //P10
            { 0, 0, 0, 0, 0, 0, 0, 0,-1, 0, 0, 1}, //P11
            {-1, 1, 1, 0, 0, 0, 0, 0, 0, 0,-1, 1}, //P12
            { 0,-1,-1, 1, 1, 0, 0, 0, 0,-1, 1, 0}, //P13
            { 0, 0, 0,-1,-1, 1, 1, 0,-1, 1, 0, 0}, //P14
            { 0, 0, 0, 0, 0,-1,-1, 1, 0, 0, 0, 0}, //P15
            { 0, 0, 0, 0, 0, 0, 0, 0,-1, 1, 0, 0}, //P16
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,-1, 1, 0}, //P17
    };
    private static final Segment[] SEGMENTS = { /*Segmentos de la red*/
            new Segment(2, Iterables.cycle(0).iterator(), "A"),
            new Segment(4, Iterables.cycle(1, 3, 5).iterator(), "B"),
            new Segment(4, Iterables.cycle(2, 4, 6).iterator(), "C"),
            new Segment(1, Iterables.cycle(7).iterator(), "D"),
            new Segment(4, Iterables.cycle(8, 9, 10, 11).iterator(), "E")
    };
    private static final List<List<Integer>> piecesTransitions = Arrays.asList( /*Caminos de producción de la RdP*/
            Arrays.asList(0,1,3,5,7), /*Ruedas 24*/
            Arrays.asList(0,2,4,6,7), /*Ruedas 32*/
            Arrays.asList(8,9,10,11)); /*Ejes*/

    /**
     * Se crean los objetos necesarios para el modelado del sistema utilizando las variables
     *  previamente definidas y mediante uno de estos objetos se inicia la ejecución del programa.
     * @param args none
     * @throws InterruptedException excepción por interrupción
     */
    public static void main(String[] args) throws InterruptedException {
        PetriNet petriNet = new PetriNet(FLUX_DATA_MATRIX, INITIAL_TOKENS);
        ModelledProcess modelledProcess = new ModelledProcess(petriNet, piecesTransitions);
        MonitorManager monitor = new MonitorManager(modelledProcess);
        SensitizedVector sensitizedVector = new SensitizedVector(monitor);
        petriNet.setSensitizedVector(sensitizedVector);
        Initializer initializer = new Initializer(monitor, SEGMENTS, modelledProcess);
        initializer.start(TIME);

        String path = "../Regex/regex1.py";
        String[] cmd = {"python3",path};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println("\nRegex execution begins");
            String commandRead;
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((commandRead = stdInput.readLine()) != null)
                System.out.println(commandRead);
            process.destroy();
            System.out.printf("Regex finished with exit code %d",process.exitValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
