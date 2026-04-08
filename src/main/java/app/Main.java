package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.entities.Cart;
import app.entities.OrderLine;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake";

    private static final ConnectionPool connectionPool =
            ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.jetty.modifyServletContextHandler(
                    handler -> handler.setSessionHandler(SessionConfig.sessionConfig())
            );
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            // config.staticFiles.add("/public"); // brug denne hvis du har css
        });

        app.get("/", ctx -> {
            Cart cart = ctx.sessionAttribute("cart");

            if (cart == null) {
                cart = new Cart();
                ctx.sessionAttribute("cart", cart);
            }

            ctx.attribute("cart", cart);
            ctx.render("index.html");
        });

        app.post("/remove-from-cart", ctx -> {
            int index = Integer.parseInt(ctx.formParam("index"));

            Cart cart = ctx.sessionAttribute("cart");

            if (cart != null) {
                cart.removeOrderLine(index);
                ctx.sessionAttribute("cart", cart);
            }

            ctx.redirect("/");
        });

        app.post("/add-to-cart", ctx -> {
            String bottom = ctx.formParam("bottom");
            String topping = ctx.formParam("topping");
            int quantity = Integer.parseInt(ctx.formParam("quantity"));

            Cart cart = ctx.sessionAttribute("cart");

            if (cart == null) {
                cart = new Cart();
            }

            cart.addOrderLine(new OrderLine(bottom, topping, quantity));

            ctx.sessionAttribute("cart", cart);
            ctx.redirect("/");
        });

        app.start(7070);
    }
}