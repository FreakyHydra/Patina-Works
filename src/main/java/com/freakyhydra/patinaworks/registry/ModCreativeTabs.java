package com.freakyhydra.patinaworks.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "patinaworks");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PATINA_WORKS_TAB = CREATIVE_MODE_TABS.register("patina_works_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.patinaworks"))
            .icon(() -> new ItemStack(ModItems.COPPER_PLATE.get()))
            .displayItems((parameters, output) -> {
                output.accept(new ItemStack(ModBlocks.PATINA_WORKBENCH.get()));
                output.accept(new ItemStack(ModItems.COPPER_PLATE.get()));
                output.accept(new ItemStack(ModItems.COPPER_GEAR.get()));
                output.accept(new ItemStack(ModItems.COPPER_RIVET.get()));
                output.accept(new ItemStack(ModItems.ENGINEERS_HAMMER.get()));
                output.accept(new ItemStack(ModItems.PATINA_LEDGER.get()));
            })
            .build());
}
