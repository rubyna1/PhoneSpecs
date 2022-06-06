package com.example.phonespecs.network

import com.example.phonespecs.entity.PhoneModel
import com.example.phonespecs.entity.SearchModel
import com.example.phonespecs.entity.SpecificationsModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("brands/nokia-phones-1")
    fun getDataByBrand(
        @Query("page") page: Int
    ): Observable<PhoneModel>

    @GET("{phone_slug}")
    fun getPhoneSpecifications(
        @Path("phone_slug") phone_slug: String
    ): Observable<SpecificationsModel>

    @GET("search")
    fun search(@Query("query") query: String): Observable<SearchModel>
}