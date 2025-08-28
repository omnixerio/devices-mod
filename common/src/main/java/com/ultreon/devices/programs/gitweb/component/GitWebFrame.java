package com.ultreon.devices.programs.gitweb.component;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.ScrollableLayout;
import com.ultreon.devices.api.app.component.Text;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.programs.gitweb.layout.ModuleLayout;
import com.ultreon.devices.programs.gitweb.module.Module;
import com.ultreon.devices.programs.gitweb.module.*;
import com.ultreon.devices.util.SiteRegistration;
import net.minecraft.client.Minecraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MrCrayfish
 */
@SuppressWarnings("unused")
public class GitWebFrame extends Component {
    public static final Pattern PATTERN_LINK = Pattern.compile("(?<domain>[a-zA-Z0-9\\p{sc=Han}\\p{InHiragana}\\p{InKatakana}\\-]+)\\.(?<extension>[a-zA-Z0-9\\p{sc=Han}\\p{InHiragana}\\p{InKatakana}]+)(?<directory>(/[a-zA-Z0-9\\p{sc=Han}\\p{InHiragana}\\p{InKatakana}\\-]+)*)(/)?");
    private static final Map<String, Module> MODULES = new HashMap<>();

    static {
        MODULES.put("header", new HeaderModule());
        MODULES.put("footer", new FooterModule());
        MODULES.put("paragraph", new ParagraphModule());
        MODULES.put("divider", new DividerModule());
        MODULES.put("banner", new BannerModule());
        MODULES.put("navigation", new NavigationModule());
        MODULES.put("crafting", new CraftingModule());
        MODULES.put("furnace", new FurnaceModule());
        MODULES.put("brewing", new BrewingModule());
        MODULES.put("anvil", new AnvilModule());
        MODULES.put("loom", new LoomModule());
        MODULES.put("download", new DownloadModule());
        MODULES.put("redirect", new RedirectModule());
        MODULES.put("applink", new AppLinkModule());
        MODULES.put("credits", new AppLinkModule());
        MODULES.put("script", new ScriptModule());
        MODULES.put("bannerII", new BannerIIModule());
    }

    private final Application app;
    public final ScrollableLayout layout;
    private final int width;
    private final int height;

    private boolean allowRemoteUrls = false;
    private boolean initialized = false;
    private String currentWebsite;
    private String pendingWebsite;
    private String pendingUrl;

    private Callback<String> loadingCallback;
    private Callback<String> loadedCallback;

    public GitWebFrame(Application app, int left, int top, int width, int height) {
        super(left, top);
        this.app = app;
        this.width = width;
        this.height = height;
        this.layout = new ScrollableLayout(left, top, width, height, height);
        this.layout.setScrollSpeed(8);
    }

    private static List<ModuleEntry> parseData(String websiteData) {
        websiteData = websiteData.replace("\r", "");

        List<ModuleEntry> modules = new LinkedList<>();
        String[] lines = websiteData.trim().split("\\n");

        Module module = null;
        Map<String, String> moduleData = null;
        for (String line : lines) {
            if (line.isEmpty())
                continue;

            if (line.startsWith("#")) {
                ModuleEntry entry = compileEntry(module, moduleData);
                if (entry != null) {
                    modules.add(entry);
                }
                module = MODULES.get(line.substring(1));
                moduleData = new HashMap<>();
            } else if (module != null) {
                String[] data = line.split("=", 2);
                if (data.length != 2)
                    return null;
                moduleData.put(data[0], data[1]);
            } else {
                modules.clear();
                modules.add(createPlainWebsite(websiteData));
                return modules;
            }
        }

        ModuleEntry entry = compileEntry(module, moduleData);
        if (entry != null) {
            modules.add(entry);
        }

        return modules;
    }

    private static ModuleEntry compileEntry(Module module, Map<String, String> data) {
        if (module != null && verifyModuleEntry(module, data)) {
            var moduleEntry = new ModuleEntry(module, data);
            moduleEntry.setId(data.getOrDefault("id", null));
            return moduleEntry;
        }
        return null;
    }

    private static ModuleEntry createPlainWebsite(String content) {
        Module module = MODULES.get("paragraph");
        Map<String, String> data = new HashMap<>(1, 1f);
        data.put("text", content);
        return new ModuleEntry(module, data);
    }

    private static boolean verifyModuleEntry(Module module, Map<String, String> data) {
        String[] requiredData = module.getRequiredData();
        for (String s : requiredData) {
            if (!data.containsKey(s)) {
                return false;
            }
        }
        return true;
    }

    private static int calculateHeight(List<ModuleEntry> modules, int width) {
        int height = 0;
        for (ModuleEntry entry : modules) {
            height += entry.getModule().calculateHeight(entry.getData(), width);
        }
        return height;
    }

    public static String parseFormatting(String s) {
        return s.replace("&", "\u00A7");
    }

    public static void dumpModules(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String key : MODULES.keySet()) {
                Module module = MODULES.get(key);
                writer.write("#" + key);
                writer.newLine();
                writer.write("Required Data:");
                writer.newLine();
                for (String p : module.getRequiredData()) {
                    writer.write("- " + p);
                    writer.newLine();
                }
                writer.write("Optional Data:");
                writer.newLine();
                for (String p : module.getOptionalData()) {
                    writer.write("- " + p);
                    writer.newLine();
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init(Layout layout) {
        layout.addComponent(this.layout);
    }

    @Override
    protected void handleLoad() {
        this.initialized = true;
        if (pendingUrl != null) {
            this.setUrl(pendingUrl);
            pendingUrl = null;
        } else if (pendingWebsite != null) {
            this.setWebsite(pendingWebsite);
            pendingWebsite = null;
        }
    }

    @Override
    protected void handleUnload() {
        this.initialized = false;
    }

    @Override
    protected void handleTick() {
        //DebugLog.log("TICK TOCK");
        for (Component component : this.layout.components) {
            //DebugLog.log("A");
            if (component instanceof ModuleLayout layout) {
             //   DebugLog.log("AAAA");
                layout._tick();
               // layout.entry.getModule()
            } else {
                DebugLog.log(component);
            }
        }
        if (pendingWebsite != null) {
            this.setWebsite(pendingWebsite);
            pendingWebsite = null;
        } else if (pendingUrl != null) {
            this.setUrl(pendingUrl);
            pendingUrl = null;
        }
    }

    public void setAllowRemoteUrls(boolean allowRemoteUrls) {
        this.allowRemoteUrls = allowRemoteUrls;
    }

    public void scrollToTop() {
        layout.resetScroll();
    }

    public void loadRaw(String data) {
        layout.clear();
        generateLayout(data, false);
    }

    public void loadWebsite(String website) {
        if (allowRemoteUrls && (website.startsWith("http") || website.startsWith("https"))) {
            pendingUrl = website;
            return;
        }
        pendingWebsite = website;
    }

    public void loadUrl(String url) {
        if (allowRemoteUrls) {
            pendingUrl = url;
        }
    }

    private void setWebsite(String website) {
        layout.clear();

        Matcher matcher = GitWebFrame.PATTERN_LINK.matcher(website);
        if (!matcher.matches()) {
            if (loadedCallback != null) {
                loadedCallback.execute(null, false);
            }
            return;
        }

        currentWebsite = website;

        String domain = matcher.group("domain");
        String extension = matcher.group("extension");
        String directory = matcher.group("directory");
        String url;

        String site = SiteRegistration.getURL(website);
        if (directory == null) {
            url = site + extension + "/" + domain + "/index";
        } else {
            if (directory.endsWith("/")) {
                directory = directory.substring(0, directory.length() - 1);
            }
            url = site + extension + "/" + domain + directory + "/index";
        }

        if (loadingCallback != null) {
            loadingCallback.execute(website, true);
        }
        this.load(url);
    }

    private void setUrl(String url) {
        layout.clear();

        try {
            new URL(url).toURI();
        } catch (Exception e) {
            if (loadedCallback != null) {
                loadedCallback.execute(null, false);
            }
            return;
        }

        currentWebsite = url;

        if (loadingCallback != null) {
            loadingCallback.execute(url, true);
        }
        this.load(url);
    }

    private void load(String url) {
        OnlineRequest.getInstance().make(url, (success, response) ->
        {
            if (success) {
                generateLayout(response, true);
            }
            if (loadedCallback != null) {
                loadedCallback.execute(response, success);
            }
        });
    }

    public String getCurrentWebsite() {
        return currentWebsite;
    }

    private void generateLayout(String websiteData, boolean dynamic) {
        Minecraft.getInstance().doRunTask(() ->
        {
            List<ModuleEntry> modules = parseData(websiteData);
            if (modules == null) {
                //DISPLAY DIALOG?
                return;
            }

            layout.clear();
            layout.height = calculateHeight(modules, width);

            int offset = 0;
            for (int i = 0; i < modules.size() - 1; i++) {
                ModuleEntry entry = modules.get(i);
                Module module = entry.getModule();
                int height = module.calculateHeight(entry.getData(), width);
                ModuleLayout moduleLayout = new ModuleLayout(0, offset, width, this, entry);
                layout.addComponent(moduleLayout);
                offset += height;
            }

            if (modules.size() > 0) {
                ModuleEntry entry = modules.get(modules.size() - 1);
                Module module = entry.getModule();
                int height = module.calculateHeight(entry.getData(), width);
                //if (height == 0)
                ModuleLayout moduleLayout = new ModuleLayout(0, offset, width, this, entry);
                //module.generate(this, moduleLayout, width, entry.getData());
                layout.addComponent(moduleLayout);
            }

            if (dynamic || initialized) {
                layout.handleLoad();
            }

            layout.resetScroll();
            updateListeners();
        });
    }

    private void updateListeners() {
        Text.WordListener listener = (word, mouseButton) ->
        {
            if (mouseButton == 0 && PATTERN_LINK.matcher(word).matches()) {
                this.pendingWebsite = word;
            }
        };
        addWordListener(layout, listener);
    }

    private void addWordListener(Layout layout, Text.WordListener listener) {
        for (Component c : layout.components) {
            if (c instanceof Layout) {
                addWordListener((Layout) c, listener);
            } else if (c instanceof Text text && !text.hasWordListener()) {
                ((Text) c).setWordListener(listener);
            }
        }
    }

    public void setLoadingCallback(Callback<String> loadingCallback) {
        this.loadingCallback = loadingCallback;
    }

    public void setLoadedCallback(Callback<String> loadedCallback) {
        this.loadedCallback = loadedCallback;
    }

    public Application getApp() {
        return app;
    }
}
