package com.example.phonespecs.ui.details

import com.example.phonespecs.database.AppDatabase
import com.example.phonespecs.network.ApiService
import javax.inject.Inject

class DetailsPageRepository @Inject constructor(val database: AppDatabase,val apiService: ApiService) {
    fun getPhoneDetailsById(id:Int) = database.phoneDataDao().getPhoneDetailsById(id)
    fun getPhoneDetailsBySlug(slug:String) = apiService.getPhoneSpecifications(slug)
}