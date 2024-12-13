package com.example.smartinvest

object FirebaseConfigHelper {

    fun fetchApiKey(onComplete: (String?) -> Unit) {
        // Fetching and activating Remote Config
        FirebaseSingleton.remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Success - get the API key
                    val apiKey = FirebaseSingleton.remoteConfig.getString("api_key")
                    onComplete(apiKey)
                } else {
                    // Failed to fetch
                    onComplete(null)
                }
            }
    }
}
