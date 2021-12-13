package com.example.image

import com.example.image.network.Network
import kotlinx.coroutines.flow.map


object Repository {

    suspend fun getTest() = Network.test()

}