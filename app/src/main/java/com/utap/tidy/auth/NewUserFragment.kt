package com.utap.tidy.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.utap.tidy.R
import com.utap.tidy.ui.MainViewModel

class NewUserFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance(): NewUserFragment {
            return NewUserFragment()
        }
    }

    private fun setTitle(newTitle: String) {
        viewModel.setTitle(newTitle)
    }

    private fun actionNew() {
        val newNameFragment = NewNameFragment.newInstance()
        parentFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.main_frame, newNameFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    private fun actionJoin() {
        val searchTeamFragment = SearchTeamFragment.newInstance()
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.main_frame, searchTeamFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_new_user, container, false)

        setTitle("New User")

        root.findViewById<ImageView>(R.id.newBtn).setOnClickListener {
            actionNew()
        }

        root.findViewById<ImageView>(R.id.joinBtn).setOnClickListener {
            actionJoin()
        }

        return root
    }

}