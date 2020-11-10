package com.rotemati.foregroundsdk.notification

interface NotificationDescriptorDSL {
	val title: String
	val body: String
	val iconResId: Int
}

class NotificationDescriptorDSLImpl : NotificationDescriptorDSL {
	override lateinit var title: String
	override lateinit var body: String
	override var iconResId: Int = 0

	fun build() = NotificationDescriptor(title, body, iconResId)
}

fun notificationDescriptor(block: NotificationDescriptorDSLImpl.() -> Unit): NotificationDescriptor {
	return NotificationDescriptorDSLImpl().apply(block).build()
}