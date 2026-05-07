package com.example.machineLearning;

import com.example.model.UserRecord;
import com.example.model.Node;
import java.util.*;

public class DecisionTreeClassifier extends BaseAlgorithm {

    private Node root;
    private final int maxDepth;
      private final int minSamples;

    private final String[] features = { "PRICE", "AMOUNT", "TOTAL", "GENDER", "BRAND" };

    public DecisionTreeClassifier(int depth, int minSamples) {
        this.maxDepth = depth;
        this.minSamples = minSamples;
    }

    @Override
    public void train(List<UserRecord> data) {

        long start = System.currentTimeMillis();

        root = build(data, 0);

        executionTime = System.currentTimeMillis() - start;
    }
    @Override
    public String predict(UserRecord user) {

        if (root == null) {
            return "Unknown";
        }

        return walk(root, user);
    }

    
    private Node build(List<UserRecord> data, int depth) {

        //kontroller
        if (data.isEmpty() || depth >= maxDepth || data.size() < minSamples || isPure(data))
            return new Node(findMostFrequentCategory(data));

        Split best = bestSplit(data);
        if (best == null)
            return new Node(findMostFrequentCategory(data));

        Node node = new Node();
        node.attribute = best.feature;
        node.isCategorical = best.isCat;

        if (best.isCat)
            node.categoryValue = (String) best.value;
        else
            node.threshold = (double) best.value;

        node.left = build(best.left, depth + 1);
        node.right = build(best.right, depth + 1);

        return node;
    }

    private Split bestSplit(List<UserRecord> data) {

        //en iyi ayrımı bulma
        Split best = null;
        double bestImp = Double.MAX_VALUE;

        for (String f : features) {
            for (Split s : computeSplits(data, f)) {
                if (s.impurity < bestImp) {
                    bestImp = s.impurity;
                    best = s;
                }
            }
        }
        return best;
    }

    private List<Split> computeSplits(List<UserRecord> data, String feature) {

        List<Split> out = new ArrayList<>();
        boolean isCategorical = feature.equals("GENDER") || feature.equals("BRAND");

        if (!isCategorical) {

            List<Double> vals = new ArrayList<>();
            for (UserRecord r : data)
                vals.add(getNumeric(r, feature));
            Collections.sort(vals);

            for (int i = 1; i < vals.size(); i++) {
                if (vals.get(i).equals(vals.get(i - 1)))
                    continue;

                double thr = (vals.get(i) + vals.get(i - 1)) / 2;

                List<UserRecord> L = new ArrayList<>();
                List<UserRecord> R = new ArrayList<>();

                for (UserRecord r : data) {
                    if (getNumeric(r, feature) <= thr)
                        L.add(r);
                    else
                        R.add(r);
                }

                if (!L.isEmpty() && !R.isEmpty())
                    out.add(new Split(feature, thr, L, R, false));
            }
        } else {
            // categorical ise
            Set<String> cats = new HashSet<>();
            for (UserRecord r : data)
                cats.add(getCategorical(r, feature));

            for (String c : cats) {

                List<UserRecord> L = new ArrayList<>();
                List<UserRecord> R = new ArrayList<>();

                for (UserRecord r : data) {
                    if (getCategorical(r, feature).equals(c))
                        L.add(r);
                    else
                        R.add(r);
                }

                if (!L.isEmpty() && !R.isEmpty())
                    out.add(new Split(feature, c, L, R, true));
            }
        }

        // impurity hesapla
        for (Split s : out)
            s.impurity = weightedEntropy(s.left, s.right);

        return out;
    }

    private double weightedEntropy(List<UserRecord> L, List<UserRecord> R) {
        double total = L.size() + R.size();
        return (L.size() / total) * entropy(L)
                + (R.size() / total) * entropy(R);
    }

    private double entropy(List<UserRecord> data) {
        Map<String, Integer> map = new HashMap<>();
        for (UserRecord r : data)
            map.merge(r.getCategory(), 1, Integer::sum);
        //entorpy formül
        double e = 0;
        for (int c : map.values()) {
            double p = c / (double) data.size();
            e -= p * (Math.log(p) / Math.log(2));
        }
        return e;
    }
    private boolean isPure(List<UserRecord> data) {
        String c = data.get(0).getCategory();
        for (UserRecord r : data)
            if (!r.getCategory().equals(c))
                return false;
        return true;
    }

    private String walk(Node n, UserRecord r) {

        if (n.isLeaf)
            return n.leafCategory;

        if (n.isCategorical) {
            String v = getCategorical(r, n.attribute);
            return walk(v.equals(n.categoryValue) ? n.left : n.right, r);
        }

        double v = getNumeric(r, n.attribute);
        return walk(v <= n.threshold ? n.left : n.right, r);
    }

    //tree yazdırma
    @Override
    public String toStringTree() {
        StringBuilder sb = new StringBuilder();
        dump(root, "", true, sb);
        return sb.toString();
    }

    private void dump(Node n, String pre, boolean last, StringBuilder sb) {
        if (n == null)
            return;

        sb.append(pre).append(last ? "└── " : "├── ");

        if (n.isLeaf)
            sb.append("CLASS: ").append(n.leafCategory).append("\n");
        else if (n.isCategorical)
            sb.append(n.attribute).append(" == ").append(n.categoryValue).append("\n");
        else
            sb.append(n.attribute).append(" <= ").append(n.threshold).append("\n");

        if (!n.isLeaf) {
            dump(n.left, pre + (last ? "    " : "│   "), false, sb);
            dump(n.right, pre + (last ? "    " : "│   "), true, sb);
        }
    }
    private static class Split {
        String feature;
        Object value;
        boolean isCat;
        double impurity;
        List<UserRecord> left, right;

        Split(String f, Object v, List<UserRecord> L, List<UserRecord> R, boolean cat) {
            feature = f;
            value = v;
            left = L;
            right = R;
            isCat = cat;
        }
    }
}
