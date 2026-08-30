package br.inf.cepp.financemanager

import android.os.Build
import androidx.room.Room
import androidx.room.RoomDatabase
import br.inf.cepp.financemanager.database.AppDatabase
import android.content.Context

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

object AndroidContext {
    lateinit var appContext: Context
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = AndroidContext.appContext.getDatabasePath("finance.db")
    return Room.databaseBuilder<AppDatabase>(
        context = AndroidContext.appContext,
        name = dbFile.absolutePath
    ).fallbackToDestructiveMigration(dropAllTables = true)
}
