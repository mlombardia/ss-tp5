package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.Particle;
import javafx.util.Pair;

import static ar.edu.itba.ss.tp5.simulation.SimulationController.minParticleRadius;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.maxParticleRadius;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.zombieVelocitySlow;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.persecutionZombieVelocity;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.deltaT;


public class Dynamics {

    public static double vdMax = 4;

    public static double tau = 0.5; // TODO revisar porque creo que no deberia ir hardcodeado

    public static double beta = 1;

    public static void cpm(Particle particle, Pair<Double, Double> target) {

        if (target != null){
            particle.currentTarget = target;
        }

        if (particle.isHuman() && !particle.isWaiting()) {      // si es un humano y no lo estan comiendo
            if (particle.isAtContactWithHuman()){
                particle.setVel(vdMax);
                particle.setRadius(minParticleRadius);
                particle.setXPos(particle.getXPos() + vdMax * deltaT * particle.currentTarget.getKey()); // pos + vel * deltaT
                particle.setYPos(particle.getYPos() + vdMax * deltaT * particle.currentTarget.getValue());
                particle.setIsAtContactWithHuman(false);
            } else {
                particle.setVel(vdMax * Math.pow((particle.getRadius()-minParticleRadius)/(maxParticleRadius-minParticleRadius), beta));
                if (particle.getRadius() < maxParticleRadius){
                    particle.setRadius(particle.getRadius() + maxParticleRadius / (tau/deltaT)); // TODO revisar porque creo que no deberia ser deltaT y calcularlo
                }
                particle.setXPos(particle.getXPos() + particle.getVel() * deltaT * particle.currentTarget.getKey()); // pos + vel * deltaT
                particle.setYPos(particle.getYPos() + particle.getVel() * deltaT * particle.currentTarget.getValue());
            }
        }
    }

    public static void reRoute(Particle particle) {

        //setear target temporal
        if (particle.isVertical){
            particle.temporalTarget = new Pair(particle.getXPos() - 4, particle.getYPos()+Math.sin(115 * Math.PI / 180.0));
        } else {
            particle.temporalTarget = new Pair(particle.getXPos()+Math.cos(205 * Math.PI / 180.0), particle.getYPos() - 4);
        }

    }

    public static Pair<Double, Double> checkStatus(Particle particle){
        if (particle.getXPos() == particle.temporalTarget.getKey() && particle.getYPos() == particle.temporalTarget.getValue()){
            return particle.originalTarget;
        } else {
            if (particle.originalTarget.equals(particle.temporalTarget)){
                return particle.originalTarget;
            } else {
                return particle.temporalTarget;
            }
        }
    }
}


/*
moviendo para zombie y humano-> en c tick move()
zombie -> persigue o se queda random
humano -> quieto o escapando -> sin obstaculos o con obstaculos -> reRoute() -> setea target
 */