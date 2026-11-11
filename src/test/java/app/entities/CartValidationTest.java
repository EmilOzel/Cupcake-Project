package app.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartValidationTest {
    @Test
    void negativeQuantitiesCannotCreateCreditAtCheckout() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderLine("Chokolade", "Vanilje", -1, 9));
        assertThrows(IllegalArgumentException.class,
                () -> new OrderLine("Chokolade", "Vanilje", 1, Double.NaN));
    }

    @Test
    void quantityChangesRespectLimitsAndRemoveEmptyLines() {
        Cart cart = new Cart();
        cart.addOrderLine(new OrderLine("Chokolade", "Vanilje", 10, 9));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(0, 1));
        assertEquals(90, cart.getTotal());
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(0, -100));
        assertEquals(90, cart.getTotal());
        Cart single = new Cart();
        single.addOrderLine(new OrderLine("Chokolade", "Vanilje", 1, 9));
        single.updateQuantity(0, -1);
        assertTrue(single.getOrderLines().isEmpty());
    }
}
