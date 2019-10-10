package br.org.cesar.discordtime.stickysessions.ui.session.notes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.cesar.discordtime.stickysessions.R
import br.org.cesar.discordtime.stickysessions.app.StickySessionApplication
import br.org.cesar.discordtime.stickysessions.domain.model.Note
import br.org.cesar.discordtime.stickysessions.presentation.notes.NotesContract
import br.org.cesar.discordtime.stickysessions.ui.adapters.NoteAdapter
import javax.inject.Inject

class NotesView(itemView: View) : RecyclerView.ViewHolder(itemView),
        NotesContract.View, View.OnClickListener {

    private val mTopicNameTextView = itemView.findViewById<TextView>(R.id.text_topic_name)
    private val mNotesList = itemView.findViewById<RecyclerView>(R.id.list_notes)
    private val mNewNoteButton = itemView.findViewById<Button>(R.id.btn_new_note)
    private val mLoadingView = itemView.findViewById<View>(R.id.loading)

    private lateinit var mNoteAdapter: NoteAdapter

    @Inject
    lateinit var mPresenter: NotesContract.Presenter

    companion object {
        fun createView(parent: ViewGroup): NotesView {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_topic_notes, parent, false)
            return NotesView(view)
        }
    }

    init {
        (itemView.context.applicationContext as StickySessionApplication).inject(this)
        mNewNoteButton.setOnClickListener(this)
        mPresenter.attachView(this)
        initializeTopicSessionList(itemView.context)
    }

    override fun displayTopicName(name: String) {
        mTopicNameTextView.text = name
    }

    override fun displayNotes(notes: List<Note>) {
        mNoteAdapter.setNotes(notes)
    }
    override fun hideLoading() {
        mLoadingView?.visibility = GONE
    }

    override fun displayLoading() {
        mLoadingView?.visibility = VISIBLE
    }

    override fun onClick(view: View?) {
        if (R.id.btn_new_note == view?.id) {
            mPresenter.onNewNoteClick()
        }
    }

    fun getPresenter() = mPresenter

    private fun initializeTopicSessionList(context: Context) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL

        mNotesList.layoutManager = linearLayoutManager

        mNoteAdapter = NoteAdapter(context)
        mNotesList.adapter = mNoteAdapter
    }
}
