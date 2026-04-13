package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> {
            String success = ctx.sessionAttribute("successMessage");
            if (success != null) {
                ctx.attribute("successMessage", success);
                ctx.sessionAttribute("successMessage", null); // ryd efter brug
            }
            ctx.render("login.html");
        });
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/logout", UserController::logout);
        app.get("/register", ctx -> ctx.render("register.html")); // ny
        app.post("/register", ctx -> register(ctx, connectionPool));
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.redirect("/");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }
    private static void register(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            UserMapper.createUser(email, password, connectionPool);
            ctx.sessionAttribute("successMessage", "Konto oprettet! Du kan nu logge ind.");
            ctx.redirect("/login");
        } catch (DatabaseException e) {
            ctx.attribute("registerError", e.getMessage());
            ctx.render("register.html");
        }
    }
}