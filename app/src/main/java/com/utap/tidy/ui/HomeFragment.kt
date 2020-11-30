package com.utap.tidy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utap.tidy.R

class HomeFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: AreaRowAdapter
    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun initAdapter(root:View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = AreaRowAdapter(viewModel)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        // get divide line in RecyclerView
        // https://developer.android.com/reference/android/support/v7/widget/DividerItemDecoration.html
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        // using !! because it never be null
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rv, container, false)
        initAdapter(root)

        viewModel.observeAreaCleanJobs().observe(viewLifecycleOwner, Observer {
            Log.d("XXX", "HomeFragment is observing AreaCleanJobs")
            adapter.submitList(it)
        })
        return root
    }
}