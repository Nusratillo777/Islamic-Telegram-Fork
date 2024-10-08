package org.telegram.messenger.components.local


/**
 * Created by Siddikov Mukhriddin on 9/6/22
 */
data class PrayTimes(
    var prays: List<PrayTime>,
)
data class PrayTime(
    var time: String,
    val day: String,
    val name: String,
)