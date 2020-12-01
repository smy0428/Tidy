package com.utap.tidy.test

import com.utap.tidy.data.CleanJob

class Repository {
    private var jobResources = hashMapOf(
        "Living Room" to CleanJob(
                "Living Room",
                "Eva",
                null,
                "50",
                "7",
                "12345678"
        ),
        "Bath Room" to CleanJob(
                "Bath Room",
                "Steele",
                null,
                "50",
                "7",
                "12345687"
        )
    )

    fun fechData(): HashMap<String, CleanJob> {
        return jobResources
    }
}
