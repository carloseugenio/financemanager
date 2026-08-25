package br.inf.cepp.financemanager

import android.os.Build
import br.inf.cepp.financemanager.Platform

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()