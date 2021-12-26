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
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.SummaryRepository
import net.artux.pda.viewmodels.ProfileViewModel
import net.artux.pda.repositories.UserRepository
import net.artux.pda.viewmodels.MemberViewModel
import net.artux.pda.viewmodels.QuestViewModel
import net.artux.pda.viewmodels.SummaryViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val usersRepository: UserRepository,
    private val questRepository: QuestRepository,
    private val summaryRepository: SummaryRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            //todo another view models
            isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(usersRepository)
            isAssignableFrom(MemberViewModel::class.java) ->
                MemberViewModel(usersRepository)
            isAssignableFrom(QuestViewModel::class.java) ->
                QuestViewModel(questRepository)
            isAssignableFrom(SummaryViewModel::class.java) ->
                SummaryViewModel(summaryRepository)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
