package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.LiquidBounce.CLIENT_NAME
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.Display
import java.awt.Color
import java.util.*

/**
 * Skid or Made By WaWa
 * @date 2023/7/23 15:31
 * @author RyF
 */
@ModuleInfo(name = "ClientSettings", description = "Settings", category = ModuleCategory.CLIENT, canEnable = false)
class ClientSettings : Module() {
    companion object {
        /*@JvmStatic
        val logSaveConfig = BoolValue("NoSaveConfigLog", false)

        @JvmStatic
        val logLoadConfig = BoolValue("NoLoadConfigLog", false)

        @JvmStatic
        val customScoreBoard = TextValue("CustomScoreBoard", "Powered By RyF")

        val scoreBoardColorCode = BoolValue("Vanilla-Colored-ScoreBoard", false)*/

        @JvmStatic
        val clientNameLang: ListValue =
            object : ListValue("ClientNameLang", arrayOf("English", "Chinese"), "English") {
                override fun onChanged(oldValue: String, newValue: String) {
                    fun getChangedName(): String {
                        return when (newValue.lowercase(Locale.getDefault())) {
                            "english" -> CLIENT_NAME
                            "chinese" -> "理智客户端"
                            else -> CLIENT_NAME
                        }
                    }
                    super.onChanged(oldValue, newValue)
                }
            }
        private val clientNameColor = ListValue(
            "ClientNameColor",
            arrayOf("Light-Blue", "Red", "Pink", "Green", "Gold", "Grey"),
            "Light-Blue"
        )

        @JvmStatic
        val settlePrefix =
            ListValue("CustomPrefix", arrayOf("Use»", "Use|", "Use>>", "Use>", "Use->"), "Use»")
        val forceFirstTitle = BoolValue("ForceFirstTitle", true)


        @JvmStatic
        var prefix = "»"

        @JvmStatic
        var cname = "Reason"

        @JvmStatic
        var ccolor = "§b"
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val sr = ScaledResolution(mc)
        val height = sr.scaledHeight
        Fonts.font35.drawString(
            "$cname Build ${LiquidBounce.CLIENT_VERSION}",
            2.0F,
            height - (Fonts.minecraftFont.FONT_HEIGHT + 2.0F),
            Color.WHITE.rgb,
            true
        )
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val timer = MSTimer()
        if (!timer.hasTimePassed(1000L)) return
        prefix = when (settlePrefix.get().lowercase(Locale.getDefault())) {
            "use»" -> "»"
            "use|" -> "|"
            "use>>" -> ">>"
            "use>" -> ">"
            "use->" -> "->"
            else -> "|"
        }

        cname = when (clientNameLang.get().lowercase(Locale.getDefault())) {
            "english" -> CLIENT_NAME
            "chinese" -> "理智客户端"
            else -> CLIENT_NAME
        }
        ccolor = when (clientNameColor.get().lowercase(Locale.getDefault())) {
            "light-blue" -> "§b"
            "red" -> "§c"
            "pink" -> "§d"
            "green" -> "§a"
            "gold" -> "§6"
            "grey" -> "§7"
            else -> "§b"
        }
        timer.reset()
    }


    override fun handleEvents(): Boolean = true
}