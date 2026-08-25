package br.inf.cepp.financemanager.util

import androidx.compose.runtime.compositionLocalOf

object PlatformProvider {
    // This will hold the active platform's implementation
    lateinit var instance: PlatformUtils
}

/**
 * CompositionLocal allows you to access [PlatformUtils] down to the Compose UI tree cleanly:
 *
 * @sample br.inf.cepp.financemanager.samples.ComposableSamples.useLocalPlatformUtils
 *
 */
val LocalPlatformUtils = compositionLocalOf<PlatformUtils> {
    error("PlatformUtils not initialized!")
}