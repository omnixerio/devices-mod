package dev.ultreon.devices.client.debug;

/// Adds a button to the title screen to test system applications that don't require the system
public class ClientAppDebug {
    public static void register() {
        // TODO port to XinexLib
//        ClientGuiEvent.INIT_POST.register(((screen, access) -> {
//            if (DeviceConfig.DEBUG_BUTTON.get()) {
//                if (!(screen instanceof TitleScreen)) return;
//                var rowHeight = 24;
//                var y = screen.height / 4 + 48;
//
//                var a = Button.builder(Component.literal("DV TEST"), (button) -> Minecraft.getInstance().setScreen(new ComputerScreen(new LaptopBlockEntity(new BlockPos(0, 0, 0), DeviceBlocks.LAPTOPS.of(DyeColor.WHITE).get().defaultBlockState()), true))).bounds(screen.width / 2 - 100, y + rowHeight * -1, 200, 20)
//                        .createNarration((output) -> Component.empty())
//                        .build();
//                access.addRenderableWidget(a);
//            }
//        }));
//
//        ClientGuiEvent.INIT_POST.register(((screen, access) -> {
//            if (DeviceConfig.DEBUG_BUTTON.get()) {
//                if (!(screen instanceof TitleScreen)) return;
//                var rowHeight = 24;
//                var y = screen.height / 4 + 48;
//
//                var a = Button.builder(Component.literal("DV TEST #2"), (button) -> {
//                    var serverLaptop = new ServerLaptop();
//                    ServerLaptop.laptops.put(serverLaptop.getUuid(), serverLaptop);
//                    var clientLaptop = new ClientLaptop();
//                    clientLaptop.setUuid(serverLaptop.getUuid());
//                    ClientLaptop.laptops.put(clientLaptop.getUuid(), clientLaptop);
//                    Minecraft.getInstance().setScreen(new ClientLaptopScreen(clientLaptop));
//                }).bounds(screen.width / 2 - 100, y + rowHeight * -2, 200, 20)
//                        .createNarration((output) -> Component.empty())
//                        .build();
//                access.addRenderableWidget(a);
//            }
//        }));
    }
}

