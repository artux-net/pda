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

/**
 * Extension functions for Fragment.
 */

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.badlogic.gdx.backends.android.AndroidApplication
import net.artux.pda.app.App
import net.artux.pda.gdx.CoreStarter
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner


fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as App).repository
    return ViewModelFactory(repository, this)
}

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory {
    val repository = (applicationContext as App).repository
    return ViewModelFactory(repository, this)
}

fun FragmentActivity.getViewModelFactory(): ViewModelFactory {
    val repository = (applicationContext as App).repository
    return ViewModelFactory(repository, this)
}


fun CoreStarter.getViewModelFactory(): ViewModelFactory {
    val repository = (applicationContext as App).repository
    return ViewModelFactory(repository, this)
}

