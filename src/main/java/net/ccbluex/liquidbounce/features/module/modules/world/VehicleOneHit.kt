/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.entity.item.EntityBoat
import net.minecraft.entity.item.EntityMinecart
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation

@ModuleInfo(name = "VehicleOneHit", spacedName = "Vehicle One Hit", description = "Allows you to break vehicles with a single hit.", category = ModuleCategory.WORLD)
class VehicleOneHit : Module() {

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityBoat || event.targetEntity is EntityMinecart) {
            for (i in 0..3) {
                mc.netHandler.addToSendQueue(C0APacketAnimation())
                mc.netHandler.addToSendQueue(C02PacketUseEntity(event.targetEntity,
                        C02PacketUseEntity.Action.ATTACK))
            }
        }
    }

}
