package com.example.a20220629_noahdoperalski_nycschools.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a20220629_noahdoperalski_nycschools.model.Schools
import com.example.a20220629_noahdoperalski_nycschools.network.NetworkState
import com.example.a20220629_noahdoperalski_nycschools.network.RepositoryLayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolsViewModel @Inject constructor(
    private val repositoryLayer: RepositoryLayer,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    init {
        getAllSchools()
    }

    private val _schoolLivedata: MutableLiveData<NetworkState> = MutableLiveData(NetworkState.LOADING())
    val schoolsLV: LiveData<NetworkState> get() = _schoolLivedata

    private val _scoresLivedata: MutableLiveData<NetworkState> = MutableLiveData(NetworkState.LOADING())
    val scoresLV: LiveData<NetworkState> get() = _scoresLivedata

    var schools: Schools? = null

    private fun getAllSchools() {
        viewModelScope.launch(coroutineDispatcher) {
            repositoryLayer.getAllNycSchools().collect {
                _schoolLivedata.postValue(it)
            }
        }
    }

    fun getSATScoresBySchool() {
        schools?.let {
            it.dbn?.let { schoolId ->
                viewModelScope.launch(coroutineDispatcher) {
                    repositoryLayer.getSchoolSATScores(schoolId).collect { state ->
                        _scoresLivedata.postValue(state)
                    }
                }
            } ?: _scoresLivedata.postValue(NetworkState.ERROR(Exception("No dbn found")))
        } ?: _scoresLivedata.postValue(NetworkState.ERROR(Exception("No school set")))
    }

    fun setSchool(school: Schools)
    {
        schools = school
        _scoresLivedata.value = NetworkState.LOADING()
    }

}