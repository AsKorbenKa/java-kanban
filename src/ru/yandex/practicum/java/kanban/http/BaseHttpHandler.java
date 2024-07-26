package ru.yandex.practicum.java.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "BaseHttpHandler response";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }


    protected void sendText(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        String response = "Completed successfully";
        exchange.sendResponseHeaders(201, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Not Found";
        exchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Not Acceptable";
        exchange.sendResponseHeaders(406, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
