package com.utap.tidy.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.utap.tidy.MainActivity

class AuthInitActivity : AppCompatActivity() {
    companion object {
        val rcSignIn = 17
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // NB: Just grab global FirebaseAuth
        if (FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "user is null!")

            // Create and launch sign-in intent
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false) //added
                    .build(),
                rcSignIn
            )
        } else {
            Log.d(javaClass.simpleName, "user ${FirebaseAuth.getInstance().currentUser?.displayName} email ${FirebaseAuth.getInstance().currentUser?.email}")
            // Only if we start the AuthUI activity to we need to continue
            finish()
        }
    }
    // Returns if our work is done and activity should finish
    private fun setDisplayNameByEmail(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        if( user == null ) {
            Log.d("AuthInitActivity","XXX, setDisplayNameByEmail current user null")
        } else if( user.displayName == null || user.displayName!!.isEmpty() ) {
            user.apply {
                val displayName = this.email?.substringBefore("@")
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                if (displayName != null && displayName.isEmpty()) {
                    this.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(javaClass.simpleName, "User profile updated.")
                            }
                            // Now we are done
                            finish()
                        }
                    return false
                } else {
                    Log.d("AuthInitActivity", "XXX displayName $displayName")
                }
            }
        } else {
            Log.d(javaClass.simpleName, "displayName set to ${user.displayName}")
        }
        return true
    }
    // If we need to log in, activity puts us here.  Do what we need to and finish(),
    // unless we have another callback (in setDisplayNameByEmail)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcSignIn) {
            val response = IdpResponse.fromResultIntent(data)

            Log.d(javaClass.simpleName, "activity result $resultCode")
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                if( setDisplayNameByEmail() ) {
                    finish()
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.d(MainActivity.TAG, "Error signing in", response?.error)
                finish()
            }
        }
    }
}