package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.CupcakeController;
import app.controllers.UserController;
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
            config.staticFiles.add("/public");
            config.http.maxRequestSize = 32_768;
        });


        app.before(ctx -> {
            String origin = System.getenv("SERVER_ORIGIN");
            ctx.attribute("demoMode", "true".equals(System.getenv("DEMO_MODE")));
            ctx.header("X-Content-Type-Options", "nosniff");
            ctx.header("X-Frame-Options", "DENY");
            if (ctx.method().name().equals("POST")) {
                if (origin != null && !origin.equals(ctx.header("Origin"))) {
                    throw new io.javalin.http.ForbiddenResponse("Ugyldig afsender");
                }
                if (!ctx.path().equals("/login") && !ctx.path().equals("/register")
                        && ctx.sessionAttribute("currentUser") == null) {
                    throw new io.javalin.http.UnauthorizedResponse("Log ind først");
                }
            }
        });
        app.exception(IllegalArgumentException.class, (error, ctx) ->
                ctx.status(400).result("Ugyldige oplysninger. Gå tilbage og prøv igen."));

        CupcakeController.addRoutes(app, connectionPool);
        UserController.addRoutes(app, connectionPool);
        AdminController.addRoutes(app, connectionPool);


        app.start(7070);
    }
}