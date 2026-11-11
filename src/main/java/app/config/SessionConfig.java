package app.config;
import jakarta.servlet.SessionTrackingMode;
import org.eclipse.jetty.server.session.SessionHandler;

import java.util.EnumSet;

public class SessionConfig {

    public static SessionHandler sessionConfig()
    {
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setUsingCookies(true);
        sessionHandler.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
        sessionHandler.setHttpOnly(true);
        sessionHandler.setMaxInactiveInterval(1800);
        sessionHandler.getSessionCookieConfig().setSecure(
                System.getenv().getOrDefault("SERVER_ORIGIN", "").startsWith("https://"));
        return sessionHandler;
    }
}

