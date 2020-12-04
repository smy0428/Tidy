package com.utap.tidy.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.utap.tidy.MainActivity
import com.utap.tidy.data.CleanJob
import com.utap.tidy.auth.FirestoreAuthLiveData
import com.utap.tidy.data.Membership
import com.utap.tidy.data.Team
import com.utap.tidy.data.User
import com.utap.tidy.test.Repository
import java.util.*
import kotlin.collections.HashMap

class MainViewModel: ViewModel() {

    val homeFragTag = "homeFragTag"
    val itemFragTag = "itemFragTag"
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private var areaListener : ListenerRegistration? = null
    private var itemListener:  ListenerRegistration? = null
    private lateinit var userID: String
    private lateinit var teamID: String
    private lateinit var areaID: String
    private lateinit var areaCleanJob: CleanJob
    private lateinit var itemCleanJob: CleanJob
    private var areaPosition = 0
    private var itemPosition = 0
    private var teamPosition = 0
    private var areaTitle = ""


    // 0 -> start, 1 -> new user, 2 -> user exists, -1 -> internet failure?
    private var userState = MutableLiveData<Int>().apply {
        value = 0
    }
    private var editAreaState = MutableLiveData<Int>().apply {
        value = 0
    }
    private var editItemState = MutableLiveData<Int>().apply {
        value = 0
    }
    private var openAreaState = MutableLiveData<Int>().apply {
        value = 0
    }
    private var teamSearchState = MutableLiveData<Int>().apply {
        value = 0
    }
    private var searchBarState = MutableLiveData<Int>().apply {
        value = 0
    }
    private var scoreFetchState = MutableLiveData<Int>().apply {
        value = 0
    }

    private var repository = Repository()
    private var title = MutableLiveData<String>()
    private var searchTeamLiveData = MutableLiveData<List<Team>>().apply {
        value = mutableListOf()
    }
    private var areaJobLivaData = MutableLiveData<List<CleanJob>>().apply {
        value = mutableListOf()
    }
    private var itemJobLivaData = MutableLiveData<List<CleanJob>>().apply {
        value = mutableListOf()
    }
    private var scoreDataMap = HashMap<String, MutableList<Pair<Timestamp, Long>>>()
    private var scoreSimpleMap = HashMap<String, Int>()

    companion object {
        const val USERS = "users"
        const val TEAMS = "teams"
        const val CURRENTTEAM = "currentTeam"
        const val MEMBERSHIP = "membership"
        const val AREACLEANJOB = "areaCleanJob"
        const val ITEMCLEANJOB = "itemCleanJob"
        const val LASTTIMEUPDATE = "lastUpdateTime"
        const val SCORE = "score"
        const val WORKDONE = "workDone"
        const val TAG = "MainViewModel"
    }

    init {
        Log.d(TAG, "XXX, MainViewModel initialized, userState ${userState.value}")
    }

    fun signOut() {
        areaListener?.remove()
        itemListener?.remove()
        FirebaseAuth.getInstance().signOut()
        areaJobLivaData.value = listOf()
    }

    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    fun observeTitle(): LiveData<String> {
        return title
    }

    // some observe function to share data with fragments
    internal fun observeAreaCleanJobs(): LiveData<List<CleanJob>> {
        return areaJobLivaData
    }

    fun observeFirebaseAuthLiveData(): LiveData<FirebaseUser?> {
        return firebaseAuthLiveData
    }

    private fun setTeamID(id: String) {
        teamID = id
    }

    private fun setUserState(state: Int) {
        userState.value = state
    }

    fun observeUserState(): LiveData<Int> {
        return userState
    }

    fun observeSearchBarState(): LiveData<Int> {
        return searchBarState
    }

    fun activateSearchBarState() {
        searchBarState.value = 1
    }

    fun deactivateSearchBarState() {
        searchBarState.value = 0
    }

    fun setAreaEditState() {
        editAreaState.value = 1
    }

    private fun setTeamSearchState() {
        teamSearchState.value = 1
    }

    fun clearAreaEditState() {
        editAreaState.value = 0
    }

    fun clearTeamSearchState() {
        teamSearchState.value = 0
    }

    fun observeAreaEditState(): LiveData<Int> {
        return editAreaState
    }

    fun observeTeamSearchState(): LiveData<Int> {
        return teamSearchState
    }

    fun initAreaCleanJob() {
        areaCleanJob = CleanJob()
    }
    fun setAreaName(name: String) {
        areaCleanJob.jobTitle = name
    }

    fun setAreaFreq(days: Int) {
        areaCleanJob.frequency = days
    }

    fun setAreaEffort(score: Int) {
        areaCleanJob.score = score
    }

    private fun getEditAreaJob(): CleanJob {
        return areaCleanJob
    }

    fun setAreaJobPos(position: Int) {
        areaPosition = position
    }

    fun setTeamPos(position: Int) {
        Log.d(TAG, "XXX, set team pos to $position")
        teamPosition = position
    }

    private fun getTeam(position: Int): Team {
        return searchTeamLiveData.value?.get(position)!!
    }

    fun getTeamName(): String {
        return getTeam(teamPosition).name!!
    }

    private fun getAreaJobPos(): Int {
        return areaPosition
    }

    fun getFinishAreaJob(): CleanJob {
        return getAreaCleanJob(getAreaJobPos())
    }

    private fun getAreaCleanJob(position: Int) : CleanJob {
        val cleanJob = areaJobLivaData.value?.get(position)
        Log.d(TAG, "XXX, get cleanJob ${cleanJob?.jobTitle}")
        Log.d(TAG, "XXX, get job $position list len ${areaJobLivaData.value?.size}")
        return cleanJob!!
    }

    fun searchTeam(term: String) {
        if (term.isEmpty()) return

        db.collection(TEAMS)
            .whereEqualTo("name", term)
            .get()
            .addOnSuccessListener { query ->
                searchTeamLiveData.value = listOf()
                if (query.size() == 0) {
                    Log.d(TAG, "XXX, successfully searched with term $term, but 0 result")
                    // in order to make a toast outside view model
                    setTeamSearchState()
                } else {
                    searchTeamLiveData.value = query.mapNotNull {
                        it.toObject(Team::class.java)
                    }
                    Log.d(TAG, "XXX, successfully searched with term $term, ${query.size()} result")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to search with term $term")
            }
    }

    fun observeSearchTeam(): LiveData<List<Team>> {
        return searchTeamLiveData
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // firestore fetch, update, save, remove area job
    //////////////////////////////////////////////////////////////////////////////////////////

    fun fetchAreaCleanJob() {
        if(FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "Can't get clean jobs, no one is logged in")
            areaJobLivaData.value = listOf()
            return
        }

        areaListener?.remove()
        val query =
            db.collection(TEAMS)
                .document(teamID)
                .collection(AREACLEANJOB)

        areaListener = query.addSnapshotListener { querySnapshot, ex ->
            if (ex != null) {
                Log.w(MainActivity.TAG, "listen:error", ex)
                return@addSnapshotListener
            }
            Log.d(TAG, "XXX, fetch ${querySnapshot!!.documents.size}")
            areaJobLivaData.value = querySnapshot.documents.mapNotNull {
                Log.d(TAG, "XXX, map query to live data, name ${it.data?.get("jobTitle")}")
                it.toObject(CleanJob::class.java)
            }
        }
    }

    fun saveAreaCleanJob() {
        areaCleanJob.rowID =
            db.collection(TEAMS)
                .document(teamID)
                .collection(AREACLEANJOB)
                .document()
                .id

        val timeStamp = Timestamp(Date())
        areaCleanJob.lastUpdateTime = timeStamp

        db.collection(TEAMS)
            .document(teamID)
            .collection(AREACLEANJOB)
            .document(areaCleanJob.rowID)
            .set(areaCleanJob)
            .addOnSuccessListener {
                fetchAreaCleanJob()
                Log.d(TAG, "XXX, successfully saved area clean job ${areaCleanJob.jobTitle}")
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to save area clean job ${areaCleanJob.jobTitle}")
            }
    }

    fun updateAreaCleanJob() {
        val position = getAreaJobPos()
        val cleanJobEdited = getEditAreaJob()
        val cleanJob = getAreaCleanJob(position)
        Log.d(TAG, "XXX, edited job title is ${cleanJobEdited.jobTitle}")
        cleanJob.jobTitle = cleanJobEdited.jobTitle
        cleanJob.frequency = cleanJobEdited.frequency
        cleanJob.score = cleanJobEdited.score

        val jobRowID = cleanJob.rowID
        Log.d(TAG, "XXX, update job rowID is $jobRowID")
        db.collection(TEAMS)
            .document(teamID)
            .collection(AREACLEANJOB)
            .document(jobRowID)
            .set(cleanJob)
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully updated area job id $jobRowID")
                fetchAreaCleanJob()
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to update area job id $jobRowID")
            }
    }

    fun finishAreaJob() {

        val timeStamp = Timestamp(Date())
        val areaJob = getAreaCleanJob(areaPosition)
        db.collection(TEAMS)
            .document(teamID)
            .collection(AREACLEANJOB)
            .document(areaJob.rowID)
            .update(LASTTIMEUPDATE, timeStamp)
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully updated area job time $timeStamp")
                fetchAreaCleanJob()
                addScore(areaJob)
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to update area job time $timeStamp")
            }
    }

    private fun addScore(cleanJob: CleanJob){
        val member = db.collection(TEAMS)
            .document(teamID)
            .collection(MEMBERSHIP)
            .document(userID)
        member.update(SCORE, FieldValue.increment(cleanJob.score!!.toLong()))
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully added ${cleanJob.score} score")
                    member
                        .collection(WORKDONE)
                        .add(cleanJob)
                        .addOnSuccessListener {
                            Log.d(TAG, "XXX, successfully added to work done")
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "XXX, failed to add to work done")
                        }
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to add ${cleanJob.score} score")
            }
    }

    fun removeAreaAt(position: Int) {
        val cleanJob = getAreaCleanJob(position)
        db.collection(TEAMS)
            .document(teamID)
            .collection(AREACLEANJOB)
            .document(cleanJob.rowID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully deleted to area clean job")
                fetchAreaCleanJob()
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to delete to area clean job")
            }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // new user check part
    //////////////////////////////////////////////////////////////////////////////////////////

    fun initNewTeam(name: String) {
        val newTeam = Team()
        newTeam.name = name
        val teamRef = db.collection(TEAMS).document()
        newTeam.rowID = teamRef.id

        teamRef.set(newTeam)
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully created a new team, uid ${teamRef.id}")
                setTeamID(teamRef.id)
                Log.d(TAG, "XXX, new team id is $teamID")
                setUserState(2)
                addNewMember(teamRef)
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to create a new team")
            }
    }

    private fun addNewMember(teamRef: DocumentReference) {
        val newMember = Membership()
        newMember.name = firebaseAuthLiveData.value!!.displayName
        newMember.score = 0
        teamRef
            .collection(MEMBERSHIP)
            .document(firebaseAuthLiveData.value!!.uid)
            .set(newMember)
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully created a new membership, id ${teamRef.id}")
                getAreaJobPos()
                db.collection(USERS)
                    .document(firebaseAuthLiveData.value!!.uid)
                    .update(CURRENTTEAM, teamRef.id)
                    .addOnSuccessListener {
                        Log.d(TAG, "XXX, successfully bound team_uid to user_uid")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "XXX, failed to bind team_uid to user_uid")
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to create a new membership")
            }
    }

    fun newUserCheck() {
        // reference to the user document
        userID = firebaseAuthLiveData.value!!.uid
        val userDocRef = db.collection(USERS).document(userID)
        userDocRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result!!.exists()){
                    val userDoc = it.result
                    val teamIDTemp = userDoc?.getString(CURRENTTEAM)
                    if (teamIDTemp == null) {
                        Log.d(TAG, "XXX, user document exists, but no team id")
                        setUserState(1)
                    } else {
                        teamID = teamIDTemp
                        Log.d(TAG, "XXX, user document exists, team id $teamID")
                        setUserState(2)
                    }
                } else {
                    // create user document from uid
                    val newUser = User()
                    userDocRef
                        .set(newUser)
                        .addOnSuccessListener { Log.d(TAG, "XXX, create user document, userID $userID") }
                        .addOnFailureListener { Log.d(TAG, "XXX, failed to create user document, userID $userID") }
                    setUserState(1)
                }
            } else {
                setUserState(-1)
                Log.d(TAG, "XXX, failed to get user document")
            }
        }
    }

    fun joinTeam() {
        //Log.d(TAG, "XXX, joinTeam fun teamPosition $teamPosition")
        teamID = getTeam(teamPosition).rowID!!
        val teamRef = db.collection(TEAMS).document(teamID)
        addNewMember(teamRef)
        setUserState(2)
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // item part
    //////////////////////////////////////////////////////////////////////////////////////////

    fun activateOpenAreaState() {
        openAreaState.value = 1
    }

    fun deactivateOpenAreaState() {
        // clear out the memory
        itemJobLivaData.value = listOf()
        openAreaState.value = 0
    }

    internal fun observeItemCleanJobs(): LiveData<List<CleanJob>> {
        return itemJobLivaData
    }

    fun observeOpenAreaState(): LiveData<Int> {
        return openAreaState
    }

    fun setAreaID() {
        val areaJob = getAreaCleanJob(areaPosition)
        areaID = areaJob.rowID
    }

    fun setAreaTitle() {
        val areaJob = getAreaCleanJob(areaPosition)
        areaTitle = areaJob.jobTitle!!
    }

    fun getAreaTitle(): String {
        return areaTitle
    }

    fun initItemCleanJob() {
        itemCleanJob = CleanJob()
    }
    fun setItemName(name: String) {
        itemCleanJob.jobTitle = name
    }

    fun setItemFreq(days: Int) {
        itemCleanJob.frequency = days
    }

    fun setItemEffort(score: Int) {
        itemCleanJob.score = score
    }

    private fun getEditItemJob(): CleanJob {
        return itemCleanJob
    }

    fun setItemJobPos(position: Int) {
        itemPosition = position
    }

    private fun getItemJobPos(): Int {
        return itemPosition
    }

    fun getFinishItemJob(): CleanJob {
        return getItemCleanJob(getItemJobPos())
    }

    private fun getItemCleanJob(position: Int) : CleanJob {
        val cleanJob = itemJobLivaData.value?.get(position)
        return cleanJob!!
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    // firestore fetch, update, save, remove item job
    //////////////////////////////////////////////////////////////////////////////////////////


    fun fetchItemCleanJob() {
        if(FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "Can't get clean jobs, no one is logged in")
            itemJobLivaData.value = listOf()
            return
        }

        itemListener?.remove()
        val query =
            db.collection(TEAMS)
                .document(teamID)
                .collection(AREACLEANJOB)
                .document(areaID)
                .collection(ITEMCLEANJOB)

        itemListener = query.addSnapshotListener { querySnapshot, ex ->
            if (ex != null) {
                Log.w(MainActivity.TAG, "listen:error", ex)
                return@addSnapshotListener
            }
            Log.d(TAG, "XXX, fetch ${querySnapshot!!.documents.size}")
            itemJobLivaData.value = querySnapshot.documents.mapNotNull {
                Log.d(TAG, "XXX, map query to live data, name ${it.data?.get("jobTitle")}")
                it.toObject(CleanJob::class.java)
            }
        }
    }

    fun saveItemCleanJob() {
        itemCleanJob.rowID =
            db.collection(TEAMS)
                .document(teamID)
                .collection(AREACLEANJOB)
                .document(areaID)
                .collection(ITEMCLEANJOB)
                .document()
                .id

        val timeStamp = Timestamp(Date())
        itemCleanJob.lastUpdateTime = timeStamp

        db.collection(TEAMS)
            .document(teamID)
            .collection(AREACLEANJOB)
            .document(areaID)
            .collection(ITEMCLEANJOB)
            .document(itemCleanJob.rowID)
            .set(itemCleanJob)
            .addOnSuccessListener {
                fetchItemCleanJob()
                Log.d(TAG, "XXX, successfully saved item clean job ${itemCleanJob.jobTitle}")
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to save item clean job ${itemCleanJob.jobTitle}")
            }
    }

    fun finishItemJob() {
        val timeStamp = Timestamp(Date())
        val itemJob = getItemCleanJob(areaPosition)
        db.collection(TEAMS)
            .document(teamID)
            .collection(AREACLEANJOB)
            .document(areaID)
            .collection(ITEMCLEANJOB)
            .document(itemJob.rowID)
            .update(LASTTIMEUPDATE, timeStamp)
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully updated item job time $timeStamp")
                fetchItemCleanJob()
                addScore(itemJob)
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to update item job time $timeStamp")
            }
    }

    fun removeItemAt(position: Int) {
        val cleanJob = getItemCleanJob(position)
        db.collection(TEAMS)
            .document(teamID)
            .collection(AREACLEANJOB)
            .document(areaID)
            .collection(ITEMCLEANJOB)
            .document(cleanJob.rowID)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "XXX, successfully deleted to area clean job")
                fetchItemCleanJob()
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to delete to area clean job")
            }
    }

    // has not worked out
    fun fetchScoreData() {
        db.collection(TEAMS)
            .document(teamID)
            .collection(MEMBERSHIP)
            .get()
            .addOnSuccessListener {query ->
                Log.d(TAG, "XXX, successfully everyone's score data")
                val count = query.size()
                Log.d(TAG, "XXX, num of members is $count")
                var i = 0
                query.documents.forEach { doc->
                    val name = doc.getString("name")!!
                    val rowID = doc.id
                    db.collection(TEAMS)
                        .document(teamID)
                        .collection(MEMBERSHIP)
                        .document(rowID)
                        .collection(WORKDONE)
                        .get()
                        .addOnSuccessListener {works ->
                            Log.d(TAG, "XXX, successfully fetched score data of $name")
                            val list = mutableListOf<Pair<Timestamp, Long>>()
                            works.documents.forEach {work ->
                                val pair = Pair(work.getTimestamp(LASTTIMEUPDATE)!!, work.get(SCORE) as Long)
                                list.add(pair)
                                if ( i + 1 == count) {
                                    // finish fetching all scores
                                    Log.d(TAG, "XXX, successfully finish fetching scores from $count persons")
                                    setScoreFetchState()
                                }
                            }
                            scoreDataMap[name] = list
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "XXX, failed to fetch score data of $name")
                        }
                    i += 1
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to fetch everyone's score data")
            }
        }

    fun fetchSimpleScore() {
        db.collection(TEAMS)
            .document(teamID)
            .collection(MEMBERSHIP)
            .get()
            .addOnSuccessListener { query ->
                Log.d(TAG, "XXX, successfully fetched everyone's score data")
                val count = query.size()
                Log.d(TAG, "XXX, num of members is $count")
                query.documents.forEach { doc ->
                    val name = doc.getString("name")!!
                    val score = doc.get(SCORE) as Long
                    scoreSimpleMap[name] = score.toInt()
                }
                Log.d(TAG, "XXX, finished fetching everyone's score data")
                setScoreFetchState()
            }
            .addOnFailureListener {
                Log.d(TAG, "XXX, failed to fetch everyone's score data")
            }

    }

    private fun setScoreFetchState() {
        scoreFetchState.value = 1
    }

    fun clearScoreFetchState() {
        scoreFetchState.value = 0
    }

    fun observeScoreFetchState(): LiveData<Int> {
        return scoreFetchState
    }

    fun getScoreDataMap(): HashMap<String, MutableList<Pair<Timestamp, Long>>> {
        return scoreDataMap
    }

    fun getScoreSimpleMap(): HashMap<String, Int> {
        return scoreSimpleMap
    }
}