package com.utap.tidy.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.utap.tidy.data.CleanJob
import com.utap.tidy.auth.FirestoreAuthLiveData
import com.utap.tidy.data.Membership
import com.utap.tidy.data.Team
import com.utap.tidy.data.User
import com.utap.tidy.test.Repository

class MainViewModel: ViewModel() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    companion object {
        const val USERS = "users"
        const val TEAMS = "teams"
        const val CURRENTTEAM = "current_team"
        const val MEMBERSHIP = "membership"
        const val TAG = "MainViewModel"
    }

    private var title = MutableLiveData<String>()
    private var repository = Repository()
    private var areaCleanJobs = MutableLiveData<List<CleanJob>>().apply {
        value = mutableListOf()
    }

    init {
        Log.d(TAG, "XXX, MainViewModel has been initialized.")
        areaCleanJobs.value = repository.fechData().values.toList()
    }

    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    fun observeTitle(): LiveData<String> {
        return title
    }

    // some observe function to share data with fragments
    internal fun observeAreaCleanJobs(): LiveData<List<CleanJob>> {
        return areaCleanJobs
    }

    fun observeFirebaseAuthLiveData(): LiveData<FirebaseUser?> {
        return firebaseAuthLiveData
    }

    fun initNewTeam(newName: String) {
        val newTeam = Team()
        val teamRef = db.collection(TEAMS).document()

        teamRef.set(newTeam)
                .addOnSuccessListener {
                    Log.d(TAG, "XXX, create a new team, uid ${teamRef.id}")
                    val newMember = Membership()
                    newMember.name = firebaseAuthLiveData.value!!.displayName
                    newMember.score = 0
                    teamRef.collection(MEMBERSHIP).document(firebaseAuthLiveData.value!!.uid)
                            .set(newMember)
                            .addOnSuccessListener {
                                Log.d(TAG, "XXX, create a new membership, id ${teamRef.id}")
                                db.collection(USERS).document(firebaseAuthLiveData.value!!.uid)
                                        .update(CURRENTTEAM, teamRef.id)
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "XXX, failed to create a new member")
                            }
                }
                .addOnFailureListener {
                    Log.d(TAG, "XXX, failed to create a new team")
                }
    }

    fun newUserCheck(): Boolean {
        // reference to the user document
        val uid = firebaseAuthLiveData.value!!.uid
        val userDocRef = db.collection(USERS).document(uid)
        var result = true
        userDocRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result!!.exists()){
                    Log.d(TAG, "XXX, user document exists")
                } else {
                    // create user document from uid
                    val newUser = User()
                    result = false
                    userDocRef
                            .set(newUser)
                            .addOnSuccessListener { Log.d(TAG, "XXX, create user document, uid $uid") }
                            .addOnFailureListener { Log.d(TAG, "XXX, failed to create user document, uid $uid") }
                }
            } else {
                Log.d(TAG, "XXX, failed to get user document")
            }
        }
        return false
    }
}