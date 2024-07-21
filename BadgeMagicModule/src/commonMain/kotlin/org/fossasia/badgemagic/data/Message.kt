package org.fossasia.badgemagic.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(val hexStrings: List<String>, val flash: Boolean = false, val marquee: Boolean = false, val speed: Speed = Speed.ONE, val mode: Mode = Mode.LEFT, var bitmapSlot: UInt = 0u)
