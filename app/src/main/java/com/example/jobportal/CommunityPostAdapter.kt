package com.example.jobportal

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CommunityPostAdapter : RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder>() {

    private var posts: List<CommunityPostGet> = emptyList()
    private val TAG = "CommunityPostAdapter"

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val ivMedia: ImageView = itemView.findViewById(R.id.ivMedia)
        val tvLikes: TextView = itemView.findViewById(R.id.tvLikes)
        val tvComments: TextView = itemView.findViewById(R.id.tvComments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        Log.d(TAG, "Creating ViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Log.d(TAG, "Binding view holder at position $position")

        if (posts.isEmpty()) {
            Log.w(TAG, "No posts available for binding")
            return
        }

        val post = posts[position]
        Log.d(TAG, "Binding post $position - Author: '${post.author}', Content: '${post.content?.take(20)}...'")

        try {
            // Set basic post information with null safety
            holder.tvAuthor.text = post.author ?: "Unknown Author"
            holder.tvContent.text = post.content ?: "No content"
            holder.tvLikes.text = "${post.likes_count ?: 0} likes"
            holder.tvComments.text = "${post.comments_count ?: 0} comments"

            // Set date - FIXED: This was missing before
            holder.tvDate.text = formatDate(post.created_at)
            Log.d(TAG, "Date set to: ${holder.tvDate.text}")

            // Handle media image
            if (!post.media.isNullOrEmpty()) {
                Log.d(TAG, "Post has media: ${post.media}")
                holder.ivMedia.visibility = View.VISIBLE
                // Load image using Glide or Picasso when ready
                // Glide.with(holder.itemView.context).load(post.media).into(holder.ivMedia)
            } else {
                Log.d(TAG, "Post has no media")
                holder.ivMedia.visibility = View.GONE
            }

            Log.d(TAG, "Successfully bound post $position")
        } catch (e: Exception) {
            Log.e(TAG, "Error binding post at position $position", e)
        }
    }

    override fun getItemCount(): Int {
        val count = posts.size
        Log.d(TAG, "getItemCount: $count")
        return count
    }

    fun updatePosts(newPosts: List<CommunityPostGet>) {
        Log.d(TAG, "updatePosts called with ${newPosts.size} items")

        posts = newPosts
        Log.d(TAG, "Posts updated, notifying data set change")

        notifyDataSetChanged()

        // Verify the update worked
        Log.d(TAG, "After update - itemCount: ${itemCount}")
        if (posts.isNotEmpty()) {
            posts.forEachIndexed { index, post ->
                Log.d(TAG, "Post $index - Author: '${post.author}', Date: '${post.created_at}'")
            }
        } else {
            Log.d(TAG, "Posts list is empty after update")
        }
    }

    // Safe date formatting using SimpleDateFormat (compatible with minSdk 24)
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) {
            Log.d(TAG, "Date string is null or empty")
            return "Recent"
        }

        Log.d(TAG, "Formatting date: $dateString")

        return try {
            // Try common date formats
            val inputFormats = arrayOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
            )

            var parsedDate: Date? = null
            for (format in inputFormats) {
                try {
                    val sdf = SimpleDateFormat(format, Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    parsedDate = sdf.parse(dateString)
                    if (parsedDate != null) {
                        Log.d(TAG, "Successfully parsed date with format: $format")
                        break
                    }
                } catch (e: Exception) {
                    // Try next format
                    continue
                }
            }

            if (parsedDate != null) {
                val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                outputFormat.timeZone = TimeZone.getDefault()
                val formattedDate = outputFormat.format(parsedDate)
                Log.d(TAG, "Formatted date: $formattedDate")
                formattedDate
            } else {
                // If all parsing fails, show the original string or a simplified version
                val fallback = if (dateString.length >= 10) {
                    dateString.substring(0, 10) // Show just YYYY-MM-DD
                } else {
                    "Recent"
                }
                Log.d(TAG, "Using fallback date: $fallback")
                fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date: $dateString", e)
            "Recent" // Final fallback
        }
    }
}