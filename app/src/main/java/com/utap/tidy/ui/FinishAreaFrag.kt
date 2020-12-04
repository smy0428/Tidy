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
import com.google.firebase.firestore.FieldValue
import com.utap.tidy.R
import java.time.LocalDateTime

class FinishAreaFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val TAG = "FinishAreaFrag"
        fun newInstance(): FinishAreaFrag {
            return FinishAreaFrag()
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
        val root = inflater.inflate(R.layout.fragment_area_finish, container, false)
        setTitle("Woo")

        val currentAreaJob = viewModel.getFinishAreaJob()
        val textOne = "you just cleaned ${currentAreaJob.jobTitle}"
        val textTwo = "+${currentAreaJob.score} points"
        root.findViewById<TextView>(R.id.jobTitle).text = textOne
        root.findViewById<TextView>(R.id.jobScores).text = textTwo

        root.findViewById<ImageView>(R.id.checkBtn)
            .setOnClickListener {
                // update the timestamp
                viewModel.finishAreaJob()
                setTitle("Area")
                parentFragmentManager.popBackStack()
            }

        root.findViewById<ImageView>(R.id.closeBtn)
            .setOnClickListener {
                setTitle("Area")
                parentFragmentManager.popBackStack()
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Area")
                Log.d(TAG, "XXX, FinishAreaFrag back to HomeFrag")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }

}