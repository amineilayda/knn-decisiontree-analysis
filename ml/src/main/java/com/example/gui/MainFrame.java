package com.example.gui;

import com.example.evaluation.Evaluator;
import com.example.evaluation.EvaluatorResult;
import com.example.machineLearning.*;
import com.example.model.UserRecord;
import com.example.util.*;
import javax.swing.*;
import java.awt.*;

import java.util.List;

public class MainFrame extends JFrame {

    private List<UserRecord> allData, trainingSet, testSet;
    private JLabel statusLabel, knnAccLabel, dtAccLabel, knnTimeLabel, dtTimeLabel;
    private JTextField kField, depthField, minLeafField;
    private JTable confusionTable;
    private ChartPanel chartPanel;
    private JRadioButton knnRadio, dtRadio;

    private Evaluator evaluator = new Evaluator();

    public MainFrame() {
        setTitle("ML Projesi");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // üst kisim
        JPanel top = new JPanel();
        JButton loadBtn = new JButton("Veri yükle");
        statusLabel = new JLabel("Henüz dosya seçilmedi");
        top.add(loadBtn);
        top.add(statusLabel);
        add(top, BorderLayout.NORTH);

        // model secim
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createTitledBorder("Model ve parametreler"));

        knnRadio = new JRadioButton("KNN", true);
        dtRadio = new JRadioButton("Decision Tree");
        ButtonGroup group = new ButtonGroup();
        group.add(knnRadio);
        group.add(dtRadio);
        left.add(knnRadio);
        left.add(dtRadio);

        left.add(new JLabel("K Değeri"));
        kField = new JTextField("5");
        left.add(kField);

        left.add(new JLabel("Max Derinlik"));
        depthField = new JTextField("10");
        left.add(depthField);

        left.add(new JLabel("Min Yaprak Sayısı"));
        minLeafField = new JTextField("5");
        left.add(minLeafField);

        JButton runBtn = new JButton("modeli Çalıştır");
        JButton treeBtn = new JButton("Ağacı Göster");
        left.add(Box.createVerticalStrut(10));
        left.add(runBtn);
        left.add(treeBtn);
        add(left, BorderLayout.WEST);
        // sonuc
        JPanel center = new JPanel(new GridLayout(3, 1));
        JPanel metrics = new JPanel(new GridLayout(2, 2));
        knnAccLabel = new JLabel("KNN Doğruluk: -");
        dtAccLabel = new JLabel("DT Doğruluk: -");
        knnTimeLabel = new JLabel("KNN Süre: -");
        dtTimeLabel = new JLabel("DT Süre: -");
        metrics.add(knnAccLabel);
        metrics.add(dtAccLabel);
        metrics.add(knnTimeLabel);
        metrics.add(dtTimeLabel);
        center.add(metrics);

        chartPanel = new ChartPanel();
        center.add(chartPanel);

        confusionTable = new JTable();
        JScrollPane scroll = new JScrollPane(confusionTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Confusion Matrix"));
        center.add(scroll);
        add(center, BorderLayout.CENTER);

        // evet ayarlam
        loadBtn.addActionListener(e -> loadData());
        runBtn.addActionListener(e -> runSelectedModel());
        treeBtn.addActionListener(e -> showDecisionTree());

        // parametre seçimi
        knnRadio.addActionListener(e -> toggleInputs());
        dtRadio.addActionListener(e -> toggleInputs());
        toggleInputs();
    }

    private void toggleInputs() {
        boolean knnSelected = knnRadio.isSelected();
        kField.setVisible(knnSelected);
        for (Component c : kField.getParent().getComponents()) {
            if (c instanceof JLabel && ((JLabel) c).getText().contains("K "))
                c.setVisible(knnSelected);
        }
        depthField.setVisible(!knnSelected);
        minLeafField.setVisible(!knnSelected);
        revalidate();
        repaint();
    }

    // Veri yükleme
    private void loadData() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            DataLoader loader = new DataLoader();
            allData = loader.loadDataFromExcel(fc.getSelectedFile().getAbsolutePath());
            PreProcessor pp = new PreProcessor();
            pp.process(allData);
            trainingSet = pp.getTrainingSet(allData);
            testSet = pp.getTestSet(allData);
            statusLabel.setText("Veri yüklendi: " + allData.size());
        }
    }

    private void runSelectedModel() {

        if (allData == null) {
            JOptionPane.showMessageDialog(this, "önce veri yükleyin");
            return;
        }

        IClassifier model;

        if (knnRadio.isSelected()) {
            model = new KNNClassifier(Integer.parseInt(kField.getText()));
        } else {
            model = new DecisionTreeClassifier(
                    Integer.parseInt(depthField.getText()),
                    Integer.parseInt(minLeafField.getText()));
        }

        long trainStart = System.currentTimeMillis();
        model.train(trainingSet);
        long trainTime = System.currentTimeMillis() - trainStart;

        EvaluatorResult result = evaluator.evaluate(model, testSet);

        double acc = result.accuracy * 100;
        long time = result.totalTime;

        if (model instanceof KNNClassifier) {
            knnAccLabel.setText("KNN Doğruluk: %" + String.format("%.2f", acc));
            knnTimeLabel.setText("KNN Süre: " + (trainTime + time) + " ms");
        } else {
            dtAccLabel.setText("DT Doğruluk: %" + String.format("%.2f", acc));
            dtTimeLabel.setText("DT Süre: " + (trainTime + time) + " ms");
        }

        chartPanel.update(
                knnRadio.isSelected() ? acc : chartPanel.getKnnAcc(),
                dtRadio.isSelected() ? acc : chartPanel.getDtAcc());

        confusionTable.setModel(
                new javax.swing.table.DefaultTableModel(
                        convert(result.confusionMatrix),
                        result.labels));
    }

    private String[][] convert(int[][] matrix) {
        String[][] data = new String[matrix.length][matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                data[i][j] = String.valueOf(matrix[i][j]);
            }
        }
        return data;
    }

    private void showDecisionTree() {
        if (trainingSet == null) {
            JOptionPane.showMessageDialog(this, "önce modeli eğitin");
            return;
        }
        int d = Integer.parseInt(depthField.getText());
        int m = Integer.parseInt(minLeafField.getText());
        DecisionTreeClassifier dt = new DecisionTreeClassifier(d, m);
        dt.train(trainingSet);

        JTextArea area = new JTextArea(dt.toStringTree());
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(600, 500));
        JOptionPane.showMessageDialog(this, scroll,
                "Decision Tree Yapısı", JOptionPane.INFORMATION_MESSAGE);
    }

    static class ChartPanel extends JPanel {
        private double knn = 0, dt = 0;

        public double getKnnAcc() {
            return knn;
        }

        public double getDtAcc() {
            return dt;
        }

        public void update(double k, double d) {
            knn = k;
            dt = d;
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int h = getHeight(), base = h - 60;
            double maxVal = Math.max(knn, dt);
            if (maxVal < 1)
                maxVal = 1;
            double scale = (h - 120) / maxVal;

            g.setColor(Color.BLUE);
            int knnBar = (int) (knn * scale);
            g.fillRect(120, base - knnBar, 100, knnBar);

            g.setColor(Color.RED);
            int dtBar = (int) (dt * scale);
            g.fillRect(300, base - dtBar, 100, dtBar);

            g.setColor(Color.BLACK);
            g.drawString("KNN: " + String.format("%.1f", knn) + "%", 120, base + 20);
            g.drawString("DT: " + String.format("%.1f", dt) + "%", 300, base + 20);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
