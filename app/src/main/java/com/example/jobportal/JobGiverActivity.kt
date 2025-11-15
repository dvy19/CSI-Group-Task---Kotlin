package com.example.jobportal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobGiverActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var tvError: TextView
    private lateinit var tvPostsCount: TextView
    private lateinit var adapter: CommunityPostAdapter
    private lateinit var btnAddPost: Button
    private lateinit var btnAddJob: Button

    // Request code for starting post creation activity
    private companion object {
        const val CREATE_POST_REQUEST = 1001
        const val TAG = "JobGiverActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        Log.d(TAG, "Activity onCreate started")
        initializeViews()
        setupRecyclerView() // ⚠️ CRITICAL: Set up RecyclerView FIRST
        setupClickListeners()

        // Wait a bit for layout to complete before loading data
        recyclerView.post {
            loadCommunityPosts()
        }
    }

    private fun initializeViews() {
        Log.d(TAG, "Initializing views")
        recyclerView = findViewById(R.id.rvCommunityPosts)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvError = findViewById(R.id.tvError)
        tvPostsCount = findViewById(R.id.tvPostsCount)
        btnAddPost = findViewById(R.id.add_comm_post)
        btnAddJob = findViewById(R.id.add_job)

        Log.d(TAG, "RecyclerView found: ${recyclerView != null}")
        Log.d(TAG, "Buttons found - AddPost: ${btnAddPost != null}, AddJob: ${btnAddJob != null}")
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")

        // ⚠️ CRITICAL: Create and set adapter immediately
        adapter = CommunityPostAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // ⚠️ CRITICAL: Set an empty list initially to avoid "no adapter" warning
        adapter.updatePosts(emptyList())

        Log.d(TAG, "RecyclerView setup complete - Adapter: ${recyclerView.adapter != null}")
        Log.d(TAG, "LayoutManager: ${recyclerView.layoutManager != null}")
    }

    private fun setupClickListeners() {
        btnAddPost.setOnClickListener {
            Log.d(TAG, "Add Post button clicked")
            val intent = Intent(this, CommunityPostActivity::class.java)
            startActivityForResult(intent, CREATE_POST_REQUEST)
        }

        btnAddJob.setOnClickListener {
            Log.d(TAG, "Add Job button clicked")
            // Handle add job button click if needed
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult - requestCode: $requestCode, resultCode: $resultCode")
        if (requestCode == CREATE_POST_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Post created successfully, refreshing posts")
            loadCommunityPosts()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume - Refreshing posts")
        loadCommunityPosts()
    }

    private fun loadCommunityPosts() {
        showLoading()
        Log.d(TAG, "Starting to load community posts")

        val token = getAuthToken()
        Log.d(TAG, "Auth token: ${if (token.isNotEmpty()) "Present (${token.length} chars)" else "Empty"}")

        if (token.isEmpty()) {
            showError("Please login first")
            hideLoading()
            return
        }

        val authHeader = "Bearer $token"
        Log.d(TAG, "Making API call with header: ${authHeader.take(20)}...")

        SignUpRetrofitClient.commPostGetInstance.getCommunityPosts(authHeader)
            .enqueue(object : Callback<CommunityPostsGetResponse> {
                override fun onResponse(
                    call: Call<CommunityPostsGetResponse>,
                    response: Response<CommunityPostsGetResponse>
                ) {
                    Log.d(TAG, "API Response received - Code: ${response.code()}")
                    hideLoading()

                    if (response.isSuccessful) {
                        val postsResponse = response.body()
                        Log.d(TAG, "Response successful - Posts count: ${postsResponse?.count ?: 0}")

                        postsResponse?.let {
                            handleSuccessResponse(it)
                        } ?: run {
                            Log.d(TAG, "Response body is null")
                            showError("No data received")
                        }
                    } else {
                        Log.d(TAG, "Response failed with code: ${response.code()}")
                        when (response.code()) {
                            401 -> showError("Unauthorized - Please login again")
                            500 -> showError("Server error - Please try again later")
                            else -> showError("Failed to load posts: ${response.code()} - ${response.message()}")
                        }
                    }
                }

                override fun onFailure(call: Call<CommunityPostsGetResponse>, t: Throwable) {
                    Log.e(TAG, "API Call failed: ${t.message}", t)
                    hideLoading()
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun handleSuccessResponse(response: CommunityPostsGetResponse) {
        val posts = response.results
        Log.d(TAG, "Processing ${posts.size} posts")

        tvPostsCount.text = "Posts: ${response.count}"

        if (posts.isNotEmpty()) {
            Log.d(TAG, "First post - Author: '${posts[0].author}', Content: '${posts[0].content?.take(30)}...'")
            adapter.updatePosts(posts)
            showPosts()
        } else {
            Log.d(TAG, "No posts in results array")
            showEmptyState()
        }
    }

    private fun showLoading() {
        Log.d(TAG, "Showing loading state")
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmptyState.visibility = View.GONE
        tvError.visibility = View.GONE
        btnAddPost.isEnabled = false
        btnAddJob.isEnabled = false
    }

    private fun hideLoading() {
        Log.d(TAG, "Hiding loading state")
        progressBar.visibility = View.GONE
        btnAddPost.isEnabled = true
        btnAddJob.isEnabled = true
    }

    private fun showPosts() {
        Log.d(TAG, "Showing posts")
        recyclerView.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
        tvError.visibility = View.GONE
    }

    private fun showEmptyState() {
        Log.d(TAG, "Showing empty state")
        recyclerView.visibility = View.GONE
        tvEmptyState.visibility = View.VISIBLE
        tvError.visibility = View.GONE
    }

    private fun showError(message: String) {
        Log.d(TAG, "Showing error: $message")
        recyclerView.visibility = View.GONE
        tvEmptyState.visibility = View.GONE
        tvError.visibility = View.VISIBLE
        tvError.text = message
    }

    private fun getAuthToken(): String {
        val sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPref.getString("access_token", "") ?: ""
        Log.d(TAG, "Retrieved auth token from shared prefs: ${if (token.isNotEmpty()) "Present" else "Empty"}")
        return token
    }

    fun onRefreshClicked(view: View) {
        Log.d(TAG, "Refresh button clicked")
        loadCommunityPosts()
    }
}