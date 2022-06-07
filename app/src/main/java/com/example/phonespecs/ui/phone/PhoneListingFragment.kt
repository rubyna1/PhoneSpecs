package com.example.phonespecs.ui.phone

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonespecs.R
import com.example.phonespecs.entity.Phones
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_phone_listing.*
import javax.inject.Inject

class PhoneListingFragment : DaggerFragment(), ItemPhoneDataAdapter.OnItemCallbacks,
    ItemSearchAdapter.OnItemCallbacks {
    @Inject
    lateinit var phoneDataViewModel: PhoneDataViewModel
    private lateinit var adapter: ItemPhoneDataAdapter
    private lateinit var searchAdapter: ItemSearchAdapter
    private lateinit var listener: NavigationInterface
    private val listOfData = mutableListOf<Phones>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_phone_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        adapter = ItemPhoneDataAdapter(requireContext(), this)
        fragment_phone_listing_recycler_view.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragment_phone_listing_recycler_view.adapter = adapter
        searchAdapter = ItemSearchAdapter(requireContext(), listOfData, this)
        fragment_phone_listing_search_recycler_view.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragment_phone_listing_search_recycler_view.adapter = searchAdapter
        fragment_phone_listing_search_recycler_view.setItemViewCacheSize(listOfData.size)
        phoneDataViewModel.fetchAllPhonesDataFromDb()
        phoneDataViewModel.dataFromDb?.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
                fragment_phone_listing_progress_bar.visibility = View.GONE
                fragment_phone_listing_recycler_view.visibility = View.VISIBLE
                fragment_phone_listing_search_recycler_view.visibility = View.GONE
                fragment_phone_listing_error_view.visibility = View.GONE
            }
        })
        fragment_phone_listing_search_edit_text.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard()
                    phoneDataViewModel.search(fragment_phone_listing_search_edit_text.text.toString())
                    fragment_phone_listing_progress_bar.visibility = View.VISIBLE
                    fragment_phone_listing_back_image_view.visibility = View.VISIBLE
                    fragment_phone_listing_search_recycler_view.visibility = View.GONE
                    fragment_phone_listing_error_view.visibility = View.GONE
                    fragment_phone_listing_recycler_view.visibility = View.GONE
                    return true
                }
                return false
            }
        })
        fragment_phone_listing_back_image_view.setOnClickListener {
            fragment_phone_listing_search_edit_text.text.clear()
            fragment_phone_listing_search_edit_text.clearFocus()
            fragment_phone_listing_progress_bar.visibility = View.GONE
            fragment_phone_listing_recycler_view.visibility = View.VISIBLE
            fragment_phone_listing_back_image_view.visibility = View.GONE
            fragment_phone_listing_search_recycler_view.visibility = View.GONE
            fragment_phone_listing_error_view.visibility = View.GONE
        }
        phoneDataViewModel.searchResponse.observe(viewLifecycleOwner, Observer {
            if (it.data != null && it.data!!.phones.isNotEmpty()) {
                listOfData.clear()
                listOfData.addAll(it.data!!.phones)
                searchAdapter.notifyDataSetChanged()
                fragment_phone_listing_progress_bar.visibility = View.GONE
                fragment_phone_listing_recycler_view.visibility = View.GONE
                fragment_phone_listing_error_view.visibility = View.GONE
                fragment_phone_listing_back_image_view.visibility = View.VISIBLE
                fragment_phone_listing_search_recycler_view.visibility = View.VISIBLE
            } else {
                fragment_phone_listing_back_image_view.visibility = View.VISIBLE
                fragment_phone_listing_progress_bar.visibility = View.GONE
                fragment_phone_listing_recycler_view.visibility = View.GONE
                fragment_phone_listing_error_view.visibility = View.VISIBLE
                fragment_phone_listing_error_view.text = getString(R.string.noSuchItem)
                fragment_phone_listing_search_recycler_view.visibility = View.GONE
            }
        })
    }

    fun hideSoftKeyboard() {
        try {
            val inputMethodManager =
                activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): PhoneListingFragment = PhoneListingFragment()
    }

    override fun onItemClicked(phoneSlug: String) {
        fragment_phone_listing_search_edit_text.clearFocus()
        listener.onPhoneItemClicked(phoneSlug)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as NavigationInterface
    }

    interface NavigationInterface {
        fun onPhoneItemClicked(phoneSlug: String)
    }
}