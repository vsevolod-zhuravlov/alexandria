package com.endpoints;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer {
    private static final int PORT = 8080;

    private Logger logger = LoggerFactory.getLogger(JettyServer.class);

    private Server server;

    public JettyServer() {
        this.server = new Server();

        // Configure connector
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(PORT);
        server.addConnector(connector);

        // Create servlet context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Configure Jersey servlets
        ResourceConfig resourceConfig = new JettyServerResourceConfig();
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(resourceConfig));
        jerseyServlet.setInitOrder(0);
        context.addServlet(jerseyServlet, "/*");

        server.setHandler(context);
    }

    public void start() throws Exception {
        logger.info("Starting Jetty Server...");
        server.start();
        server.join();
    }

    public void stop() throws Exception {
        logger.info("Stopping Jetty Server...");
        server.stop();
    }
}
