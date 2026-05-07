package com.example.machineLearning;

import com.example.model.UserRecord;
import java.util.*;

public class KNNClassifier extends BaseAlgorithm {

    private List<UserRecord> trainingData;
    private final int k;

    public KNNClassifier(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k pozitif olmalı");
        }
        this.k = k;
    }

    @Override
    public void train(List<UserRecord> trainingData) {
        this.trainingData = trainingData;
    }

    @Override
    public String predict(UserRecord testUser) {

        final String[] result = new String[1];

        runWithTimer(() -> {

            PriorityQueue<Neighbor> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a.distance));

            for (UserRecord trainUser : trainingData) {
                double dist = calculateDistance(testUser, trainUser);
                pq.add(new Neighbor(trainUser.getCategory(), dist));
            }

            List<Neighbor> neighbors = new ArrayList<>();
            for (int i = 0; i < k && !pq.isEmpty(); i++) {
                neighbors.add(pq.poll());
            }

            Map<String, Double> votes = new HashMap<>();
            for (Neighbor n : neighbors) {
                double weight = 1.0 / (n.distance + 1e-6);
                votes.put(n.category,
                        votes.getOrDefault(n.category, 0.0) + weight);
            }

            result[0] = Collections.max(
                    votes.entrySet(),
                    Map.Entry.comparingByValue()).getKey();
        });

        return result[0];
    }

    private double calculateDistance(UserRecord u1, UserRecord u2) {
        double[] f1 = u1.getFeatureVector();
        double[] f2 = u2.getFeatureVector();

        double[] weights = {
                0.20, // gender
                0.25, // brand
                0.15, // amount
                0.25, // norm tot
                0.15 // price
        };
        double sum = 0;
        int len = Math.min(f1.length, weights.length);

        for (int i = 0; i < len; i++) {
            double diff = f1[i] - f2[i];
            sum += weights[i] * diff * diff;
        }

        return Math.sqrt(sum);
    }

    private static class Neighbor {
        String category;
        double distance;

        Neighbor(String category, double distance) {
            this.category = category;
            this.distance = distance;
        }
    }

    @Override
    public String toStringTree() {
        System.out.println();
        return null;
    }
}
