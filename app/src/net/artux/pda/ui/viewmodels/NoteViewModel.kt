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
import net.artux.pda.repositories.util.Result
import net.artux.pdanetwork.model.NoteCreateDto
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    var repository: NoteRepository
) : ViewModel() {
    var activeNote: MutableLiveData<NoteModel> = MutableLiveData()
    var notes: MutableLiveData<List<NoteModel>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var noteMapper: NoteMapper = NoteMapper.INSTANCE
    var statusMapper: StatusMapper = StatusMapper.INSTANCE

    fun openNote(uuid: UUID) {
        viewModelScope.launch {
            val result = repository.getCachedNote(uuid)
            if (result is Result.Success) {
                activeNote.postValue(noteMapper.model(result.data))
            }
            updateNotes()
        }
    }

    fun updateNotes() {
        viewModelScope.launch {
            val result = repository.getNotes()
            if (result is Result.Success) {
                notes.postValue(noteMapper.model(result.data))
                val currentNote = activeNote.value
                if (currentNote != null)
                    activeNote.postValue(
                        noteMapper
                            .model(repository.getCachedNote(currentNote.id).getOrThrow())
                    )
            } else if (result is Result.Error)
                status.postValue(StatusModel(result.exception))
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
            val result = repository.createNote(
                noteMapper.createDto(currentNote)
            )

            if (result is Result.Success) {
                currentNote.id = result.data.id
                activeNote.postValue(noteMapper.model(result.data))
                updateNotesFromCache()
            }
        }
    }

    fun editNote(title: String, content: String, uuid: UUID) {
        viewModelScope.launch {
            val currentNote =
                repository.getCachedNote(uuid).map { noteMapper.model(it) }.getOrNull()
            if (currentNote != null)
                if (!currentNote.title.equals(title) ||
                    !currentNote.content.equals(content)
                ) {
                    val editedNote = NoteCreateDto()
                        .title(title)
                        .content(content)

                    val result = repository.editNote(editedNote, uuid)

                    if (result is Result.Success) {
                        val activeNoteModel = activeNote.value
                        if (activeNoteModel != null && activeNoteModel.id.equals(result.data.id))
                            activeNote.postValue(noteMapper.model(result.data))

                        updateNotesFromCache()
                    }
                }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            val currentNote = activeNote.value
            if (currentNote != null) {
                val result = repository.deleteNote(currentNote.id)
                if (result is Result.Success) {
                    activeNote.postValue(null)
                    updateNotesFromCache()
                    if (!result.data.description.isNullOrBlank())
                        status.postValue(statusMapper.model(result.data))
                }
            }
        }
    }

}