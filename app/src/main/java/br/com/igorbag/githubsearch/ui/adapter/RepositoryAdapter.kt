package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
  RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

  var shareRepositoryLink: (String) -> Unit = {}
  var openBrowser: (String) -> Unit = {}

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.tvRepoName.text = repositories[position].name
    holder.imgShare.setOnClickListener {
      shareRepositoryLink(repositories[position].htmlUrl)
    }
    holder.cardView.setOnClickListener {
      openBrowser(repositories[position].htmlUrl)
    }
  }

  override fun getItemCount(): Int = repositories.size

  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvRepoName: TextView
    val cardView: CardView
    val imgShare: ImageView
    init {
      view.apply {
        tvRepoName = findViewById(R.id.tv_repo_name)
        cardView = findViewById(R.id.cv_repos)
        imgShare = findViewById(R.id.iv_share)
      }
    }
  }
}


