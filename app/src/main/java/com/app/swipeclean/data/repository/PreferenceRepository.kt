package com.app.swipeclean.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// extension property, creates a single DataStore instance for the app
private val Context.dataStore by preferencesDataStore(name = "app_prefs")

@Singleton
class PreferenceRepository @Inject constructor(
    @ApplicationContext private val context: Context
){
    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val TRASH_TTL_DAYS = intPreferencesKey("trash_ttl_days") // default 30
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled") // default true
        val SESSION_GOAL = intPreferencesKey("session_goal") // default 50
    }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data.catch {
        emit(emptyPreferences())
    }.map{ it[Keys.ONBOARDING_DONE] ?: false}

    val trashTtlDays: Flow<Int> = context.dataStore.data.catch{
        emit(emptyPreferences())
    }.map { it[Keys.TRASH_TTL_DAYS] ?: 30 }

    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.catch {
        emit(emptyPreferences())
    }.map { it[Keys.HAPTICS_ENABLED] ?: true }

    val sessionGoal: Flow<Int> = context.dataStore.data.catch {
        emit(emptyPreferences())
    }.map { it[Keys.SESSION_GOAL] ?: 50 }

    suspend fun setOnboardingDone(){
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = true }
    }

    suspend fun setSessionGoal(goal: Int) {
        context.dataStore.edit { it[Keys.SESSION_GOAL] = goal }
    }
}