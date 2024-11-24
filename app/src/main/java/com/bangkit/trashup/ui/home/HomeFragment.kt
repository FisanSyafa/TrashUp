package com.bangkit.trashup.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.trashup.databinding.FragmentHomeBinding
import com.bangkit.trashup.ui.ArticlesAdapter
import com.bangkit.trashup.ui.ViewModelFactory
import com.bangkit.trashup.data.remote.response.ListStoryItem
import com.bangkit.trashup.data.Result
import com.bangkit.trashup.ui.detail.DetailArticlesActivity
import com.bangkit.trashup.ui.analyze.UploadActivity


class HomeFragment : Fragment(), ArticlesAdapter.OnItemClickCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var storyAdapter: ArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]

        storyAdapter = ArticlesAdapter(this)

        binding.rvPopularTutor.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = storyAdapter
        }

        homeViewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data.let {
                        storyAdapter.submitList(it)
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.error == "Login first") {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        homeViewModel.getStories()
        binding.fabAnalyze.setOnClickListener {
            val intent = Intent(requireContext(), UploadActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(story: ListStoryItem) {
        val intent = Intent(requireContext(), DetailArticlesActivity::class.java)
        intent.putExtra(DetailArticlesActivity.EXTRA_ARTICLES, story)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
