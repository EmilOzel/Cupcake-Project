package app.entities;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<OrderLine> orderLines = new ArrayList<>();

    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
    }

    public void removeOrderLine(int index) {
        if (index >= 0 && index < orderLines.size()) {
            orderLines.remove(index);
        }
    }

    // Ny metode — +1 eller -1, og fjerner hvis quantity rammer 0
    public void updateQuantity(int index, int change) {
        if (index >= 0 && index < orderLines.size()) {
            OrderLine line = orderLines.get(index);
            int newQty = line.getQuantity() + change;
            if (newQty <= 0) {
                orderLines.remove(index);
            } else {
                line.setQuantity(newQty);
            }
        }
    }

    public List<OrderLine> getOrderLines() { return orderLines; }

    public double getTotal() {
        double total = 0;
        for (OrderLine line : orderLines) {
            total += line.getPrice() * line.getQuantity();
        }
        return total;
    }
    //.
}