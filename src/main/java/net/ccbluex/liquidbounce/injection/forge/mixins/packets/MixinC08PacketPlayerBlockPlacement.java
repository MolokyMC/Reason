package net.ccbluex.liquidbounce.injection.forge.mixins.packets;

import de.enzaxd.viaforge.ViaForge;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(C08PacketPlayerBlockPlacement.class)
public abstract class MixinC08PacketPlayerBlockPlacement {
    @Shadow private BlockPos position;

    @Shadow private int placedBlockDirection;

    @Shadow public ItemStack stack;

    @Shadow public float facingX;

    @Shadow public float facingY;

    @Shadow public float facingZ;

    // Love From Genshin

    /**
     * @author
     */
    @Overwrite()
    public void readPacketData(PacketBuffer p_readPacketData_1_)throws IOException{
        this.position = p_readPacketData_1_.readBlockPos();
        this.placedBlockDirection = p_readPacketData_1_.readUnsignedByte();
        this.stack = p_readPacketData_1_.readItemStackFromBuffer();
        if (ViaForge.getInstance().getVersion() <= 47) {
            this.facingX = (float)p_readPacketData_1_.readUnsignedByte() / 16.0f;
            this.facingY = (float)p_readPacketData_1_.readUnsignedByte() / 16.0f;
            this.facingZ = (float)p_readPacketData_1_.readUnsignedByte() / 16.0f;
        } else {
            this.facingX = p_readPacketData_1_.readUnsignedByte();
            this.facingY = p_readPacketData_1_.readUnsignedByte();
            this.facingZ = p_readPacketData_1_.readUnsignedByte();
        }
    }
    /**
     * @author
     */
    @Overwrite()
    public void writePacketData(PacketBuffer p_writePacketData_1_) throws IOException{
        p_writePacketData_1_.writeBlockPos(this.position);
        p_writePacketData_1_.writeByte(this.placedBlockDirection);
        p_writePacketData_1_.writeItemStackToBuffer(this.stack);
        if (ViaForge.getInstance().getVersion() <= 47) {
            p_writePacketData_1_.writeByte((int)(this.facingX * 16.0f));
            p_writePacketData_1_.writeByte((int)(this.facingY * 16.0f));
            p_writePacketData_1_.writeByte((int)(this.facingZ * 16.0f));
        } else {
            p_writePacketData_1_.writeByte((int)this.facingX);
            p_writePacketData_1_.writeByte((int)this.facingY);
            p_writePacketData_1_.writeByte((int)this.facingZ);
        }
    }
}