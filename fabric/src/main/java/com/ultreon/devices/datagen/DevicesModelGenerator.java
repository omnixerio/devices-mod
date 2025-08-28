package com.ultreon.devices.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.level.block.Block;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class DevicesModelGenerator extends FabricModelProvider {

    private final FabricDataOutput dataOutput;

    public DevicesModelGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
        this.dataOutput = dataOutput;
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        DeviceBlocks.LAPTOPS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_closed"), () -> new Gson().fromJson(String.format(laptopClosedPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_full"), () -> new Gson().fromJson(String.format(laptopFullPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_flitem"), () -> new Gson().fromJson(String.format(laptopFullItemPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @NotNull
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(laptopStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get(), "_flitem"));
        });

        DeviceBlocks.OFFICE_CHAIRS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_legs"), () -> new Gson().fromJson(String.format(officeChairFullLegsPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_full"), () -> new Gson().fromJson(String.format(officeChairFullItemPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_seat"), () -> new Gson().fromJson(String.format(officeChairFullSeatPain(), dye.getName()), JsonElement.class));
            //blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @NotNull
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(officeChairStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get(), "_full"));
        });

        DeviceBlocks.ROUTERS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(routerPain(), dye.getName()), JsonElement.class));
            //blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @NotNull
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(routerStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get()));
        });

        DeviceBlocks.PRINTERS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(printerPain(), dye.getName()), JsonElement.class));
            //blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @NotNull
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(printerStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get()));
        });

        //new BlockStateGenerator() {
        //                @Override
        //                public Block getBlock() {
        //                    return block.get();
        //                }
        //
        //                @Override
        //                public JsonElement get() {
        //                    return new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class);
        //                }
        //            }
    }

    private String printerPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("printer/printerpain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String printerStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("printer/printerstatepain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String routerPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("router/routerpain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String routerStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("router/routerstatepain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("laptop/laptoppain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopClosedPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("laptop/laptopclosedpain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("laptop/laptopstatepain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopFullPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("laptop/laptopfullpain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopFullItemPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("laptop/laptopfullitempain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String officeChairFullItemPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("office_chair/officechairfullpain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String officeChairFullLegsPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("office_chair/officechairlegspain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String officeChairFullSeatPain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("office_chair/officechairseatpain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String officeChairStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataOutput.getModContainer().findPath("office_chair/officechairstatepain.txt").orElseThrow().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(DeviceItems.GLASS_DUST.get(), ModelTemplates.FLAT_ITEM);
    }
}
