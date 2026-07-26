package com.freakyhydra.patinaworks.block;

import com.freakyhydra.patinaworks.block.entity.StoneAnvilBlockEntity;
import com.freakyhydra.patinaworks.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class StoneAnvilBlock extends Block implements EntityBlock {
    private static final int MAX_STRIKES = 3;

    public StoneAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoneAnvilBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof StoneAnvilBlockEntity be)) {
            return InteractionResult.PASS;
        }
        ItemStack stored = be.getInputStack();
        if (stored.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            be.setInputStack(ItemStack.EMPTY);
            be.resetProgress();
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4f, 1.0f);
            if (!player.isCreative()) {
                if (!player.getInventory().add(stored)) {
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, stored));
                }
            }
            be.setChanged();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof StoneAnvilBlockEntity be)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack stored = be.getInputStack();

        // Engineer's Hammer → strike
        if (heldItem.is(ModItems.ENGINEERS_HAMMER.get())) {
            if (stored.isEmpty() || !stored.is(Items.COPPER_INGOT)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide) {
                be.incrementStrikeProgress();
                heldItem.hurtAndBreak(1, player, Player.getSlotForHand(hand));
                level.playSound(null, pos, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
                ((ServerLevel) level).sendParticles(ParticleTypes.CRIT,
                        pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                        6, 0.2, 0.1, 0.2, 0.05);
                if (be.getStrikeProgress() >= MAX_STRIKES) {
                    be.setInputStack(new ItemStack(ModItems.COPPER_PLATE.get()));
                    be.resetProgress();
                    level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.6f, 1.0f);
                }
                be.setChanged();
            }
            return ItemInteractionResult.SUCCESS;
        }

        // Copper Ingot → place on anvil
        if (heldItem.is(Items.COPPER_INGOT)) {
            if (!stored.isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide) {
                be.setInputStack(new ItemStack(Items.COPPER_INGOT));
                be.resetProgress();
                level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 0.8f, 1.0f);
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                be.setChanged();
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
