package com.example.util;

import com.example.model.UserRecord;

import java.util.Collections;
import java.util.List;

import java.util.Random;

public class PreProcessor {

    private final Random random = new Random(42);

    private double min;
    private double max;

    private boolean isFitted = false;

    
    public void process(List<UserRecord> records) {
        if (records == null || records.isEmpty())
            return;

        Collections.shuffle(records, random);
    }

   
    public List<UserRecord> getTrainingSet(List<UserRecord> all) {

        int limit = (int) (all.size() * 0.8);
        List<UserRecord> train = all.subList(0, limit);

        fit(train); // öğren
        transform(train); 

        return train;
    }

    
    public List<UserRecord> getTestSet(List<UserRecord> all) {

        int limit = (int) (all.size() * 0.8);
        List<UserRecord> test = all.subList(limit, all.size());

        transform(test); // sadece uygula

        return test;
    }

    private void fit(List<UserRecord> records) {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE; 

        for (UserRecord r : records) {
            if (r == null) continue;

            double value = r.getLineNetTotal();
            if (value < min) min = value;
            if (value > max) max = value;
        }

        // Eğer tüm değerler aynıysa 0 A BÖLME HATASI
        if (max == min) {
            max = min + 1.0;
        }

        isFitted = true;
    }

  

    private void transform(List<UserRecord> records) {

        if (!isFitted)
            throw new IllegalStateException("PreProcessor fit edilmeden transform cagirilamaz");

        double range = max - min;

        for (UserRecord r : records) {

            if (r == null)
                continue;

        
            double value;

            try {
                value = r.getLineNetTotal();
            } catch (Exception e) {
                continue;
            }

            //encode
            String gender = r.getGender();

            if (gender == null || gender.isEmpty()) {
                r.setGenderEncoded(-1);
            } else if (gender.equalsIgnoreCase("E") || gender.equalsIgnoreCase("Male")) {
                r.setGenderEncoded(1);
            } else {
                r.setGenderEncoded(0);
            }

            if (r.getBrand() == null) {
                r.setBrandEncoded(-1);
            } else {
                r.setBrandEncoded(Math.abs(r.getBrand().hashCode() % 100));
            }
           
            double normalized = (value - min) / range;
            r.setNormalizedTotal(normalized);
        }
    }


}