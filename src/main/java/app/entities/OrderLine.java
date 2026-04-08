package app.entities;

public class OrderLine {
    private String bottom;
    private String topping;
    private int quantity;

    public  OrderLine(String bottom, String topping, int quantity) {
        this.bottom = bottom;
        this.topping = topping;
        this.quantity = quantity;
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
}
