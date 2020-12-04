package com.utap.tidy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.utap.tidy.R

class EditAreaEffortFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val TAG = "EditAreaEffortFrag"
        fun newInstance(): EditAreaEffortFrag {
            return EditAreaEffortFrag()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_area_effort, container, false)
        setTitle("Edit Effort")
        val currentAreaJob = viewModel.getFinishAreaJob()

        val np = root.findViewById<NumberPicker>(R.id.newEffort)
        np.minValue = 0
        np.maxValue = 300
        np.isActivated = true
        np.value = currentAreaJob.score!!

        root.findViewById<ImageView>(R.id.checkBtn)
            .setOnClickListener {
                val effort = root.findViewById<NumberPicker>(R.id.newEffort).value
                if (effort == 0) {
                    Toast.makeText(context, "Frequency is invalid", Toast.LENGTH_SHORT).show()
                } else {
                    // update CleanJob to firestore
                    viewModel.setAreaEffort(effort)
                    viewModel.updateAreaCleanJob()
                    viewModel.clearAreaEditState()
                    setTitle("Area")
                    parentFragmentManager.popBackStack(viewModel.homeFragTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Edit Frequency")
                Log.d(TAG, "XXX, EditEffortFrag back to FreqFrag ")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return root
    }
}