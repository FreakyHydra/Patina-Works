package com.freakyhydra.patinaworks.client.event;

import com.freakyhydra.patinaworks.client.screen.PatinaRecipeScreen;
import com.freakyhydra.patinaworks.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(ModItems.PATINA_LEDGER.get())) {
            Minecraft.getInstance().setScreen(new PatinaRecipeScreen());
            event.setCanceled(true);
        }
    }
}
