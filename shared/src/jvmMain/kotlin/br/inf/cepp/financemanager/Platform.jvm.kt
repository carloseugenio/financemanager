package br.inf.cepp.financemanager

import androidx.room.Room
import androidx.room.RoomDatabase
import br.inf.cepp.financemanager.database.AppDatabase
import java.io.File

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "finance.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    ).fallbackToDestructiveMigration(dropAllTables = true)
}
