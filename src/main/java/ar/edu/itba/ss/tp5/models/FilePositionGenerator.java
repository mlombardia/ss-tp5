package ar.edu.itba.ss.tp5.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class FilePositionGenerator {
    private FileWriter fw1;
    private final BufferedWriter buffer1;

    public FilePositionGenerator() {
        try {
            FileWriter pw1 = new FileWriter( "ParticlesPosition.xyz");
            pw1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.fw1 = new FileWriter("ParticlesPosition.xyz", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.buffer1 = new BufferedWriter(fw1);
    }

    public void addParticles(List<Particle> particles) {
        try {
            buffer1.write(particles.size() + "\n");
            buffer1.write("XPosition YPosition Radius Color\n");
            for (Particle particle : particles) {
                buffer1.write(particle.getXPos() + " " + particle.getYPos() + " " + Math.pow(10, -1) + " " + particle.getColor() + " " + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFiles() {
        try {
            buffer1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
