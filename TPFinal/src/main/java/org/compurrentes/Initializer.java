package org.compurrentes;

import org.compurrentes.beans.Segment;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * En esta clase se definen los métodos necesarios para la creación, iniciación e
 *  interrupción de los hilos de ejecución.
 */
public class Initializer {

    private final MonitorManager monitor;
    private final Segment[] segments;
    private final ModelledProcess modelledProcess;

    /**
     * Constructor de la clase.
     *
     * @param monitor monitor del sistema
     * @param segments Array de segmentos de la RdP
     * @param modelledProcess proceso modelado de la RdP
     */
    public Initializer(MonitorManager monitor, Segment[] segments, ModelledProcess modelledProcess) {
        this.monitor = monitor;
        this.segments = segments;
        this.modelledProcess = modelledProcess;
    }

    /**
     * A partir de un objeto de la clase Segment recibido como parámetro, crea una cantidad definida
     *  (cantidad máxima de hilos del segmento) de objetos Runnable (Shooter).
     *  Luego utiliza estos objetos como parámetro para la creación de objetos Thread, a los cuales se les
     *  asigna un nombre en función del segmento y su orden de creación.
     *
     * @param segment Segmento particular para el que se quieren crear los shooters
     * @return Threads Un stream de Threads que contiene los hilos creados para el segmento específico
     */
    private Stream<Thread> createShooters(Segment segment) {
        return IntStream.range(0, segment.getThreadNumber()).mapToObj(i -> {
            Runnable shooter = new Shooter(monitor, segment.getTransitions(), modelledProcess);
            return new Thread(shooter, String.format("S%sN%s", segment, i));
        });
    }

    /**
     * Este método funciona como lanzador del sistema, inicialmente crea los shooters necesarios para cada
     *  segmento definido de la red haciendo uso del método anterior.
     *  Luego se encarga de lanzar todos los hilos generados, y luego de un tiempo establecido interrumpirlos.
     *
     * @param time tiempo de ejecución del programa (luego de este tiempo se interrumpen los hilos)
     * @throws InterruptedException Excepción por interrupción
     */
    public void start(int time) throws InterruptedException {
        List<Thread> shooters = Arrays.stream(segments)
                .flatMap(this::createShooters)
                .collect(Collectors.toList());
        shooters.parallelStream().forEach(Thread::start);
        TimeUnit.SECONDS.sleep(time);
        shooters.parallelStream().forEach(Thread::interrupt);

        try {
            for(Thread shooter:shooters){
                shooter.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Threads have been interrupted");
        System.out.println(modelledProcess.getPetriNet().getTokens());
        int[] finalTransitions = {1,2,3,4,5,6,7,9,10,11};
        int size = modelledProcess.getPetriNet().getTotalTransitions()-2;
        boolean end = false;
        while (!end){
            end = true;
            for(int i=0; i<size; i++){
                if(modelledProcess.getPetriNet().shoot(finalTransitions[i], true)){
                    modelledProcess.realizeTask(finalTransitions[i]);
                    end = false;
                }
            }
        }

        System.out.printf("Final tokens %s\n",modelledProcess.getPetriNet().getTokens());
        System.out.printf("Final transition counter %s\n", Arrays.toString(modelledProcess.getTransitionActionCounter()));
        System.out.println("Program execution finished");
    }
}
