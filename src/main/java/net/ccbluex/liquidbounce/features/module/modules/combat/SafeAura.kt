/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.world.Teams
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.RotationUtils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MathHelper
import org.apache.commons.lang3.RandomUtils
import kotlin.math.min


@ModuleInfo(name = "SafeAura", description = "wawa", category = ModuleCategory.COMBAT)
class SafeAura : Module() {

    // CPS - Attack speed
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }
    val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    val rangeValue = FloatValue("Range", 3.50F, 0.00F, 6.00F)

    val rangeFix = FloatValue("AirLessRange",0.35F,0.00F,2.00F)

    private var blockRange = FloatValue("AutoBlockRange", 3f, 0f, 8f)

    private val silentRotationValue = BoolValue("SilentRotation", true)

    // Turn Speed
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }

    // Attack delay
    private val attackTimer = MSTimer()
    private var attackDelay = 0L

    private val switchTimer = MSTimer()

    private var hittable = false
    var target: Entity? = null
    private val prevTargetEntities = mutableListOf<Int>()

    private var blocking = false

    private var range = rangeValue.get() - (if (mc.thePlayer!!.posY.toInt() > target!!.posY.toInt()) rangeFix.get() else 0F)

    fun runAttack(){
        mc.thePlayer!!.swingItem()
        mc.netHandler.addToSendQueue(C02PacketUseEntity(target!!, C02PacketUseEntity.Action.ATTACK))

        if(switchTimer.hasTimePassed(300L)){
            prevTargetEntities.add(target!!.entityId)
            switchTimer.reset()
        }
    }
    fun updateRotations(entity: Entity):Boolean{
        var boundingBox: AxisAlignedBB = entity.entityBoundingBox
        boundingBox = boundingBox.offset(
            (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(0.95f, 1.75f),
            (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(0.95f, 1.75f),
            (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(0.95f, 1.75f)
        )
        val (vec, rotation) = searchCenter(
            boundingBox,
            false,
            false,
            true,
            false,
            range
        ) ?: return false

        val (_, rotation1) = lockView_Down(
            boundingBox,
            false,
            false,
            true,
            false,
            range
        ) ?: return false

        if (silentRotationValue.get()) {
            if (mc.thePlayer!!.posY.toInt() < target!!.posY.toInt()){
                setTargetRotation(rotation1, 0)
            }else {
                setTargetRotation(rotation, 0)
            }
        }else {
            if (mc.thePlayer!!.posY.toInt() < target!!.posY.toInt()){
                rotation1.toPlayer(mc.thePlayer!!)
            }else {
                rotation.toPlayer(mc.thePlayer!!)
            }
            rotation.toPlayer(mc.thePlayer!!)
            return true
        }

        return true
    }
    private fun updateTarget() {
        // Reset fixed target to null
        target = null

        // Settings
        val hurtTime = 10
        val fov = fovValue.get()
        val switchMode = true

        // Find possible targets
        val targets = mutableListOf<EntityLivingBase>()

        val theWorld = mc.theWorld!!
        val thePlayer = mc.thePlayer!!

        for (entity in theWorld.loadedEntityList) {
            if (entity !is EntityLivingBase || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(entity.entityId)))
                continue

            val distance = thePlayer.getDistanceToEntityBox(entity)
            val entityFov = getRotationDifference(entity)

            if (distance <= range && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime)
                targets.add(entity)
        }

        // Sort targets
        targets.sortBy { thePlayer.getDistanceToEntityBox(it) } // Sort by distance


        // Find best target
        for (entity in targets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            target = entity
            return
        }

        // Cleanup last targets when no target found and try again
        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        updateTarget()
        val reach = min(range.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(target!!)) + 1
        hittable = isFaced(target!!, reach)
        if (target != null) {
            if (mc.thePlayer!!.getDistanceToEntity(target!!) <= blockRange.get() && mc.thePlayer!!.heldItem!!.item is ItemSword) {
                mc.gameSettings.keyBindUseItem.pressed = true
                blocking = true
            }
        }
        if (target == null) {
            mc.gameSettings.keyBindUseItem.pressed = false
            blocking = false
        }
        if (hittable && target != null && attackTimer.hasTimePassed(attackDelay) &&
            (mc.thePlayer!!.getDistanceToEntityBox(target!!) < range)) {
            runAttack()
            attackTimer.reset()
            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val player = mc.thePlayer!!
        if (target != null && targetRotation != null) {
            if (getRotationDifference(target!!) > 60.0) {
                if (player.hurtTime == 0 && mc.gameSettings.keyBindJump.isKeyDown) player.motionY = 0.0
                val (yaw) = targetRotation ?: return
                var strafe = event.strafe
                var forward = event.forward
                var friction = event.friction

                var angleDiff =
                    ((MathHelper.wrapAngleTo180_float(mc.thePlayer!!.rotationYaw - yaw - 22.5f - 135.0f) + 180.0) / (45.0).toDouble()).toInt()
                var calcYaw = yaw + 45.0f * angleDiff.toFloat()

                var calcMoveDir = Math.max(Math.abs(strafe), Math.abs(forward)).toFloat()
                calcMoveDir *= calcMoveDir
                var calcMultiplier = MathHelper.sqrt(calcMoveDir / Math.min(1.0f, calcMoveDir * 2.0f))

                when (angleDiff) {
                    1, 3, 5, 7, 9 -> {
                        if ((Math.abs(forward) > 0.005 || Math.abs(strafe) > 0.005) && !(Math.abs(forward) > 0.005 && Math.abs(
                                strafe
                            ) > 0.005)
                        ) {
                            friction = friction / calcMultiplier
                        } else if (Math.abs(forward) > 0.005 && Math.abs(strafe) > 0.005) {
                            friction = friction * calcMultiplier
                        }
                    }
                }

                var f = strafe * strafe + forward * forward

                if (f >= 1.0E-4F) {
                    f = MathHelper.sqrt(f)

                    if (f < 1.0F)
                        f = 1.0F

                    f = friction / f
                    strafe *= f
                    forward *= f

                    val yawSin = MathHelper.sin((calcYaw * Math.PI / 180F).toFloat())
                    val yawCos = MathHelper.cos((calcYaw * Math.PI / 180F).toFloat())

                    player.motionX += strafe * yawCos - forward * yawSin
                    player.motionZ += forward * yawCos + strafe * yawSin
                }
                event.cancelEvent()
            }else {
                targetRotation.applyStrafeToPlayer(event)
                event.cancelEvent()
            }
        }
    }

    override fun onDisable() {
        attackTimer.reset()
        prevTargetEntities.clear()
        hittable = false
        target = null
        mc.gameSettings.keyBindUseItem.pressed = false
    }
    private fun isEnemy(entity: Entity?): Boolean {
        if (classProvider.isEntityLivingBase(entity) && entity != null && (EntityUtils.targetDead || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.invisible)
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                val player = entity

                if (player.spectator || AntiBot.isBot(player))
                    return false

                if (player.isClientFriend() && !LiquidBounce.moduleManager[NoFriends::class.java]!!.state)
                    return false

                val teams = LiquidBounce.moduleManager[Teams::class.java] as Teams

                return !teams.state || !teams.isInYourTeam(entity.asEntityLivingBase())
            }

            return EntityUtils.targetMobs && entity.isMob() || EntityUtils.targetAnimals && entity.isAnimal()
        }

        return false
    }
    private fun isAlive(entity: EntityLivingBase) = entity.health > 0
}