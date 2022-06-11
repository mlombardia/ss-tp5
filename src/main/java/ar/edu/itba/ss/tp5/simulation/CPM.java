package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.models.Particle;
import ar.edu.itba.ss.tp5.models.Wall;
import javafx.util.Pair;

import java.util.*;

import static ar.edu.itba.ss.tp5.App.*;
import static ar.edu.itba.ss.tp5.simulation.SimulationController.*;

public class CPM {
    private static Map<Particle, Double[]> directions = new HashMap<>();
    private static Map<Particle, Particle> targets = new HashMap<>();
    private static Map<Pair<Particle, Particle>, Long> transformations = new HashMap<>();

    private static double attackAttempts = 0;
    private static double t = 0;
    private static int zombiesInitial = 0;

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
                    }
                } else if (!particle.isHuman()) { //zombie
                    handleZombie(particle);
                } else {
                    handleHuman(particle, closestParticle, closestWall);
                }
                if (isOutOfBounds(particle)) {
                    targets.remove(particle);
                    follow(particle, circleRadius, circleRadius);
                }
            }
            updatePositions();
            filePositionGenerator.addParticles(particles);
            filePositionGenerator.addFraction(t, zombies, N);
            filePositionGenerator.addVelocity(t, zombiesInitial, zombies);
            zombiesInitial = zombies;
            t+=deltaT;
        }
        System.out.println("Final time: " + t);
    }

    private static void checkBites() {
        List<Pair<Particle, Particle>> toRemove = new ArrayList<>();
        for (Pair<Particle, Particle> particlePair : transformations.keySet()) {
            Particle particle1 = particlePair.getKey();
            Particle particle2 = particlePair.getValue();
            long time = transformations.get(particlePair);
            if (System.currentTimeMillis() - time >= 700) {
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

                    Particle human = particles.get((int) getClosestHuman(particle1)[0]);
                    particle2.setAttackAttempts(attackAttempts);
                    follow(particle2, human.getXPos(), human.getYPos());
                    targets.put(particle2, human);

                    human = particles.get((int) getClosestHuman(particle2)[0]);
                    particle1.setAttackAttempts(attackAttempts);
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

    private static void handleZombie(Particle particle) {
        Particle human;

        double[] closestParticle = getClosestHuman(particle);
        particle.setAttackAttempts(particle.getAttackAttempts() - 1);
        if ((closestParticle[1] <= zombieInteractionDistance) && particle.getAttackAttempts() <= 0) {
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
        double[] closestZombie = getClosestZombie(particle);
        if (closestZombie[1] > humanInteractionDistance && closestZombie[1] < 2 * humanInteractionDistance && closestParticle[1] > humanInteractionDistance/4 && closestWall[2] > humanInteractionDistance/4) {
            Particle aux = particles.get((int) closestZombie[0]);
            escape(particle, aux.getXPos(), aux.getYPos(), !particle.isHuman());
        } else if (closestParticle[1] < closestWall[2]) {
            Particle aux = particles.get((int) closestParticle[0]);
            if (closestParticle[1] > humanInteractionDistance) {
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
        double dist = calculateDistance(particle.getXPos(), x, particle.getYPos(), y);
        if (dist <= zombieInteractionDistance) {
            particle.setXVel((deltaX * vzMax * (particle.getRadius() - rMin) / (rMax - rMin)));
            particle.setYVel((deltaY * vzMax * (particle.getRadius() - rMin) / (rMax - rMin)));
        } else {
            particle.setXVel((deltaX * vzMin * (particle.getRadius() - rMin) / (rMax - rMin)));
            particle.setYVel((deltaY * vzMin * (particle.getRadius() - rMin) / (rMax - rMin)));
        }
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

    public static double[] getClosestHuman(Particle particle) {
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
        return new double[]{closest.getId(), distance};
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

    public static double[] getClosestZombie(Particle particle) {
        Particle closest = particles.get(particles.size() - 1);
        double distance = calculateDistanceParticle(particle, closest);
        for (Particle p : particles) {
            if (!p.equals(particle) && !p.isHuman()) {
                if (distance > calculateDistanceParticle(p, particle)) {
                    closest = p;
                    distance = calculateDistanceParticle(p, particle);
                }
            }
        }
        return new double[]{closest.getId(), distance};
    }
}
