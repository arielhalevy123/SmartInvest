package com.example.smartinvest

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {


    lateinit var mainlayout: FrameLayout
    lateinit var registerframelayout: FrameLayout
    lateinit var emailInput: TextInputLayout
    lateinit var passwordInput: TextInputLayout
    lateinit var registerFinalButton: MaterialButton
    lateinit var loginButton: MaterialButton
    lateinit var registerButton: MaterialButton
    lateinit var loginFrameLayout: FrameLayout
    lateinit var loginConfirmButton: MaterialButton
    lateinit var loginEmailInput: TextInputLayout
    lateinit var loginPasswordInput: TextInputLayout
    private lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth
    override fun onStart(){
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun reload() {
        goToInvestActivity()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainlayout = findViewById(R.id.mainFrameLayout)
        loginButton = findViewById(R.id.logInBtn)
        registerButton = findViewById(R.id.regBtn)
        registerframelayout=findViewById(R.id.registerFrameLayout)
        auth = Firebase.auth
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        registerFinalButton = findViewById(R.id.buttonRegisterFinal)
        loginFrameLayout = findViewById(R.id.loginFrameLayout)
        loginEmailInput = findViewById(R.id.loginEmailInput)
        loginPasswordInput = findViewById(R.id.loginPasswordInput)
        loginConfirmButton = findViewById(R.id.loginConfirmButton)

        registerFinalButton.setOnClickListener {
            val email = emailInput.editText?.text.toString()
            val password = passwordInput.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // קריאה לפונקציה לרישום
                registerUser(email, password)
            }
        }
        loginConfirmButton.setOnClickListener {
            val email = loginEmailInput.editText?.text.toString()
            val password = loginPasswordInput.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                logInUser(email, password)
            }
        }

    }

    fun register(view: View) {
        mainlayout.visibility = View.INVISIBLE
        registerframelayout.visibility = View.VISIBLE
        loginFrameLayout.visibility = View.INVISIBLE
    }

    fun logIn(view: View) {
        mainlayout.visibility = View.INVISIBLE
        registerframelayout.visibility = View.INVISIBLE
        loginFrameLayout.visibility = View.VISIBLE
    }
    fun backToMain(view: View){
        mainlayout.visibility = View.VISIBLE;
        registerframelayout.visibility = View.INVISIBLE
        loginFrameLayout.visibility = View.INVISIBLE
    }

    private fun registerUser(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    // מעבר למסך הבא
                    goToInvestActivity()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun logInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Log In Successful!", Toast.LENGTH_SHORT).show()
                    goToInvestActivity()
                } else {
                    Toast.makeText(
                        this,
                        "Log In Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun goToInvestActivity() {
        val intent = Intent(this, InvestActivity::class.java)
        startActivity(intent)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("mainLayoutVisibility", mainlayout.visibility)
        outState.putInt("registerLayoutVisibility", registerframelayout.visibility)
        outState.putInt("loginLayoutVisibility", loginFrameLayout.visibility)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // טען את המצבים שנשמרו
        mainlayout.visibility = savedInstanceState.getInt("mainLayoutVisibility")
        registerframelayout.visibility = savedInstanceState.getInt("registerLayoutVisibility")
        loginFrameLayout.visibility = savedInstanceState.getInt("loginLayoutVisibility")
    }






}