package dev.ultreon.devices.object;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import dev.ultreon.devices.Devices;
import dev.ultreon.devices.Reference;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.Permission;
import dev.ultreon.devices.core.PermissionManager;
import dev.ultreon.devices.debug.DebugLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class AppInfo {
    public static final Comparator<AppInfo> SORT_NAME = Comparator.comparing(AppInfo::getName);

    private transient final ResourceLocation APP_ID;

    private final transient boolean systemApp;

    private static final TintProvider DEFAULT_TINT_PROVIDER = new TintProvider() {
        @Override
        public int getTintColor(AppInfo info, int o) {
            return switch (o) {
                case 1 -> ComputerScreen.getSystem().getSettings().getColorScheme().getBackgroundColor();
                case 2 -> ComputerScreen.getSystem().getSettings().getColorScheme().getBackgroundSecondaryColor();
                default -> new Color(255, 255, 255).getRGB();
            };
        }

        @Override
        public CompoundTag toTag() {
            return new CompoundTag();
        }

    };
    private Set<String> extensions = new HashSet<>();

    public static TintProvider getDefaultTintProvider() {
        return DEFAULT_TINT_PROVIDER;
    }

    public TintProvider getTintProvider() {
        return tintProvider;
    }

    private TintProvider tintProvider = DEFAULT_TINT_PROVIDER;

    private String name;
    private String[] authors;
    private String[] contributors;
    private String description;
    private String version;
    private Icon icon;
    private String[] screenshots;
    private Support support;

    public AppInfo(ResourceLocation identifier, boolean isSystemApp) {
        this.APP_ID = identifier;
        this.systemApp = isSystemApp;
    }

    public AppInfo(ResourceLocation identifier, boolean isSystemApp, TintProvider tintProvider) {
        this.APP_ID = identifier;
        this.systemApp = isSystemApp;
        this.tintProvider = tintProvider;
    }

    public void registerExtension(String extension) {
        if (PermissionManager.hasPermission(Permission.APP_EXTENSION_REGISTER)) throw new IllegalArgumentException("No permission to register extension " + extension);
        extensions.add(extension);
    }

    /// Gets the id of the application
    ///
    /// @return the app resource location
    public ResourceLocation getId() {
        return APP_ID;
    }

    /// Gets the formatted version of the application's id
    ///
    /// @return a formatted id
    public String getFormattedId() {
        return getId().toString();
    }

    /// Gets the name of the application
    ///
    /// @return the application name
    public String getName() {
        return name;
    }

    public String[] getAuthors() {
        return authors;
    }

    /// `contributors` should include all authors, plus extra contributors
    ///
    /// <code>
    ///     {
    ///         "authors": ["Me!"],
    ///         "contributors": ["You!"]
    ///     }
    /// </code>
    /// should return ["Me!", "You!"] with this method.
    public String[] getContributors() {
        return contributors;
    }

    @Deprecated
    public String getAuthor() {
        StringBuilder a = new StringBuilder();
        if (authors == null) return "<unknown>";
        Arrays.stream(authors).forEach((str -> a.append(str).append(", ")));
        if (a.length() >= 2) {
            a.deleteCharAt(a.length() - 1);
            a.deleteCharAt(a.length() - 1);
        }
        return a.toString();
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public int getTint(int i) {
        return this.tintProvider != null ? this.tintProvider.getTintColor(this, i) : DEFAULT_TINT_PROVIDER.getTintColor(this, i);
    }

    public void setTintProvider(TintProvider tintProvider) {
        this.tintProvider = tintProvider;
    }

    public Collection<String> getExtensions() {
        return this.extensions;
    }

    public static class Icon {
        Glyph base;
        Glyph overlay0;
        Glyph overlay1;
        public static class Glyph {
            private ResourceLocation resourceLocation;
            private int u = -1;
            private int v = -1;
            private int type;
            private Glyph(ResourceLocation res) {
                this.resourceLocation = res;
            }
            private static Glyph of(ResourceLocation res) {
                return new Glyph(res);
            }

            public ResourceLocation getResourceLocation() {
                return resourceLocation;
            }

            public void setU(int u) {
                this.u = u;
            }

            public void setV(int v) {
                this.v = v;
            }

            public int getU() {
                return u;
            }

            public int getV() {
                return v;
            }

            public int getType() {
                return type;
            }
        }

        private Icon(AppInfo info) {
            this.base = Glyph.of(ResourceLocation.fromNamespaceAndPath(info.APP_ID.getNamespace(), "textures/app/icon/base/" + info.APP_ID.getPath() + ".png"));
            this.base.type = 0;
            this.overlay0 = Glyph.of(ResourceLocation.fromNamespaceAndPath(info.APP_ID.getNamespace(), "textures/app/icon/overlay0/" + info.APP_ID.getPath() + ".png"));
            this.overlay0.type = 1;
            this.overlay1 = Glyph.of(ResourceLocation.fromNamespaceAndPath(info.APP_ID.getNamespace(), "textures/app/icon/overlay1/" + info.APP_ID.getPath() + ".png"));
            this.overlay1.type = 2;
        }

        /// @deprecated Used in legacy <code>icon</code> in schema version 0
        @Deprecated
        private Icon() {
        }

        public Glyph getBase() {
            return base;
        }

        public Glyph getOverlay0() {
            return overlay0;
        }

        public Glyph getOverlay1() {
            return overlay1;
        }
    }

    public String[] getScreenshots() {
        return screenshots;
    }

    public Support getSupport() {
        return support;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AppInfo info)) return false;
        return this == info || getFormattedId().equals(info.getFormattedId());
    }

    public void reload(@NotNull ResourceManager resourceManager) {
        resetInfo();

        // TODO "Check if the resource manager can be used on client side."
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(APP_ID.getNamespace(), "apps/" + APP_ID.getPath() + ".json");
        DebugLog.log("Reloading app info for '" + APP_ID + "'");
        Resource resource = resourceManager.getResource(resourceLocation).orElse(null);

        if (resource == null)
            throw new NoSuchElementException("Missing app info json for '" + APP_ID + "'");

        try (Reader reader = resource.openAsReader()) {
            JsonElement obj = JsonParser.parseReader(reader);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(AppInfo.class, new Deserializer(this));
            Gson gson = builder.create();
            gson.fromJson(obj, AppInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetInfo() {
        name = null;
        authors = null;
        contributors = null;
        description = null;
        version = null;
        icon = null;
        screenshots = null;
        support = null;
    }

    private static class Support {
        private String paypal;
        private String patreon;
        public String kofi;
        private String twitter;
        private String youtube;
    }

    public static class Deserializer implements JsonDeserializer<AppInfo> {
        private static final Pattern LANG = Pattern.compile("\\$\\{[a-z]+}");

        private final AppInfo info;

        public Deserializer(AppInfo info) {
            this.info = info;
        }

        @Override
        public AppInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            try {
                switch (getSchemaVersion(json)) {
                    case 0 -> deserializeSchemaVersion0(json, context);
                    case 1 -> deserializeSchemaVersion1(json, context);
                    case 2 -> deserializeSchemaVersion2(json, context);
                    case 3 -> deserializeSchemaVersion3(json, context);
                    default -> throw new RuntimeException("Schema " + getSchemaVersion(json) + " is not implemented in " + Reference.VERSION + "!");
                }
            } catch (JsonParseException e) {
                Devices.LOGGER.error("Malformed app info json for '{}'", info.getFormattedId());
            }

            return info;
        }

        private void deserializeSchemaVersion0(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
            info.name = convertToLocal(json.getAsJsonObject().get("name").getAsString());
            info.authors = new String[]{convertToLocal(json.getAsJsonObject().get("author").getAsString())};
            info.description = convertToLocal(json.getAsJsonObject().get("description").getAsString());
            info.version = json.getAsJsonObject().get("version").getAsString();

            if (json.getAsJsonObject().has("screenshots") && json.getAsJsonObject().get("screenshots").isJsonArray()) {
                info.screenshots = context.deserialize(json.getAsJsonObject().get("screenshots"), new TypeToken<String[]>() {
                }.getType());
            }

            if (json.getAsJsonObject().has("icon") && json.getAsJsonObject().get("icon").isJsonPrimitive()) {
                info.icon = new Icon();
                info.icon.base = Icon.Glyph.of(ResourceLocation.parse(json.getAsJsonObject().get("icon").getAsString()));
                info.icon.base.type = 0;
                info.icon.overlay0 = Icon.Glyph.of(ResourceLocation.fromNamespaceAndPath(info.APP_ID.getNamespace(), "textures/app/icon/overlay0/empty.png"));
                info.icon.overlay0.type = 1;
                info.icon.overlay1 = Icon.Glyph.of(ResourceLocation.fromNamespaceAndPath(info.APP_ID.getNamespace(), "textures/app/icon/overlay1/empty.png"));
                info.icon.overlay1.type = 2;
            }

            if (json.getAsJsonObject().has("support") && !json.getAsJsonObject().get("support").getAsJsonObject().isEmpty()) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Support support = new Support();

                if (supportObj.has("paypal")) {
                    support.paypal = supportObj.get("paypal").getAsString();
                }
                if (supportObj.has("patreon")) {
                    support.patreon = supportObj.get("patreon").getAsString();
                }
                if (supportObj.has("twitter")) {
                    support.twitter = supportObj.get("twitter").getAsString();
                }
                if (supportObj.has("youtube")) {
                    support.youtube = supportObj.get("youtube").getAsString();
                }

                info.support = support;
            }
        }

        private void deserializeSchemaVersion1(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
            info.name = convertToLocal(json.getAsJsonObject().get("name").getAsString());
            info.authors = new String[]{convertToLocal(json.getAsJsonObject().get("author").getAsString())};
            info.description = convertToLocal(json.getAsJsonObject().get("description").getAsString());
            info.version = json.getAsJsonObject().get("version").getAsString();
            if (json.getAsJsonObject().has("screenshots") && json.getAsJsonObject().get("screenshots").isJsonArray()) {
                info.screenshots = context.deserialize(json.getAsJsonObject().get("screenshots"), new TypeToken<String[]>() {
                }.getType());
            }

            if (json.getAsJsonObject().has("icon") && json.getAsJsonObject().get("icon").isJsonPrimitive()) {
                Devices.LOGGER.warn("{} uses removed \"icon\"! Please advise {} to fix the icon!", info.name, info.authors[0]);
            }

            if (json.getAsJsonObject().has("support") && !json.getAsJsonObject().get("support").getAsJsonObject().isEmpty()) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Support support = new Support();

                if (supportObj.has("paypal")) {
                    support.paypal = supportObj.get("paypal").getAsString();
                }
                if (supportObj.has("patreon")) {
                    support.patreon = supportObj.get("patreon").getAsString();
                }
                if (supportObj.has("twitter")) {
                    support.twitter = supportObj.get("twitter").getAsString();
                }
                if (supportObj.has("youtube")) {
                    support.youtube = supportObj.get("youtube").getAsString();
                }

                info.support = support;
            }
            info.icon = new Icon(info);
        }

        private void deserializeSchemaVersion2(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
            info.name = convertToLocal(json.getAsJsonObject().get("name").getAsString());

            ArrayList<String> authors = new ArrayList<>();
            if (json.getAsJsonObject().has("authors") && json.getAsJsonObject().get("authors").isJsonArray()) {
                json.getAsJsonObject().get("authors").getAsJsonArray().forEach(e -> authors.add(convertToLocal(e.getAsString())));
                info.authors = authors.toArray(new String[0]);
            }

            ArrayList<String> contributors = new ArrayList<>();
            if (json.getAsJsonObject().has("contributors") && json.getAsJsonObject().get("contributors").isJsonArray()) {
                json.getAsJsonObject().get("contributors").getAsJsonArray().forEach(e -> contributors.add(convertToLocal(e.getAsString())));
            }

            var d = false;
            if (info.authors.length == 0) {
                info.authors = new String[]{"the application's developer"};
                d = true;
            }

            if (json.getAsJsonObject().has("author") && json.getAsJsonObject().get("author").isJsonPrimitive()) {
                if (info.authors == null) {
                    info.authors = new String[]{convertToLocal(json.getAsJsonObject().get("author").getAsString())};
                    Devices.LOGGER.warn("{} uses deprecated \"author\"!, Please advise {} to replace \"author\": \"{}\" with the \"authors\": [] format", info.name, info.authors[0], info.authors[0]);
                }
            }

            info.description = convertToLocal(json.getAsJsonObject().get("description").getAsString());
            info.version = json.getAsJsonObject().get("version").getAsString();
            if (json.getAsJsonObject().has("screenshots") && json.getAsJsonObject().get("screenshots").isJsonArray()) {
                info.screenshots = context.deserialize(json.getAsJsonObject().get("screenshots"), new TypeToken<String[]>() {
                }.getType());
            }

            if (json.getAsJsonObject().has("icon") && json.getAsJsonObject().get("icon").isJsonPrimitive()) {
                Devices.LOGGER.warn("{} uses removed \"icon\"! Please advise {} to fix the icon!", info.name, info.authors[0]);
            }

            if (d) info.authors = new String[0];
            var l = new ArrayList<>(List.of(info.authors));l.addAll(contributors);
            info.contributors = l.toArray(new String[0]);

            if (json.getAsJsonObject().has("support") && !json.getAsJsonObject().get("support").getAsJsonObject().isEmpty()) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Support support = new Support();

                if (supportObj.has("paypal")) {
                    support.paypal = supportObj.get("paypal").getAsString();
                }
                if (supportObj.has("patreon")) {
                    support.patreon = supportObj.get("patreon").getAsString();
                }
                if (supportObj.has("twitter")) {
                    support.twitter = supportObj.get("twitter").getAsString();
                }
                if (supportObj.has("youtube")) {
                    support.youtube = supportObj.get("youtube").getAsString();
                }

                info.support = support;
            }
            info.icon = new Icon(info);
        }

        private void deserializeSchemaVersion3(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
            info.name = convertToLocal(json.getAsJsonObject().get("name").getAsString());

            ArrayList<String> authors = new ArrayList<>();
            if (json.getAsJsonObject().has("authors") && json.getAsJsonObject().get("authors").isJsonArray()) {
                json.getAsJsonObject().get("authors").getAsJsonArray().forEach(e -> authors.add(convertToLocal(e.getAsString())));
                info.authors = authors.toArray(new String[0]);
            }

            ArrayList<String> contributors = new ArrayList<>();
            if (json.getAsJsonObject().has("contributors") && json.getAsJsonObject().get("contributors").isJsonArray()) {
                json.getAsJsonObject().get("contributors").getAsJsonArray().forEach(e -> contributors.add(convertToLocal(e.getAsString())));
            }

            if (info.authors.length == 0) {
                throw new RuntimeException("No authors defined!");
            }

            info.description = convertToLocal(json.getAsJsonObject().get("description").getAsString());
            info.version = json.getAsJsonObject().get("version").getAsString();
            if (json.getAsJsonObject().has("screenshots") && json.getAsJsonObject().get("screenshots").isJsonArray()) {
                info.screenshots = context.deserialize(json.getAsJsonObject().get("screenshots"), new TypeToken<String[]>() {
                }.getType());
            }

            var l = new ArrayList<>(List.of(info.authors));l.addAll(contributors);
            info.contributors = l.toArray(new String[0]);

            if (json.getAsJsonObject().has("support") && !json.getAsJsonObject().get("support").getAsJsonObject().isEmpty()) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Support support = new Support();

                if (supportObj.has("paypal")) {
                    support.paypal = supportObj.get("paypal").getAsString();
                }
                if (supportObj.has("patreon")) {
                    support.patreon = supportObj.get("patreon").getAsString();
                }
                if (supportObj.has("ko-fi")) {
                    support.kofi = supportObj.get("ko-fi").getAsString();
                }
                if (supportObj.has("twitter")) {
                    support.twitter = supportObj.get("twitter").getAsString();
                }
                if (supportObj.has("youtube")) {
                    support.youtube = supportObj.get("youtube").getAsString();
                }

                info.support = support;
            }
            info.icon = new Icon(info);
        }

        private int getSchemaVersion(JsonElement json) {
            boolean has = json.getAsJsonObject().has("schemaVersion");
            boolean isPrimitive = has && json.getAsJsonObject().get("schemaVersion").isJsonPrimitive();
            if (has && isPrimitive) return json.getAsJsonObject().get("schemaVersion").getAsInt();
            return 2;
        }

        private String convertToLocal(String s) {
            Matcher m = LANG.matcher(s);
            while (m.find()) {
                String found = m.group();
                s = s.replace(found, I18n.get("app." + info.getFormattedId() + "." + found.substring(2, found.length() - 1)));
            }
            return s;
        }
    }

    @ApiStatus.Experimental
    public interface TintProvider {
        int getTintColor(AppInfo info, int o);

        CompoundTag toTag();
    }
}
