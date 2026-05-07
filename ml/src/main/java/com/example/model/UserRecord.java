package com.example.model;

public class UserRecord {

    private String clientCode;
    private String gender;
    private double lineNetTotal;
    private String brand;
    private String category;
    private double price;
    private int amount;

    
    private int genderEncoded;
    private int brandEncoded;
    private double normalizedTotal;

    
    public UserRecord(String clientCode, String gender, double lineNetTotal,
                      String brand, String category, double price, int amount) {

        this.clientCode = clientCode;
        this.gender = gender;
        this.lineNetTotal = lineNetTotal;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.amount = amount;
    }

    
    public String getClientCode() { return clientCode; }
    public String getGender() { return gender; }
    public double getLineNetTotal() { return lineNetTotal; }
    public String getBrand() { return brand; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getAmount() { return amount; }

    public int getGenderEncoded() { return genderEncoded; }
    public int getBrandEncoded() { return brandEncoded; }
    public double getNormalizedTotal() { return normalizedTotal; }


    public void setGenderEncoded(int genderEncoded) {
        this.genderEncoded = genderEncoded;
    }
    public void setBrandEncoded(int brandEncoded) {
        this.brandEncoded = brandEncoded;
    }
    public void setNormalizedTotal(double normalizedTotal) {
        this.normalizedTotal = normalizedTotal;
    }

    public double[] getFeatureVector() {
        return new double[] {
                genderEncoded,
                brandEncoded,
                amount,
                normalizedTotal
        };
    }

    @Override
    public String toString() {
        return "UserRecord{" +
                "clientCode='" + clientCode + '\'' +
                ", gender='" + gender + '\'' +
                ", total=" + lineNetTotal +
                ", category='" + category + '\'' +
                '}';
    }

    public double getNumericValue(String featureName) {
        switch (featureName.toUpperCase()) {
            case "PRICE":   return this.price;
            case "AMOUNT":  return (double) this.amount;
            case "TOTAL":   return this.normalizedTotal; 
            case "GENDER":  return (double) this.genderEncoded;
            case "BRAND":   return (double) this.brandEncoded;
            default:        return 0.0;
        }
    }
    
}