package com.utap.tidy.ui

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.utap.tidy.R
import com.utap.tidy.auth.NewUserFragment
import com.utap.tidy.ui.item.ItemFrag


class HomeFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: AreaRowAdapter
    private var currentUser: FirebaseUser? = null
    companion object {
        const val TAG = "HomeFragment"
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun initAuth() {
        viewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, Observer {
            currentUser = it
            if (currentUser != null) {
                viewModel.newUserCheck()
            }
        })
    }

    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START)
            {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    val position = viewHolder.adapterPosition
                    Log.d(TAG, "XXX, Swipe delete $position")
                    viewModel.removeAreaAt(position)
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }

    private fun initAdapter(root:View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)

        // add finish callback to adapter
        adapter = AreaRowAdapter(viewModel) {
            // call back for adapter to finish
            initActionFinish()
        }

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        // get divide line in RecyclerView
        // https://developer.android.com/reference/android/support/v7/widget/DividerItemDecoration.html
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        // using !! because it never be null
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)

        // Swipe left to delete
        initTouchHelper().attachToRecyclerView(rv)
    }

    private fun initTitleObservers() {
        val titleTV = (activity as AppCompatActivity).findViewById<TextView>(R.id.actionTitle)
        viewModel.observeTitle().observe(viewLifecycleOwner, Observer {
            titleTV.text = it
        })
    }

    private fun initSearchBarObservers() {
        viewModel.observeSearchBarState().observe(viewLifecycleOwner, Observer {searchBarState ->
            if (searchBarState == 1) {
                val searchET = requireActivity().findViewById<EditText>(R.id.actionSearch)
                val searchBtn = requireActivity().findViewById<ImageView>(R.id.searchBtn)
                val signOutBtn = requireActivity().findViewById<ImageView>(R.id.actionSignOut)
                searchET.visibility = View.VISIBLE
                searchBtn.visibility = View.VISIBLE
                signOutBtn.visibility = View.GONE
            } else {
                val searchET = requireActivity().findViewById<EditText>(R.id.actionSearch)
                val searchBtn = requireActivity().findViewById<ImageView>(R.id.searchBtn)
                val signOutBtn = requireActivity().findViewById<ImageView>(R.id.actionSignOut)
                searchET.visibility = View.INVISIBLE
                searchBtn.visibility = View.GONE
                signOutBtn.visibility = View.VISIBLE
            }
        })
    }

    private fun initActionScore() {
        val scoreFrag = ScoreFrag.newInstance()
        Log.d(TAG, "XXX, transaction to ScoreFrag")
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(viewModel.homeFragTag)
            .add(R.id.main_frame, scoreFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun actionNewUser() {
        val newUserFragment = NewUserFragment.newInstance()
        Log.d(TAG, "XXX, transaction to NewUserFragment")
        parentFragmentManager
                .beginTransaction()
                .addToBackStack(viewModel.homeFragTag)
                .add(R.id.main_frame, newUserFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    private fun initActionAdd() {
        val addAreaNameFrag = AddAreaNameFrag.newInstance()
        Log.d(TAG, "XXX, transaction to AddAreaNameFrag")
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(viewModel.homeFragTag)
            .add(R.id.main_frame, addAreaNameFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initActionEdit() {
        val editAreaNameFrag = EditAreaNameFrag.newInstance()
        Log.d(TAG, "XXX, transaction to EditAreaNameFrag")
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(viewModel.homeFragTag)
            .add(R.id.main_frame, editAreaNameFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initActionFinish() {
        val finishAreaFrag = FinishAreaFrag.newInstance()
        Log.d(TAG, "XXX, transaction to FinishAreaFrag")
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(viewModel.homeFragTag)
            .add(R.id.main_frame, finishAreaFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initActionOpen() {
        val itemFrag = ItemFrag.newInstance()
        Log.d(TAG, "XXX, transaction to ItemFrag")
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(viewModel.homeFragTag)
            .add(R.id.main_frame, itemFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun hideAddAndScoreBtn() {
        requireView().findViewById<TextView>(R.id.newArea).visibility = View.INVISIBLE
        requireView().findViewById<ImageView>(R.id.score).visibility = View.INVISIBLE
    }

    private fun initNewUserObservers() {
        viewModel.observeUserState().observe(viewLifecycleOwner, Observer { userState ->
            when (userState) {
                1 -> {
                    actionNewUser()
                }
                2 -> {
                    // all good, start normal state
                    // active the add area button
                    val addAreaTV = requireView().findViewById<TextView>(R.id.newArea)
                    addAreaTV.setOnClickListener { initActionAdd() }
                    addAreaTV.visibility = View.VISIBLE

                    val scoreIV = requireView().findViewById<ImageView>(R.id.score)
                    scoreIV.setOnClickListener { initActionScore() }
                    scoreIV.visibility = View.VISIBLE

                    viewModel.observeAreaCleanJobs().observe(viewLifecycleOwner, Observer {
                        Log.d(TAG, "XXX, HomeFragment is observing AreaCleanJobs")
                        adapter.submitList(it)
                        // failed to detect title edit
                        adapter.notifyDataSetChanged()
                    })

                    viewModel.observeAreaEditState().observe(viewLifecycleOwner, Observer {editState ->
                        if (editState == 1) {
                            Log.d(TAG, "XXX, HomeFragment is start to edit AreaCleanJobs")
                            initActionEdit()
                        }
                    })

                    viewModel.fetchAreaCleanJob()
                    Log.d(TAG, "XXX, user exists")
                }
                -1 -> {
                    Toast.makeText(context, "Internet connection failed.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initOpenAreaObservers() {
        viewModel.observeOpenAreaState().observe(viewLifecycleOwner, Observer { openAreaState ->
            if (openAreaState == 1) {
                initActionOpen()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rv, container, false)
        requireActivity().findViewById<ImageView>(R.id.actionSignOut).setOnClickListener {
            Log.d(TAG, "sign out")
            viewModel.signOut()
            Toast.makeText(context, "You have successfully signed out", Toast.LENGTH_SHORT).show()
            hideAddAndScoreBtn()
        }
        initAuth()
        initAdapter(root)
        initTitleObservers()
        initSearchBarObservers()
        initOpenAreaObservers()
        initNewUserObservers()
        return root
    }
}