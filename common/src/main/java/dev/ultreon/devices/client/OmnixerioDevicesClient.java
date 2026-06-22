package dev.ultreon.devices.client;

import com.cinemamod.mcef.MCEF;
import dev.ultreon.devices.core.DevicesVEFIScheme;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

public class OmnixerioDevicesClient {
    public static void init() {
        MCEF.scheduleForInit(OmnixerioDevicesClient::onInit);
    }

    private static void onInit(boolean b) {
        MCEF.getClient().getHandle().addMessageRouter(CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("devicesVefiQuery", "devicesVefiCancel"), new DevicesVefiRouter()));
        MCEF.getApp().getHandle().registerSchemeHandlerFactory("devices-vefi", "", OmnixerioDevicesClient::createVefiScheme);
//        MCEF.getApp().getHandle().adregisterSchemeHandlerFactory("devices-vefi", "", OmnixerioDevicesClient::createVefiScheme);
    }

    private static CefResourceHandler createVefiScheme(CefBrowser browser, CefFrame frame, String url1, CefRequest request) {
        return new DevicesVEFIScheme(request.getURL());
    }

}
