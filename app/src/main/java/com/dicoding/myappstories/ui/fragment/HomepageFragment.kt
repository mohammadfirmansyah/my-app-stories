package com.dicoding.myappstories.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.myappstories.R
import com.dicoding.myappstories.databinding.FragmentHomepageBinding
import com.dicoding.myappstories.db.model.Stories
import com.dicoding.myappstories.ui.adapter.StoriesAdapter
import com.dicoding.myappstories.ui.viewmodel.HomepageViewModel
import com.dicoding.myappstories.utilities.SessionManager
import com.dicoding.myappstories.utilities.animateVisibility
import dagger.hilt.android.AndroidEntryPoint

@Suppress("SameParameterValue")
@AndroidEntryPoint
@ExperimentalPagingApi
class HomepageFragment : Fragment() {

    private var _binding: FragmentHomepageBinding? = null
    private val binding get() = _binding
    private val homepageViewModel: HomepageViewModel by viewModels()
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var storyRecyclerView: RecyclerView
    private lateinit var token: String
    private lateinit var pref: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomepageBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SessionManager(requireContext())
        token = pref.fetchAuthToken().toString()

        initAction()
        initSwipeRefreshLayout()
        initStoryRecyclerView()
        getAllStories()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAction() {
        binding.apply {
            binding?.btnAccount?.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_profileFragment)
            )
        }
    }

    private fun getAllStories() {
        homepageViewModel.getAllStories(token).observe(viewLifecycleOwner) { result ->
            setRecyclerViewData(result)
        }
    }

    private fun initSwipeRefreshLayout() {
        binding?.swipeRefresh?.setOnRefreshListener {
            getAllStories()
        }
    }

    private fun initStoryRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        storiesAdapter = StoriesAdapter()

        storiesAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && storiesAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                binding?.apply {
                    tvNotFoundError.animateVisibility(true)
                    ivNotFoundError.animateVisibility(true)
                    rvStories.animateVisibility(false)
                }
            } else {
                binding?.apply {
                    tvNotFoundError.animateVisibility(false)
                    ivNotFoundError.animateVisibility(false)
                    isLoading(false)
                    rvStories.animateVisibility(true)
                }
            }
            binding?.swipeRefresh?.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            storyRecyclerView = binding?.rvStories!!
            storyRecyclerView.apply {
                adapter = storiesAdapter
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun setRecyclerViewData(stories: PagingData<Stories>) {
        val rvState = storyRecyclerView.layoutManager?.onSaveInstanceState()
        storiesAdapter.submitData(lifecycle, stories)
        storyRecyclerView.layoutManager?.onRestoreInstanceState(rvState)
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.apply {
                this!!.shimmerLoading.visibility = View.VISIBLE
                shimmerLoading.startShimmer()
                rvStories.visibility = View.INVISIBLE
            }
        } else {
            binding.apply {
                this!!.rvStories.visibility = View.VISIBLE
                shimmerLoading.stopShimmer()
                shimmerLoading.visibility = View.INVISIBLE
            }
        }
    }

}