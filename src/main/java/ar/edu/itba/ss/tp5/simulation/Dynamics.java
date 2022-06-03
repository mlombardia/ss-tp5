package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.Particle;
import javafx.util.Pair;

public class Dynamics {

    public static double rMin = 0.2;
    public static double rMax = 0.4;
    public static double vdMax = 4;

    public static double deltaT = 0.1;

    public static double tau = 0.2; // TODO revisar porque creo que no deberia ir hardcodeado

    public static double beta = 1;

    public static void cpm(Particle particle) {

        if (particle.isAtContact()){
            particle.setVel(vdMax);
            particle.setRadius(rMin);
            particle.setXPos(particle.getXPos() + vdMax * deltaT);
            particle.setYPos(particle.getYPos() + vdMax * deltaT);
        } else {
            particle.setVel(vdMax * Math.pow((particle.getRadius()-rMin)/(rMax-rMin), beta));
            if (particle.getRadius() < rMax){
                particle.setRadius(particle.getRadius() + rMax / (tau/deltaT)); // TODO revisar porque creo que no deberia ser deltaT y calcularlo
            }
            particle.setXPos(particle.getXPos() + particle.getVel() * deltaT);
            particle.setYPos(particle.getYPos() + particle.getVel() * deltaT);
        }
    }

    public static void predictiveCollisionAvoidance(Particle particle){

        double desiredVel = 0; // TODO chequear como seria esto de la desired vel

        double Fg = (desiredVel-particle.getVel())/tau;

        double Fe = 0; // TODO verificar approaches

    }

    public static void reRoute(Particle particle) {

        //setear target temporal
        if (particle.isVertical){
            particle.targets.push(new Pair(particle.getXPos()-4, particle.getYPos()+Math.sin(115 * Math.PI / 180.0)));
        } else {
            particle.targets.push(new Pair(particle.getXPos()+Math.cos(205 * Math.PI / 180.0), particle.getYPos()-4));
        }

    }
}


/*
moviendo para zombie y humano-> en c tick move()
zombie -> persigue o se queda random
humano -> quieto o escapando -> sin obstaculos o con obstaculos -> reRoute() -> setea target
 */