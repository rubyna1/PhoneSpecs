package com.example.phonespecs.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.phonespecs.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_details.*
import javax.inject.Inject

private const val SLUG = "slug"

class DetailsFragment : DaggerFragment() {
    @Inject
    lateinit var detailsPageViewModel: DetailsPageViewModel
    private var slug: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            slug = it.getString(SLUG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
//        val details = id?.let { detailsPageViewModel.getPhoneDetailsById(it) }
//        details?.observe(viewLifecycleOwner, Observer {
//            Log.i("DetailsFragment", "$it")
//        })
        fragment_detail_page_back_image_view.setOnClickListener {
            requireActivity().onBackPressed()
        }
        slug?.let { detailsPageViewModel.getPhoneDetailsBySlug(it) }
        detailsPageViewModel.detailsResponse.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                fragment_details_main_view.visibility = View.VISIBLE
                fragment_detail_page_progress_bar.visibility = View.GONE
                fragment_detail_page_error_view.visibility = View.GONE
                Glide.with(requireContext()).load(it.data!!.thumbnail)
                    .into(fragment_detail_page_image_view)
                fragment_detail_page_name_text_view.text = slug
                fragment_detail_page_release_date_text_view.text = it.data?.releaseDate
                fragment_detail_page_dimension_text_view.text = it.data?.dimension
                fragment_detail_page_os_text_view.text = it.data?.os
                fragment_detail_page_storage_text_view.text = it.data?.storage
            } else {
                fragment_details_main_view.visibility = View.GONE
                fragment_detail_page_progress_bar.visibility = View.GONE
                fragment_detail_page_error_view.visibility = View.VISIBLE
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(SLUG, param1)
                }
            }
    }
}