package com.utap.tidy.ui

import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.utap.tidy.MainActivity
import com.utap.tidy.R
import com.utap.tidy.data.CleanJob
import com.utap.tidy.data.Team
import java.util.*

/**
 * Created by siyuan on 11/30/2020
 */

class TeamRowAdapter (private val viewModel: MainViewModel,
                      private val selectTeam: ()->Unit):
        ListAdapter<Team, TeamRowAdapter.ViewHolder>(CleanJobDiff()) {
    class CleanJobDiff: DiffUtil.ItemCallback<Team>() {

        override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.timeStamp == newItem.timeStamp
                    && oldItem.rowID == newItem.rowID
        }
    }

    companion object {
        private val dateFormat = SimpleDateFormat("d MMM yyyy")
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private var layout = view.findViewById<ConstraintLayout>(R.id.teamRow)
        private var nameTV = view.findViewById<TextView>(R.id.nameTV)
        private var timeTV = view.findViewById<TextView>(R.id.timeTV)

        init {
            // click to select team
            layout.setOnClickListener {
                // remember the position of clean job
                viewModel.setTeamPos(adapterPosition)
                selectTeam()
                true
            }
        }

        fun bind(item: Team?) {
            if (item == null) return
            nameTV.text = item.name
            val text = "joined on " + dateFormat.format(item.timeStamp!!.toDate())
            timeTV.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_team, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(MainActivity.TAG, "XXX, Bind pos $position")
        holder.bind(getItem(position))
    }
}