package dev.ultreon.devices;

import com.google.common.base.Suppliers;
import com.google.gson.*;
import com.mojang.serialization.Lifecycle;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.utils.OnlineRequest;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.client.ClientNotification;
import dev.ultreon.devices.client.debug.ClientAppDebug;
import dev.ultreon.devices.core.io.task.*;
import dev.ultreon.devices.core.network.task.TaskConnect;
import dev.ultreon.devices.core.network.task.TaskGetDevices;
import dev.ultreon.devices.core.network.task.TaskPing;
import dev.ultreon.devices.core.print.task.TaskPrint;
import dev.ultreon.devices.core.task.TaskInstallApp;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.event.InitializationEvent;
import dev.ultreon.devices.event.WorldDataHandler;
import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.devices.network.task.SyncApplicationPacket;
import dev.ultreon.devices.network.task.SyncConfigPacket;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.devices.programs.IconsApp;
import dev.ultreon.devices.programs.PixelPainterApp;
import dev.ultreon.devices.programs.TestApp;
import dev.ultreon.devices.programs.auction.task.TaskAddAuction;
import dev.ultreon.devices.programs.auction.task.TaskBuyItem;
import dev.ultreon.devices.programs.auction.task.TaskGetAuctions;
import dev.ultreon.devices.programs.debug.TextAreaApp;
import dev.ultreon.devices.programs.email.task.*;
import dev.ultreon.devices.programs.example.ExampleApp;
import dev.ultreon.devices.programs.example.task.TaskNotificationTest;
import dev.ultreon.devices.programs.system.SystemApp;
import dev.ultreon.devices.programs.system.task.*;
import dev.ultreon.devices.util.SiteRegistration;
import dev.ultreon.devices.util.Vulnerability;
import dev.ultreon.mods.xinexlib.Env;
import dev.ultreon.mods.xinexlib.EnvExecutor;
import dev.ultreon.mods.xinexlib.ModPlatform;
import dev.ultreon.mods.xinexlib.client.event.LocalPlayerQuitEvent;
import dev.ultreon.mods.xinexlib.event.interact.UseBlockEvent;
import dev.ultreon.mods.xinexlib.event.server.ServerPlayerJoinEvent;
import dev.ultreon.mods.xinexlib.event.server.ServerStartingEvent;
import dev.ultreon.mods.xinexlib.event.server.ServerStoppedEvent;
import dev.ultreon.mods.xinexlib.event.system.EventSystem;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
import dev.ultreon.mods.xinexlib.registrar.RegistrarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.MappedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class UltreonDevices {
    public static final boolean DEVELOPER_MODE = XinexPlatform.isDevelopmentEnvironment();
    public static final String MOD_ID = "devices";
    public static final Logger LOGGER = LoggerFactory.getLogger("Ultreon Devices Mod");

    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> XinexPlatform.getRegistrarManager(MOD_ID));
    public static final List<SiteRegistration> SITE_REGISTRATIONS = new ProtectedArrayList<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final DevicesEarlyConfig EARLY_CONFIG = new DevicesEarlyConfig();
    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();
    private static final String GITWEB_REGISTER_URL = "https://ultreon.gitlab.io/gitweb/site_register.json";
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final SiteRegisterStack SITE_REGISTER_STACK = new SiteRegisterStack();

    //---- Registry : Start ----//
    public static MappedRegistry<TrayItem> trayItemRegistry = new MappedRegistry<>(ResourceKey.createRegistryKey(id("tray_item")), Lifecycle.stable());
    //---- Registry : End ----//

    static List<AppInfo> allowedApps = new ArrayList<>();
    private static final List<Vulnerability> vulnerabilities = new ArrayList<>();
    private static UltreonDevices instance;
    private ArrayList<Application> apps;

    public static List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }
    private static MinecraftServer server;
    private static TestManager tests;

    protected UltreonDevices() {
        UltreonDevices.instance = this;
    }

    public static UltreonDevices getInstance() {
        return instance;
    }

    public void init() {
        LOGGER.info("Initializing Ultreon Devices on {} platform.", XinexPlatform.getPlatformName());

        if (XinexPlatform.getPlatformName().equals(ModPlatform.Fabric)) {
            preInit();
            serverSetup();
        }
//        BlockEntityUtil.sendUpdate(null, null, null);

        WorldDataHandler.init();

        // STOPSHIP: 3/11/2022 should be moved to dedicated testmod
        final var property = System.getProperty("ultreon.devices.tests");
        tests = new TestManager();
        if (property != null) {
            LOGGER.warn("Ultreon Devices: Tests enabled: {}", property);
            String[] split = property.split(",");
            tests.load(Set.of(split));
        }

        //LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        LOGGER.info("Doing some common setup.");

        PacketHandler.init();

        if (XinexPlatform.getPlatformName().equals(ModPlatform.Fabric)) {
            registerApplications();
        }

        if (XinexPlatform.getPlatformName().equals(ModPlatform.Fabric)) {
            EnvExecutor.runInEnv(Env.CLIENT, () -> UltreonDevices::doClientInit);
        }

        LOGGER.info("Registering events.");
        setupEvents();

        EnvExecutor.runInEnv(Env.CLIENT, () -> UltreonDevices::setupClientEvents); //todo
        if (XinexPlatform.getPlatformName() != ModPlatform.Forge) {
            loadComplete();
        }
    }

    public static void doClientInit() {
        ClientAppDebug.register();
        ClientModEvents.clientSetup(); //todo
        UltreonDevices.setupSiteRegistrations();
        UltreonDevices.checkForVulnerabilities();
    }

    public static void preInit() {
        EventSystem.MAIN.on(InitializationEvent.AppRegistrationEvent.class, BuiltinApps::registerBuiltinApps);
        DeviceConfig.init();
    }


    public static boolean isDevelopmentPreview() {
        return IS_DEV_PREVIEW;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static TestManager getTests() {
        return tests;
    }

    public void serverSetup() {
        LOGGER.info("Doing some server setup.");
    }

    public void loadComplete() {
        LOGGER.info("Doing some load complete handling.");
    }


    public void registerApplications() {
        // Applications (Both)
        EventSystem.MAIN.publish(new InitializationEvent.AppRegistrationEvent(this));
        // Core
        TaskManager.registerTask("update_application_data", TaskUpdateApplicationData::new, TaskUpdateApplicationData.class);
        TaskManager.registerTask("print", TaskPrint::new, TaskPrint.class);
        TaskManager.registerTask("update_system_data", TaskUpdateSystemData::new, TaskUpdateSystemData.class);
        TaskManager.registerTask("connect", TaskConnect::new, TaskConnect.class);
        TaskManager.registerTask("ping", TaskPing::new, TaskPing.class);
        TaskManager.registerTask("get_devices", TaskGetDevices::new, TaskGetDevices.class);

        // File browser
        TaskManager.registerTask("send_action", TaskSendAction::new, TaskSendAction.class);
        TaskManager.registerTask("setup_file_browser", TaskSetupFileBrowser::new, TaskSetupFileBrowser.class);
        TaskManager.registerTask("get_files", TaskGetFiles::new, TaskGetFiles.class);
        TaskManager.registerTask("get_main_drive", TaskGetMainDrive::new, TaskGetMainDrive.class);

        // App Store
        TaskManager.registerTask("install_app", TaskInstallApp::new, TaskInstallApp.class);

        // Ender Mail
        TaskManager.registerTask("update_inbox", TaskUpdateInbox::new, TaskUpdateInbox.class);
        TaskManager.registerTask("send_email", TaskSendEmail::new, TaskSendEmail.class);
        TaskManager.registerTask("check_email_account", TaskCheckEmailAccount::new, TaskCheckEmailAccount.class);
        TaskManager.registerTask("register_email_account", TaskRegisterEmailAccount::new, TaskRegisterEmailAccount.class);
        TaskManager.registerTask("delete_email", TaskDeleteEmail::new, TaskDeleteEmail.class);
        TaskManager.registerTask("view_email", TaskViewEmail::new, TaskViewEmail.class);

        if (XinexPlatform.isDevelopmentEnvironment() || UltreonDevices.EARLY_CONFIG.enableBetaApps) {
            // Auction
            TaskManager.registerTask("add_auction", TaskAddAuction::new, TaskAddAuction.class);
            TaskManager.registerTask("get_auctions", TaskGetAuctions::new, TaskGetAuctions.class);
            TaskManager.registerTask("buy_item", TaskBuyItem::new, TaskBuyItem.class);

            // Bank
            TaskManager.registerTask("deposit", TaskDeposit::new, TaskDeposit.class);
            TaskManager.registerTask("withdraw", TaskWithdraw::new, TaskWithdraw.class);
            TaskManager.registerTask("get_balance", TaskGetBalance::new, TaskGetBalance.class);
            TaskManager.registerTask("pay", TaskPay::new, TaskPay.class);
            TaskManager.registerTask("add", TaskAdd::new, TaskAdd.class);
            TaskManager.registerTask("remove", TaskRemove::new, TaskRemove.class);
        }

        if (XinexPlatform.isDevelopmentEnvironment() || UltreonDevices.EARLY_CONFIG.enableDebugApps) {
            // Applications (Developers)
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "example"), () -> ExampleApp::new, false);
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "icons"), () -> IconsApp::new, false);
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "text_area"), () -> TextAreaApp::new, false);
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "test"), () -> TestApp::new, false);

            TaskManager.registerTask("notification_test", TaskNotificationTest::new, TaskNotificationTest.class);
        }

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> PrintingManager.registerPrint(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "picture"), PixelPainterApp.PicturePrint.class));
    }

    public abstract int getBurnTime(ItemStack stack, RecipeType<?> type);

    protected List<Application> loadApps() {
        if (apps != null) {
            return apps;
        }
        apps = new ArrayList<>();
        ComputerScreen.loadApplications(apps::addAll);
        return apps;
    }

    public static void setAllowedApps(List<AppInfo> allowedApps) {
        UltreonDevices.allowedApps = allowedApps;
    }

    public abstract String getVersion();

    public interface ApplicationSupplier {

        /// Gets a result.
        ///
        /// @return a result
        Supplier<Application> get();

        boolean isSystem();
    }

    /// DO NOT CALL: FOR INTERNAL USE ONLY
    @Nullable
    @ApiStatus.Internal
    public Application registerApplication(ResourceLocation identifier, ApplicationSupplier app) {
        if ("minecraft".equals(identifier.getNamespace())) {
            throw new IllegalArgumentException("Identifier cannot be \"minecraft\"!");
        }

        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }

        if (app.isSystem()) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }

        AtomicReference<Application> application = new AtomicReference<>(null);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            Application theAppWeGot = app.get().get();
            List<Application> apps = loadApps();
            assert apps != null;
            apps.add(theAppWeGot);

            AppInfo info = new AppInfo(identifier, SystemApp.class.isAssignableFrom(theAppWeGot.getClass()));

            theAppWeGot.setInfo(info);

            application.set(theAppWeGot);
        });
        return application.get();
    }

    protected abstract Map<String, IPrint.Renderer> getRegisteredRenders();

    protected abstract void setRegisteredRenders(Map<String, IPrint.Renderer> map);

    public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        LOGGER.debug("Registering print: {}", identifier.toString());

        try {
            Constructor<? extends IPrint> constructor = classPrint.getConstructor();
            IPrint print = constructor.newInstance();
            Class<? extends IPrint.Renderer> classRenderer = print.getRenderer();
            try {
                IPrint.Renderer renderer = classRenderer.getConstructor().newInstance();
                Map<String, IPrint.Renderer> idToRenderer = getRegisteredRenders(); //ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
                if (idToRenderer == null) {
                    idToRenderer = new HashMap<>();
                    setRegisteredRenders(idToRenderer);
                    //ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, idToRenderer, "registeredRenders");
                }
                idToRenderer.put(identifier.toString(), renderer);
            } catch (InstantiationException e) {
                UltreonDevices.LOGGER.error("The print renderer '{}' is missing an empty constructor and could not be registered!", classRenderer.getName());
                return false;
            }
            return true;
        } catch (Exception e) {
            UltreonDevices.LOGGER.error("The print '{}' is missing an empty constructor and could not be registered!", classPrint.getName());
        }
        return false;
    }

    public static void showNotification(CompoundTag tag) {
        LOGGER.debug("Showing notification");

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ClientNotification notification = ClientNotification.loadFromTag(tag);
            notification.push();
        });
    }

    public static boolean hasAllowedApplications() {
        return allowedApps != null;
    }

    public static List<AppInfo> getAllowedApplications() {
        if (allowedApps == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(allowedApps);
    }

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(UltreonDevices.MOD_ID, path);
    }

    private static void setupClientEvents() {
        EventSystem.MAIN.on(LocalPlayerQuitEvent.class, (event -> {
            LOGGER.debug("Client disconnected from server");

            allowedApps = null;
            DeviceConfig.restore();
        }));
    }

    private static void setupEvents() {
        EventSystem.MAIN.on(ServerStartingEvent.class, event -> server = event.getServer());
        EventSystem.MAIN.on(ServerStoppedEvent.class, event -> server = null);

        EventSystem.MAIN.on(UseBlockEvent.class, event -> {
//            Player player = event.getPlayer();
//            InteractionHand hand = event.getHand();
//            BlockPos blockPosition = event.getBlockPosition();
//            Block block = event.getBlock();
//            Level level = player.level();
//            if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() == Items.PAPER) {
//                if (block instanceof PrinterBlock) {
//                    event.cancel(InteractionResult.CONSUME);
//                    //event.setUseBlock(Event.Result.ALLOW); //todo
//                }
//            }
        });

        EventSystem.MAIN.on(ServerPlayerJoinEvent.class, event -> {
            ServerPlayer player = event.getPlayer();

            LOGGER.info("Player logged in: {}", player.getName());

            if (allowedApps != null) {
                PacketHandler.sendToClient(new SyncApplicationPacket(allowedApps), player);
            }
            PacketHandler.sendToClient(new SyncConfigPacket(), player);
        });
    }

    private static void setupSiteRegistrations() {
        setupSiteRegistration(GITWEB_REGISTER_URL);
    }

    private static void checkForVulnerabilities() {

    }

    private static CompletableFuture<Void> setupSiteRegistration(String url) {
        SITE_REGISTER_STACK.push();

        enum Type {
            SITE_REGISTER, REGISTRATION
        }

        CompletableFuture<Void> future = new CompletableFuture<>();

        OnlineRequest.getInstance().make(url, (success, response) -> CompletableFuture.runAsync(() -> {
            if (success) {
                //Minecraft.getInstance().doRunTask(() -> {
                JsonElement root = JsonParser.parseString(new String(response));
                DebugLog.log("root = " + root);
                JsonArray rootArray = root.getAsJsonArray();
                for (JsonElement rootRegister : rootArray) {
                    DebugLog.log("rootRegister = " + rootRegister);
                    JsonObject elem = rootRegister.getAsJsonObject();
                    var registrant = elem.get("registrant") != null ? elem.get("registrant").getAsString() : null;
                    var type = Type.REGISTRATION;
                    JsonElement typeElem;
                    if ((typeElem = elem.get("type")) != null && typeElem.isJsonPrimitive() && typeElem.getAsJsonPrimitive().isString()) {
                        switch (typeElem.getAsString()) {
                            case "registration" -> {
                            }
                            case "site-register" -> type = Type.SITE_REGISTER;
                            default -> {
                                LOGGER.error("Invalid element type: {}", typeElem.getAsString());
                                continue;
                            }
                        }
                    }

                    switch (type) {
                        case REGISTRATION -> {
                            @SuppressWarnings("all") //no
                            var dev = elem.get("dev") != null ? elem.get("dev").getAsBoolean() : false;
                            var site = elem.get("site").getAsString();
                            if (dev && !IS_DEV_PREVIEW) {
                                continue;
                            }
                            for (JsonElement registration : elem.get("registrations").getAsJsonArray()) {
                                var a = registration.getAsJsonObject().keySet();
                                var d = registration.getAsJsonObject();
                                for (String string : a) {
                                    var registrationType = d.get(string).getAsString();
                                    SITE_REGISTRATIONS.add(new SiteRegistration(registrant, string, registrationType, site));
                                }
                            }
                        }
                        case SITE_REGISTER -> {
                            if (!elem.has("register") || !elem.get("register").isJsonPrimitive() || !elem.get("register").getAsJsonPrimitive().isString()) {
                                continue;
                            }
                            var registerUrl = elem.get("register").getAsString();
                            try {
                                var registerFuture = setupSiteRegistration(registerUrl);
                                registerFuture.join();
                            } catch (Exception e) {
                                LOGGER.error("Error when loading site register: {}", registerUrl);
                            }
                        }
                    }
                }
            } else {
                LOGGER.error("Error occurred when loading site registrations at: {}", url);
                future.complete(null);
                return;
            }
            future.complete(null);
            SITE_REGISTER_STACK.pop();
        }));

        return future;
    }

    /**
     * @deprecated Use {@link #res(String)} instead!
     */
    @Deprecated
    public static ResourceLocation id(String id) {
        return res(id);
    }

    private static class ProtectedArrayList<T> extends ArrayList<T> {
        private final StackWalker stackWalker = StackWalker.getInstance(EnumSet.of(StackWalker.Option.RETAIN_CLASS_REFERENCE));
        private boolean frozen = false;

        private void freeze() {
            frozen = true;
        }

        private void freezeCheck() {
            if (frozen) throw new IllegalStateException("Already frozen!");
        }

        @Override
        public boolean add(T t) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.add(t);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.addAll(c);
        }

        @Override
        public void add(int index, T element) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.add(index, element);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean remove(Object o) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeAll(c);
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeIf(filter);
        }

        @Override
        public T remove(int index) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(index);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private static class SiteRegisterStack extends Stack<Object> {
        public Object push() {
            return super.push(new Object());
        }

        @Override
        public synchronized Object pop() {
            Object pop = super.pop();
            if (isEmpty()) {
                ((ProtectedArrayList<SiteRegistration>) SITE_REGISTRATIONS).freeze();
            }
            return pop;
        }
    }


}