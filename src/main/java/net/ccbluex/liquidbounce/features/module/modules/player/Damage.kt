/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import java.util.*

@ModuleInfo(name = "Damage", description = "Deals damage to yourself.", category = ModuleCategory.PLAYER, onlyEnable = true)
class Damage : Module() {

    private val modeValue = ListValue("Mode", arrayOf("NCP", "AAC"), "NCP")
    private val ncpMode = ListValue("NCPMode", arrayOf("Glitch", "JumpPacket"), "Glitch")
    private val damageValue = IntegerValue("Damage", 1, 1, 20)
    private val onlyGround = BoolValue("OnlyGround", true)
    private val jumpYPosArr = arrayOf(
        0.41999998688698,
        0.7531999805212,
        1.00133597911214,
        1.16610926093821,
        1.24918707874468,
        1.24918707874468,
        1.1707870772188,
        1.0155550727022,
        0.78502770378924,
        0.4807108763317,
        0.10408037809304,
        0.0
    )

    override fun onEnable() {
        if (onlyGround.get() && !mc.thePlayer.onGround) {
            return
        }

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "ncp" -> {
                when (ncpMode.get().lowercase()) {
                    "glitch" -> {
                        val x = mc.thePlayer.posX
                        val y = mc.thePlayer.posY
                        val z = mc.thePlayer.posZ

                        repeat((55 + damageValue.get() * 10.204).toInt()) {
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.049, z, false))
                            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                        }
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, true))
                    }

                    "jumppacket" -> {
                        var x = mc.thePlayer.posX
                        var y = mc.thePlayer.posY
                        var z = mc.thePlayer.posZ
                        repeat(4) {
                            jumpYPosArr.forEach {
                                PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(x, y + it, z, false))
                            }
                            PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(x, y, z, false))
                        }
                        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(x, y, z, true))
                    }
                }
            }

            "aac" -> mc.thePlayer.motionY = 4 + damageValue.get().toDouble()
        }
    }
}

