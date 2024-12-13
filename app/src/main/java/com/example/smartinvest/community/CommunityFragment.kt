package com.example.smartinvest.community
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinvest.FirebaseSingleton
import com.example.smartinvest.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var communityAdapter: CommunityAdapter
    private lateinit var progressBar: View  // To manage the ProgressBar
    private val posts = mutableListOf<Post>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.communityRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the ProgressBar and RecyclerView
        progressBar = view.findViewById(R.id.progressBar)

        communityAdapter = CommunityAdapter(posts)
        recyclerView.adapter = communityAdapter
        progressBar.visibility = View.VISIBLE
        fetchPostsFromFirebase()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        val addPostButton: FloatingActionButton = view.findViewById(R.id.addPostButton)
        addPostButton.setOnClickListener {
            showAddPostDialog()
        }
    }


    fun savePostToFirebase(content: String) {
        val user = FirebaseSingleton.auth.currentUser
        if (user != null) {
            val postId = FirebaseSingleton.database.child("posts").push().key ?: return
            val post = Post(
                postId = postId,
                uid = user.uid,
                username = user.email ?: "Anonymous",
                content = content,
                likes = 0,
                likedBy = emptyMap()
            )
            FirebaseSingleton.database.child("posts").child(postId).setValue(post)
                .addOnSuccessListener {
                    Toast.makeText(context, "Post saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error saving post: $e", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchPostsFromFirebase() {
        FirebaseSingleton.database.child("posts").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = View.GONE
                posts.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { posts.add(it) }
                }
                posts.sortByDescending { it.postId }
                communityAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Error fetching posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showAddPostDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_post, null)
        val postContentInput = dialogView.findViewById<EditText>(R.id.postContentInput)
        val usernameDisplay = dialogView.findViewById<TextView>(R.id.usernameDisplay)
        val user = FirebaseSingleton.auth.currentUser
        usernameDisplay.text = user?.email ?: "Anonymous" // הצגת אימייל או טקסט ברירת מחדל
        val dialog = AlertDialog.Builder(context)
            .setTitle("Add Post")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val content = postContentInput.text.toString()
                if (content.isNotBlank()) {
                    savePostToFirebase(content) // העבר רק את תוכן הפוסט
                } else {
                    Toast.makeText(context, "Content cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


}
