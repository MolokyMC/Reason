/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.client.ClientSettings
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.world.AutoDisable
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S45PacketTitle

@ModuleInfo(name = "AutoGG", description = "AutoGG after you won a game.", category = ModuleCategory.PLAYER)
class AutoGG : Module() {
    private val startMsgValue = BoolValue("StartChat", false)
    private val startMsg = TextValue(
        "StartMsg",
        "[${ClientSettings.cname}]我正在使用 ${ClientSettings.cname}"
    ) { startMsgValue.get() }
    private val ggMessageValue = TextValue("GGMessage", "[${ClientSettings.cname}]GoodGame, Love from Reason-1.8.9.")

    private var winning = false
    private var winverify1 = false
    private var winverify2 = false
    private var gamestarted = false
    private var started1 = false
    private var started2 = false
    private var gameend = false
    override fun onEnable() {
        stateReset()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        stateReset()
    }

    private fun stateReset() {
        winning = false
        winverify1 = false
        winverify2 = false
        gamestarted = false
        started1 = false
        started2 = false
        gameend = false
    }


    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S02PacketChat) {
            val message = packet.chatComponent.unformattedText

            if (message.contains("恭喜") && !message.contains(":") && message.startsWith("起床战争")) {
                winverify1 = true
            }
            if (message.startsWith("[起床战争]") && message.contains("赢得了游戏") && message.contains(":")) {
                winverify1 = true
            }
            if (message.contains("游戏开始 ...") && message.startsWith("起床战争")) {
                gamestarted = true
            }
            if (message.startsWith("你现在是观察者状态. 按E打开菜单.")) {
                gameend = true
            }
            if (message.startsWith("开始倒计时: 1 秒")) {
                started2 = true
            }
        }
        if (packet is S45PacketTitle) {
            val title = (packet.message ?: return).unformattedText
            if (title.contains("恭喜")) {
                winverify2 = true
            }
            if (title.contains("你的队伍获胜了")) {
                winverify2 = true
            }
            if (title.contains("VICTORY")) {
                winning = true
            }
            if (title.contains("战斗开始")) {
                started1 = true
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (winning || (winverify1 && winverify2)) {
            gg2()
        }
        if (gamestarted || (started1 && started2)) {
            total()
        }
        if (gameend) lose()

    }

    private fun gg2() {
        if (this.state) {
            LiquidBounce.hud.addNotification(Notification(this.name, "You won the game, GG!", NotifyType.SUCCESS))
            mc.thePlayer.sendChatMessage((ggMessageValue.get()))
        }
        AutoDisable.handleGameEnd()
        stateReset()
    }

    private fun total() {
        if (this.state && startMsgValue.get()) {
            mc.thePlayer!!.sendChatMessage((startMsg.get()))
        }
        stateReset()
    }

    private fun lose() {
        AutoDisable.handleGameEnd()
        stateReset()
    }


    override fun handleEvents() = true
}
