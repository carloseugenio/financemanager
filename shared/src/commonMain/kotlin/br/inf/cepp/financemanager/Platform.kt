package br.inf.cepp.financemanager

import androidx.room.RoomDatabase
import br.inf.cepp.financemanager.database.AppDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
