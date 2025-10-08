package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.NoteMapper
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.profile.NoteModel
import net.artux.pda.repositories.NoteRepository
import net.artux.pdanetwork.model.NoteCreateDto
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    var repository: NoteRepository
) : ViewModel() {
    var activeNote: MutableLiveData<NoteModel?> = MutableLiveData()
    var notes: MutableLiveData<List<NoteModel>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    var noteMapper: NoteMapper = NoteMapper.INSTANCE
    var statusMapper: StatusMapper = StatusMapper.INSTANCE

    fun openNote(uuid: UUID) {
        viewModelScope.launch {
            syncActiveNote()
            repository.getCachedNote(uuid)
                .onSuccess {
                    activeNote.postValue(noteMapper.model(it))
                    updateNotesFromCache()
                }
                .onFailure {
                    updateNotes()
                }
        }
    }

    fun updateNotes() {
        viewModelScope.launch {
            repository.getCachedNotes()
                .onSuccess {
                    notes.postValue(noteMapper.model(it))
                }

            repository.getNotes()
                .onSuccess {
                    notes.postValue(noteMapper.model(it))
                }
                .onFailure {
                    status.postValue(StatusModel(it))
                }

            val currentNote = activeNote.value
            if (currentNote != null)
                activeNote.postValue(
                    noteMapper
                        .model(repository.getCachedNote(currentNote.id).getOrNull())
                )
        }
    }

    private fun updateNotesFromCache() {
        notes.postValue(repository.getCachedNotes().map { noteMapper.model(it) }
            .getOrDefault(Collections.emptyList()))
    }

    fun createNote(title: String, content: String) {
        viewModelScope.launch {
            val currentNote = NoteModel()
            currentNote.title = title
            currentNote.content = content

            repository.createNote(
                noteMapper.createDto(currentNote)
            ).onSuccess {
                openNote(it.id)
            }.onFailure {
                status.postValue(StatusModel(it))
            }
        }
    }

    fun syncActiveNote() {
        viewModelScope.launch {
            val note = activeNote.value
            if (note != null) {
                val editedNote = NoteCreateDto()
                    .title(note.title)
                    .content(note.content)

                repository.editNote(editedNote, note.id)
                    .onSuccess {
                        val activeNoteModel = activeNote.value
                        if (activeNoteModel != null && activeNoteModel.id.equals(it.id))
                            activeNote.postValue(noteMapper.model(it))

                        updateNotesFromCache()
                    }
            }
        }
    }


    fun editTitle(title: String) {
        if (title.isNotBlank()) {
            val currentNote = activeNote.value
            if (currentNote != null && !currentNote.title.equals(title)) {
                currentNote.title = title
            }
        }
    }

    fun editContent(content: String) {
        if (content.isNotBlank()) {
            val currentNote = activeNote.value
            if (currentNote != null && !currentNote.content.equals(content)) {
                currentNote.content = content
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            val currentNote = activeNote.value
            if (currentNote != null) {
                repository.deleteNote(currentNote.id)
                    .onSuccess {
                        activeNote.postValue(null)
                        updateNotesFromCache()
                    }
                    .onFailure {
                        status.postValue(StatusModel(it))
                    }
            }
        }
    }
}