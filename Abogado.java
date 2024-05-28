import java.io.*;
import java.util.*;

public class Abogado {
    private Map<String, Integer> wordCount;
    private Map<String, Map<String, Integer>> labelWordCount;
    private Map<String, Integer> labelCount;
    private Set<String> vocabulary;
    private int totalDocuments;

    public Abogado() {
        this.wordCount = new HashMap<>();
        this.labelWordCount = new HashMap<>();
        this.labelCount = new HashMap<>();
        this.vocabulary = new HashSet<>();
        this.totalDocuments = 0;
    }

    private String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-zA-Z\\s]", "").trim();
    }

    public void train(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 2)
            return;

        String sentence = normalize(parts[0].trim());
        String label = parts[1].trim();

        labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        totalDocuments++;

        String[] words = sentence.split("\\s+");
        for (String word : words) {
            vocabulary.add(word);

            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);

            labelWordCount.putIfAbsent(label, new HashMap<>());
            Map<String, Integer> wordMap = labelWordCount.get(label);
            wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
        }
    }

    public String classify(String sentence) {
        String normalizedSentence = normalize(sentence);
        String[] words = normalizedSentence.split("\\s+");

        double maxProb = Double.NEGATIVE_INFINITY;
        String bestLabel = null;

        for (String label : labelCount.keySet()) {
            double logProb = Math.log(labelCount.get(label) / (double) totalDocuments);
            for (String word : words) {
                int wordFrequency = labelWordCount.getOrDefault(label, new HashMap<>()).getOrDefault(word, 0);
                int labelWordTotal = labelWordCount.get(label).values().stream().mapToInt(Integer::intValue).sum();
                double wordProb = (wordFrequency + 1) / (double) (labelWordTotal + vocabulary.size());
                logProb += Math.log(wordProb);
            }
            if (logProb > maxProb) {
                maxProb = logProb;
                bestLabel = label;
            }
        }
        return bestLabel;
    }

    public void printModel() {
        System.out.println("Label Counts: " + labelCount);
        System.out.println("Word Counts: " + wordCount);
        System.out.println("Label Word Counts: " + labelWordCount);
    }

    public static void main(String[] args) {
        Abogado classifier = new Abogado();
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                classifier.train(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test the classifier with a sample sentence
        String testSentence = "Hoy es un mal día";
        System.out.println("Clasificado como: " + classifier.classify(testSentence));

        // Print the internal model (for debugging purposes)
        classifier.printModel();
    }
}
