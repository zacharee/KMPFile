package dev.zwander.kotlin.file

import android.content.Context
import androidx.startup.Initializer

@Suppress("unused")
class ContextInitializer : Initializer<Context> {
    companion object {
        lateinit var appContext: Context
    }

    override fun create(context: Context): Context {
        appContext = context
        return appContext
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}