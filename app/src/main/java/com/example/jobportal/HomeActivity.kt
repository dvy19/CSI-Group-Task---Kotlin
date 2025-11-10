package com.example.jobportal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private val allJobs = mutableListOf<Job>()
    private var filteredJobs = mutableListOf<Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen)

        // Initialize jobs
        initializeJobs()

        // Setup RecyclerView
        setupRecyclerView()

        // Setup filter buttons
        setupFilterButtons()
    }

    private fun initializeJobs() {
        allJobs.apply {
            add(Job("Android Developer", "Development"))
            add(Job("iOS Developer", "Development"))
            add(Job("Web Developer", "Development"))
            add(Job("Data Scientist", "Machine Learning"))
            add(Job("ML Engineer", "Machine Learning"))
            add(Job("AI Researcher", "Machine Learning"))
            add(Job("UI/UX Designer", "Design"))
            add(Job("Graphic Designer", "Design"))
            add(Job("Mobile App Developer", "App"))
            add(Job("Flutter Developer", "App"))
        }
        filteredJobs.addAll(allJobs)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewJobs)
        jobAdapter = JobAdapter(filteredJobs)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = jobAdapter
        }
    }

    private fun setupFilterButtons() {
        findViewById<Button>(R.id.btnAll).setOnClickListener { filterJobs("All") }
        findViewById<Button>(R.id.btnDevelopment).setOnClickListener { filterJobs("Development") }
        findViewById<Button>(R.id.btnMachineLearning).setOnClickListener { filterJobs("Machine Learning") }
        findViewById<Button>(R.id.btnDesign).setOnClickListener { filterJobs("Design") }
        findViewById<Button>(R.id.btnApp).setOnClickListener { filterJobs("App") }
    }

    private fun filterJobs(category: String) {
        filteredJobs.clear()

        if (category == "All") {
            filteredJobs.addAll(allJobs)
        } else {
            filteredJobs.addAll(allJobs.filter { it.category == category })
        }

        jobAdapter.updateList(filteredJobs)
    }
}