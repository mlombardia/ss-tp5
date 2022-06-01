package ar.edu.itba.ss.tp5.simulation;

import ar.edu.itba.ss.tp5.models.FilePositionGenerator;
import ar.edu.itba.ss.tp5.models.Particle;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationController {

    static List<Particle> particles = new ArrayList<>();
    FilePositionGenerator filePositionGenerator;
    int N;
    int circleRadius = 11;
    double velZ;
    double velH = 4;
    int countZombies = 1;
    double deltaT;
    double t = 0;

    public static double interactionDistance = 4;
    public static double zombieVelocity = 0.3;
    public static double persecutionZombieVelocity = 4;

    public static Map<Particle, Particle> persecutions = new HashMap<>();

    public SimulationController(int N, double velZ, double deltaT, FilePositionGenerator filePositionGenerator) {
        this.N = N;
        this.velZ = velZ;
        this.deltaT = deltaT;
        this.filePositionGenerator = filePositionGenerator;
        generateMap();
        simulate();
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
                particles.add(new Particle(particles.size(), randomX, randomY, 0, 0.2, 0.5, true, false));
            }
        }

        // agrega al zombie
        particles.add(new Particle(particles.size(), circleRadius, circleRadius, zombieVelocity, 0.2, 1, false, false));

        // agrega las paredes
        for (double j = 0; j < 360.0; j += 0.1) {
            particles.add(new Particle(circleRadius + circleRadius * Math.cos(j), circleRadius + circleRadius * Math.sin(j), 0, 0.2, 0.75, true, true));
        }
        filePositionGenerator.addParticles(particles);
    }

    public void simulate() {
        Particle closestParticle;
        while (!cutCondition(countZombies)) {


            for (Particle particle : particles) {
                Pair<Double, Particle> closest = checkProximity(particle); //se fija si tiene a alguien cerca
                particle.move(closest);
            }

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
        return true;
        //return ((countZombies/N)>=0.75? true : false);
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
        return Math.sqrt(Math.pow(p1.getXPos() - p2.getXPos(), 2) - Math.pow(p1.getYPos() - p2.getYPos(), 2));
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
        int rand = (int) getRandom(0, humans.size());
        return humans.get(rand);
    }
}

