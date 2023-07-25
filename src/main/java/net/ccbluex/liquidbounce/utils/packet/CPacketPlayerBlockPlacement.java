package net.ccbluex.liquidbounce.utils.packet;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

public class CPacketPlayerBlockPlacement extends C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer> {
    private static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);
    private BlockPos position;
    private BlockPos position2;
    private int placedBlockDirection;
    private ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;

    public CPacketPlayerBlockPlacement() {
    }

    public CPacketPlayerBlockPlacement(ItemStack p_i45930_1_) {
        this(field_179726_a, 255, p_i45930_1_, 0.0F, 0.0F, 0.0F);
    }

    public CPacketPlayerBlockPlacement(BlockPos p_i45931_1_, int p_i45931_2_, ItemStack p_i46858_3_, float p_i45931_4_, float p_i45931_5_, float p_i45931_6_) {
        this.position2 = p_i45931_1_;
        this.placedBlockDirection = p_i45931_2_;
        this.stack = p_i46858_3_ != null ? p_i46858_3_.copy() : null;
        this.facingX = p_i45931_4_;
        this.facingY = p_i45931_5_;
        this.facingZ = p_i45931_6_;
    }

    public void readPacketData(PacketBuffer p_readPacketData_1_) throws IOException {
        this.position = p_readPacketData_1_.readBlockPos();
        this.placedBlockDirection = p_readPacketData_1_.readUnsignedByte();
        this.stack = p_readPacketData_1_.readItemStackFromBuffer();
        this.facingX = (float)p_readPacketData_1_.readUnsignedByte() / 16.0F;
        this.facingY = (float)p_readPacketData_1_.readUnsignedByte() / 16.0F;
        this.facingZ = (float)p_readPacketData_1_.readUnsignedByte() / 16.0F;
    }

    public void writePacketData(PacketBuffer p_writePacketData_1_) throws IOException {
        p_writePacketData_1_.writeBlockPos(this.position);
        p_writePacketData_1_.writeByte(this.placedBlockDirection);
        p_writePacketData_1_.writeItemStackToBuffer(this.stack);
        p_writePacketData_1_.writeByte((int)(this.facingX * 16.0F));
        p_writePacketData_1_.writeByte((int)(this.facingY * 16.0F));
        p_writePacketData_1_.writeByte((int)(this.facingZ * 16.0F));
    }

    public void processPacket(INetHandlerPlayServer p_processPacket_1_) {
        p_processPacket_1_.processPlayerBlockPlacement(this);
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public int getPlacedBlockDirection() {
        return this.placedBlockDirection;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public float getPlacedBlockOffsetX() {
        return this.facingX;
    }

    public float getPlacedBlockOffsetY() {
        return this.facingY;
    }

    public float getPlacedBlockOffsetZ() {
        return this.facingZ;
    }
}
