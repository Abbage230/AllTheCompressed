package com.Pdiddy973.AllTheCompressed.overlay;

import com.Pdiddy973.AllTheCompressed.AllTheCompressed;
import com.Pdiddy973.AllTheCompressed.ModRegistry;
import com.google.common.base.Suppliers;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.Pdiddy973.AllTheCompressed.ModRegistry.CREATIVE_TAB;

public class OverlayEntry {
    public final ResourceLocation parent;
    public final RegistryObject<Block> x1;
    public final RegistryObject<Block> x2;
    public final RegistryObject<Block> x3;
    public final RegistryObject<Block> x4;
    public final RegistryObject<Block> x5;
    public final RegistryObject<Block> x6;
    public final RegistryObject<Block> x7;
    public final RegistryObject<Block> x8;
    public final RegistryObject<Block> x9;
    public final List<RegistryObject<Block>> xall;

    public final RegistryObject<BlockItem> i1;
    public final RegistryObject<BlockItem> i2;
    public final RegistryObject<BlockItem> i3;
    public final RegistryObject<BlockItem> i4;
    public final RegistryObject<BlockItem> i5;
    public final RegistryObject<BlockItem> i6;
    public final RegistryObject<BlockItem> i7;
    public final RegistryObject<BlockItem> i8;
    public final RegistryObject<BlockItem> i9;
    public final List<RegistryObject<BlockItem>> iall;

    private boolean blockLoaded = false;

    /**
     * If the parent block has been loaded successfully
     * @return the result
     */
    public boolean isBlockLoaded() {
        return blockLoaded;
    }

    public OverlayEntry(ResourceLocation parent) {
        this.parent = parent;
        Supplier<BlockBehaviour.Properties> properties = getProperties(parent);
        boolean pillar = parent.getPath().endsWith("_log");


        x1 = block(parent, properties, 1, pillar);
        x2 = block(parent, properties, 2, pillar);
        x3 = block(parent, properties, 3, pillar);
        x4 = block(parent, properties, 4, pillar);
        x5 = block(parent, properties, 5, pillar);
        x6 = block(parent, properties, 6, pillar);
        x7 = block(parent, properties, 7, pillar);
        x8 = block(parent, properties, 8, pillar);
        x9 = block(parent, properties, 9, pillar);
        xall = List.of(x1, x2, x3, x4, x5, x6, x7, x8, x9);

        i1 = blockItem(x1);
        i2 = blockItem(x2);
        i3 = blockItem(x3);
        i4 = blockItem(x4);
        i5 = blockItem(x5);
        i6 = blockItem(x6);
        i7 = blockItem(x7);
        i8 = blockItem(x8);
        i9 = blockItem(x9);
        iall = List.of(i1, i2, i3, i4, i5, i6, i7, i8, i9);
    }

    private static final BlockBehaviour.Properties defaultProperties = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F);

    /**
     * Retrieve the block properties for the parent block at registration time
     * @param parent the parent block
     * @return a supplier for the properties
     */
    private Supplier<BlockBehaviour.Properties> getProperties(ResourceLocation parent) {
        return Suppliers.memoize(() -> {
            BlockBehaviour.Properties properties = defaultProperties;
            if (ModList.get().isLoaded(parent.getNamespace())) {
                var block = ForgeRegistries.BLOCKS.getValue(parent);
                if (block != null) {
                    blockLoaded = true;
                    properties = BlockBehaviour.Properties.copy(block);
                } else {
                    AllTheCompressed.LOGGER.error("Trying to register an overlay for a block that doesn't exist! {}", parent);
                }
            }
            return properties;
        });
    }

    /**
     * Register a BlockItem for a Block
     *
     * @param registryObject the Block
     * @return the new registry object
     */
    private static RegistryObject<BlockItem> blockItem(RegistryObject<Block> registryObject) {
        return ModRegistry.ITEMS.register(registryObject.getId().getPath(),
            () -> new BlockItem(registryObject.get(), new Item.Properties()));
    }

    /**
     * Register an OverlayBlock
     * @param parent the block this is based on
     * @param properties the block properties for the new block
     * @param level the compression level of the block
     * @param pillar whether the block is a pillar block
     * @return the new registry entry
     */
    private static RegistryObject<Block> block(ResourceLocation parent, Supplier<BlockBehaviour.Properties> properties, int level, boolean pillar) {
        Supplier<Block> supplier;
        if (pillar) {
            supplier = () -> new OverlayPillarBlock(properties.get(), level);
        } else {
            supplier = () -> new OverlayBlock(properties.get(), level);
        }
        return ModRegistry.BLOCKS.register(generateName(parent, level), supplier);
    }

    private static String generateName(ResourceLocation parent, int level) {
        return String.format("%s_%dx", parent.getPath(), level);
    }
}