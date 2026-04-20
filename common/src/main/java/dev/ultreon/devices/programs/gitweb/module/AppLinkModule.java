package dev.ultreon.devices.programs.gitweb.module;

import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.gitweb.GitWebApp;
import dev.ultreon.devices.programs.gitweb.component.GitWebFrame;
import dev.ultreon.devices.programs.system.AppStore;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class AppLinkModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"app"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"text"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return 45;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        int height = calculateHeight(data, width) - 5;
        AppInfo info = ApplicationManager.getApplication(Identifier.tryParse(data.get("app")));

        int section = layout.width / 6;
        Button button = new Button(0, 10, "Install", Icons.IMPORT);
        button.left = section * 5 - 70 - 5;
        button.setSize(70, height - 15);
        button.setClickListener(event -> {
            if (frame.getApp() instanceof GitWebApp gitWeb) {
                DebugLog.log("FRAME");
                gitWeb.getSystem().ifPresent(a -> {
                    DebugLog.log("OPENING APP");
                    var b = a.openApplication(ApplicationManager.getApplication(Identifier.tryParse("devices:app_store")));
                    if (b != null && b instanceof AppStore store) {
                        store.queueOpen(info);
                    }
                });
            }
        });
        layout.addComponent(button);
    }
}
