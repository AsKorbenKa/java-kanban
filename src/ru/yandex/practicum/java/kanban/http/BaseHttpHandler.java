package ru.yandex.practicum.java.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public abstract class BaseHttpHandler implements HttpHandler {
    public static final Logger logger = Logger.getLogger(BaseHttpHandler.class.getName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "BaseHttpHandler response";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendText(HttpExchange exchange, String response) throws IOException {
        logger.info(response);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        String response = "Completed successfully";
        logger.info(response);
        exchange.sendResponseHeaders(201, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Not Found";
        logger.warning(response);
        exchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Server could not produce a response because the task execution time intersects with " +
                "another task";
        logger.warning(response);
        exchange.sendResponseHeaders(406, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
