package com.example.machineLearning;

import java.util.List;

import com.example.model.UserRecord;

public interface IClassifier {

    void train(List<UserRecord> trainingData); // Öğrenme aşaması
    String predict(UserRecord user); //tahmin
    
    String toStringTree();
}
