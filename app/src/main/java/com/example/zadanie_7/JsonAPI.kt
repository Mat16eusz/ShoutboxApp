package com.example.zadanie_7

import retrofit2.Call
import retrofit2.http.*

interface JsonAPI {
    companion object {
        const val BASE_URL = "http://tgryl.pl"
    }

    @GET("/shoutbox/messages")
    fun getComments(): Call<List<FetchedComment>>

    @POST("/shoutbox/message")
    fun addComment(@Body body: AddComment): Call<FetchedComment>

    @FormUrlEncoded
    @PUT("/shoutbox/message/{id}")
    fun editComment(@Path("id") id:String, @Field("login") login:String, @Field("content") content:String): Call<Void>

    @DELETE("/shoutbox/message/{id}")
    fun deleteComment(@Path("id") id:String): Call<Void>
}