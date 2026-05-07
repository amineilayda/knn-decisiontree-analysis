package com.example.evaluation;

import com.example.machineLearning.IClassifier;
import com.example.model.UserRecord;

import java.util.*;


public class Evaluator {

    public EvaluatorResult evaluate(IClassifier classifier, List<UserRecord> testSet) {

        int correct = 0;
        long startTime = System.currentTimeMillis();

        Set<String> labelsSet = new TreeSet<>();
        for (UserRecord r : testSet) {
            labelsSet.add(r.getCategory());
        }
        List<String> labels = new ArrayList<>(labelsSet);
        int n = labels.size();

        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < n; i++) index.put(labels.get(i), i);

        int[][] matrix = new int[n][n];

        for (UserRecord r : testSet) {

            String pred = classifier.predict(r);
            String actual = r.getCategory();

            if (pred == null) pred = "Unknown";

            if (pred.equals(actual)) correct++;

            Integer i = index.get(actual);
            Integer j = index.get(pred);

            if (i != null && j != null) {
                matrix[i][j]++;
            }
        }

        long total = System.currentTimeMillis() - startTime;

        EvaluatorResult res = new EvaluatorResult();
        res.accuracy = (double) correct / testSet.size();
        res.totalTime = total;
        res.avgTime = (double) total / testSet.size();
        res.labels = labels.toArray(new String[0]);
        res.confusionMatrix = matrix;

        return res;
    }
}