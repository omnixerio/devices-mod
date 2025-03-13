package dev.ultreon.devices.api.utils;

import dev.ultreon.devices.Devices;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/// OnlineRequest is a simple built in request system for handling URL connections.
/// It runs in the background, so it doesn't freeze the user interface of your application.
/// All requests are returned with a string, use how you please!
///
/// @author MrCrayfish
@SuppressWarnings("unused")
public class OnlineRequest {
    private static OnlineRequest instance = null;

    private final Queue<RequestWrapper> requests;

    private Thread thread;
    private volatile boolean running = true;

    private OnlineRequest() {
        this.requests = new ConcurrentLinkedQueue<>();
        start();
    }

    /// Gets a singleton instance of OnlineRequest. Use this instance to
    /// start making requests.
    ///
    /// @return the singleton OnlineRequest object
    public static OnlineRequest getInstance() {
        if (instance == null) {
            instance = new OnlineRequest();
        }
        return instance;
    }

    public static void checkURLForSuspicions(URI url) throws IOException {
        if (!isSafe(url.getHost()) || !url.getScheme().equals("https")) {
            throw new IOException("Unsafe URL");
        }
    }

    public static boolean isSafeAddress(String address) {
        try {
            URI url = new URI(address);
            return isSafe(url.getHost());
        } catch (Exception e) {
            return false;
        }
    }

    // ignore that
    private static boolean isSafe(String host) {
        return switch (host) {
            case "ultreon.gitlab.io", "cdn.discordapp.com", "jab125.com", "jab125.dev", "raw.githubusercontent.com",
                 "github.com", "i.imgur.com", "i.giphy.com", "avatars1.githubusercontent.com" -> true;
            default -> false;
        };
    }

    private void start() {
        thread = new Thread(new RequestRunnable(), "Online Request Thread");
        thread.start();
    }

    /// Adds a request to the queue. Use the handler to process the
    /// response you get from the URL connection.
    ///
    /// @param url     the URL you want to make a request to
    /// @param handler the response handler for the request
    public void make(String url, ResponseHandler handler) {
        make(url, handler, false);
    }

    /// Adds a request to the queue. Use the handler to process the
    /// response you get from the URL connection.
    ///
    /// @param url         the URL you want to make a request to
    /// @param handler     the response handler for the request
    /// @param bypassCheck if you want to bypass the URL check
    public void make(String url, ResponseHandler handler, boolean bypassCheck) {
        if (!bypassCheck) {
            try {
                checkURLForSuspicions(new URI(url));
            } catch (IOException e) {
                handler.handle(false, e.getMessage().getBytes());
                return;
            } catch (URISyntaxException e) {
                handler.handle(false, "Malformed URL".getBytes());
                return;
            }
        }

        synchronized (requests) {
            requests.offer(new RequestWrapper(url, handler));
            requests.notify();
        }
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

    public interface ResponseHandler {
        /// Handles the response from an OnlineRequest
        ///
        /// @param success  if the request was successful or not
        /// @param response the response from the request. null if success is false
        void handle(boolean success, byte[] response);
    }

    private record RequestWrapper(String url, ResponseHandler handler) {
    }

    private class RequestRunnable implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    synchronized (requests) {
                        requests.wait();
                    }
                } catch (InterruptedException e) {
                    return;
                }

                while (!requests.isEmpty()) {
                    RequestWrapper wrapper = requests.poll();
                    URI url;
                    try {
                        url = new URI(wrapper.url);
                    } catch (URISyntaxException e) {
                        wrapper.handler.handle(false, "Malformed URL".getBytes());
                        continue;
                    }
                    try (HttpClient client = HttpClient.newHttpClient()) {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(url)
                                .GET()
                                .build();
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        try {
                            wrapper.handler.handle(response.statusCode() >= 200 && response.statusCode() < 400, response.body().getBytes());
                        } catch (Exception e) {
                            Devices.LOGGER.error("An error has occurred.", e);
                            try {
                                wrapper.handler.handle(false, "Internal error".getBytes());
                            } catch (Exception ex) {
                                Devices.LOGGER.error("A double fault has occurred.", ex);
                            }
                        }
                    } catch (Exception e) {
                        Devices.LOGGER.error("An error has occurred.", e);
                        try {
                            wrapper.handler.handle(false, "Internal error".getBytes());
                        } catch (Exception ex) {
                            Devices.LOGGER.error("A double fault has occurred.", ex);
                        }
                    }
                }
            }
        }
    }
}
