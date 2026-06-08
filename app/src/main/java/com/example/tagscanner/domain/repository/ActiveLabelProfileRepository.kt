package com.example.tagscanner.domain.repository

import com.example.tagscanner.domain.model.LabelProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds the label profile selected for the current session.
 * Defaults to [LabelProfile.Generic] and stays set until the user changes it.
 */
object ActiveLabelProfileRepository {

    private val _activeProfile = MutableStateFlow<LabelProfile>(LabelProfile.default())

    fun observeActiveProfile(): Flow<LabelProfile> = _activeProfile.asStateFlow()

    fun currentProfile(): LabelProfile = _activeProfile.value

    fun setProfile(profile: LabelProfile) {
        _activeProfile.value = profile
    }
}
