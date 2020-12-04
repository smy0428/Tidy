package com.utap.tidy.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.utap.tidy.R
import com.utap.tidy.ui.MainViewModel

class SelectTeamFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance(): SelectTeamFragment {
            return SelectTeamFragment()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_team_select, container, false)
        setTitle("Join")
        viewModel.deactivateSearchBarState()

        root.findViewById<TextView>(R.id.teamName).text = viewModel.getTeamName()

        root.findViewById<ImageView>(R.id.checkBtn)
                .setOnClickListener {
                    viewModel.joinTeam()
                    setTitle("Area")
                    parentFragmentManager.popBackStack(viewModel.homeFragTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }

        root.findViewById<ImageView>(R.id.closeBtn)
            .setOnClickListener {
                setTitle("Search")
                viewModel.activateSearchBarState()
                parentFragmentManager.popBackStack()
            }

        // back button must have lifecycle
        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setTitle("Search")
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return root
    }
}