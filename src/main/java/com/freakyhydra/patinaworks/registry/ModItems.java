package com.freakyhydra.patinaworks.registry;

import com.freakyhydra.patinaworks.item.PatinaLedger;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("patinaworks");

    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.registerSimpleItem("copper_plate");
    public static final DeferredItem<Item> COPPER_GEAR = ITEMS.registerSimpleItem("copper_gear");
    public static final DeferredItem<Item> COPPER_RIVET = ITEMS.registerSimpleItem("copper_rivet");
    public static final DeferredItem<Item> ENGINEERS_HAMMER = ITEMS.registerSimpleItem("engineers_hammer",
            new Item.Properties().durability(256));
    public static final DeferredItem<Item> PATINA_LEDGER = ITEMS.register("patina_ledger",
            () -> new PatinaLedger(new Item.Properties().stacksTo(1)));
}
