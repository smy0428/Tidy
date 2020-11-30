package com.utap.tidy.test

import com.utap.tidy.api.CleanJob

class Repository {
    private var jobResources = hashMapOf(
        "Living Room" to CleanJob(
                "Living Room",
                "Eva",
                "50",
                "7",
                "12345678"
        ),
        "Bath Room" to CleanJob(
                    "Bath Room",
                    "Steele",
                    "50",
                    "7",
                    "12345687"
        )
    )

    fun fechData(): HashMap<String, CleanJob> {
        return jobResources
    }
}
