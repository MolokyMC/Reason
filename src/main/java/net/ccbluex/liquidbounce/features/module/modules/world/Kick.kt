/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import java.util.*

@ModuleInfo(name = "Kick", description = "Allows you to kick yourself from a server.", category = ModuleCategory.WORLD, onlyEnable = true)
class Kick : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Quit", "InvalidPacket", "SelfHurt", "IllegalChat", "PacketSpam"), "Quit")

    override fun onEnable() {
        if (mc.isIntegratedServerRunning) {
            ClientUtils.displayChatMessage("§c§lError: §aYou can't enable §c§l'Kick' §ain SinglePlayer.")
            return
        }

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "quit" -> mc.theWorld.sendQuittingDisconnectingPacket()
            "invalidpacket" -> mc.netHandler.addToSendQueue(
                C04PacketPlayerPosition(
                    Double.NaN,
                    Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY,
                    !mc.thePlayer.onGround
                )
            )

            "selfhurt" -> mc.netHandler.addToSendQueue(
                C02PacketUseEntity(
                    mc.thePlayer,
                    C02PacketUseEntity.Action.ATTACK
                )
            )

            "illegalchat" -> mc.thePlayer.sendChatMessage(Random().nextInt().toString() + "§§§" + Random().nextInt())
            "packetspam" -> {
                repeat(9999) {
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            it.toDouble(), it.toDouble(), it.toDouble(),
                            Random().nextBoolean()
                        )
                    )
                }
            }
        }
    }

}