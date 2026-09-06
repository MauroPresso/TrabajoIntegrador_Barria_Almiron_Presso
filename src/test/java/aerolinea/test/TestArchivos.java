package aerolinea.test;

import aerolinea.dominio.Pasajero;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Laboratorio de entrada/salida y serializacion.
 *
 * <p>Todos los archivos se generan dentro de target/laboratorio-archivos,
 * por lo que no contaminan el codigo fuente ni el repositorio Git.</p>
 */
public class TestArchivos {

    public static void main(String[] args) throws Exception {

        Path carpeta = Path.of("target", "laboratorio-archivos");
        Files.createDirectories(carpeta);

        Path texto = carpeta.resolve("datos.txt");
        Path buffer = carpeta.resolve("buffer.txt");
        Path binario = carpeta.resolve("dato.bin");
        Path objeto = carpeta.resolve("pasajero.dat");

        System.out.println("=== FileWriter / FileReader ===");

        try (FileWriter writer = new FileWriter(texto.toFile())) {
            writer.write("Sistema de Aerolinea");
        }

        try (FileReader reader = new FileReader(texto.toFile())) {
            StringBuilder contenido = new StringBuilder();
            int caracter;

            while ((caracter = reader.read()) != -1) {
                contenido.append((char) caracter);
            }

            System.out.println(contenido);
        }

        System.out.println();
        System.out.println("=== BufferedWriter / BufferedReader ===");

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(buffer.toFile()))) {

            writer.write("Vuelo AR100");
            writer.newLine();
            writer.write("Destino Buenos Aires");
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(buffer.toFile()))) {

            String linea;

            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
        }

        System.out.println();
        System.out.println("=== Streams binarios ===");

        try (BufferedOutputStream salida =
                     new BufferedOutputStream(new FileOutputStream(binario.toFile()))) {

            salida.write(65); // ASCII A
            salida.write(82); // ASCII R
        }

        try (BufferedInputStream entrada =
                     new BufferedInputStream(new FileInputStream(binario.toFile()))) {

            int dato;
            StringBuilder contenidoBinario = new StringBuilder();

            while ((dato = entrada.read()) != -1) {
                contenidoBinario.append((char) dato);
            }

            System.out.println("Contenido binario interpretado: " + contenidoBinario);
        }

        System.out.println();
        System.out.println("=== ObjectOutputStream / ObjectInputStream ===");

        Pasajero pasajeroOriginal =
                new Pasajero(33444555, "Maria", "Lopez", "AR33444555");

        try (ObjectOutputStream salida =
                     new ObjectOutputStream(new FileOutputStream(objeto.toFile()))) {

            salida.writeObject(pasajeroOriginal);
        }

        Pasajero pasajeroRecuperado;

        try (ObjectInputStream entrada =
                     new ObjectInputStream(new FileInputStream(objeto.toFile()))) {

            pasajeroRecuperado = (Pasajero) entrada.readObject();
        }

        System.out.println("Recuperado: "
                + pasajeroRecuperado.getNombreCompleto()
                + " | DNI: "
                + pasajeroRecuperado.getDni());

        if (!pasajeroOriginal.equals(pasajeroRecuperado)) {
            throw new IllegalStateException("La serializacion no preservo la identidad logica.");
        }

        System.out.println("Archivos del laboratorio: " + carpeta.toAbsolutePath());
        System.out.println("TestArchivos finalizado correctamente.");
    }
}
