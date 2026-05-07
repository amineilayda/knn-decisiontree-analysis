package com.example.machineLearning;

import com.example.model.UserRecord;
import java.util.*;

public abstract class BaseAlgorithm implements IClassifier {

    protected long executionTime;

    public long getExecutionTime() {
        return executionTime;
    }

    
    protected void runWithTimer(Runnable task) {
        long start = System.currentTimeMillis();

        task.run();  // algoritma burada çalışır

        executionTime = System.currentTimeMillis() - start;
    }

    
    protected double getNumeric(UserRecord r, String feature) {
        switch (feature) {
            case "PRICE":
                return r.getPrice();
            case "AMOUNT":
                return r.getAmount();
            case "TOTAL":
                return r.getLineNetTotal();
            default:
                return 0;
        }
    }

    protected String getCategorical(UserRecord r, String feature) {
        switch (feature) {
            case "GENDER":
                return r.getGender();
            case "BRAND":
                return r.getBrand();
            default:
                return "";
        }
    }

    protected String findMostFrequentCategory(List<UserRecord> data) {
        Map<String, Integer> map = new HashMap<>();
        for (UserRecord r : data) {
            map.merge(r.getCategory(), 1, Integer::sum);
        }
        return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public void printTree() {
        System.out.println();
    }
}