package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Abogado classifier = new Abogado();

        // Leer datos de entrenamiento desde un archivo
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                classifier.train(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Imprimir modelo interno para depuración
        classifier.printModel();

        // Interacción con el usuario a través de la terminal
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Ingrese una frase para clasificar (o 'exit' para salir):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            String classification = classifier.classify(input);
            System.out.println("Clasificación: " + classification);
        }

        scanner.close();
    }
}
