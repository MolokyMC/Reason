/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ModuleInfo(name = "PingSpoof", spacedName = "Ping Spoof", description = "Spoofs your ping to a given value.", category = ModuleCategory.WORLD)
public class PingSpoof extends Module {

    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 1000, 0, 5000, "ms") {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int minDelayValue = PingSpoof.this.minDelayValue.get();

            if(minDelayValue > newValue)
                set(minDelayValue);
        }
    };

    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 500, 0, 5000, "ms") {

        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int maxDelayValue = PingSpoof.this.maxDelayValue.get();

            if(maxDelayValue < newValue)
                set(maxDelayValue);
        }
    };

    private final HashMap<Packet<?>, Long> packetsMap = new HashMap<>();

    @Override
    public void onDisable() {
        packetsMap.clear();
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet packet = event.getPacket();

        if ((packet instanceof C00PacketKeepAlive || packet instanceof C16PacketClientStatus) && !(mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0) && !packetsMap.containsKey(packet)) {
            event.cancelEvent();

            synchronized(packetsMap) {
                packetsMap.put(packet, System.currentTimeMillis() + TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get()));
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onUpdate(final UpdateEvent event) {
        try {
            synchronized(packetsMap) {
                for(final Iterator<Map.Entry<Packet<?>, Long>> iterator = packetsMap.entrySet().iterator(); iterator.hasNext(); ) {
                    final Map.Entry<Packet<?>, Long> entry = iterator.next();

                    if(entry.getValue() < System.currentTimeMillis()) {
                        mc.getNetHandler().addToSendQueue(entry.getKey());
                        iterator.remove();
                    }
                }
            }
        }catch(final Throwable t) {
            t.printStackTrace();
        }
    }

}
