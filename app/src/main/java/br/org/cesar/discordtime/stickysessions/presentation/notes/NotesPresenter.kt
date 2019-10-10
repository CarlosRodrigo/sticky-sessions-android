package br.org.cesar.discordtime.stickysessions.presentation.notes

import android.util.Log
import br.org.cesar.discordtime.stickysessions.domain.model.Note
import br.org.cesar.discordtime.stickysessions.domain.model.NoteFilter
import br.org.cesar.discordtime.stickysessions.executor.IObservableUseCase
import io.reactivex.observers.DisposableSingleObserver

class NotesPresenter(
        private val mListNotes: IObservableUseCase<NoteFilter, List<Note>>?,
        private val addNote: IObservableUseCase<Note, Note>?,
        private val removeNote: IObservableUseCase<Note, Boolean>?
) : NotesContract.Presenter {
    private var mView: NotesContract.View? = null
    private var mNoteTopicDetail: INoteTopicDetail? = null

    override fun attachView(view: NotesContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun updateData(noteTopicDetail: INoteTopicDetail?) {
        mNoteTopicDetail = noteTopicDetail
        mView?.displayTopicName(noteTopicDetail?.getTopicName() ?: "")

        loadNotesForSession()
    }

    override fun onNewNoteClick() {

    }

    private fun loadNotesForSession() {
        mView?.displayLoading()
        mListNotes?.execute(object: DisposableSingleObserver<List<Note>>() {
            override fun onSuccess(notes: List<Note>) {
                mView?.displayNotes(
                        notes.filter { note -> note.topic == mNoteTopicDetail?.getTopicName() })
                mView?.hideLoading()
            }

            override fun onError(e: Throwable) {
                Log.e("devlog", "displayNotes: " + e.message)

            }
        }, NoteFilter(mNoteTopicDetail?.getSessionId()))
    }
}
