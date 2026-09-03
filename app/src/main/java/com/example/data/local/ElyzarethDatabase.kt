package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ArtifactEntity
import com.example.data.model.BaseTitleEntity

@Database(
    entities = [BaseTitleEntity::class, ArtifactEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ElyzarethDatabase : RoomDatabase() {
    abstract fun elyzarethDao(): ElyzarethDao

    companion object {
        @Volatile
        private var INSTANCE: ElyzarethDatabase? = null

        fun getDatabase(context: Context): ElyzarethDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ElyzarethDatabase::class.java,
                    "elyzareth_corpus_vault.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
