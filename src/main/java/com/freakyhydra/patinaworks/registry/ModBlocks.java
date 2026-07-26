package com.freakyhydra.patinaworks.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("patinaworks");

    public static final DeferredBlock<Block> PATINA_WORKBENCH = BLOCKS.registerSimpleBlock("patina_workbench",
            BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE)
                    .strength(2.5f));

    public static final DeferredItem<BlockItem> PATINA_WORKBENCH_ITEM = ModItems.ITEMS.registerSimpleBlockItem("patina_workbench", PATINA_WORKBENCH);
}
