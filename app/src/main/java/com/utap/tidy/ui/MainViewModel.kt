package com.utap.tidy.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.utap.tidy.api.CleanJob
import com.utap.tidy.test.Repository

class MainViewModel: ViewModel() {
    private var title = MutableLiveData<String>()
    private var repository = Repository()
    private var areaCleanJobs = MutableLiveData<List<CleanJob>>().apply {
        value = mutableListOf()
    }

    init {
        Log.d("XXX", "MainViewModel has been initialized.")
        areaCleanJobs.value = repository.fechData().values.toList()
    }

    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    // some observe function to share data with fragments
    internal fun observeAreaCleanJobs(): LiveData<List<CleanJob>> {
        return areaCleanJobs
    }

}