package com.aldebaran.simplechat.ui.room

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aldebaran.simplechat.R
import com.aldebaran.simplechat.databinding.ActivityMainBinding
import com.aldebaran.simplechat.goToActivity
import com.aldebaran.simplechat.ui.UpdateBottomDialog
import com.aldebaran.simplechat.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mAdapter: ChatAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecycler()
        viewClicked()
        subscribeVm()
        viewModel.streamDb()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_room, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_logout -> {
                viewModel.actionLogout()
                goToActivity<LoginActivity>()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeVm(){
        viewModel.chat.observe(this, Observer { messages ->
            mAdapter.setChatList(messages)
            binding.chatList.smoothScrollToPosition(0)
        })

        viewModel.bubbleChat.observe(this, Observer { chat ->
            val bottomSheet = UpdateBottomDialog(chat, viewModel)
            bottomSheet.show(supportFragmentManager, "")
        })
    }

    private fun viewClicked(){
        binding.btnSend.setOnClickListener {
            val message = binding.textBox.text.toString()
            if(message.isNotEmpty()){
                viewModel.sendMessage(message, viewModel.getUserName())
                binding.textBox.setText("")
            }
        }
    }

    private fun setupRecycler(){
        val linearManager = LinearLayoutManager(this)
        mAdapter = ChatAdapter(viewModel, viewModel.getUserName())
        binding.chatList.apply {
            linearManager.stackFromEnd = true
            layoutManager = linearManager
            adapter = mAdapter
        }
    }
}
