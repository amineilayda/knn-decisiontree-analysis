package com.example;

import java.util.List;

import javax.swing.SwingUtilities;
import com.example.evaluation.Evaluator;
import com.example.gui.MainFrame;
import com.example.machineLearning.DecisionTreeClassifier;
import com.example.machineLearning.IClassifier;
import com.example.machineLearning.KNNClassifier;
import com.example.model.UserRecord;
import com.example.util.DataLoader;
import com.example.util.PreProcessor;

public class Main {
    public static void main(String[] args) {

      /*   System.out.println("baslatiliyor...");
        DataLoader loader = new DataLoader();
        List<UserRecord> data = loader.loadDataFromExcel("src/main/resources/MarketSalesKocaeli.xlsx");

        if (data.isEmpty()) {
            System.out.println("veri yuklenemedi");
            return;
        }

    
        PreProcessor processor = new PreProcessor();
        processor.process(data);
        List<UserRecord> trainingSet = processor.getTrainingSet(data);
        List<UserRecord> testSet = processor.getTestSet(data);

        System.out.println("bilgiler");
        System.out.println("Egitim verisi sayisi: " + trainingSet.size());
        System.out.println("test verisi sayis:   " + testSet.size());

        
        Evaluator evaluator = new Evaluator();

        IClassifier knn = new KNNClassifier(11);
        knn.train(trainingSet);
        evaluator.evaluate(knn, testSet);

         

        IClassifier decisionTree = new DecisionTreeClassifier(8, 5);
        decisionTree.train(trainingSet);
        evaluator.evaluate(decisionTree, testSet);
         decisionTree.printTree();
        

        
        if (!testSet.isEmpty()) {
            UserRecord sample = testSet.get(0);

            System.out.println("\orn");
            System.out.println("Gercek: " + sample.getCategory());
            System.out.println("KNN Tahmin: " + knn.predict(sample));
            System.out.println("Tree Tahmin: " + decisionTree.predict(sample));
        }

        */
       

        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        }); 
    }
}