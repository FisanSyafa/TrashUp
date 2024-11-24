package com.bangkit.trashup.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit.trashup.data.remote.response.ListStoryItem
import com.bangkit.trashup.databinding.ItemRowArticlesBinding

class ArticlesAdapter(private val onItemClickCallback: OnItemClickCallback) : ListAdapter<ListStoryItem, ArticlesAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemRowArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    class StoryViewHolder(private val binding: ItemRowArticlesBinding, private val listener: OnItemClickCallback) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            val context = binding.root.context

            binding.tvItemName.text = story.name

            Glide.with(context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                listener.onItemClick(story)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClick(story: ListStoryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
