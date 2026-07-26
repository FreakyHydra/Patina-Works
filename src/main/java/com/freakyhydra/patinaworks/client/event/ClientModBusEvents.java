package com.freakyhydra.patinaworks.client.event;

import com.freakyhydra.patinaworks.block.entity.ModBlockEntities;
import com.freakyhydra.patinaworks.client.renderer.blockentity.StoneAnvilBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientModBusEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.STONE_ANVIL.get(), StoneAnvilBlockEntityRenderer::new);
    }
}
