package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.models.Particle;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {

    List<Particle> particles = new ArrayList<>();
    FilePositionGenerator filePositionGenerator;
    int N;
    int radius;
    double velZ;

    public SimulationController(int N, double velZ, FilePositionGenerator filePositionGenerator){
        this.radius = 11;
        this.N = N;
        this.velZ = velZ;
        this.filePositionGenerator = filePositionGenerator;
        generateMap();
        simulate();
    }

    public void generateMap(){
        double randomX, randomY;
        //agrega las paredes
        for(double j=0; j<360.0; j+=0.1){
            particles.add(new Particle(11+radius*Math.cos(j), 11+radius*Math.sin(j), 0, 0, 1));
        }

        //agrega las particulas (humanos y al zombie)
        particles.add(new Particle(11, 11, 0,0, 1));

        for(int i = 0; i<N; i++){
            double randomAngle = Math.random()*(360);
            double randomRadius = Math.random()*(radius-1)+1;
            randomX = 11+randomRadius*Math.cos(randomAngle);
            randomY = 11+randomRadius*Math.sin(randomAngle);
            particles.add(new Particle(randomX , randomY, 0, 0, 0.5));
        }
        filePositionGenerator.addParticles(particles);
    }

    public void simulate(){
        while(!cutCondition()){

        }
    }

    public boolean cutCondition(){
        return true;
    }
}
