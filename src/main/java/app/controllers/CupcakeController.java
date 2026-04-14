package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.get("/", ctx -> {
            ctx.attribute("currentUser", ctx.sessionAttribute("currentUser"));
            ctx.render("index.html");
        });

        app.get("/build", ctx -> showBuild(ctx, connectionPool));

        app.post("/add-to-cart", ctx -> addToCart(ctx, connectionPool));

        app.post("/remove-from-cart", ctx -> removeFromCart(ctx)); // FIX

        app.post("/update-quantity", ctx -> updateQuantity(ctx));

        app.get("/checkout", ctx -> showCheckout(ctx));

        app.post("/checkout", ctx -> processCheckout(ctx, connectionPool));
    }

    private static void showBuild(Context ctx, ConnectionPool cp) {

        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.redirect("/login");
            return;
        }

        List<Bottom> bottoms = BottomMapper.getAllBottoms(cp);
        List<Topping> toppings = ToppingMapper.getAllToppings(cp);

        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null) cart = new Cart();

        ctx.sessionAttribute("cart", cart);

        ctx.attribute("bottoms", bottoms);
        ctx.attribute("toppings", toppings);
        ctx.attribute("cart", cart);
        ctx.attribute("currentUser", ctx.sessionAttribute("currentUser"));

        ctx.render("build.html");
    }

    private static void addToCart(Context ctx, ConnectionPool cp) {

        String bottom = ctx.formParam("bottom");
        String topping = ctx.formParam("topping");
        int qty = Integer.parseInt(ctx.formParam("quantity"));

        Bottom b = BottomMapper.getBottomByName(cp, bottom);
        Topping t = ToppingMapper.getToppingByName(cp, topping);

        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null) cart = new Cart();

        cart.addOrderLine(new OrderLine(
                b.getName(),
                t.getName(),
                qty,
                b.getPrice() + t.getPrice()
        ));

        ctx.sessionAttribute("cart", cart);
        ctx.redirect("/build");
    }

    private static void removeFromCart(Context ctx) {

        int index = Integer.parseInt(ctx.formParam("index"));

        Cart cart = ctx.sessionAttribute("cart");
        if (cart != null) {
            cart.removeOrderLine(index);
        }

        ctx.redirect("/build");
    }

    private static void updateQuantity(Context ctx) {

        int index = Integer.parseInt(ctx.formParam("index"));
        int change = Integer.parseInt(ctx.formParam("change"));

        Cart cart = ctx.sessionAttribute("cart");
        if (cart != null) {
            cart.updateQuantity(index, change);
        }

        ctx.redirect("/build");
    }

    private static void showCheckout(Context ctx) {

        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.redirect("/login");
            return;
        }

        Cart cart = ctx.sessionAttribute("cart");

        if (cart == null || cart.getOrderLines().isEmpty()) {
            ctx.redirect("/build");
            return;
        }

        ctx.attribute("cart", cart);
        ctx.attribute("currentUser", ctx.sessionAttribute("currentUser"));

        ctx.render("checkout.html");
    }

    private static void processCheckout(Context ctx, ConnectionPool cp) {

        User user = ctx.sessionAttribute("currentUser");
        Cart cart = ctx.sessionAttribute("cart");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        if (cart == null || cart.getOrderLines().isEmpty()) {
            ctx.redirect("/build");
            return;
        }

        try {
            double total = cart.getTotal();

            int orderId = CheckoutMapper.placeOrder(user.getUserId(), cart, cp);

            User updatedUser = UserMapper.getUserById(user.getUserId(), cp);
            ctx.sessionAttribute("currentUser", updatedUser);

            ctx.sessionAttribute("cart", new Cart());

            ctx.attribute("orderId", orderId);
            ctx.attribute("total", total);
            ctx.attribute("currentUser", updatedUser);
            ctx.attribute("cart", new Cart());

            ctx.render("checkout.html");

        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.attribute("cart", cart);
            ctx.attribute("currentUser", user);
            ctx.render("checkout.html");
        }
    }
}