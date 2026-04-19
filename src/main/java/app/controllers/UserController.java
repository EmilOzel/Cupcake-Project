package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/logout", UserController::logout);
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
            User user = UserMapper.register(email, password, connectionPool);

            ctx.sessionAttribute("currentUser", user);

            ctx.redirect("/");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("register.html");
        }
    }
    //SDD
    //SDD

}