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

class EditAreaNameFrag: Fragment() {
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
        const val TAG = "EditAreaNameFrag"
        fun newInstance(): EditAreaNameFrag {
            return EditAreaNameFrag()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun actionNextStep() {
        val nextFrag = EditAreaFreqFrag.newInstance()
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
        setTitle("Edit Name")
        initAdapter(root)
        val currentAreaJob = viewModel.getFinishAreaJob()
        root.findViewById<AutoCompleteTextView>(R.id.newArea).setText(currentAreaJob.jobTitle)

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
                viewModel.clearAreaEditState()
                parentFragmentManager.popBackStack()
                Log.d(TAG, "XXX, EditAreaNameFrag back to HomeFrag")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }
}