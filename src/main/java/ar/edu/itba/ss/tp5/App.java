package ar.edu.itba.ss.tp5;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.simulation.SimulationController;

public class App {
    public static int N = 2;

    public static void main(String[] args) {
        int velZ = 3;
        FilePositionGenerator filePositionGenerator = new FilePositionGenerator();
        SimulationController controller = new SimulationController(velZ, filePositionGenerator);
        controller.simulate();
        filePositionGenerator.closeFiles();
    }

    // que pasa si un humano choca la pared u otro humano?
    // inicialmente los humanos estan quietos y se mueven cuando tienen un zombie cerca
    // o se estan moviendo todoo el tiempo?
}
