package com.utap.tidy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.utap.tidy.R

class AddAreaNameFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        val AREAS: Array<String> = arrayOf(
            "Living Room",
            "Bedroom",
            "Bathroom",
            "Kitchen",
            "Dining room",
            "Laundry",
            "Outside",
            "Guest Room",
            "Work Room"
        )
        const val TAG = "AddAreaNameFrag"
        fun newInstance(): AddAreaNameFrag {
            return AddAreaNameFrag()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun actionNextStep() {
        val nextFrag = AddAreaFreqFrag.newInstance()
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
            ArrayAdapter<String>(it, R.layout.item_autocomplete, R.id.tvCustom ,AREAS)
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
        viewModel.initAreaCleanJob()

        root.findViewById<ImageView>(R.id.checkBtn)
            .setOnClickListener {
                val newAreaName = root.findViewById<EditText>(R.id.newArea).text.toString()
                if (newAreaName.isEmpty()) {
                    Toast.makeText(context, "Area name is invalid", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setAreaName(newAreaName)
                    actionNextStep()
                }
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Area")
                Log.d(TAG, "XXX, AreaNameFrag back to HomeFrag")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }

}