package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a simple text classifier using a Bag of Words model and Bayesian inference.
 */
public class Abogado {
    private final Map<String, Integer> wordCount;
    private final Map<String, Map<String, Integer>> labelWordCount;
    private final Map<String, Integer> labelCount;
    private final Set<String> vocabulary;
    private int totalDocuments;

    /**
     * Constructor initializing the classifier.
     */
    public Abogado() {
        this.wordCount = new HashMap<>();
        this.labelWordCount = new HashMap<>();
        this.labelCount = new HashMap<>();
        this.vocabulary = new HashSet<>();
        this.totalDocuments = 0;
    }

    /**
     * Normalizes a given text by converting it to lowercase, removing non-alphabetic characters, and trimming spaces.
     *
     * @param text the text to normalize
     * @return the normalized text
     */
    private String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-zA-Z\\s]", "").trim();
    }

    /**
     * Trains the classifier with a given line of text and its associated label.
     *
     * @param line the line of text in the format "sentence | label"
     */
    public void train(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 2) {
            System.err.println("Invalid input format. Expected 'sentence | label'.");
            return;
        }

        String sentence = normalize(parts[0].trim());
        String label = parts[1].trim();

        labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        totalDocuments++;

        String[] words = sentence.split("\\s+");
        for (String word : words) {
            vocabulary.add(word);

            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);

            labelWordCount.computeIfAbsent(label, k -> new HashMap<>())
                    .put(word, labelWordCount.get(label).getOrDefault(word, 0) + 1);
        }
    }

    /**
     * Classifies a given sentence based on the trained data.
     *
     * @param sentence the sentence to classify
     * @return the predicted label for the sentence
     */
    public String classify(String sentence) {
        String normalizedSentence = normalize(sentence);
        String[] words = normalizedSentence.split("\\s+");

        double maxProb = Double.NEGATIVE_INFINITY;
        String bestLabel = null;

        for (String label : labelCount.keySet()) {
            double logProb = Math.log(labelCount.get(label) / (double) totalDocuments);
            int labelWordTotal = labelWordCount.get(label).values().stream().mapToInt(Integer::intValue).sum();
            for (String word : words) {
                int wordFrequency = labelWordCount.get(label).getOrDefault(word, 0);
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

    /**
     * Prints the current state of the model, including label counts, word counts, and label-word counts.
     */
    public void printModel() {
        System.out.println("Label Counts: " + labelCount);
        System.out.println("Word Counts: " + wordCount);
        System.out.println("Label Word Counts: " + labelWordCount);
    }
}
