package app.entities;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<OrderLine> orderLines = new ArrayList<>();

    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }
}