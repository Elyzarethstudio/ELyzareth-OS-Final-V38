package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CreativeDnaEntity
import com.example.data.model.EvidenceLedgerEntity
import com.example.data.model.ForensicReportEntity
import com.example.data.model.IdeaBlueprintEntity
import com.example.data.model.MasterReleaseEntity
import com.example.data.model.MusicProductionEntity
import com.example.data.model.RegisteredAssetEntity
import com.example.data.model.VisualDirectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OsBranchDao {

    // 1. Idea & Blueprint Studio
    @Query("SELECT * FROM idea_blueprints ORDER BY createdTimestampMs DESC")
    fun getAllBlueprints(): Flow<List<IdeaBlueprintEntity>>

    @Query("SELECT COUNT(*) FROM idea_blueprints")
    suspend fun getBlueprintsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlueprints(items: List<IdeaBlueprintEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlueprint(item: IdeaBlueprintEntity)

    // 4. Creative DNA Engine
    @Query("SELECT * FROM creative_dna_profiles ORDER BY id ASC")
    fun getAllDnaProfiles(): Flow<List<CreativeDnaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDnaProfiles(items: List<CreativeDnaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDnaProfile(item: CreativeDnaEntity)

    // 5. Music Production Console
    @Query("SELECT * FROM music_production_tracks ORDER BY id ASC")
    fun getAllProductionTracks(): Flow<List<MusicProductionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductionTracks(items: List<MusicProductionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductionTrack(item: MusicProductionEntity)

    // 6. Visual Direction Studio
    @Query("SELECT * FROM visual_direction_assets ORDER BY id ASC")
    fun getAllVisualAssets(): Flow<List<VisualDirectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisualAssets(items: List<VisualDirectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisualAsset(item: VisualDirectionEntity)

    // 8. Forensic Lab
    @Query("SELECT * FROM forensic_reports ORDER BY auditedTimestampMs DESC")
    fun getAllForensicReports(): Flow<List<ForensicReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForensicReports(items: List<ForensicReportEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForensicReport(item: ForensicReportEntity)

    // 10. Evidence Vault
    @Query("SELECT * FROM evidence_ledger ORDER BY recordedTimestampMs DESC")
    fun getAllEvidenceRecords(): Flow<List<EvidenceLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidenceRecords(items: List<EvidenceLedgerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidenceRecord(item: EvidenceLedgerEntity)

    // 11. Asset Registry
    @Query("SELECT * FROM asset_registry ORDER BY id ASC")
    fun getAllRegisteredAssets(): Flow<List<RegisteredAssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegisteredAssets(items: List<RegisteredAssetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegisteredAsset(item: RegisteredAssetEntity)

    // 12. Master Vault & Player
    @Query("SELECT * FROM master_releases ORDER BY releasedTimestampMs DESC")
    fun getAllMasterReleases(): Flow<List<MasterReleaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMasterReleases(items: List<MasterReleaseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMasterRelease(item: MasterReleaseEntity)

    @Update
    suspend fun updateMasterRelease(item: MasterReleaseEntity)
}
