package com.soten.remotemonster.viedeochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.soten.remotemonster.R
import com.soten.remotemonster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var array: MutableList<UserDTO> = arrayListOf()
    var uids: MutableList<String> = arrayListOf()
    private val myUid = FirebaseAuth.getInstance().uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener { task ->
            array.clear()
            uids.clear()
            for (item in task.result!!.documents) {
                if (myUid != item.id) {
                    array.add(item.toObject(UserDTO::class.java)!!)
                    uids.add(item.id)
                }
            }
            binding.peopleListRecyclerview.adapter?.notifyDataSetChanged()
        }
        binding.peopleListRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.peopleListRecyclerview.adapter = RecyclerviewAdapter()
        watchingMyUidVideoRequest()
    }

    private fun watchingMyUidVideoRequest() {

        FirebaseFirestore.getInstance().collection("users").document(myUid!!)
            .addSnapshotListener { value, error ->
                val userDTO = value?.toObject(UserDTO::class.java)
                if (userDTO?.channel != null) {
                    showJoinDialog(userDTO.channel!!)
                }
            }
    }

    private fun showJoinDialog(channel: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("${channel}방에 참여하시겠습니까?")
        builder.setPositiveButton("Yes") { _, _ ->
            openVideoActivity(channel)
            removeChannelStr()
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.create().show()
    }

    private fun removeChannelStr() {
        val map = mutableMapOf<String, Any>()
        map["channel"] = FieldValue.delete()
        FirebaseFirestore.getInstance().collection("users").document(myUid!!).update(map)
    }

    inner class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerviewAdapter.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return array.size
        }

        override fun onBindViewHolder(holder: RecyclerviewAdapter.ViewHolder, position: Int) {
            holder.itemEmail.text = array[position].email
            holder.itemView.setOnClickListener {
                val channelNumber = (1000..1000000).random().toString()
                openVideoActivity(channelNumber)
                createVideoChatRoom(position, channelNumber)
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemEmail = view.findViewById<TextView>(R.id.item_email)
        }

    }

    fun openVideoActivity(channelId: String) {
        val intent = Intent(this, VideoActivity::class.java)
        intent.putExtra("channelId", "myChannel")
        startActivity(intent)
    }

    fun createVideoChatRoom(position: Int, channel: String) {
        val map = mutableMapOf<String, Any>()
        map["channel"] = channel
        FirebaseFirestore.getInstance().collection("users").document(uids[position]).update(map)
    }

}