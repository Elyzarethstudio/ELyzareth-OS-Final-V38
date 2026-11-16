package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ArtifactEntity
import com.example.data.model.BaseTitleEntity
import com.example.data.model.CreativeDnaEntity
import com.example.data.model.EvidenceLedgerEntity
import com.example.data.model.ForensicReportEntity
import com.example.data.model.IdeaBlueprintEntity
import com.example.data.model.MasterReleaseEntity
import com.example.data.model.MusicProductionEntity
import com.example.data.model.RegisteredAssetEntity
import com.example.data.model.VisualDirectionEntity

@Database(
    entities = [
        BaseTitleEntity::class,
        ArtifactEntity::class,
        IdeaBlueprintEntity::class,
        CreativeDnaEntity::class,
        MusicProductionEntity::class,
        VisualDirectionEntity::class,
        ForensicReportEntity::class,
        EvidenceLedgerEntity::class,
        RegisteredAssetEntity::class,
        MasterReleaseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ElyzarethDatabase : RoomDatabase() {
    abstract fun elyzarethDao(): ElyzarethDao
    abstract fun osBranchDao(): OsBranchDao

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
