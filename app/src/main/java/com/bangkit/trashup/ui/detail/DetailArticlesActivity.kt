package com.bangkit.trashup.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bangkit.trashup.databinding.ActivityDetailArticlesBinding
import com.bangkit.trashup.data.remote.response.ListStoryItem

class DetailArticlesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailArticlesBinding

    companion object {
        const val EXTRA_ARTICLES = "extra_articles"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val ivDetailPhoto = binding.ivDetailPhoto
        val tvDetailName = binding.tvDetailName
        val tvDetailDescription = binding.tvDetailDescription

        val story: ListStoryItem? = if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_ARTICLES, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_ARTICLES)
        }

        if (story != null) {

            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            Glide.with(this)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
