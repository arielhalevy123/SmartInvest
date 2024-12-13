package com.example.smartinvest.community
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinvest.FirebaseSingleton
import com.example.smartinvest.R
import com.google.firebase.database.DatabaseReference

class CommunityAdapter(private val items: MutableList<Post>) :
    RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>() {

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
           // val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val currentUserId = FirebaseSingleton.auth.currentUser?.uid


            holder.username.text = item.username
            holder.postText.text = item.content
            holder.likeCount.text = "${item.likes} Likes"

            val backgroundColor = if (position % 2 == 0) {
                holder.itemView.context.getColor(R.color.backgroundColor) // צבע לפוסטים במיקום זוגי
            } else {
                holder.itemView.context.getColor(R.color.white) // צבע לפוסטים במיקום אי-זוגי
            }
            holder.itemView.setBackgroundColor(backgroundColor)

            // בדיקה אם המשתמש הנוכחי עשה לייק
            val isLiked = currentUserId != null && item.likedBy.containsKey(currentUserId)
            holder.likeButton.setImageResource(
                if (isLiked) R.drawable.baseline_thumb_up_24 else R.drawable.baseline_thumb_up_empty_24
            )

            // טיפול בלחיצה על כפתור הלייק
            holder.likeButton.setOnClickListener {
                if (currentUserId != null) {
                    val database = FirebaseSingleton.database.child("posts").child(item.postId)
                    if (isLiked) {
                        // הסרת לייק
                        item.likes -= 1
                        item.likedBy = item.likedBy - currentUserId
                        database.apply{
                            child("likes").setValue(item.likes)
                            child("likedBy").child(currentUserId).removeValue()
                        }
                        holder.likeButton.setImageResource(R.drawable.baseline_thumb_up_empty_24)
                    } else {
                        // הוספת לייק
                        item.likes += 1
                        item.likedBy = item.likedBy + (currentUserId to true)
                        database.apply {
                            child("likes").setValue(item.likes)
                            child("likedBy").child(currentUserId).setValue(true)
                        }
                        holder.likeButton.setImageResource(R.drawable.baseline_thumb_up_24)
                    }
                    holder.likeCount.text = "${item.likes} Likes"
                } else {
                    Toast.makeText(holder.itemView.context, "Please log in to like posts", Toast.LENGTH_SHORT).show()
                }
            }
        }
    override fun getItemCount(): Int = items.size
}