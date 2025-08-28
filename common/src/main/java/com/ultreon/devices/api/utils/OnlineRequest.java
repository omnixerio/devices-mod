package com.ultreon.devices.api.utils;

import com.ultreon.devices.util.StreamUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * OnlineRequest is a simple built in request system for handling URL connections.
 * It runs in the background, so it doesn't freeze the user interface of your application.
 * All requests are returned with a string, use how you please!
 *
 * @author MrCrayfish
 */
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

    /**
     * Gets a singleton instance of OnlineRequest. Use this instance to
     * start making requests.
     *
     * @return the singleton OnlineRequest object
     */
    public static OnlineRequest getInstance() {
        if (instance == null) {
            instance = new OnlineRequest();
        }
        return instance;
    }

    public static void checkURLForSuspicions(URL url) throws IOException {
        if (!isSafe(url.getHost()) || !url.getProtocol().equals("https")) {
            throw new IOException("Unsafe URL");
        }
    }

    public static boolean isSafeAddress(String address) {
        try {
            URL url = new URL(address);
            return isSafe(url.getHost());
        } catch (Exception e) {
            return false;
        }
    }

    // ignore that
    private static boolean isSafe(String host) {
        return switch (host) {
            case "ultreon.gitlab.io", "cdn.discordapp.com", "jab125.com", "jab125.dev", "raw.githubusercontent.com", "github.com", "i.imgur.com", "i.giphy.com", "avatars1.githubusercontent.com" -> true;
            default -> false;
        };
    }

    private void start() {
        thread = new Thread(new RequestRunnable(), "Online Request Thread");
        thread.start();
    }

    /**
     * Adds a request to the queue. Use the handler to process the
     * response you get from the URL connection.
     *
     * @param url     the URL you want to make a request to
     * @param handler the response handler for the request
     */
    public void make(String url, ResponseHandler handler) {
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
        /**
         * Handles the response from an OnlineRequest
         *
         * @param success  if the request was successful or not
         * @param response the response from the request. null if success is false
         */
        void handle(boolean success, String response);
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
                    try {
                        URL url = new URL(wrapper.url);
                        checkURLForSuspicions(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                        wrapper.handler.handle(false, "DOMAIN NOT BLACKLISTED/ERROR PARSING DOMAIN");
                        continue;
                    }
                    try (CloseableHttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build()).build()) {
                        HttpGet get = new HttpGet(wrapper.url);
                        try (CloseableHttpResponse response = client.execute(get)) {
                            String raw = StreamUtils.convertToString(response.getEntity().getContent());
                            wrapper.handler.handle(true, raw);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        wrapper.handler.handle(false, "");
                    }
                }
            }
        }
    }
}
