package com.example.jobportal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)



        // Initialize buttons and set click listeners
        val addJob = view.findViewById<Button>(R.id.add_job)
        val addCommJob = view.findViewById<Button>(R.id.add_comm_post)

        addJob.setOnClickListener {
            val intent = Intent(requireContext(), AddJobActivity::class.java)
            startActivity(intent)

        }

        addCommJob.setOnClickListener {
            val intent = Intent(requireContext(), CommunityPostActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}