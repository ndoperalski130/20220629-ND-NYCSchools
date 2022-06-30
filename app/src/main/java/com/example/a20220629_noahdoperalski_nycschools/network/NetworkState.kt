package com.example.a20220629_noahdoperalski_nycschools.network

sealed class NetworkState {
    data class LOADING(val isLoading: Boolean = true): NetworkState()
    data class SUCCESS<T>(val response :T): NetworkState()
    data class ERROR(val error: Exception): NetworkState()
}
