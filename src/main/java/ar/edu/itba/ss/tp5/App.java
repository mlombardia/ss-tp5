package ar.edu.itba.ss.tp5;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.simulation.SimulationController;

public class App {
    public static int N = 140;
    public static int circleRadius = 11;
    public static double NOT_BITTEN = -1;

    public static void main(String[] args) {
        int velZ = 3;
        FilePositionGenerator filePositionGenerator = new FilePositionGenerator();
        SimulationController controller = new SimulationController(filePositionGenerator);
        controller.simulate();
        filePositionGenerator.closeFiles();
    }
}
