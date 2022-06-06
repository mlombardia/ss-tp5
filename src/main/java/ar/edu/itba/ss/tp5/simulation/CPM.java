package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.Particle;
import ar.edu.itba.ss.tp5.models.Wall;

import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;

public class CPM {
    public static void run() {
        for (Particle particle : particles) {
            double[] closestParticle = getClosestParticle(particle);
            double[] closestWall = getClosestWall(particle);
            if (closestParticle[1] != 0 && closestWall[2] != 0 && particle.getRadius() < rMax) {
                particle.setRadius(particle.getRadius() + deltaR);
            }
        }
    }

    private static double getVd(Particle particle) {
        return vdMax * ((particle.getRadius() - rMin) / (rMax - rMin)); //as beta = 1, its a linear function
    }

    public static double calculateDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double calculateDistanceParticle(Particle p1, Particle p2) {
        return calculateDistance((p1.getXPos() - p1.getRadius()), (p2.getXPos() - p2.getRadius()), (p1.getYPos() - p1.getRadius()), (p2.getYPos() - p2.getRadius()));
    }

    public static double calculateDistanceWall(Particle p1, Wall wall) {
        return calculateDistance((p1.getXPos() - p1.getRadius()), (wall.getXPos() - wall.getRadius()), (p1.getYPos() - p1.getRadius()), (wall.getYPos() - wall.getRadius()));

    }

    public static double[] getClosestParticle(Particle particle) {
        Particle closest = particle.equals(particles.get(0)) ? particles.get(1) : particles.get(0);
        double distance = calculateDistanceParticle(particle, closest);
        for (Particle p : particles) {
            if (!p.equals(particle)) {
                if (distance > calculateDistanceParticle(p, particle)) {
                    closest = p;
                    distance = calculateDistanceParticle(p, particle);
                }
            }
        }
        return new double[]{closest.getId(), distance};
    }

    public static double[] getClosestWall(Particle particle) {
        Wall closest = walls.get(0);
        double distance = calculateDistanceWall(particle, closest);
        for (Wall wall : walls) {
            if (distance > calculateDistanceWall(particle, wall)) {
                closest = wall;
                distance = calculateDistanceWall(particle, wall);
            }
        }
        return new double[]{closest.getXPos(), closest.getYPos(), distance};
    }
}
