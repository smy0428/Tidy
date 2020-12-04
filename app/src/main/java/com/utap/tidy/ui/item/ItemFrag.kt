package com.utap.tidy.ui.item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utap.tidy.R
import com.utap.tidy.ui.*
import java.util.*

class ItemFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ItemRowAdapter
    companion object {
        const val TAG = "ItemFrag"
        fun newInstance(): ItemFrag {
            return ItemFrag()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
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
                    viewModel.removeItemAt(position)
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
    }

    private fun initAdapter(root:View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)

        // add finish callback to adapter
        adapter = ItemRowAdapter(viewModel) {
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

    private fun initActionAdd() {
        val addItemNameFrag = AddItemNameFrag.newInstance()
        Log.d(TAG, "XXX, transaction to AddAreaNameFrag")
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(viewModel.itemFragTag)
            .add(R.id.main_frame, addItemNameFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initActionFinish() {
        val finishItemFrag = FinishItemFrag.newInstance()
        Log.d(TAG, "XXX, transaction to FinishAreaFrag")
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(viewModel.itemFragTag)
            .add(R.id.main_frame, finishItemFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initObservers() {
        viewModel.observeItemCleanJobs().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d(TAG, "XXX, ItemFrag is observing ItemCleanJobs")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rv_item, container, false)
        setTitle(viewModel.getAreaTitle())
        initAdapter(root)
        initObservers()
        val addItemTV = root.findViewById<TextView>(R.id.newItem)
        addItemTV.setOnClickListener { initActionAdd() }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Area")
                viewModel.deactivateOpenAreaState()
                parentFragmentManager.popBackStack()
                Log.d(TAG, "XXX, ItemFrag back to HomeFrag")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        viewModel.fetchItemCleanJob()
        return root
    }
}