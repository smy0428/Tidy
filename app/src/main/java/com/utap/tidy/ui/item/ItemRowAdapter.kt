package com.utap.tidy.ui.item

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
import com.utap.tidy.ui.MainViewModel
import java.util.*

/**
 * Created by siyuan on 11/30/2020
 */

// This adapter inherits from ListAdapter, which mean that all need
// to do is give it a new list and an old list and we will never
// have to call notifyDatasetChanged(). However, it failed to detect the
// title change somehow (String), under certain circumstance, we will have
// to call notifyDataSetChanged()

class ItemRowAdapter (private val viewModel: MainViewModel,
                      private val finishItem: ()->Unit):
        ListAdapter<CleanJob, ItemRowAdapter.ViewHolder>(CleanJobDiff()) {
    class CleanJobDiff: DiffUtil.ItemCallback<CleanJob>() {

        override fun areItemsTheSame(oldItem: CleanJob, newItem: CleanJob): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: CleanJob, newItem: CleanJob): Boolean {
            return oldItem.jobTitle == newItem.jobTitle
                    && oldItem.lastUpdateTime == newItem.lastUpdateTime
                    && oldItem.score == newItem.score
                    && oldItem.frequency == newItem.frequency
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        // private var completeRowLayout = view.findViewById<ConstraintLayout>(R.id.areaRow)
        private var timeGroupLayout = view.findViewById<LinearLayout>(R.id.timeGroup)
        private var titleTV = view.findViewById<TextView>(R.id.title)
        private var timeCountIV = view.findViewById<ImageView>(R.id.timeIV)
        private var dueDayTV = view.findViewById<TextView>(R.id.timeTV)

        init {
            // long click to finish
            timeGroupLayout.setOnLongClickListener {
                // remember the position of clean job
                viewModel.setItemJobPos(adapterPosition)
                finishItem()
                true
            }

            /*
            // long click on textView to edit
            titleTV.setOnLongClickListener {
                viewModel.setAreaJobPos(adapterPosition)
                viewModel.setAreaEditState()
                true
            }

            // click on item row to open even small items
            completeRowLayout.setOnClickListener {
                viewModel.setAreaJobPos(adapterPosition)
                viewModel.activateOpenAreaState()
                true
            }
             */
        }

        private fun generateDueSentence(pastDays: Int, frequency: Int): String {
            when {
                pastDays < frequency -> {
                    val days = frequency - pastDays
                    return if (days == 1) {
                        "Due in $days day"
                    } else {
                        "Due in $days days"
                    }
                }
                pastDays == frequency -> {
                    return "Due today"
                }
                else -> {
                    val days = pastDays - frequency
                    return if (days == 1) {
                        "$days day overdue"
                    } else {
                        "$days days overdue"
                    }
                }
            }
        }

        private fun calPastDays(date: Date): Int {
            return (Date().time / 86400000 - date.time / 86400000).toInt()
        }

        fun bind(item: CleanJob?) {
            if (item == null) return

            /*
            Log.d("AreaRowAdapter", "XXX, clean job is ${item.jobTitle}")
            Log.d("AreaRowAdapter", "XXX, last update time ${item.lastUpdateTime}")
            Log.d("AreaRowAdapter", "XXX, time stamp is ${item.timeStamp}")
             */

            titleTV.text = item.jobTitle
            Log.d("ItemRowAdapter", "XXX, bind name is ${item.jobTitle}")
            val freq = item.frequency!!
            val pastDays = calPastDays(item.lastUpdateTime!!.toDate())
            dueDayTV.text = generateDueSentence(pastDays, freq)
            Log.d("ItemRowAdapter", "XXX, past day is $pastDays")

            var drawableID = 0
            when {
                pastDays < freq -> {
                    val rate = 100.0 * pastDays / freq
                    when {
                        rate < 10 -> {
                            drawableID = R.drawable.ic_due_10
                        }
                        rate < 20 -> {
                            drawableID = R.drawable.ic_due_20
                        }
                        rate < 30 -> {
                            drawableID = R.drawable.ic_due_30
                        }
                        rate < 40 -> {
                            drawableID = R.drawable.ic_due_40
                        }
                        rate < 50 -> {
                            drawableID = R.drawable.ic_due_50
                        }
                        rate < 60 -> {
                            drawableID = R.drawable.ic_due_60
                        }
                        rate < 70 -> {
                            drawableID = R.drawable.ic_due_70
                        }
                        rate < 80 -> {
                            drawableID = R.drawable.ic_due_80
                        }
                        rate < 90 -> {
                            drawableID = R.drawable.ic_due_90
                        }
                        else -> {
                            drawableID = R.drawable.ic_due_100
                        }
                    }

                }
                pastDays == freq -> {
                    drawableID = R.drawable.ic_due_105
                }
                else -> {
                    when (pastDays - freq) {
                        1 -> {
                            drawableID = R.drawable.ic_due_110
                        }
                        2 -> {
                            drawableID = R.drawable.ic_due_120
                        }
                        3 -> {
                            drawableID = R.drawable.ic_due_130
                        }
                        4 -> {
                            drawableID = R.drawable.ic_due_140
                        }
                        5 -> {
                            drawableID = R.drawable.ic_due_150
                        }
                        else -> {
                            drawableID = R.drawable.ic_due_160
                        }
                    }
                }
            }
            timeCountIV.setImageResource(drawableID)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_area, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(MainActivity.TAG, "XXX, Bind pos $position")
        holder.bind(getItem(position))
    }
}