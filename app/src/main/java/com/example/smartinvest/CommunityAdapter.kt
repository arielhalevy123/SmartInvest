package com.example.smartinvest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

class CommunityAdapter (private val items: MutableList<Post>,private val databaseReference: DatabaseReference) : RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {

        class CommunityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val username: TextView = view.findViewById(R.id.username)
            val postText: TextView = view.findViewById(R.id.postText)
            val likeCount: TextView = view.findViewById(R.id.likeCount)
            val likeButton: ImageButton = view.findViewById(R.id.likeButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item_community, parent, false)
            return CommunityViewHolder(view)
        }

        override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
            val item = items[position]
            var isLiked = false
            holder.username.text = item.username
            holder.postText.text = item.content
            holder.likeCount.text = "${item.likes} Likes"
            holder.likeButton.tag = "unliked"
            // Handle like button click
            holder.likeButton.setOnClickListener {
                // Add logic for "liking" a post
                val newLikes = if (holder.likeButton.tag == "liked") {
                    // Unlike post
                    holder.likeButton.setImageResource(R.drawable.baseline_thumb_up_empty_24)
                    holder.likeButton.tag = "unliked"
                    item.likes - 1
                } else {
                    // Like post
                    holder.likeButton.setImageResource(R.drawable.baseline_thumb_up_24)
                    holder.likeButton.tag = "liked"
                    item.likes + 1
                }
                item.likes = newLikes // Update locally
                holder.likeCount.text = "$newLikes Likes"

                // Update Firebase
                val postId = item.postId // Replace with unique ID if needed
                databaseReference.child("posts").child(postId).child("likes").setValue(newLikes)

            }
        }
    override fun getItemCount(): Int = items.size
}