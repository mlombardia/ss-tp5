package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.models.Particle;
import ar.edu.itba.ss.tp5.models.Wall;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;

import java.util.*;

import static ar.edu.itba.ss.tp5.App.*;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;

public class CPM {
    private static Map<Particle, Double[]> directions = new HashMap<>();
    private static Map<Particle, Particle> targets = new HashMap<>();

    public static void run(FilePositionGenerator filePositionGenerator) {
        for (int i = 0; i < 5000; i++) {
            directions.clear();
            for (Particle particle : particles) {
                checkBites(particle);
                double[] closestParticle = getClosestParticle(particle);
                double[] closestWall = getClosestWall(particle);
                if (closestParticle[1] > particle.getRadius() && closestWall[2] > particle.getRadius() && particle.getRadius() < rMax) {
                    particle.setRadius(particle.getRadius() + deltaR);
                }
                if (crashed(particle, closestParticle, closestWall)) {
                    particle.setRadius(rMin);
                    particle.setXVel(0);
                    particle.setYVel(0);
                    if (!particles.get((int) closestParticle[0]).isHuman()) { //particle and zombie
                        particle.setBiteTime(System.currentTimeMillis());
                    }else{ //
                        if (closestParticle[1] - particles.get((int) closestParticle[0]).getRadius() < particle.getRadius()) escape(particle,particles.get((int) closestParticle[0]).getXPos(), particles.get((int) closestParticle[0]).getYPos(), !particles.get((int) closestParticle[0]).isHuman() );
                        else escape(particle, closestWall[0], closestWall[1], !particle.isHuman());

                    }
                } else if (!particle.isHuman()) { //zombie
                    handleZombie(particle, closestParticle, closestWall);
                } else {
                    handleHuman(particle, closestParticle, closestWall);
                }
                if (isOutOfBounds(particle)) {
                    particle.setXVel(0);
                    particle.setYVel(0);
                    targets.remove(particle);
                    System.out.println("here");
                }
            }
            updatePositions();
            filePositionGenerator.addParticles(particles);
        }
    }

    private static void checkBites(Particle particle) {
        if (particle.getBiteTime() != NOT_BITTEN) {
            if (System.currentTimeMillis() - particle.getBiteTime() >= 7000) {
                particle.setHuman(false);
                particle.setBiteTime(NOT_BITTEN);
            }
        }
    }

    private static boolean crashed(Particle particle, double[] closestParticle, double[] closestWall) {
        return (closestParticle[1] - particles.get((int) closestParticle[0]).getRadius() < particle.getRadius() || closestWall[2] < particle.getRadius());
    }

    private static void handleZombie(Particle particle, double[] closestParticle, double[] closestWall) {
        Particle human;
        double attackAttempts = 50;
        particle.setAttackAttempts(particle.getAttackAttempts() - 1);
        if ((closestParticle[1] <= zombieInteractionDistance) && particle.getAttackAttempts() <= 0) {
            human = particles.get((int) closestParticle[0]);
            particle.setAttackAttempts(attackAttempts);
        } else {
            if (targets.containsKey(particle)) {
                human = targets.get(particle); // get assigned target
            } else { //target out of range
                human = getRandomTarget();
                particle.setAttackAttempts(attackAttempts);
            }
        }
        follow(particle, human.getXPos(), human.getYPos());
        targets.put(particle, human); //zombie human
        System.out.println(particle.getXPos() + " " + particle.getYPos());
        System.out.println(particle + " " + human);
    }

    private static void handleHuman(Particle particle, double[] closestParticle, double[] closestWall) {
        if (closestParticle[1] < closestWall[2]) {
            if (closestParticle[1] > humanInteractionDistance) {
                //stand still
            } else {
                Particle aux = particles.get((int) closestParticle[0]);
                escape(particle, aux.getXPos(), aux.getYPos(), !particle.isHuman());
                saveDirections(particle);
            }
        } else {
            if (closestWall[2] > humanInteractionDistance) {
                //stand still
            } else {
                escape(particle, closestWall[0], closestWall[1], !particle.isHuman());
                saveDirections(particle);
            }
        }
    }

    private static void follow(Particle particle, double x, double y) {
        double deltaX = particle.getXPos() - x - (2 * particle.getRadius());
        double deltaY = particle.getYPos() - y - (2 * particle.getRadius());
        double angle = Math.atan2(deltaY, deltaX) * (180 / Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
        particle.setXVel(vzMax * Math.cos(angle));
        particle.setYVel(vzMax * Math.sin(angle));
        saveDirections(particle);
        particle.setXPos(particle.getXPos() + particle.getXVel());
        particle.setYPos(particle.getYPos() + particle.getYVel());
    }

    private static void saveDirections(Particle particle) {
        directions.put(particle, new Double[]{particle.getXPos() + particle.getXVel() * deltaT, particle.getYPos() + particle.getYVel() * deltaT});
    }

    private static void updatePositions() {
        for (Particle particle : directions.keySet()) {
            Double[] positions = directions.get(particle);
            particle.setXPos(positions[0]);
            particle.setYPos(positions[1]);
        }
    }

    private static void escape(Particle particle, double x, double y, boolean isZombie) {
        double xPos = particle.getXPos() - x;
        double yPos = particle.getYPos() - y;
        double length = Math.sqrt(Math.pow(xPos, 2) + Math.pow(yPos, 2));
        xPos = xPos / length;
        yPos = yPos / length;
        if (isZombie) {
            particle.setXVel(xPos * vzMax);
            particle.setYVel(yPos * vzMax);
        } else {
            particle.setXVel(xPos * vdMax);
            particle.setYVel(yPos * vdMax);
        }
    }

    public static boolean isOutOfBounds(Particle particle) {
        return Math.sqrt(Math.pow(particle.getXPos() - circleRadius, 2) + Math.pow(particle.getYPos() - circleRadius, 2)) >= circleRadius;
    }

    private static double getVd(Particle particle) {
        return vdMax * ((particle.getRadius() - rMin) / (rMax - rMin)); //as beta = 1, it's a linear function
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

    public static Particle getRandomTarget() {
        List<Particle> humans = new ArrayList<>();
        for (Particle p : particles) {
            if (p.isHuman()) humans.add(p);
        }
        int rand = (int) getRandom(0, humans.size() - 1);
        System.out.println(rand);
        return humans.get(rand);
    }
}
