package com.utap.tidy.ui.item

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
import com.utap.tidy.ui.MainViewModel
import java.time.LocalDateTime

class FinishItemFrag: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    companion object {
        const val TAG = "FinishItemFrag"
        fun newInstance(): FinishItemFrag {
            return FinishItemFrag()
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

        val currentItemJob = viewModel.getFinishItemJob()
        val textOne = "you just cleaned ${currentItemJob.jobTitle}"
        val textTwo = "+${currentItemJob.score} points"
        root.findViewById<TextView>(R.id.jobTitle).text = textOne
        root.findViewById<TextView>(R.id.jobScores).text = textTwo

        root.findViewById<ImageView>(R.id.checkBtn)
            .setOnClickListener {
                // update the timestamp
                viewModel.finishItemJob()
                setTitle(viewModel.getAreaTitle())
                parentFragmentManager.popBackStack()
            }

        root.findViewById<ImageView>(R.id.closeBtn)
            .setOnClickListener {
                setTitle(viewModel.getAreaTitle())
                parentFragmentManager.popBackStack()
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle(viewModel.getAreaTitle())
                Log.d(TAG, "XXX, FinishItemFrag back to ItemFrag")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }
}