package com.example.model;

public class Node {
    public String attribute;    // Bölünme kriter
    public double threshold;    // eşik
    public String value;        // 
    public Node left, right;    
    public boolean isLeaf;      // Yaprak mı
    public String leafCategory; 
    public boolean isCategorical;
    public String categoryValue;

    
    public Node(String category) {
        this.isLeaf = true;
        this.leafCategory = category;
    }
    
    public Node() {
        this.isLeaf = false;
    }

    
    
}