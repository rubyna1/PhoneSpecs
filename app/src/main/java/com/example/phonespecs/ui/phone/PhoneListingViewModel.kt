package com.example.phonespecs.ui.phone

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.phonespecs.entity.Phones
import com.example.phonespecs.entity.SearchModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PhoneDataVM"

class PhoneDataViewModel @Inject constructor(
    app: Application, private val phoneDataRepository: PhoneListingRepository,
    private val compositeDisposable: CompositeDisposable,
) : AndroidViewModel(app) {
    var dataFromDb: LiveData<PagedList<Phones>>? = null
    var searchResponse = MutableLiveData<SearchModel>()

    fun fetchAllPhonesDataFromDb() {
        if (dataFromDb == null) {
            dataFromDb = phoneDataRepository.getAllPhonesDataFromDb()
        }
    }

    class PhonesBoundaryCallback(private val phoneDataRepository: PhoneListingRepository) :
        PagedList.BoundaryCallback<Phones>() {
        override fun onItemAtEndLoaded(itemAtEnd: Phones) {
            super.onItemAtEndLoaded(itemAtEnd)
            CoroutineScope(Dispatchers.IO).launch {
                val pageKey = phoneDataRepository.getTotalDataCount() / 40 + 1
                val pageNo = if (phoneDataRepository.getTotalDataCount() == 40) {
                    2
                } else {
                    pageKey
                }
                Log.i(TAG,"$pageKey $pageNo")
                phoneDataRepository.getPhoneDate(pageNo).subscribeOn(
                    Schedulers.io()
                ).observeOn(Schedulers.io()).subscribeBy(
                    onNext = {
                        it.data?.phones?.let { it1 -> phoneDataRepository.saveAllPhonesDataToDb(it1) }
                    },

                    onError = {
                        Log.i(TAG, "${it.message}")
                    }
                )
            }
        }

        override fun onZeroItemsLoaded() {
            super.onZeroItemsLoaded()
            CoroutineScope(Dispatchers.IO).launch {
                phoneDataRepository.getPhoneDate(1).subscribeOn(
                    Schedulers.io()
                ).observeOn(Schedulers.io()).subscribeBy(
                    onNext = {
                        it.data?.phones?.let { it1 -> phoneDataRepository.saveAllPhonesDataToDb(it1) }
                    },
                    onError = {
                        Log.i(TAG, "this is the error ${it.message}")
                    }
                )
            }
        }
    }

    fun search(query: String) {
        compositeDisposable.add(phoneDataRepository.search(query).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onNext = {
                    searchResponse.value = it
                },
                onError = {
                    it.printStackTrace()
                }
            ))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}