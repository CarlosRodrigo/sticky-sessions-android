package br.org.cesar.discordtime.stickysessions.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.org.cesar.discordtime.stickysessions.presentation.notes.INoteTopicDetail
import br.org.cesar.discordtime.stickysessions.ui.session.notes.NotesView

class TopicSessionNotesAdapter : RecyclerView.Adapter<NotesView>() {
    private var mNoteTopicDetailList: MutableList<out INoteTopicDetail>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = NotesView.createView(parent)

    override fun getItemCount(): Int = mNoteTopicDetailList?.size ?: 0

    override fun onBindViewHolder(holder: NotesView, position: Int) {
        holder.getPresenter().updateData(getItem(position))
    }

    fun replaceData(sessionDetail: MutableList<out INoteTopicDetail>) {
        mNoteTopicDetailList = sessionDetail
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): INoteTopicDetail? = mNoteTopicDetailList?.get(position)
}