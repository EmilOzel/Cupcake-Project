package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {

    public static void addRoutes(Javalin app, ConnectionPool cp) {

        app.get("/admin", ctx -> {
            if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
            ctx.attribute("users", AdminMapper.getAllUsers(cp));
            ctx.attribute("orders", AdminMapper.getAllOrders(cp));
            ctx.render("admin.html");
        });

        app.get("/admin/order/{orderId}", ctx -> {
            if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            ctx.attribute("orderLines", OrderMapper.getOrderLines(orderId, cp));
            ctx.attribute("orderId", orderId);
            ctx.render("order-details.html");
        });

        app.post("/admin/delete-user", ctx -> {
            if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
            int userId = Integer.parseInt(ctx.formParam("userId"));
            AdminMapper.deleteUser(userId, cp);
            ctx.redirect("/admin");
        });

        app.post("/admin/add-balance", ctx -> {
            if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
            int userId = Integer.parseInt(ctx.formParam("userId"));
            double amount = Double.parseDouble(ctx.formParam("amount"));
            AdminMapper.addBalance(userId, amount, cp);
            ctx.redirect("/admin");
        });

        app.post("/admin/delete-order", ctx -> {
            if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            AdminMapper.deleteOrder(orderId, cp);
            ctx.redirect("/admin");
        });

        app.post("/admin/update-order-status", ctx -> {
            if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            String status = ctx.formParam("status");
            AdminMapper.updateOrderStatus(orderId, status, cp);
            ctx.redirect("/admin");
        });
    }

    private static boolean isAdmin(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        return user != null && user.isAdmin();
    }
}