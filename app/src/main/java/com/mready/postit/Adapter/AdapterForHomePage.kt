package com.mready.postit.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mready.postit.R
import com.mready.postit.model.MessageModel
import kotlinx.android.synthetic.main.layout_message_show_adapter.view.*

class AdapterForHomePage(
    val context: Context,
    private val messageViewModel: List<MessageModel>
) : RecyclerView.Adapter<AdapterForHomePage.ViewModel>() {

    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewModel {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_message_show_adapter, parent, false)
        return ViewModel(view)

    }

    override fun getItemCount(): Int {
        return messageViewModel.size
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.textViewDisplayName.text = messageViewModel[position].displayName
        holder.textViewMassage.text = messageViewModel[position].message
        holder.textViewCreatedAt.text = messageViewModel[position].createdAt
    }

    inner class ViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewDisplayName: TextView = itemView.editText_displayName
        var textViewMassage: TextView = itemView.editText_massage
        var textViewCreatedAt: TextView = itemView.editText_createdAt


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition)

            }
        }
    }
}