//package controllers;
//
//import app.entities.LifehackSite;
//import app.persistence.ConnectionPool;
//import io.javalin.Javalin;
//import io.javalin.http.Context;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainController {
//
//    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
//        app.get("/", ctx -> index(ctx));
//    }
//
//    private static void index(@NotNull Context ctx) {
//
//        ctx.attribute("lifehackSites", lifehackSites);
//        ctx.render("index.html");
//    }
//}