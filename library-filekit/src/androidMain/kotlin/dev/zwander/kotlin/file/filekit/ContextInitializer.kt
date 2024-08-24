package dev.zwander.kotlin.file.filekit

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer

@Suppress("unused")
@Keep
class ContextInitializer : Initializer<Context> {
    companion object {
        internal lateinit var appContext: Context
    }

    override fun create(context: Context): Context {
        appContext = context
        return appContext
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}
