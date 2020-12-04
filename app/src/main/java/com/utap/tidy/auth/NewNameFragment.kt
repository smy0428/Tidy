package com.utap.tidy.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.utap.tidy.R
import com.utap.tidy.ui.MainViewModel

class NewNameFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance(): NewNameFragment {
            return NewNameFragment()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_new_name, container, false)
        setTitle("New Name")

        root.findViewById<ImageView>(R.id.checkBtn)
                .setOnClickListener {
                    val teamName = root.findViewById<EditText>(R.id.new_name).text.toString()
                    if (teamName.isEmpty()) {
                        Toast.makeText(context, "Your name is invalid", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.initNewTeam(teamName)
                        setTitle("Area")
                        Toast.makeText(context, "$teamName has been created", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack(viewModel.homeFragTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("New User")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }
}