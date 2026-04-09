package app.entities;

public class Bottom {
    private int bottomId;
    private String name;
    private double price;

    public Bottom(int bottomId, String name, double price) {
        this.bottomId = bottomId;
        this.name = name;
        this.price = price;
    }

    public int getBottomId() {return bottomId;}

    public void setBottomId(int bottomId) {this.bottomId = bottomId;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public double getPrice() {return price;}

    public void setPrice(double price) {this.price = price;}
}
