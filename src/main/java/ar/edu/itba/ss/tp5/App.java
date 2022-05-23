package ar.edu.itba.ss.tp5;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.simulation.SimulationController;

public class App 
{
    public static void main( String[] args )
    {
        int N = 100;
        int velZ = 3;
        FilePositionGenerator filePositionGenerator = new FilePositionGenerator();
        SimulationController controller = new SimulationController(N, velZ, filePositionGenerator);
        filePositionGenerator.closeFiles();
    }
}
