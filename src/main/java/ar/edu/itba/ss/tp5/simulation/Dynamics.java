package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.Particle;

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
}
