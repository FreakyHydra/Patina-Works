package com.freakyhydra.patinaworks.block.entity;

import com.freakyhydra.patinaworks.registry.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "patinaworks");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StoneAnvilBlockEntity>> STONE_ANVIL = BLOCK_ENTITIES.register("stone_anvil",
            () -> BlockEntityType.Builder.of(StoneAnvilBlockEntity::new, ModBlocks.STONE_ANVIL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperSteamBoilerBlockEntity>> COPPER_STEAM_BOILER = BLOCK_ENTITIES.register("copper_steam_boiler",
            () -> BlockEntityType.Builder.of(CopperSteamBoilerBlockEntity::new, ModBlocks.COPPER_STEAM_BOILER.get()).build(null));
}
