package com.example.smartinvest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var communityAdapter: CommunityAdapter
    private val posts = mutableListOf<Post>() // Mutable list to hold posts
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.communityRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        communityAdapter = CommunityAdapter(posts, database)
        recyclerView.adapter = communityAdapter

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

    fun savePostToFirebase(username: String, content: String) {
        val postId = database.child("posts").push().key ?: return
        val post = Post(postId, username, content, 0) // Create Post object

        database.child("posts").child(postId).setValue(post)
            .addOnSuccessListener {
                Toast.makeText(context, "Post saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving post: $e", Toast.LENGTH_SHORT).show()
            }
    }

    fun fetchPostsFromFirebase() {
        database.child("posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.clear() // Clear the list before adding new data
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { posts.add(it) }
                }
                communityAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showAddPostDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_post, null)
        val usernameInput = dialogView.findViewById<EditText>(R.id.usernameInput)
        val postContentInput = dialogView.findViewById<EditText>(R.id.postContentInput)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Add Post")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val username = usernameInput.text.toString()
                val content = postContentInput.text.toString()
                if (username.isNotBlank() && content.isNotBlank()) {
                    savePostToFirebase(username, content)
                } else {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


}
