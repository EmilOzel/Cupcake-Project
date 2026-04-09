package app.controllers;

import app.entities.Bottom;
import app.entities.Cart;
import app.entities.OrderLine;
import app.entities.Topping;
import app.persistence.BottomMapper;
import app.persistence.ConnectionPool;
import app.persistence.ToppingMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> showIndex(ctx, connectionPool));
        app.post("/add-to-cart", ctx -> addToCart(ctx, connectionPool));
        app.post("/update-quantity", ctx -> updateQuantity(ctx)); // ny

    }
    private static void updateQuantity(Context ctx) {
        int index = Integer.parseInt(ctx.formParam("index"));
        int change = Integer.parseInt(ctx.formParam("change")); // +1 eller -1

        Cart cart = ctx.sessionAttribute("cart");
        if (cart != null) {
            cart.updateQuantity(index, change);
            ctx.sessionAttribute("cart", cart);
        }

        ctx.redirect("/");
    }

    private static void showIndex(Context ctx, ConnectionPool connectionPool) {
        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            ctx.sessionAttribute("cart", cart);
        }

        List<Bottom> bottoms = BottomMapper.getAllBottoms(connectionPool);
        List<Topping> toppings = ToppingMapper.getAllToppings(connectionPool);

        ctx.attribute("cart", cart);
        ctx.attribute("bottoms", bottoms);
        ctx.attribute("toppings", toppings);
        ctx.render("index.html");
    }

    private static void addToCart(Context ctx, ConnectionPool connectionPool) {
        String bottomName = ctx.formParam("bottom");
        String toppingName = ctx.formParam("topping");
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        // Slå priser op fra DB
        Bottom bottom = BottomMapper.getBottomByName(connectionPool, bottomName);
        Topping topping = ToppingMapper.getToppingByName(connectionPool, toppingName);

        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
        double totalPrice = bottom.getPrice() + topping.getPrice();
        cart.addOrderLine(new OrderLine(bottom.getName(), topping.getName(), quantity, totalPrice));
        ctx.sessionAttribute("cart", cart);
        ctx.redirect("/");
    }

    private static void removeFromCart(Context ctx) {
        int index = Integer.parseInt(ctx.formParam("index"));

        Cart cart = ctx.sessionAttribute("cart");
        if (cart != null) {
            cart.removeOrderLine(index);
            ctx.sessionAttribute("cart", cart);
        }

        ctx.redirect("/");
    }
}