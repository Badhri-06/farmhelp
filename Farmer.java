package farmhelp.model;

import farmhelp.exception.FarmerException;

public class Farmer extends Person {
    private String crop;
    private double landSize;
    private double income;
    public static final int WORKERS_PER_ACRE = 2;

    public Farmer(String name, String location, String crop, double landSize, double income) throws FarmerException {
        super(name, location);
        this.crop = crop;
        this.landSize = landSize;
        this.income = income;
        if (!validate()) {
            throw new FarmerException("Invalid farmer data");
        }
    }

    public Farmer(int id, String name, String location, String crop, double landSize, double income) {
        super(id, name, location);
        this.crop = crop;
        this.landSize = landSize;
        this.income = income;
    }

    @Override
    public void displayInfo() {
        System.out.println("=== FARMER INFO ===");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Location: " + location);
        System.out.println("Crop: " + crop);
        System.out.println("Land Size: " + landSize + " acres");
        System.out.println("Income: ₹" + income);
    }

    @Override
    public boolean validate() {
        return name != null && !name.trim().isEmpty() &&
                location != null && !location.trim().isEmpty() &&
                crop != null && !crop.trim().isEmpty() &&
                landSize > 0 && income >= 0;
    }

    public int getMaxWorkersAllowed() {
        return (int)(landSize * WORKERS_PER_ACRE);
    }

    public String getCrop() { return crop; }
    public double getLandSize() { return landSize; }
    public double getIncome() { return income; }
}