package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.models.Particle;
import ar.edu.itba.ss.tp5.models.Wall;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import javafx.util.Pair;

import java.util.*;

import static ar.edu.itba.ss.tp5.App.*;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;

public class CPM {
    private static Map<Particle, Double[]> directions = new HashMap<>();
    private static Map<Particle, Particle> targets = new HashMap<>();
    private static Map<Pair<Particle, Particle>, Long> transformations = new HashMap<>();


    public static void run(FilePositionGenerator filePositionGenerator) {
        while (!cutCondition()) {
            directions.clear();
            checkBites();
            for (Particle particle : particles) {
                double[] closestParticle = getClosestParticle(particle);
                double[] closestWall = getClosestWall(particle);
                if (closestParticle[1] > 0 && closestWall[2] > 0 && particle.getRadius() < rMax) {
                    particle.setRadius(particle.getRadius() + deltaR);
                }
                if (crashed(particle, closestParticle, closestWall)) {
                    Particle aux = particles.get((int) closestParticle[0]);
                    particle.setRadius(rMin);
                    if (closestParticle[1] - rMax < particle.getRadius()) {
                        if (particle.isHuman()) {
                            if (aux.isHuman()) {
                                escape(particle, aux.getXPos(), aux.getYPos(), false);
                            } else {
                                if (!transformations.containsKey(new Pair<>(particle, aux))) {
                                    System.out.println(aux + " " + particle);
                                    transformations.put(new Pair<>(particle, aux), System.currentTimeMillis());
                                    particle.setBiteTime(System.currentTimeMillis());
                                    aux.setBiteTime(System.currentTimeMillis());
                                    particle.setXVel(0);
                                    particle.setYVel(0);
                                    aux.setXVel(0);
                                    aux.setYVel(0);
                                    directions.remove(aux);
                                    directions.remove(particle);
                                }
                            }
                        } else {
                            if (!aux.isHuman()) {
                                escape(particle, aux.getXPos(), aux.getYPos(), true);
                            }
                        }

                    } else if (closestWall[2] < particle.getRadius()) {
                        escape(particle, closestWall[0], closestWall[1], !particle.isHuman());
                    } else {
                        //?
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
                    follow(particle, circleRadius, circleRadius);
//                    escape(particle, closestWall[0], closestWall[1], !particle.isHuman());
                }
            }
            updatePositions();
            filePositionGenerator.addParticles(particles);
        }
    }

    private static void checkBites() {
        List<Pair<Particle, Particle>> toRemove = new ArrayList<>();
        for (Pair<Particle, Particle> particlePair : transformations.keySet()) {
            Particle particle1 = particlePair.getKey();
            Particle particle2 = particlePair.getValue();
            long time = transformations.get(particlePair);
            if (System.currentTimeMillis() - time >= 10) {
                toRemove.add(particlePair); //remove later on
                double random = getRandom(0, 100);
                targets.remove(particle1);
                targets.remove(particle2);

                particle1.setBiteTime(NOT_BITTEN);
                particle2.setBiteTime(NOT_BITTEN);

                if (random / 100 <= RECOVERY_POSSIBILITY) {
                    if (!particle2.isHuman()) zombies--;
                    particle2.setHuman(true);
                    if (!particle1.isHuman()) zombies--;
                    particle1.setHuman(true);
                } else {
                    if (particle1.isHuman()) zombies++;
                    particle1.setHuman(false);
                    if (particle2.isHuman()) zombies++;
                    particle2.setHuman(false);

                    Particle human = getClosestHuman(particle1);
                    particle2.setAttackAttempts(30);
                    follow(particle2, human.getXPos(), human.getYPos());
                    targets.put(particle2, human);

                    human = getClosestHuman(particle2);
                    particle1.setAttackAttempts(30);
                    follow(particle1, human.getXPos(), human.getYPos());
                    targets.put(particle1, human); //zombie human
                }
            }
        }
        toRemove.forEach(item -> transformations.remove(item));

    }

    private static boolean crashed(Particle particle, double[] closestParticle, double[] closestWall) {
        return (closestParticle[1] - rMin < particle.getRadius() || closestWall[2] < particle.getRadius());
    }

    private static void handleZombie(Particle particle, double[] closestParticle, double[] closestWall) {
        Particle human;
        double attackAttempts = 50;
        particle.setAttackAttempts(particle.getAttackAttempts() - 1);
        if (particles.get((int) closestParticle[0]).isHuman() && (closestParticle[1] <= zombieInteractionDistance) && particle.getAttackAttempts() <= 0) {
            human = particles.get((int) closestParticle[0]);
            particle.setAttackAttempts(attackAttempts);
            chaseHuman(particle, human.getXPos(), human.getYPos());
        } else {
            if (targets.containsKey(particle) && particle.getAttackAttempts() > 0) {
                human = targets.get(particle); // get assigned target
                chaseHuman(particle, human.getXPos(), human.getYPos());
            } else { //target out of range
                human = getRandomTarget();
                particle.setAttackAttempts(attackAttempts);
                follow(particle, human.getXPos(), human.getYPos());
            }
        }
        targets.put(particle, human); //zombie human

    }

    private static void handleHuman(Particle particle, double[] closestParticle, double[] closestWall) {
        if (closestParticle[1] < closestWall[2]) {
            Particle aux = particles.get((int) closestParticle[0]);
            if (!aux.isHuman()) {
                escape(particle, aux.getXPos(), aux.getYPos(), !particle.isHuman());
            } else if (closestParticle[1] > humanInteractionDistance) {
                particle.setXVel(0);
                particle.setYVel(0);
            } else {
                escape(particle, aux.getXPos(), aux.getYPos(), !particle.isHuman());
            }
        } else {
            if (closestWall[2] > humanInteractionDistance) {
                particle.setXVel(0);
                particle.setYVel(0);
            } else {
                escape(particle, closestWall[0], closestWall[1], !particle.isHuman());
            }
        }
    }

    private static void follow(Particle particle, double x, double y) {
        double deltaX = particle.getXPos() - x;
        double deltaY = particle.getYPos() - y;
        double angle = Math.atan2(deltaY, deltaX) * (180 / Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
        if (calculateDistance(particle.getXPos(), x, particle.getYPos(), y) > zombieInteractionDistance) {
            particle.setXVel(vzMin * Math.cos(angle));
            particle.setYVel(vzMin * Math.sin(angle));
        } else {
            particle.setXVel(vzMax * Math.cos(angle));
            particle.setYVel(vzMax * Math.sin(angle));
        }
        saveDirections(particle);
    }

    private static void chaseHuman(Particle particle, double x, double y) {
        double deltaX = x - particle.getXPos();
        double deltaY = y - particle.getYPos();
        double distFromZombie = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        deltaX = deltaX / distFromZombie;
        deltaY = deltaY / distFromZombie;
        particle.setXVel((deltaX * vzMax * (particle.getRadius() - rMin) / (rMax - rMin)));
        particle.setYVel((deltaY * vzMax * (particle.getRadius() - rMin) / (rMax - rMin)));
        saveDirections(particle);
    }

    private static void saveDirections(Particle particle) {
        directions.put(particle, new Double[]{particle.getXPos() + particle.getXVel() * deltaT, particle.getYPos() + particle.getYVel() * deltaT});
    }

    private static void updatePositions() {
        for (Particle particle : directions.keySet()) {
            Double[] positions = directions.get(particle);
            if (particle.getBiteTime() == NOT_BITTEN) {
                particle.setXPos(positions[0]);
                particle.setYPos(positions[1]);
            }
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
        saveDirections(particle);

    }

    public static boolean isOutOfBounds(Particle particle) {
        return Math.sqrt(Math.pow(particle.getXPos() - particle.getRadius() - circleRadius, 2) + Math.pow(particle.getYPos() - particle.getRadius() - circleRadius, 2)) >= circleRadius;
    }

    private static double getVd(Particle particle) {
        return vdMax * ((particle.getRadius() - rMin) / (rMax - rMin)); //as beta = 1, it's a linear function
    }

    public static double calculateDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double calculateDistanceParticle(Particle p1, Particle p2) {
        return calculateDistance((p1.getXPos() - p1.getRadius()), (p2.getXPos() - p2.getRadius()), (p1.getYPos() - p2.getRadius()), (p2.getYPos() - p2.getRadius()));
    }

    public static double calculateDistanceWall(Particle p1, Wall wall) {
        return calculateDistance((p1.getXPos() - p1.getRadius()), (wall.getXPos() - wall.getRadius()), (p1.getYPos() - p1.getRadius()), (wall.getYPos() - wall.getRadius()));

    }

    public static Particle getClosestHuman(Particle particle) {
        Particle closest = getRandomTarget();
        double distance = calculateDistanceParticle(particle, closest);
        for (Particle p : particles) {
            if (!p.equals(particle) && p.isHuman()) {
                if (distance > calculateDistanceParticle(p, particle)) {
                    closest = p;
                    distance = calculateDistanceParticle(p, particle);
                }
            }
        }
        return closest;
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
        return humans.get(rand);
    }
}
