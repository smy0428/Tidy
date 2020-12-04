package com.utap.tidy.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class User(
        var currentTeam: String? = null,
        @ServerTimestamp
        val timeStamp: Timestamp? = null
)