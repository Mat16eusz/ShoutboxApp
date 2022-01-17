package com.example.zadanie_7

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val commentsList: List<Comment>, private val onItemClickListener: OnItemClickListener):
        RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_layout,
            parent, false)

        return ViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment = commentsList[position]

        holder.content.text = currentComment.content
        holder.login.text = currentComment.login
        holder.date.text = currentComment.date
        holder.id.text = currentComment.id
    }

    override fun getItemCount() = commentsList.size

    inner class ViewHolder(itemView: View, onItemClickListener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(id.text.toString(), login.text.toString(), content.text.toString(), date.text.toString())
            }
        }
        val content: TextView = itemView.findViewById(R.id.editContent)
        val login: TextView = itemView.findViewById(R.id.login)
        val date: TextView = itemView.findViewById(R.id.editDate)
        val id: TextView = itemView.findViewById(R.id.id)
    }

    interface OnItemClickListener {
        fun onClick(id: String, login: String, content: String, date: String)
    }
}