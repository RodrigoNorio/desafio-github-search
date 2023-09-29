package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

  lateinit var userName: EditText
  lateinit var btnConfirm: Button
  lateinit var repoList: RecyclerView
  lateinit var githubApi: GitHubService
  lateinit var imgNoConnection: ImageView
  lateinit var txtNoConnection: TextView
  lateinit var progressBar: ProgressBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupView()
    setupListeners()
    showUserName()
    setupRetrofit()
  }

  override fun onResume() {
    super.onResume()
    getAllReposByUserName()
  }

  private fun setupView() {
    userName = findViewById(R.id.et_user_name)
    btnConfirm = findViewById(R.id.btn_confirm)
    repoList = findViewById(R.id.rv_repo_list)
    imgNoConnection = findViewById(R.id.iv_no_connection)
    txtNoConnection = findViewById(R.id.tv_no_connection)
    progressBar = findViewById(R.id.pb_loader)
  }

  private fun setupListeners() {
    btnConfirm.setOnClickListener {
      saveUserLocal()
      clearFocus(userName)
    }
  }

  private fun clearFocus(view: View) {
    view.clearFocus()
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }
  private fun saveUserLocal() {
    val inputName = userName.text.toString()
    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return

    with (sharedPref.edit()) {
      putString(getString(R.string.saved_name), inputName)
      apply()
    }
  }

  private fun showUserName() {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    val savedName = sharedPref.getString(getString(R.string.saved_name), "")
    userName.setText(savedName)
  }

  private fun setupRetrofit() {
    val retrofit = Retrofit.Builder()
      .baseUrl("https://api.github.com/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    githubApi = retrofit.create(GitHubService::class.java)
  }

  private fun getAllReposByUserName() {
    val userName = userName.text.toString()
    githubApi.getAllRepositoriesByUser(userName).enqueue(object: Callback<List<Repository>> {
      override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
        if(response.isSuccessful) {
          progressBar.isVisible = false
          imgNoConnection.isVisible = false
          txtNoConnection.isVisible = false
          response.body()?.let {
            setupAdapter(it)
          }
        } else {
          Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
      }

      override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
        emptyState()
        Toast.makeText(this@MainActivity, "No connection", Toast.LENGTH_SHORT).show()
      }
    })
  }

  fun setupAdapter(list: List<Repository>) {
    repoList.isVisible = true
    val adapter = RepositoryAdapter(list)
    repoList.adapter = adapter
  }

  fun emptyState() {
    progressBar.isVisible = false
    repoList.isVisible = false
    imgNoConnection.isVisible = true
    txtNoConnection.isVisible = true
  }


  // Metodo responsavel por compartilhar o link do repositorio selecionado
  // @Todo 11 - Colocar esse metodo no click do share item do adapter
  fun shareRepositoryLink(urlRepository: String) {
    val sendIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, urlRepository)
      type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
  }

  // Metodo responsavel por abrir o browser com o link informado do repositorio

  // @Todo 12 - Colocar esse metodo no click item do adapter
  fun openBrowser(urlRepository: String) {
    startActivity(
      Intent(
        Intent.ACTION_VIEW,
        Uri.parse(urlRepository)
      )
    )

  }

}