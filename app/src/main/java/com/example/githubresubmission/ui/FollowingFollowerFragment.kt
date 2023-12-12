package com.example.githubresubmission.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubresubmission.adapter.UserAdapter
import com.example.githubresubmission.data.response.ItemsItem
import com.example.githubresubmission.databinding.FragmentFollowingFollowerBinding
import com.example.githubresubmission.viewmodel.MainViewModel

class FollowingFollowerFragment : Fragment() {

    private lateinit var binding : FragmentFollowingFollowerBinding

    companion object {
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingFollowerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]

        super.onViewCreated(view, savedInstanceState)
        arguments?.let { it ->
            val position = it.getInt(ARG_POSITION)
            val username = it.getString(ARG_USERNAME).toString()
            val userAdapter = UserAdapter()

            if (position == 1) {
                mainViewModel.followers(username)
                mainViewModel.listFollowers.observe(viewLifecycleOwner){
                    binding.rvFollower.layoutManager = LinearLayoutManager(requireActivity())
                    userAdapter.submitList(it)
                    binding.rvFollower.adapter = userAdapter
                }

            } else {
                mainViewModel.following(username)
                mainViewModel.listFollowing.observe(viewLifecycleOwner){
                    binding.rvFollower.layoutManager = LinearLayoutManager(requireActivity())
                    userAdapter.submitList(it)
                    binding.rvFollower.adapter = userAdapter
                }
            }
            userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
                override fun onItemClicked(data: ItemsItem) {
                    val intent = Intent(activity, UserDetailActivity::class.java)
                    intent.putExtra("user", data)
                    startActivity(intent)
                }
            })
        }
        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressbar.visibility = View.VISIBLE
        } else {
            binding.progressbar.visibility = View.GONE
        }
    }


}