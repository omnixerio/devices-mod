package com.ultreon.devices.object;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.Reference;
import com.ultreon.devices.core.Laptop;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class AppInfo {
    public static final StreamCodec<ByteBuf, AppInfo> STREAM_CODEC = StreamCodec.of((buf, appInfo) -> {
        ResourceLocation.STREAM_CODEC.encode(buf, appInfo.appId);
        ByteBufCodecs.STRING_UTF8.encode(buf, appInfo.name);
        ByteBufCodecs.<ByteBuf, String, List<String>>collection(ArrayList::new, ByteBufCodecs.STRING_UTF8).encode(buf, appInfo.getAuthors());
        ByteBufCodecs.STRING_UTF8.encode(buf, appInfo.description);
        ByteBufCodecs.STRING_UTF8.encode(buf, appInfo.version);
        ByteBufCodecs.<ByteBuf, String, List<String>>collection(ArrayList::new, ByteBufCodecs.STRING_UTF8).encode(buf, appInfo.getScreenshots());
        Support.STREAM_CODEC.encode(buf, appInfo.support);
    }, buf -> new AppInfo(
            ResourceLocation.STREAM_CODEC.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8).decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8).decode(buf),
            Support.STREAM_CODEC.decode(buf)
    ));
    public static final Comparator<AppInfo> SORT_NAME = Comparator.comparing(AppInfo::getName);

    private transient final ResourceLocation appId;

    private final transient boolean systemApp;

    private static final TintProvider DEFAULT_TINT_PROVIDER = new TintProvider() {
        @Override
        public int getTintColor(AppInfo info, int o) {
            return switch (o) {
                case 1 -> Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor();
                case 2 -> Laptop.getSystem().getSettings().getColorScheme().getBackgroundSecondaryColor();
                default -> new Color(255, 255, 255).getRGB();
            };
        }

        @Override
        public CompoundTag toTag() {
            return new CompoundTag();
        }

    };

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
        this.appId = identifier;
        this.systemApp = isSystemApp;
    }

    public AppInfo(ResourceLocation appId, String name, List<String> authors, String description, String version, List<String> screenshots, Support support) {
        this(appId, false);
        this.name = name;
        this.authors = authors.toArray(new String[0]);
        this.description = description;
        this.version = version;
        this.screenshots = screenshots.toArray(new String[0]);
        this.support = support;
    }

    public AppInfo(ResourceLocation identifier) {
        this(identifier, false);
    }

    /**
     * Gets the id of the application
     *
     * @return the app resource location
     */
    public ResourceLocation getAppId() {
        return appId;
    }

    /**
     * Gets the formatted version of the application's id
     *
     * @return a formatted id
     */
    public String getFormattedId() {
        return getAppId().toString();
    }

    /**
     * Gets the name of the application
     *
     * @return the application name
     */
    public String getName() {
        return name;
    }

    public List<String> getAuthors() {
        return List.of(authors);
    }

    /**
     * {@code contributors} should include all authors, plus extra contributors
     * <p><code>
     * {
     * "authors": ["Me!"],
     * "contributors": ["You!"]
     * }
     * </code><br/>
     * should return ["Me!", "You!"] with this method.</p>
     */
    public String[] getContributors() {
        return contributors;
    }

    @Deprecated
    public String getAuthor() {
        StringBuilder a = new StringBuilder();
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
            this.base = Glyph.of(ResourceLocation.fromNamespaceAndPath(info.appId.getNamespace(), "textures/app/icon/base/" + info.appId.getPath() + ".png"));
            this.base.type = 0;
            this.overlay0 = Glyph.of(ResourceLocation.fromNamespaceAndPath(info.appId.getNamespace(), "textures/app/icon/overlay0/" + info.appId.getPath() + ".png"));
            this.overlay0.type = 1;
            this.overlay1 = Glyph.of(ResourceLocation.fromNamespaceAndPath(info.appId.getNamespace(), "textures/app/icon/overlay1/" + info.appId.getPath() + ".png"));
            this.overlay1.type = 2;
        }

        /**
         * @deprecated Used in legacy <code>icon</code> in schema version 0
         */
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

    public List<String> getScreenshots() {
        return List.of(screenshots);
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

    public void reload() {
        resetInfo();
        if (Minecraft.getInstance().getResourceManager() == null) return;
        // TODO "Check if the resource manager can be used on client side."
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(appId.getNamespace(), "/apps/" + appId.getPath() + ".json")).orElse(null);

        if (resource == null)
            throw new RuntimeException("Missing app info json for '" + appId + "'");

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

    private record Support(
            Optional<String> paypal,
            Optional<String> patreon,
            Optional<String> kofi,
            Optional<String> twitter,
            Optional<String> youtube
    ) {
        public static final StreamCodec<ByteBuf, Support> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Support::paypal,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Support::patreon,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Support::kofi,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Support::twitter,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Support::youtube,
                Support::new
        );

        public Support() {
            this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }
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
                    default ->
                            throw new RuntimeException("Schema " + getSchemaVersion(json) + " is not implemented in " + Reference.VERSION + "!");
                }
            } catch (JsonParseException e) {
                OmnixerioDevicesMod.LOGGER.error("Malformed app info json for '" + info.getFormattedId() + "'");
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
                info.icon.overlay0 = Icon.Glyph.of(ResourceLocation.fromNamespaceAndPath(info.appId.getNamespace(), "textures/app/icon/overlay0/empty.png"));
                info.icon.overlay0.type = 1;
                info.icon.overlay1 = Icon.Glyph.of(ResourceLocation.fromNamespaceAndPath(info.appId.getNamespace(), "textures/app/icon/overlay1/empty.png"));
                info.icon.overlay1.type = 2;
            }

            if (json.getAsJsonObject().has("support") && json.getAsJsonObject().get("support").getAsJsonObject().size() > 0) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Optional<String> paypal = Optional.empty();
                Optional<String> patreon = Optional.empty();
                Optional<String> kofi = Optional.empty();
                Optional<String> twitter = Optional.empty();
                Optional<String> youtube = Optional.empty();
                if (supportObj.has("paypal")) {
                    paypal = Optional.of(supportObj.get("paypal").getAsString());
                }
                if (supportObj.has("patreon")) {
                    patreon = Optional.of(supportObj.get("patreon").getAsString());
                }
                if (supportObj.has("kofi")) {
                    kofi = Optional.of(supportObj.get("kofi").getAsString());
                }
                if (supportObj.has("twitter")) {
                    twitter = Optional.of(supportObj.get("twitter").getAsString());
                }
                if (supportObj.has("youtube")) {
                    youtube = Optional.of(supportObj.get("youtube").getAsString());
                }

                info.support = new Support(
                        paypal,
                        patreon,
                        kofi,
                        twitter,
                        youtube
                );

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
                OmnixerioDevicesMod.LOGGER.warn("{} uses removed \"icon\"! Please advise {} to fix the icon!", info.name, info.authors[0]);
            }

            if (json.getAsJsonObject().has("support") && json.getAsJsonObject().get("support").getAsJsonObject().size() > 0) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Optional<String> paypal = Optional.empty();
                Optional<String> patreon = Optional.empty();
                Optional<String> kofi = Optional.empty();
                Optional<String> twitter = Optional.empty();
                Optional<String> youtube = Optional.empty();
                if (supportObj.has("paypal")) {
                    paypal = Optional.of(supportObj.get("paypal").getAsString());
                }
                if (supportObj.has("patreon")) {
                    patreon = Optional.of(supportObj.get("patreon").getAsString());
                }
                if (supportObj.has("kofi")) {
                    kofi = Optional.of(supportObj.get("kofi").getAsString());
                }
                if (supportObj.has("twitter")) {
                    twitter = Optional.of(supportObj.get("twitter").getAsString());
                }
                if (supportObj.has("youtube")) {
                    youtube = Optional.of(supportObj.get("youtube").getAsString());
                }

                info.support = new Support(
                        paypal,
                        patreon,
                        kofi,
                        twitter,
                        youtube
                );
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
                    OmnixerioDevicesMod.LOGGER.warn("{} uses deprecated \"author\"!, Please advise {} to replace \"author\": \"{}\" with the \"authors\": [] format", info.name, info.authors[0], info.authors[0]);
                }
            }

            info.description = convertToLocal(json.getAsJsonObject().get("description").getAsString());
            info.version = json.getAsJsonObject().get("version").getAsString();
            if (json.getAsJsonObject().has("screenshots") && json.getAsJsonObject().get("screenshots").isJsonArray()) {
                info.screenshots = context.deserialize(json.getAsJsonObject().get("screenshots"), new TypeToken<String[]>() {
                }.getType());
            }

            if (json.getAsJsonObject().has("icon") && json.getAsJsonObject().get("icon").isJsonPrimitive()) {
                OmnixerioDevicesMod.LOGGER.warn("{} uses removed \"icon\"! Please advise {} to fix the icon!", info.name, info.authors[0]);
            }

            if (d) info.authors = new String[0];
            var l = new ArrayList<String>(List.of(info.authors));
            l.addAll(contributors);
            info.contributors = l.toArray(new String[0]);

            if (json.getAsJsonObject().has("support") && json.getAsJsonObject().get("support").getAsJsonObject().size() > 0) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Optional<String> paypal = Optional.empty();
                Optional<String> patreon = Optional.empty();
                Optional<String> kofi = Optional.empty();
                Optional<String> twitter = Optional.empty();
                Optional<String> youtube = Optional.empty();
                if (supportObj.has("paypal")) {
                    paypal = Optional.of(supportObj.get("paypal").getAsString());
                }
                if (supportObj.has("patreon")) {
                    patreon = Optional.of(supportObj.get("patreon").getAsString());
                }
                if (supportObj.has("kofi")) {
                    kofi = Optional.of(supportObj.get("kofi").getAsString());
                }
                if (supportObj.has("twitter")) {
                    twitter = Optional.of(supportObj.get("twitter").getAsString());
                }
                if (supportObj.has("youtube")) {
                    youtube = Optional.of(supportObj.get("youtube").getAsString());
                }

                info.support = new Support(
                        paypal,
                        patreon,
                        kofi,
                        twitter,
                        youtube
                );
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

            var l = new ArrayList<String>(List.of(info.authors));
            l.addAll(contributors);
            info.contributors = l.toArray(new String[0]);

            if (json.getAsJsonObject().has("support") && json.getAsJsonObject().get("support").getAsJsonObject().size() > 0) {
                JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                Optional<String> paypal = Optional.empty();
                Optional<String> patreon = Optional.empty();
                Optional<String> kofi = Optional.empty();
                Optional<String> twitter = Optional.empty();
                Optional<String> youtube = Optional.empty();
                if (supportObj.has("paypal")) {
                    paypal = Optional.of(supportObj.get("paypal").getAsString());
                }
                if (supportObj.has("patreon")) {
                    patreon = Optional.of(supportObj.get("patreon").getAsString());
                }
                if (supportObj.has("kofi")) {
                    kofi = Optional.of(supportObj.get("kofi").getAsString());
                }
                if (supportObj.has("twitter")) {
                    twitter = Optional.of(supportObj.get("twitter").getAsString());
                }
                if (supportObj.has("youtube")) {
                    youtube = Optional.of(supportObj.get("youtube").getAsString());
                }

                info.support = new Support(
                        paypal,
                        patreon,
                        kofi,
                        twitter,
                        youtube
                );
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
