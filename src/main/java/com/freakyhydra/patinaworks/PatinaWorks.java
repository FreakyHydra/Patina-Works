package com.freakyhydra.patinaworks;

import com.freakyhydra.patinaworks.registry.ModBlocks;
import com.freakyhydra.patinaworks.registry.ModCreativeTabs;
import com.freakyhydra.patinaworks.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(PatinaWorks.MOD_ID)
public class PatinaWorks {
    public static final String MOD_ID = "patinaworks";

    public PatinaWorks(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
