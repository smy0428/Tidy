package com.utap.tidy.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Team(
        var name: String? = null,
        @ServerTimestamp
        val timeStamp: Timestamp? = null,
        var rowID: String? = null
)

