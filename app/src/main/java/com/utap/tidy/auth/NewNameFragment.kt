package com.utap.tidy.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.utap.tidy.R
import com.utap.tidy.ui.MainViewModel

class NewNameFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val homeFragTag = "homeFragTag"

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

        root.findViewById<ImageView>(R.id.confirm_button)
                .setOnClickListener {
                    val newName = root.findViewById<EditText>(R.id.new_name).text.toString()
                    if (newName.isEmpty()) {
                        Toast.makeText(context, "Your name is invalid", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.initNewTeam(newName)
                        setTitle("Area")
                        parentFragmentManager.popBackStack(homeFragTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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