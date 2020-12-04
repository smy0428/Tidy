package com.utap.tidy.ui.item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.utap.tidy.R
import com.utap.tidy.ui.MainViewModel

class AddItemNameFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        val ITEMS: Array<String> = arrayOf(
            "Clean sink",
            "Clean toilet",
            "Change towels",
            "Shower",
            "Clean Mirrors",
            "Disinfect floor",
            "Disinfect bathtub"
        )
        const val TAG = "AddItemNameFrag"
        fun newInstance(): AddItemNameFrag {
            return AddItemNameFrag()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun actionNextStep() {
        val nextFrag = AddItemFreqFrag.newInstance()
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.main_frame, nextFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun initAdapter(root: View) {
        val adapter = context?.let {
            // custom adapter to enable the popup theme
            ArrayAdapter<String>(it, R.layout.item_autocomplete, R.id.tvCustom, ITEMS)
        }
        val tv = root.findViewById<AutoCompleteTextView>(R.id.newArea)
        tv.setAdapter(adapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_area_name, container, false)
        setTitle("Name")
        initAdapter(root)
        viewModel.initItemCleanJob()

        root.findViewById<ImageView>(R.id.checkBtn)
            .setOnClickListener {
                val newItemName = root.findViewById<EditText>(R.id.newArea).text.toString()
                if (newItemName.isEmpty()) {
                    Toast.makeText(context, "Item name is invalid", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setItemName(newItemName)
                    actionNextStep()
                }
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle(viewModel.getAreaTitle())
                Log.d(TAG, "XXX, ItemNameFrag back to HomeFrag")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }

}