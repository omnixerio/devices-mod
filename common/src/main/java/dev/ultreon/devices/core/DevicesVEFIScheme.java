package dev.ultreon.devices.core;

import dev.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;

public class DevicesVEFIScheme implements CefResourceHandler {
    private String contentType = null;
    private InputStream is = null;
    private final String url;

    public DevicesVEFIScheme(String url) {
        this.url = url;
    }

    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        String url = this.url.substring("devices-vefi://".length());
        int pos = url.indexOf('/');
        if (pos < 0) {
            cefCallback.cancel();
            return false;
        } else {
            String mod = this.removeSlashes(url.substring(0, pos));
            String loc = this.removeSlashes(url.substring(pos + 1));
            if (!mod.isEmpty() && !loc.isEmpty() && mod.charAt(0) != '.' && loc.charAt(0) != '.') {
                ResourceLocation resourceLocation = ResourceLocation.tryBuild(mod.toLowerCase(Locale.US), "html/" + loc);
                if (resourceLocation == null) {
                    OmnixerioDevicesMod.LOGGER.warn("Resource URL {} NOT found!", this.url);
                    cefCallback.cancel();
                    return false;
                }
                Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
                if (resource.isPresent()) {
                    try {
                        this.is = resource.get().open();
                    } catch (IOException e) {
                        OmnixerioDevicesMod.LOGGER.error("Failed to open resource {}", this.url, e);
                        cefCallback.cancel();
                        return false;
                    }

                    this.contentType = null;
                    pos = loc.lastIndexOf('.');
                    if (pos >= 0 && pos < loc.length() - 2) {
                        this.contentType = MIMEUtil.mimeFromExtension(loc.substring(pos + 1));
                    }

                    cefCallback.Continue();
                    return true;
                } else {
                    OmnixerioDevicesMod.LOGGER.warn("Resource {} NOT found!", resourceLocation);
                    cefCallback.cancel();
                    return false;
                }
            } else {
                OmnixerioDevicesMod.LOGGER.warn("Invalid URL {}", url);
                cefCallback.cancel();
                return false;
            }
        }
    }

    private String removeSlashes(String loc) {
        int i;
        i = 0;
        while (i < loc.length() && loc.charAt(i) == '/') {
            ++i;
        }

        return loc.substring(i);
    }

    public void getResponseHeaders(CefResponse cefResponse, IntRef contentLength, StringRef redir) {
        if (this.contentType != null) {
            cefResponse.setMimeType(this.contentType);
        }

        cefResponse.setStatus(200);
        cefResponse.setStatusText("OK");
        contentLength.set(0);
    }

    public boolean readResponse(byte[] output, int bytesToRead, IntRef bytesRead, CefCallback cefCallback) {
        try {
            int ret = this.is.read(output, 0, bytesToRead);
            if (ret <= 0) {
                this.is.close();
                bytesRead.set(0);
                return false;
            } else {
                bytesRead.set(ret);
                return true;
            }
        } catch (IOException e) {
            OmnixerioDevicesMod.LOGGER.error("Failed to read resource {}", this.url, e);

            try {
                this.is.close();
            } catch (Throwable var7) {
            }

            return false;
        }
    }

    public void cancel() {
        try {
            this.is.close();
        } catch (Throwable var2) {
        }

    }
}
