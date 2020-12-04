package com.utap.tidy.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.utap.tidy.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.util.*
import kotlin.collections.ArrayList

class ScoreFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val TAG = "ScoreFrag"
        fun newInstance(): ScoreFrag {
            return ScoreFrag()
        }
        //const val period = 30
       // const val barNum = 3
       // val colorsAccent = Color.rgb(85, 11, 38)
        val colors = listOf(
            Color.rgb(216, 27, 96),
            Color.rgb(239, 124, 42),
            Color.rgb(125, 216, 77),
            Color.rgb(242, 194, 57)
        )
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun calPastDays(date: Date): Int {
        return (Date().time / 86400000 - date.time / 86400000).toInt()
    }

    private fun setBarChart(barChart: BarChart) {
        viewModel.observeScoreFetchState().observe(viewLifecycleOwner, Observer { scoreFetchState ->
            if (scoreFetchState == 1) {
                Log.d(TAG, "XXX, start to observe score")
                val scoreDataMap = viewModel.getScoreSimpleMap()
                val numPersons = scoreDataMap.size
                if (numPersons > 1) {
                    val barDataSets = ArrayList<BarDataSet>()
                    var i = 0
                    for ((key, value) in scoreDataMap) {
                        Log.d(TAG, "XXX, $key got $value scores")
                        val entries = ArrayList<BarEntry>()
                        entries.add(BarEntry(i.toFloat(), value.toFloat()))
                        val set = BarDataSet(entries, key)
                        set.color = colors[i % colors.size]
                        barDataSets.add(set)
                        i += 1
                    }
                    val data = BarData(barDataSets as List<IBarDataSet>?)
                    val groupSpace = 0.05f;
                    val barSpace = 0.3f
                    val barWidth = 0.2f
                    data.barWidth = barWidth
                    barChart.data = data
                    barChart.groupBars(0f, groupSpace, barSpace)
                    data.setValueTextSize(10.5f)
                    data.setValueTextColor(Color.WHITE)
                    barChart.axisLeft.textColor = Color.WHITE
                    barChart.legend.textColor = Color.WHITE
                    barChart.axisRight.isEnabled = false
                    barChart.legend.formSize = 20f
                    barChart.legend.formToTextSpace = 10f
                    barChart.xAxis.isEnabled = false
                    barChart.description.isEnabled = false
                    barChart.setExtraOffsets(0f,20f,0f,20f)
                    barChart.legend.yOffset = 10f
                    barChart.animateXY(2000, 2000)
                    barChart.invalidate()
                } else if (numPersons == 1){
                    for ((key, value) in scoreDataMap) {
                        Log.d(TAG, "XXX, $key got $value scores")
                        val entries = ArrayList<BarEntry>()
                        entries.add(BarEntry(0f, value.toFloat()))
                        val set = BarDataSet(entries, key)
                        set.color = colors[2]
                        val data: BarData = BarData(set)
                        data.barWidth = 0.2f
                        data.setValueTextSize(10.5f)
                        data.setValueTextColor(Color.WHITE)
                        barChart.axisLeft.textColor = Color.WHITE
                        barChart.legend.textColor = Color.WHITE
                        barChart.axisRight.isEnabled = false
                        barChart.legend.formSize = 20f
                        barChart.legend.formToTextSpace = 10f
                        barChart.xAxis.isEnabled = false
                        barChart.description.isEnabled = false
                        barChart.setExtraOffsets(0f,20f,0f,20f)
                        barChart.legend.yOffset = 10f
                        barChart.data = data
                        barChart.invalidate()
                        barChart.animateXY(2000, 2000)
                        barChart.invalidate()
                    }
                }
            }
        })
        viewModel.clearScoreFetchState()

                // does not work out
                /*
                val scoreData = viewModel.getScoreDataMap()
                val barDataSets = ArrayList<BarDataSet>()
                var i = 0
                for (key in scoreData.keys) {
                    Log.d(TAG, "XXX, reading score from $key")
                    val scoreList = scoreData[key]!!
                    val scores = mutableListOf(0f, 0f, 0f)
                    scoreList.forEach {
                        Log.d(TAG, "XXX, score now is ${it.second}")
                        val date = it.first.toDate()
                        when (calPastDays(date) / period) {
                            0 -> {
                                scores[2] = scores[2] + it.second
                            }
                            1 -> {
                                scores[1] = scores[1] + it.second
                            }
                            2 -> {
                                scores[3] = scores[3] + it.second
                            }
                        }
                    }
                    val entries = ArrayList<BarEntry>()
                    var j = 0f
                    for (s in scores) {
                        entries.add(BarEntry(j, s))
                        j += 1
                    }
                    val set = BarDataSet(entries, key)
                    set.color = colors[i]
                    barDataSets[i] = set
                    i += 1
                }
                val data = BarData(barDataSets as List<IBarDataSet>?)

                val groupSpace = 0.06f;
                val barSpace = 0.02f
                val barWidth = 0.45f
                data.barWidth = barWidth
                barChart.data = data
                barChart.groupBars(3.0f, groupSpace, barSpace)
                barChart.setFitBars(true)
                barChart.animateXY(2000, 2000)
                barChart.invalidate()
            }
            viewModel.clearScoreFetchState()
        })
        */
        /*
       viewModel.observeScoreFetchState().observe(viewLifecycleOwner, Observer { scoreFetchState ->
           if (scoreFetchState == 1) {
               Log.d(TAG, "XXX, start to observe score")
               val scoreDataMap = viewModel.getScoreSimpleMap()
               val entries = ArrayList<BarEntry>()
               var i = 1.5
               for ((key, value) in scoreDataMap) {
                   Log.d(TAG, "XXX, $key got $value scores")
                   entries.add(BarEntry(i.toFloat(), value.toFloat()))
                   i += 1.5
               }

               Log.d(TAG, "XXX, entries value are $entries")
               val dataSet: BarDataSet = BarDataSet(entries, "scores")
               dataSet.color = Color.WHITE
               val data: BarData = BarData(dataSet)
               data.barWidth = 1f
               data.setValueTextSize(18f)
               barChart.data = data
               barChart.setFitBars(true)
               barChart.invalidate()
               barChart.animateXY(2000, 2000)
               barChart.invalidate()
           }
       })
        */
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.fetchSimpleScore()
        val root = inflater.inflate(R.layout.bar_chart, container, false)
        setTitle("Score")
        val barChartView = root.findViewById<BarChart>(R.id.chartView)
        setBarChart(barChartView)

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Area")
                Log.d(TAG, "XXX, ScoreFrag back to HomeFrag")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }

}