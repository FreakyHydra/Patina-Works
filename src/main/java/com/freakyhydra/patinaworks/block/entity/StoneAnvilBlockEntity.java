package com.freakyhydra.patinaworks.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StoneAnvilBlockEntity extends BlockEntity {
    private ItemStack inputStack = ItemStack.EMPTY;
    private int strikeProgress = 0;

    public StoneAnvilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STONE_ANVIL.get(), pos, state);
    }

    public ItemStack getInputStack() {
        return inputStack;
    }

    public void setInputStack(ItemStack stack) {
        this.inputStack = stack;
        sync();
    }

    public int getStrikeProgress() {
        return strikeProgress;
    }

    public void incrementStrikeProgress() {
        this.strikeProgress++;
        sync();
    }

    public void resetProgress() {
        this.strikeProgress = 0;
        sync();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("InputStack", inputStack.saveOptional(registries));
        tag.putInt("StrikeProgress", strikeProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inputStack = ItemStack.parseOptional(registries, tag.getCompound("InputStack"));
        strikeProgress = tag.getInt("StrikeProgress");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("InputStack", inputStack.saveOptional(registries));
        tag.putInt("StrikeProgress", strikeProgress);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
