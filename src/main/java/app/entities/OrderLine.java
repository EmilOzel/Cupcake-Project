package app.entities;

public class OrderLine {
    private String bottom;
    private String topping;
    private int quantity;
    private double price;

    public  OrderLine(String bottom, String topping, int quantity, double price) {
        if (quantity < 1 || quantity > 10 || !Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Ugyldig ordrelinje");
        }
        this.bottom = bottom;
        this.topping = topping;
        this.quantity = quantity;
        this.price = price;
    }

    public String getBottom() {
        return bottom;
    }


    public String getTopping() {
        return topping;
    }


    public int getQuantity() {
        return quantity;
    }
    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1 || quantity > 10) throw new IllegalArgumentException("Ugyldigt antal");
        this.quantity = quantity;
    }
}
