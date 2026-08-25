package br.inf.cepp.financemanager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform