package com.utap.tidy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.utap.tidy.R
import com.utap.tidy.api.CleanJob

/**
 * Created by siyuan on 11/30/2020
 */

// This adapter inherits from ListAdapter, which mean that all need
// to do is give it a new list and an old list and we will never
// have to call notifyDatasetChanged().

class AreaRowAdapter (private val viewModel: MainViewModel):
        ListAdapter<CleanJob, AreaRowAdapter.ViewHolder>(CleabJobDiff()) {
    class CleabJobDiff: DiffUtil.ItemCallback<CleanJob>() {

        override fun areItemsTheSame(oldItem: CleanJob, newItem: CleanJob): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: CleanJob, newItem: CleanJob): Boolean {
            return oldItem.jobTitle == newItem.jobTitle
                    && oldItem.responsiblePerson == newItem.responsiblePerson
                    && oldItem.scores == newItem.scores
                    && oldItem.frequency == newItem.frequency
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        //TODO
        fun bind(item: CleanJob) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_area, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}