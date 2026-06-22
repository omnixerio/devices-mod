package dev.ultreon.devices.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.core.ModernLaptop;
import dev.ultreon.devices.core.io.task.TaskVefiFileOperation;
import dev.ultreon.devices.core.network.NetworkDevice;
import dev.ultreon.devices.core.network.Router;
import dev.ultreon.devices.init.ModDataComponents;
import dev.ultreon.devices.item.EthernetCableItem;
import dev.ultreon.devices.item.data.EthernetConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import java.util.UUID;

class DevicesVefiRouter extends CefMessageRouterHandlerAdapter {
    private static final Gson GSON = new Gson();

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        OmnixerioDevicesMod.LOGGER.info("VEFI query: type={}, request={}", persistent, request);

        try {
            JsonObject json = JsonParser.parseString(request).getAsJsonObject();
            String type = json.get("type").getAsString();

            switch (type) {
                case "file_read" -> handleFileRead(json, callback);
                case "file_write" -> handleFileWrite(json, callback);
                case "file_list" -> handleFileList(json, callback);
                case "file_mkdir" -> handleFileMkdir(json, callback);
                case "file_exists" -> handleFileExists(json, callback);
                case "file_delete" -> handleFileDelete(json, callback);
                case "ethernet_status" -> handleEthernetStatus(callback);
                case "wifi_scan" -> handleWifiScan(callback);
                case "wifi_connect" -> handleWifiConnect(json, callback);
                default -> callback.failure(404, "Unknown VEFI request type: " + type);
            }
        } catch (JsonSyntaxException e) {
            callback.failure(400, "Invalid JSON: " + e.getMessage());
        } catch (Exception e) {
            OmnixerioDevicesMod.LOGGER.error("VEFI query error", e);
            callback.failure(500, "Internal error: " + e.getMessage());
        }
        return true;
    }

    @Override
    public void onQueryCanceled(CefBrowser cefBrowser, CefFrame cefFrame, long l) {
        OmnixerioDevicesMod.LOGGER.debug("VEFI query canceled: {}", l);
    }

    private void handleFileRead(JsonObject json, CefQueryCallback callback) {
        String path = json.get("path").getAsString();
        runOnMain(() -> {
            var task = new TaskVefiFileOperation(getLaptopPos(), "read", path, null);
            task.setCallback((tag, success) -> {
                if (success && tag.getBoolean("success")) {
                    JsonObject response = new JsonObject();
                    response.addProperty("content", tag.getString("content"));
                    callback.success(GSON.toJson(response));
                } else {
                    callback.failure(404, "File not found: " + path);
                }
            });
            TaskManager.sendTask(task);
        });
    }

    private void handleFileWrite(JsonObject json, CefQueryCallback callback) {
        String path = json.get("path").getAsString();
        String content = json.get("content").getAsString();
        runOnMain(() -> {
            var task = new TaskVefiFileOperation(getLaptopPos(), "write", path, content);
            task.setCallback((tag, success) -> {
                if (success && tag.getBoolean("success")) {
                    JsonObject response = new JsonObject();
                    response.addProperty("success", true);
                    callback.success(GSON.toJson(response));
                } else {
                    callback.failure(500, "Failed to write file: " + path);
                }
            });
            TaskManager.sendTask(task);
        });
    }

    private void handleFileList(JsonObject json, CefQueryCallback callback) {
        String path = json.get("path").getAsString();
        runOnMain(() -> {
            var task = new TaskVefiFileOperation(getLaptopPos(), "list", path, null);
            task.setCallback((tag, success) -> {
                if (success && tag.getBoolean("success")) {
                    JsonArray files = new JsonArray();
                    var filesList = tag.getList("files", 10);
                    for (int i = 0; i < filesList.size(); i++) {
                        files.add(filesList.getCompound(i).getString("name"));
                    }
                    JsonObject response = new JsonObject();
                    response.add("files", files);
                    callback.success(GSON.toJson(response));
                } else {
                    callback.failure(404, "Folder not found: " + path);
                }
            });
            TaskManager.sendTask(task);
        });
    }

    private void handleFileMkdir(JsonObject json, CefQueryCallback callback) {
        String path = json.get("path").getAsString();
        runOnMain(() -> {
            var task = new TaskVefiFileOperation(getLaptopPos(), "mkdir", path, null);
            task.setCallback((tag, success) -> {
                if (success && tag.getBoolean("success")) {
                    JsonObject response = new JsonObject();
                    response.addProperty("success", true);
                    callback.success(GSON.toJson(response));
                } else {
                    callback.failure(500, "Failed to create directory: " + path);
                }
            });
            TaskManager.sendTask(task);
        });
    }

    private void handleFileExists(JsonObject json, CefQueryCallback callback) {
        String path = json.get("path").getAsString();
        runOnMain(() -> {
            var task = new TaskVefiFileOperation(getLaptopPos(), "exists", path, null);
            task.setCallback((tag, success) -> {
                boolean exists = tag.getBoolean("exists");
                JsonObject response = new JsonObject();
                response.addProperty("exists", exists);
                callback.success(GSON.toJson(response));
            });
            TaskManager.sendTask(task);
        });
    }

    private void handleFileDelete(JsonObject json, CefQueryCallback callback) {
        String path = json.get("path").getAsString();
        runOnMain(() -> {
            var task = new TaskVefiFileOperation(getLaptopPos(), "delete", path, null);
            task.setCallback((tag, success) -> {
                if (success && tag.getBoolean("success")) {
                    JsonObject response = new JsonObject();
                    response.addProperty("success", true);
                    callback.success(GSON.toJson(response));
                } else {
                    callback.failure(404, "File not found: " + path);
                }
            });
            TaskManager.sendTask(task);
        });
    }

    private void handleEthernetStatus(CefQueryCallback callback) {
        runOnMain(() -> {
            try {
                boolean connected = isEthernetConnected();
                JsonObject response = new JsonObject();
                response.addProperty("connected", connected);
                callback.success(GSON.toJson(response));
            } catch (Exception e) {
                callback.failure(500, e.getMessage());
            }
        });
    }

    private void handleWifiScan(CefQueryCallback callback) {
        runOnMain(() -> {
            try {
                JsonArray networks = new JsonArray();
                Router router = getConnectedRouter();
                if (router != null) {
                    Level level = Minecraft.getInstance().level;
                    if (level != null) {
                        for (NetworkDevice device : router.getConnectedDevices(level)) {
                            if (device.getId() != null) {
                                JsonObject network = new JsonObject();
                                network.addProperty("name", device.getName());
                                network.addProperty("uuid", device.getId().toString());
                                networks.add(network);
                            }
                        }
                    }
                }
                JsonObject response = new JsonObject();
                response.add("networks", networks);
                callback.success(GSON.toJson(response));
            } catch (Exception e) {
                callback.failure(500, e.getMessage());
            }
        });
    }

    private void handleWifiConnect(JsonObject json, CefQueryCallback callback) {
        String uuidStr = json.has("network") ? json.getAsJsonObject("network").get("uuid").getAsString() : null;
        String name = json.has("network") ? json.getAsJsonObject("network").get("name").getAsString() : null;

        runOnMain(() -> {
            try {
                boolean connected = false;
                String errorMessage = null;

                if (uuidStr == null) {
                    errorMessage = "No network specified";
                } else {
                    Router router = getConnectedRouter();
                    if (router == null) {
                        errorMessage = "No router available";
                    } else {
                        Level level = Minecraft.getInstance().level;
                        if (level != null) {
                            UUID uuid = UUID.fromString(uuidStr);
                            for (NetworkDevice device : router.getConnectedDevices(level)) {
                                if (device.getId().equals(uuid)) {
                                    connected = true;
                                    break;
                                }
                            }
                            if (!connected) {
                                errorMessage = "Network not found: " + name;
                            }
                        }
                    }
                }

                JsonObject response = new JsonObject();
                response.addProperty("connected", connected);
                if (errorMessage != null) {
                    response.addProperty("errorMessage", errorMessage);
                }
                callback.success(GSON.toJson(response));
            } catch (Exception e) {
                callback.failure(500, e.getMessage());
            }
        });
    }

    // ========== Network helpers ==========

    private boolean isEthernetConnected() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        for (int i = 0; i < mc.player.getInventory().getContainerSize(); i++) {
            var stack = mc.player.getInventory().getItem(i);
            if (stack.getItem() instanceof EthernetCableItem) {
                EthernetConnection connection = stack.get(ModDataComponents.ETHERNET_CONNECTION.get());
                if (connection != null && connection.devicePos() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private Router getConnectedRouter() {
        ComputerBlockEntity be = getBlockEntity();
        if (be == null) return null;

        Level level = Minecraft.getInstance().level;
        if (level == null) return null;

        BlockPos routerPos = be.getConnection() != null ? be.getConnection().getRouterPos() : null;
        if (routerPos == null) return null;

        BlockEntity routerBe = level.getBlockEntity(routerPos);
        if (routerBe instanceof RouterBlockEntity router) {
            return router.getRouter();
        }
        return null;
    }

    // ========== Position helpers ==========

    private static BlockPos getLaptopPos() {
        BlockPos pos = Laptop.getPos();
        if (pos == null) {
            pos = ModernLaptop.getPos();
            if (pos == null)
                throw new IllegalStateException("No laptop position available");
        }
        return pos;
    }

    private ComputerBlockEntity getBlockEntity() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;

        BlockPos laptopPos = Laptop.getPos();
        if (laptopPos == null) {
            laptopPos = ModernLaptop.getPos();
            if (laptopPos == null)
                return null;
        }

        BlockEntity be = mc.level.getBlockEntity(laptopPos);
        if (be instanceof ComputerBlockEntity computer) {
            return computer;
        }
        return null;
    }

    private static void runOnMain(Runnable task) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isSameThread()) {
            task.run();
        } else {
            mc.execute(task);
        }
    }
}
