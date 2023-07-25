package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.Colors
import net.ccbluex.liquidbounce.utils.render.Render
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.math.toRadians
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.VisualUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "JumpCircle", description =  "JumpCircle", category = ModuleCategory.RENDER)
class JumpCircle : Module() {
    val typeValue = ListValue("Mode", arrayOf("OldCircle", "NewCircle"), "OldCircle")
    //NewCircle
    val disappearTime = IntegerValue("Time", 1000, 1000,3000)
    val radius = FloatValue("Radius", 2f, 1f,5f)
    val rainbow = BoolValue("Rainbow", false)
    val start = FloatValue("Start", 0.5f, 0f,1f)
    val end = FloatValue("End", 0.3f, 0f,1f)
    //
    var r = IntegerValue("Red",255,0,255)
    var g = IntegerValue("green",255,0,255)
    var b = IntegerValue("blue",255,0,255)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)

    private val points = mutableMapOf<Int, MutableList<Render>>()
    var jump=false;
    var entityjump=false;
    val circles = mutableListOf<Circle>()
    var red = r.get()
    var green = g.get()
    var blue = b.get()

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        when (typeValue.get().lowercase(Locale.getDefault())) {
            "oldcircle" -> {
                points.forEach {
                    for (point in it.value) {
                        point.draw()
                        if (point.alpha < 0F) {
                            it.value.remove(point)
                        }
                    }
                }
            }

            "newcircle" -> {
                circles.removeIf { System.currentTimeMillis() > it.time + disappearTime.get() }

                GL11.glPushMatrix()

                GL11.glEnable(GL11.GL_BLEND)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glDisable(GL11.GL_CULL_FACE)
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glDisable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(false)
                GL11.glDisable(GL11.GL_ALPHA_TEST)
                GL11.glShadeModel(GL11.GL_SMOOTH)

                circles.forEach { it.draw() }

                GL11.glDisable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_CULL_FACE)
                GL11.glEnable(GL11.GL_TEXTURE_2D)
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(true)
                GL11.glEnable(GL11.GL_ALPHA_TEST)
                GL11.glShadeModel(GL11.GL_FLAT)

                GL11.glPopMatrix()
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.onGround && !jump) {
            jump = true
        }
        if (mc.thePlayer.onGround && jump) {
            updatePoints(mc.thePlayer);
            jump = false
        }
        /* for(entity in mc.theWorld.playerEntities) {
             if (!entity.onGround && !entityjump) {
                 entityjump = true
             }
             if (entity.onGround && entityjump) {
                 updatePoints(entity);
                 entityjump = false
             }
         }*/
    }

    fun updatePoints(entity: EntityLivingBase) {
        when (typeValue.get().lowercase(Locale.getDefault())) {
            "oldcircle" -> {
                val counter = intArrayOf(0)
                (points[entity.entityId] ?: mutableListOf<Render>().also { points[entity.entityId] = it }).add(
                    Render(
                        entity.posX, entity.entityBoundingBox.minY, entity.posZ, System.currentTimeMillis(),
                        Colors.astolfoRainbow(counter[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                    )
                )
                counter[0] = counter[0] + 1
            }

            "newcircle" -> {
                circles.add(Circle(System.currentTimeMillis(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ))
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        points.clear()
    }

    override fun onDisable() {
        points.clear()
    }

    class Circle(val time: Long, val x: Double, val y: Double, val z: Double){
        val jumpModule = LiquidBounce.moduleManager.getModule(JumpCircle::class.java) as JumpCircle
        var red = jumpModule.r.get()
        var green = jumpModule.g.get()
        var blue = jumpModule.b.get()

        fun draw() {
            if(jumpModule == null) {
                return
            }

            val dif = (System.currentTimeMillis() - time)
            val c = 255 - (dif / jumpModule.disappearTime.get().toFloat()) * 255

            GL11.glPushMatrix()

            GL11.glTranslated(
                x - mc.renderManager.viewerPosX,
                y - mc.renderManager.viewerPosY,
                z - mc.renderManager.viewerPosZ
            )

            GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
            for (i in 0..360) {
                val color = if (jumpModule.rainbow.get()) Color.getHSBColor(i / 360f, 1f, 1f)
                else Color(red,green,blue)

                val x = (dif * jumpModule.radius.get() * 0.001 * sin(i.toDouble().toRadians()))
                val z = (dif * jumpModule.radius.get() * 0.001 * cos(i.toDouble().toRadians()))

                VisualUtils.glColor(color.red, color.green, color.blue, 0)
                GL11.glVertex3d(x / 2, 0.0, z / 2)

                VisualUtils.glColor(color.red, color.green, color.blue, c.toInt())
                GL11.glVertex3d(x, 0.0, z)
            }
            GL11.glEnd()

            GL11.glPopMatrix()
        }
    }
}
