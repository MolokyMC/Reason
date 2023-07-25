/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * Some parts of the code are taken and modified from Rilshrink/Minecraft-Disablers.
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.network.play.client.*
import java.util.*

@ModuleInfo(name = "CustomDisabler", spacedName = "Custom Disabler", description = "Disabler but fully customizable. Contains 10+ options.", category = ModuleCategory.WORLD)
class CustomDisabler : Module() {

	// automatically set the disabler to proper values for bypassing purpose
	private val presets: ListValue = object : ListValue("Presets", arrayOf("Ghostly", "OldKauri", "Basic"), "Ghostly") {
        override fun onChanged(oldValue: String, newValue: String) {
            when (newValue.lowercase(Locale.getDefault())) {
                "ghostly" -> {
                    cancelTrans.set(true)
                    cancelAlive.set(true)
                    ridingPacket.set("EmptyArgs")
                    spectatePacket.set("None")
                }

                "oldkauri" -> {
                    cancelTrans.set(true)
                    cancelAlive.set(false)
                    ridingPacket.set("None")
                    spectatePacket.set("None")
                }

                "basic" -> {
                    cancelTrans.set(true)
                    cancelAlive.set(true)
                    ridingPacket.set("None")
                    spectatePacket.set("None")
                }
            }
        }
	}
	private val cancelTrans = BoolValue("CancelTransactions", false)
	private val cancelAlive = BoolValue("CancelKeepAlive", false)
	private val transEnabled = BoolValue("Transactions", false, { !cancelTrans.get() })
	private val aliveEnabled = BoolValue("KeepAlive", false, { !cancelAlive.get() })
	private val ridingPacket = ListValue("Riding", arrayOf("None", "EmptyArgs", "Valid"), "None")
	private val spectatePacket = ListValue("Spectate", arrayOf("None", "Random", "Player"), "None")
	private val ridingPriority = BoolValue("Riding-Priority", true, { !ridingPacket.get().equals("none", true) })
	private val transDelayMode = ListValue("Transactions-DelayMode", arrayOf("PlayerTick", "SystemTick", "Dynamic", "BusSize"), "Dynamic", { !cancelTrans.get() && transEnabled.get() })
	private val aliveDelayMode = ListValue("KeepAlive-DelayMode", arrayOf("PlayerTick", "SystemTick", "Dynamic", "BusSize"), "Dynamic", { !cancelAlive.get() && aliveEnabled.get() })
	private val transSendMethod = ListValue("Transactions-SendMethod", arrayOf("PollFirst", "PollLast", "FlushAll"), "PollFirst", { !cancelTrans.get() && transEnabled.get() })
	private val aliveSendMethod = ListValue("KeepAlive-SendMethod", arrayOf("PollFirst", "PollLast", "FlushAll"), "PollFirst", { !cancelAlive.get() && aliveEnabled.get() })
	private val trans_pTickDelay = IntegerValue("Transactions-PlayerTick-Delay", 1, 1, 2000, { !cancelTrans.get() && transEnabled.get() && transDelayMode.get().equals("playertick", true) }) 
	private val trans_sTick_MinDelay: IntegerValue = object : IntegerValue("Transactions-SystemTick-MinDelay", 0, 0, 30000, "ms", { !cancelTrans.get() && transEnabled.get() && transDelayMode.get().equals("systemtick", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = trans_sTick_MaxDelay.get()
            if (v < newValue) set(v)
        }
    }
    private val trans_sTick_MaxDelay: IntegerValue = object : IntegerValue("Transactions-SystemTick-MaxDelay", 0, 0, 30000, "ms", { !cancelTrans.get() && transEnabled.get() && transDelayMode.get().equals("systemtick", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = trans_sTick_MinDelay.get()
            if (v > newValue) set(v)
        }
    }
	private val alive_pTickDelay = IntegerValue("KeepAlive-PlayerTick-Delay", 1, 1, 2000, { !cancelAlive.get() && aliveEnabled.get() && aliveDelayMode.get().equals("playertick", true) }) 
	private val alive_sTick_MinDelay: IntegerValue = object : IntegerValue("KeepAlive-SystemTick-MinDelay", 0, 0, 30000, "ms", { aliveEnabled.get() && aliveDelayMode.get().equals("systemtick", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = alive_sTick_MaxDelay.get()
            if (v < newValue) set(v)
        }
    }
    private val alive_sTick_MaxDelay: IntegerValue = object : IntegerValue("KeepAlive-SystemTick-MaxDelay", 0, 0, 30000, "ms", { !cancelAlive.get() && aliveEnabled.get() && aliveDelayMode.get().equals("systemtick", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = alive_sTick_MinDelay.get()
            if (v > newValue) set(v)
        }
    }
	private val trans_poll_Min: IntegerValue = object : IntegerValue("Transactions-Poll-MinAmount", 1, 1, 300, { !cancelTrans.get() && transEnabled.get() && !transSendMethod.get().equals("flushall", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = trans_poll_Max.get()
            if (v < newValue) set(v)
        }
    }
    private val trans_poll_Max: IntegerValue = object : IntegerValue("Transactions-Poll-MaxAmount", 1, 1, 300, { !cancelTrans.get() && transEnabled.get() && !transSendMethod.get().equals("flushall", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = trans_poll_Min.get()
            if (v > newValue) set(v)
        }
    }
	private val alive_poll_Min: IntegerValue = object : IntegerValue("KeepAlive-Poll-MinAmount", 1, 1, 300, { !cancelAlive.get() && aliveEnabled.get() && !aliveSendMethod.get().equals("flushall", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = alive_poll_Max.get()
            if (v < newValue) set(v)
        }
    }
    private val alive_poll_Max: IntegerValue = object : IntegerValue("KeepAlive-Poll-MaxAmount", 1, 1, 300, { !cancelAlive.get() && aliveEnabled.get() && !aliveSendMethod.get().equals("flushall", true) }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val v = alive_poll_Min.get()
            if (v > newValue) set(v)
        }
    }
	private val transBusMinSize = IntegerValue("Transactions-MinBusSize", 0, 0, 300, { !cancelTrans.get() && transEnabled.get() && transDelayMode.get().equals("bussize", true) })
	private val aliveBusMinSize = IntegerValue("KeepAlive-MinBusSize", 0, 0, 300, { !cancelAlive.get() && aliveEnabled.get() && aliveDelayMode.get().equals("bussize", true) })
	private val transDupe = IntegerValue("Transactions-DupeAmount", 1, 1, 100, "x", { !cancelTrans.get() && transEnabled.get() })
	private val aliveDupe = IntegerValue("KeepAlive-DupeAmount", 1, 1, 100, "x", { !cancelAlive.get() && aliveEnabled.get() })
	private val clearTransSent = BoolValue("Clear-Transactions-After-Send", false, { !cancelTrans.get() && transEnabled.get() })
	private val clearAliveSent = BoolValue("Clear-Alive-After-Send", false, { !cancelAlive.get() && aliveEnabled.get() })
	private val flushWhenDisable = BoolValue("Flush-When-Disable", false)
	private val debugValue = BoolValue("Debug", false)

	private val trans = LinkedList<C0FPacketConfirmTransaction>()
	private val alive = LinkedList<C00PacketKeepAlive>()
	
	private val msTimerTrans = MSTimer()
	private val msTimerAlive = MSTimer()

	private var dynTransDelay = 0
	private var dynAliveDelay = 0
	private var lastTransTick = 0L
	private var lastAliveTick = 0L
	private var transDelay = 0
	private var aliveDelay = 0

	fun debug(s: String) {
		if (debugValue.get())
			ClientUtils.displayChatMessage("§6[§3§lCustom Disabler§6]§f $s")
	}

	override fun onEnable() {
		lastTransTick = 0L
		lastAliveTick = 0L
		transDelay = RandomUtils.nextInt(trans_sTick_MinDelay.get(), trans_sTick_MaxDelay.get())
		aliveDelay = RandomUtils.nextInt(alive_sTick_MinDelay.get(), alive_sTick_MaxDelay.get())
		alive.clear()
		trans.clear()
		msTimerTrans.reset()
		msTimerAlive.reset()
	}

	override fun onDisable() {
		if (flushWhenDisable.get()) {
			try {
				if (aliveEnabled.get() && !alive.isEmpty()) alive.forEach {
					PacketUtils.sendPacketNoEvent(it)
				}
				if (transEnabled.get() && !trans.isEmpty()) trans.forEach {
					PacketUtils.sendPacketNoEvent(it)
				}
			} catch (e: Exception) {
				// ignore
			}
		}

		lastTransTick = 0L
		lastAliveTick = 0L
		transDelay = RandomUtils.nextInt(trans_sTick_MinDelay.get(), trans_sTick_MaxDelay.get())
		aliveDelay = RandomUtils.nextInt(alive_sTick_MinDelay.get(), alive_sTick_MaxDelay.get())
		alive.clear()
		trans.clear()
		msTimerTrans.reset()
		msTimerAlive.reset()

		mc.timer.timerSpeed = 1F
	}

	@EventTarget
	fun onPacket(event: PacketEvent) {
		if (mc.thePlayer == null || mc.theWorld == null) return
		val packet = event.packet

		if (packet is C0FPacketConfirmTransaction && cancelTrans.get()) {
			event.cancelEvent()
			debug("cancelled c0f")
			return
		}

		if (packet is C00PacketKeepAlive && cancelAlive.get()) {
			event.cancelEvent()
			debug("cancelled c00")
			return
		}

		if (packet is C03PacketPlayer) {
			if (!ridingPacket.get().equals("none", true) && ridingPriority.get()) {
				mc.netHandler.addToSendQueue(if (ridingPacket.get().equals("emptyargs", true)) C0CPacketInput() else C0CPacketInput(mc.thePlayer.moveStrafing.coerceAtMost(0.98F), mc.thePlayer.moveForward.coerceAtMost(0.98F), mc.thePlayer.movementInput.jump, mc.thePlayer.movementInput.sneak))
				debug("pre c0c")
			}
			if (!spectatePacket.get().equals("none", true)) {
				mc.netHandler.addToSendQueue(C18PacketSpectate(if (spectatePacket.get().equals("player", true)) mc.thePlayer.uniqueID else UUID.randomUUID()))
				debug("spectate")
			}
			if (!ridingPacket.get().equals("none", true) && !ridingPriority.get()) {
				mc.netHandler.addToSendQueue(if (ridingPacket.get().equals("emptyargs", true)) C0CPacketInput() else C0CPacketInput(0.98F, 0.98F, false, false))
				debug("post c0c")
			}
		}

		if (packet is C0FPacketConfirmTransaction && transEnabled.get()) {
			if (lastTransTick != 0L)
				dynTransDelay = (System.currentTimeMillis() - lastTransTick).toInt()

			lastTransTick = System.currentTimeMillis()

			repeat (transDupe.get()) {
				trans.add(packet)
			}
			event.cancelEvent()
			debug("duped c0f ${transDupe.get()}x, dynamic delay: ${dynTransDelay}ms. detail: uid ${packet.uid}, windowId ${packet.windowId}")
		}

		if (packet is C00PacketKeepAlive && aliveEnabled.get()) {
			if (lastAliveTick != 0L)
				dynAliveDelay = (System.currentTimeMillis() - lastAliveTick).toInt()

			lastAliveTick = System.currentTimeMillis()

			repeat (aliveDupe.get()) {
				alive.add(packet)
			}
			event.cancelEvent()
			debug("duped c00 ${aliveDupe.get()}x, dynamic delay: ${dynAliveDelay}ms. detail: key ${packet.key}")
		}
	}

	@EventTarget
	fun onWorld(event: WorldEvent) {
		lastTransTick = 0L
		lastAliveTick = 0L
		transDelay = RandomUtils.nextInt(trans_sTick_MinDelay.get(), trans_sTick_MaxDelay.get())
		aliveDelay = RandomUtils.nextInt(alive_sTick_MinDelay.get(), alive_sTick_MaxDelay.get())
		alive.clear()
		trans.clear()
		msTimerTrans.reset()
		msTimerAlive.reset()
	}

	@EventTarget
	fun onUpdate(event: UpdateEvent) {
		if (mc.thePlayer == null || mc.theWorld == null) return
		if (transEnabled.get() && !cancelTrans.get()) {
            val sendWhen = when (transDelayMode.get().lowercase(Locale.getDefault())) {
                "playertick" -> mc.thePlayer.ticksExisted > 0 && mc.thePlayer.ticksExisted % trans_pTickDelay.get() == 0
                "systemtick" -> msTimerTrans.hasTimePassed(transDelay.toLong())
                "dynamic" -> msTimerTrans.hasTimePassed(dynTransDelay.toLong())
                else -> trans.size >= transBusMinSize.get()
            }
            if (!trans.isEmpty() && sendWhen) {
                if (transSendMethod.get().equals("flushall", true)) {
                    while (trans.size > 0)
                        PacketUtils.sendPacketNoEvent(trans.poll())

                    debug("flushed.")
                } else {
                    val hake = RandomUtils.nextInt(trans_poll_Min.get(), trans_poll_Max.get()).coerceAtMost(trans.size)
                    repeat(hake) {
                        if (transSendMethod.get().equals("pollfirst"))
                            PacketUtils.sendPacketNoEvent(trans.pollFirst())
                        else
                            PacketUtils.sendPacketNoEvent(trans.pollLast())
                    }

                    debug("poll $hake times.")
                }
                if (clearTransSent.get()) trans.clear()
                transDelay = RandomUtils.nextInt(trans_sTick_MinDelay.get(), trans_sTick_MaxDelay.get())
                msTimerTrans.reset()
            }
        }
		if (aliveEnabled.get() && !cancelAlive.get()) {
            val sendWhen = when (aliveDelayMode.get().lowercase(Locale.getDefault())) {
                "playertick" -> mc.thePlayer.ticksExisted > 0 && mc.thePlayer.ticksExisted % alive_pTickDelay.get() == 0
                "systemtick" -> msTimerAlive.hasTimePassed(aliveDelay.toLong())
                "dynamic" -> msTimerAlive.hasTimePassed(dynAliveDelay.toLong())
                else -> alive.size >= aliveBusMinSize.get()
            }
            if (!alive.isEmpty() && sendWhen) {
                if (aliveSendMethod.get().equals("flushall", true)) {
                    while (alive.size > 0)
                        PacketUtils.sendPacketNoEvent(alive.poll())

                    debug("flushed.")
                } else {
                    val hake2 = RandomUtils.nextInt(alive_poll_Min.get(), alive_poll_Max.get()).coerceAtMost(alive.size)
                    repeat(hake2) {
                        if (aliveSendMethod.get().equals("pollfirst"))
                            PacketUtils.sendPacketNoEvent(alive.pollFirst())
                        else
                            PacketUtils.sendPacketNoEvent(alive.pollLast())
                    }

                    debug("poll $hake2 times.")
                }
                if (clearAliveSent.get()) alive.clear()
                aliveDelay = RandomUtils.nextInt(alive_sTick_MinDelay.get(), alive_sTick_MaxDelay.get())
                msTimerAlive.reset()
            }
        }
	}
}