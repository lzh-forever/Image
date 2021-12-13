package com.example.image.network

import retrofit2.http.GET

interface TestService {
    @GET(Api.TEST_URL)
    suspend fun test() :Response
}