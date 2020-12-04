package com.utap.tidy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.utap.tidy.R

class AddAreaFreqFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val TAG = "AddAreaFreqFrag"
        fun newInstance(): AddAreaFreqFrag {
            return AddAreaFreqFrag()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun actionNextStep() {
        val nextFrag = AddAreaEffortFrag.newInstance()
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.main_frame, nextFrag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_area_freq, container, false)
        setTitle("Frequency")

        val np = root.findViewById<NumberPicker>(R.id.newFreq)
        np.minValue = 0
        np.maxValue = 180
        np.isActivated = true

        root.findViewById<ImageView>(R.id.checkBtn)
            .setOnClickListener {
                val dayFreq = root.findViewById<NumberPicker>(R.id.newFreq).value
                if (dayFreq == 0) {
                    Toast.makeText(context, "Frequency is invalid", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setAreaFreq(dayFreq)
                    actionNextStep()
                }
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Name")
                Log.d(AddAreaNameFrag.TAG, "XXX, FreqFrag back to AreaNameFrag")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }
}