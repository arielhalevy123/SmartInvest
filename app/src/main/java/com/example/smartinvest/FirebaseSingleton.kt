package com.example.smartinvest

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig


object FirebaseSingleton {
    // עבור Realtime Database
    val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    // עבור Firestore
    val firestoreDatabase: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val remoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }



}
