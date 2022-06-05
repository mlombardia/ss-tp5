package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.models.Particle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ar.edu.itba.ss.tp5.App.N;

public class SimulationController {

    static List<Particle> particles = new ArrayList<>();
    FilePositionGenerator filePositionGenerator;
    public static int walls = 0;
    public static int circleRadius = 11;
    double velZ;
    double velH = 4;
    public static int countZombies = 1;
    public static double deltaT = 0.05;
    double t = 0;

    public static double interactionDistance = 4;

    public static double humanInteractionDistance = 2;
    public static double zombieVelocitySlow = 0.3;
    public static double persecutionZombieVelocity = 4;

    public static double minParticleRadius = 0.15;

    public static double maxParticleRadius = 0.32;
    public static double radiusStep = 0.05;

    public double zombieColor = 1;
    public double humanColor = 0.5;
    public double wallColor = 0.75;

    public double count = 0;


    public static Map<Particle, Particle> persecutions = new HashMap<>();

    public SimulationController(double velZ, FilePositionGenerator filePositionGenerator) {
        this.velZ = velZ;
        this.filePositionGenerator = filePositionGenerator;
        generateMap();
    }

    public void generateMap() {
        double randomX, randomY;

        // agrega los humanos
        while (particles.size() < N) {
            double randomAngle = Math.random() * (360);
            double randomRadius = Math.random() * (circleRadius - 1) + 1;
            randomX = circleRadius + randomRadius * Math.cos(randomAngle);
            randomY = circleRadius + randomRadius * Math.sin(randomAngle);
            if (!particleOverlaps(randomX, randomY)) {
                particles.add(new Particle(particles.size(), randomX, randomY, 4, minParticleRadius, humanColor, true, false));
            }
        }

        // agrega al zombie
        particles.add(new Particle(particles.size(), circleRadius, circleRadius, zombieVelocitySlow, minParticleRadius, zombieColor, false, false));

        // agrega las paredes
        for (double j = 0; j < 360.0; j += 0.1) {
            walls += 1;
            particles.add(new Particle(circleRadius + circleRadius * Math.cos(j), circleRadius + circleRadius * Math.sin(j), 0, minParticleRadius, wallColor, true, true));
        }
        filePositionGenerator.addWalls(particles);
        filePositionGenerator.addParticles(particles);
    }

    public void simulate() {
        while (!cutCondition(countZombies)) {
            for (Particle particle : particles) {
                if (particle.isWall()) {
                    break;
                }
                Pair<Double, Particle> closest = checkProximity(particle); //se fija si tiene a alguien cerca
                particle.move(closest);
                if (particle.isWaiting())
                    System.out.printf("%d %b %b\n", particle.getId(), particle.isZombie(), particle.isWaiting());
                if (particle.isOutOfBounds()){
                    System.out.println("out");
                }
            }
            filePositionGenerator.addParticles(particles);

            // maso como seria
            // zombies recorren de manera random buscando humanos a velocidad baja (0.3m/s)
            // si un zombie encuentra humanos, se dirige al mas cercano con velZ
            // los humanos intentan escapar del zombie
            // si aparece uno mas cercano, cambia a ese target
            // se quedan pegados 7 segundos
            // el humano se convierte en zombie

            // humanos quieren escapar del zombie, esquivando paredes y humanos

            // guarda posiciones de todas las particulas

        }
    }

    // puede ser el que querramos.
    // un tiempo/un porcentaje de zombies/etc
    public boolean cutCondition(int countZombies) {
        //return true;
//        return (((double) countZombies / N) >= 0.75);
        count += 1;
        System.out.println(count);
        return count > 10;
    }

    public boolean particleOverlaps(double x, double y) {
        for (Particle particle : particles) {
            if ((Math.abs(x - particle.getXPos()) < 0) && (Math.abs(y - particle.getYPos()) < 0)) {
                return true;
            }
        }
        return false;
    }


    public static double calculateDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow((p1.getXPos() - p1.getRadius()) - (p2.getXPos() - p2.getRadius()), 2) - Math.pow((p1.getYPos() - p1.getRadius()) - (p2.getYPos() - p2.getRadius()), 2));
    }

    public Pair<Double, Particle> checkProximity(Particle particle) {
        Particle closestParticle = particle.equals(particles.get(0)) ? particles.get(1) : particles.get(0);
        double closestDistance = calculateDistance(particle, closestParticle);
        for (Particle p : particles) {
            double dist = calculateDistance(particle, p);
            if ((!p.equals(particle)) && (dist < closestDistance) && !(p.isZombie() && particle.isZombie())) {
                closestParticle = p;
                closestDistance = dist;
            }
        }
        return new Pair<>(closestDistance, closestParticle);
    }

    public static double getRandom(double min, double max) {
        return (Math.random() * (max - min + 1) + min);
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

