package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/admin", ctx -> showAdmin(ctx, connectionPool));
        app.post("/admin/delete-user", ctx -> deleteUser(ctx, connectionPool));
        app.post("/admin/add-balance", ctx -> addBalance(ctx, connectionPool));
        app.get("/admin/order/{orderId}", ctx -> showOrder(ctx, connectionPool));
        app.post("/admin/delete-order", ctx -> deleteOrder(ctx, connectionPool));
        app.post("/admin/update-order-status", ctx -> updateOrderStatus(ctx, connectionPool));
    }

    private static boolean isAdmin(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        return user != null && user.isAdmin();
    }

    private static void showAdmin(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
        try {
            ctx.attribute("users", AdminMapper.getAllUsers(connectionPool));
            ctx.attribute("orders", AdminMapper.getAllOrders(connectionPool));
            ctx.render("admin.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("admin.html");
        }
    }

    private static void deleteUser(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
        try {
            int userId = Integer.parseInt(ctx.formParam("userId"));
            AdminMapper.deleteUser(userId, connectionPool);
            ctx.redirect("/admin");
        } catch (DatabaseException e) {
            ctx.redirect("/admin");
        }
    }

    private static void addBalance(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
        try {
            int userId = Integer.parseInt(ctx.formParam("userId"));
            double amount = Double.parseDouble(ctx.formParam("amount"));
            AdminMapper.addBalance(userId, amount, connectionPool);
            ctx.redirect("/admin");
        } catch (DatabaseException e) {
            ctx.redirect("/admin");
        }
    }

    private static void showOrder(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            ctx.attribute("orderLines", OrderMapper.getOrderLines(orderId, connectionPool));
            ctx.attribute("orderId", orderId);
            ctx.render("order-details.html");
        } catch (DatabaseException e) {
            ctx.redirect("/admin");
        }
    }

    private static void deleteOrder(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            AdminMapper.deleteOrder(orderId, connectionPool);
            ctx.redirect("/admin");
        } catch (DatabaseException e) {
            ctx.redirect("/admin");
        }
    }

    private static void updateOrderStatus(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) { ctx.redirect("/"); return; }
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            String status = ctx.formParam("status");
            AdminMapper.updateOrderStatus(orderId, status, connectionPool);
            ctx.redirect("/admin");
        } catch (DatabaseException e) {
            ctx.redirect("/admin");
        }
    }
}