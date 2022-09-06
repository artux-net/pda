/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.artux.pda.ui.util

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import net.artux.pda.app.DataManager
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.SummaryRepository
import net.artux.pda.repositories.UserRepository
import net.artux.pda.ui.fragments.rating.RatingViewModel
import net.artux.pda.ui.viewmodels.*

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val usersRepository: UserRepository,
    private val questRepository: QuestRepository,
    private val summaryRepository: SummaryRepository,
    val dataManager: DataManager,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(usersRepository, UserMapper.INSTANCE)
            isAssignableFrom(UserViewModel::class.java) ->
                UserViewModel(usersRepository, UserMapper.INSTANCE, StatusMapper.INSTANCE)
            isAssignableFrom(QuestViewModel::class.java) ->
                QuestViewModel(questRepository, StoryMapper.INSTANCE, StatusMapper.INSTANCE)
            isAssignableFrom(SummaryViewModel::class.java) ->
                SummaryViewModel(summaryRepository)
            isAssignableFrom(RatingViewModel::class.java) ->
                RatingViewModel(usersRepository, UserMapper.INSTANCE)
            isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(
                    usersRepository,
                    UserMapper.INSTANCE,
                    StatusMapper.INSTANCE,
                    dataManager
                )
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
