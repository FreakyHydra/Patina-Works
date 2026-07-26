package com.freakyhydra.patinaworks.registry;

import com.freakyhydra.patinaworks.block.CopperTankBlock;
import com.freakyhydra.patinaworks.block.StoneAnvilBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("patinaworks");

    public static final DeferredBlock<Block> PATINA_WORKBENCH = BLOCKS.registerSimpleBlock("patina_workbench",
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE)
                    .strength(2.5f));

    public static final DeferredBlock<Block> STONE_ANVIL = BLOCKS.register("stone_anvil",
            () -> new StoneAnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> COPPER_TANK = BLOCKS.register("copper_tank",
            () -> new CopperTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredItem<BlockItem> PATINA_WORKBENCH_ITEM = ModItems.ITEMS.registerSimpleBlockItem("patina_workbench", PATINA_WORKBENCH);

    public static final DeferredItem<BlockItem> STONE_ANVIL_ITEM = ModItems.ITEMS.registerSimpleBlockItem("stone_anvil", STONE_ANVIL);

    public static final DeferredItem<BlockItem> COPPER_TANK_ITEM = ModItems.ITEMS.registerSimpleBlockItem("copper_tank", COPPER_TANK);
}
