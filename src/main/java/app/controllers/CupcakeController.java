package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/build", ctx -> showBuild(ctx, connectionPool));
        app.post("remove-from-cart", ctx -> removeFromCart(ctx));
        app.post("/add-to-cart", ctx -> addToCart(ctx, connectionPool));
        app.post("/update-quantity", ctx -> updateQuantity(ctx));
        app.get("/checkout", ctx -> showCheckout(ctx));
        app.post("/checkout", ctx -> processCheckout(ctx, connectionPool));
        app.get("/my-orders", ctx -> showMyOrders(ctx, connectionPool));
        app.get("/my-orders/{orderId}", ctx -> showMyOrderDetails(ctx, connectionPool));
    }

    private static void showBuild(Context ctx, ConnectionPool connectionPool) {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.redirect("/login");
            return;
        }

        List<Bottom> bottoms = BottomMapper.getAllBottoms(connectionPool);
        List<Topping> toppings = ToppingMapper.getAllToppings(connectionPool);
        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            ctx.sessionAttribute("cart", cart);
        }

        ctx.attribute("bottoms", bottoms);
        ctx.attribute("toppings", toppings);
        ctx.attribute("cart", cart);
        ctx.render("build.html");
    }

    private static void updateQuantity(Context ctx) {
        int index = Integer.parseInt(ctx.formParam("index"));
        int change = Integer.parseInt(ctx.formParam("change"));

        Cart cart = ctx.sessionAttribute("cart");
        if (cart != null) {
            cart.updateQuantity(index, change);
            ctx.sessionAttribute("cart", cart);
        }

        ctx.redirect("/build");
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
        ctx.attribute("currentUser", ctx.sessionAttribute("currentUser"));
        ctx.render("build.html");
    }

    private static void addToCart(Context ctx, ConnectionPool connectionPool) {
        String bottomName = ctx.formParam("bottom");
        String toppingName = ctx.formParam("topping");
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        Bottom bottom = BottomMapper.getBottomByName(connectionPool, bottomName);
        Topping topping = ToppingMapper.getToppingByName(connectionPool, toppingName);

        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }

        double totalPrice = bottom.getPrice() + topping.getPrice();
        cart.addOrderLine(new OrderLine(bottom.getName(), topping.getName(), quantity, totalPrice));
        ctx.sessionAttribute("cart", cart);
        ctx.redirect("/build");
    }

    private static void removeFromCart(Context ctx) {
        int index = Integer.parseInt(ctx.formParam("index"));

        Cart cart = ctx.sessionAttribute("cart");
        if (cart != null) {
            cart.removeOrderLine(index);
            ctx.sessionAttribute("cart", cart);
        }


        ctx.redirect("/build");
    }
    private static void showCheckout(Context ctx) {
        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null || cart.getOrderLines().isEmpty()) {
            ctx.redirect("/");
            return;
        }
        ctx.attribute("cart", cart);
        ctx.render("checkout.html");
    }

    private static void processCheckout(Context ctx, ConnectionPool connectionPool) {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.redirect("/login");
            return;
        }

        Cart cart = ctx.sessionAttribute("cart");
        if (cart == null || cart.getOrderLines().isEmpty()) {
            ctx.redirect("/");
            return;
        }

        try {
            int orderId = CheckoutMapper.placeOrder(currentUser.getUserId(), cart, connectionPool);
            ctx.sessionAttribute("cart", new Cart()); // tøm kurven
            ctx.attribute("orderId", orderId);
            ctx.attribute("total", cart.getTotal());
            ctx.render("checkout.html");
        } catch (DatabaseException e) {
            ctx.attribute("cart", cart);
            ctx.attribute("error", e.getMessage());
            ctx.render("checkout.html");
        }
    }
    private static void showMyOrders(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) { ctx.redirect("/login"); return; }

        try {
            ctx.attribute("orders", OrderMapper.getOrdersByUser(user.getUserId(), connectionPool));
            ctx.render("my-orders.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("my-orders.html");
        }
    }

    private static void showMyOrderDetails(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) { ctx.redirect("/login"); return; }

        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            ctx.attribute("orderLines", OrderMapper.getOrderLines(orderId, connectionPool));
            ctx.attribute("orderId", orderId);
            ctx.render("order-details.html");
        } catch (DatabaseException e) {
            ctx.redirect("/my-orders");
        }
    }
}