package com.utap.tidy.ui.item

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
import com.utap.tidy.ui.MainViewModel

class AddItemEffortFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val TAG = "AddItemEffortFrag"
        fun newInstance(): AddItemEffortFrag {
            return AddItemEffortFrag()
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
        setTitle("Effort")

        val np = root.findViewById<NumberPicker>(R.id.newEffort)
        np.minValue = 0
        np.maxValue = 300
        np.isActivated = true

        root.findViewById<ImageView>(R.id.checkBtn)
            .setOnClickListener {
                val effort = root.findViewById<NumberPicker>(R.id.newEffort).value
                if (effort == 0) {
                    Toast.makeText(context, "Frequency is invalid", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setItemEffort(effort)
                    // upload new CleanJob to firestore
                    viewModel.saveItemCleanJob()
                    setTitle(viewModel.getAreaTitle())
                    parentFragmentManager.popBackStack(viewModel.itemFragTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Frequency")
                Log.d(TAG, "XXX, EffortFrag back to FreqFrag ")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }

}