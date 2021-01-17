package com.rotemati.foregroundsdk.common.api

fun test(block: TestDsl.() -> Unit) {
	TestDsl().run { block() }
}

class TestDsl {
	fun arrange(block: () -> Unit) = block()
	fun act(block: () -> Unit) = block()
	fun assert(block: () -> Unit) = block()
}