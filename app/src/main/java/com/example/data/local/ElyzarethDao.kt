package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ArtifactEntity
import com.example.data.model.BaseTitleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElyzarethDao {

    @Query("SELECT * FROM base_titles ORDER BY baseTitle ASC")
    fun getAllBaseTitles(): Flow<List<BaseTitleEntity>>

    @Query("SELECT * FROM base_titles WHERE id = :id LIMIT 1")
    fun getBaseTitleById(id: String): Flow<BaseTitleEntity?>

    @Query("SELECT * FROM base_titles WHERE containsAmbiguity = 1 ORDER BY baseTitle ASC")
    fun getReviewRequiredBaseTitles(): Flow<List<BaseTitleEntity>>

    @Query("SELECT * FROM artifacts WHERE baseTitleId = :baseTitleId ORDER BY versionLabel ASC")
    fun getArtifactsForTitle(baseTitleId: String): Flow<List<ArtifactEntity>>

    @Query("SELECT * FROM artifacts ORDER BY filename ASC")
    fun getAllArtifacts(): Flow<List<ArtifactEntity>>

    @Query("SELECT * FROM artifacts WHERE requiresHumanReview = 1 ORDER BY filename ASC")
    fun getReviewRequiredArtifacts(): Flow<List<ArtifactEntity>>

    @Query("SELECT COUNT(*) FROM base_titles")
    suspend fun getBaseTitlesCount(): Int

    @Query("SELECT COUNT(*) FROM artifacts")
    suspend fun getArtifactsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaseTitles(titles: List<BaseTitleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifacts(artifacts: List<ArtifactEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaseTitle(title: BaseTitleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifact(artifact: ArtifactEntity)

    @Update
    suspend fun updateBaseTitle(title: BaseTitleEntity)

    @Update
    suspend fun updateArtifact(artifact: ArtifactEntity)

    @Query("DELETE FROM base_titles")
    suspend fun clearAllBaseTitles()

    @Query("DELETE FROM artifacts")
    suspend fun clearAllArtifacts()
}
