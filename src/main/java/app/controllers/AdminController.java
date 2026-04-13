package app.controllers;

import app.entities.User;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {

    public static void addRoutes(Javalin app, ConnectionPool cp) {

        app.get("/admin", ctx -> {
            if (!isAdmin(ctx)) {
                ctx.redirect("/");
                return;
            }

            ctx.attribute("users", AdminMapper.getAllUsers(cp));
            ctx.attribute("orders", AdminMapper.getAllOrders(cp));
            ctx.attribute("currentUser", ctx.sessionAttribute("currentUser"));

            ctx.render("admin.html");
        });
    }

    private static boolean isAdmin(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        return user != null && user.isAdmin();
    }
}