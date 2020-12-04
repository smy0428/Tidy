package com.utap.tidy.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utap.tidy.R
import com.utap.tidy.ui.MainViewModel
import com.utap.tidy.ui.TeamRowAdapter

class SearchTeamFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: TeamRowAdapter
    private var searchTerm = ""

    companion object {
        const val TAG = "SearchTeamFrag"
        fun newInstance(): SearchTeamFragment {
            return SearchTeamFragment()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun initSearch() {
        val searchET = requireActivity().findViewById<EditText>(R.id.actionSearch)
        val searchBtn = requireActivity().findViewById<ImageView>(R.id.searchBtn)

        searchBtn.setOnClickListener {
            val teamName = searchET.text.toString()
            if (teamName.isEmpty()) {
                Toast.makeText(context, "Team name is invalid", Toast.LENGTH_SHORT).show()
            } else {
                searchTerm = teamName
                viewModel.searchTeam(teamName)
            }
        }
    }


    private fun initAdapter(root:View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = TeamRowAdapter(viewModel) {
            // start select fragment
            initActionSelect()
        }

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        // get divide line in RecyclerView
        // https://developer.android.com/reference/android/support/v7/widget/DividerItemDecoration.html
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        // using !! because it never be null
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)
    }

    private fun initObservers() {
        viewModel.observeSearchTeam().observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "XXX, searchFragment is observing searchTeam")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.observeTeamSearchState().observe(viewLifecycleOwner, Observer {searchState ->
            Log.d(TAG, "XXX, searchFragment is observing TeamSearchState")
            if (searchState == 1) {
                Toast.makeText(context, "No team found for $searchTerm", Toast.LENGTH_SHORT).show()
                viewModel.clearTeamSearchState()
            }
        })
    }

    private fun initActionSelect() {
        val selectTeamFragment = SelectTeamFragment.newInstance()
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.main_frame, selectTeamFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_rv, container, false)
        setTitle("Search")
        viewModel.activateSearchBarState()
        initSearch()
        initAdapter(root)
        initObservers()

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("New User")
                viewModel.deactivateSearchBarState()
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }
}