package com.example.phonespecs.ui.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.phonespecs.entity.SpecificationsModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private const val TAG = "DetailsPageVM"

class DetailsPageViewModel @Inject constructor(
    app: Application,
    private val detailsPageRepository: DetailsPageRepository,
    private val compositeDisposable: CompositeDisposable
) : AndroidViewModel(app) {
    val detailsResponse = MutableLiveData<SpecificationsModel>()
    fun getPhoneDetailsBySlug(slug: String) {
        compositeDisposable.add(
            detailsPageRepository.getPhoneDetailsBySlug(slug)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        Log.i(TAG, "$it")
                        detailsResponse.value = it
                    },
                    onError = {
                        Log.i(TAG, "${it.message}")
                        it.printStackTrace()
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}