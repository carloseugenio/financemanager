package br.inf.cepp.financemanager

import br.inf.cepp.financemanager.Platform

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()