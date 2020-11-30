package com.utap.tidy.api

//import com.google.firebase.Timestamp
//import com.google.firebase.firestore.ServerTimestamp

data class CleanJob(
        var jobTitle: String? = null,
        var responsiblePerson: String? = null,
        // @ServerTimestamp val timeStamp: Timestamp? = null,
        var scores: String? = null,
        var frequency: String? = null,
        // rowID is generated by firestore, used as primary key
        var rowID: String = ""
)