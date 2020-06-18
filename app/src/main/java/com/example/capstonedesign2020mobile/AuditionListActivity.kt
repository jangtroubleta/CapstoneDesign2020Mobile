package com.example.capstonedesign2020mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstonedesign2020mobile.databinding.ItemAuditionBinding
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_audition_list.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.textView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.nav_name
import kotlinx.android.synthetic.main.activity_main.nav_spinner
import kotlinx.android.synthetic.main.item_audition.view.*
import okhttp3.*
import java.io.IOException

class AuditionListActivity : AppCompatActivity() {

    var list_of_items = arrayOf("마이페이지", "로그아웃")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audition_list)

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        nav_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        nav_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 1) {
                    val intent = Intent(this@AuditionListActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        val id = intent.getStringExtra("id")
        val userid = intent.getStringExtra("userid").toString()
        val name = intent.getStringExtra("name").toString()
        val audlist = intent.getSerializableExtra("audlist") as ArrayList<Audition>
        val myIp = intent.getSerializableExtra("myIp") as EditIp
//        val mediaContentList = intent.getSerializableExtra("mediaContentList") as ArrayList<one_room_message>

        nav_name.text = name + " 님"
        textView.text = "$name 님께서 등록하신 오디션입니다."

//        val audlist = arrayListOf<Audition>()
//        for (i in 0..20) {
//            audlist.add(Audition("1", "바보$i", "10일남음"))
//        }

        audRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AuditionListActivity)
            adapter = AuditionAdapter(audlist) {audition ->
                println("-----------------------------------------------------")
                println(audition.id)
                var id = audition.id
                val url = "http://${myIp.ip}:8000/mobile/services/AuditionDetail.php?id=$id"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("실패함~~~~~~~~~~~~~~~~~~~~~~~")
                        println(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        println("성공함~~~~~~~~~~~~~~~~~~")
                        val body = response?.body()?.string()
                        //Gson으로 파싱
                        val gson = GsonBuilder().create()
                        val list = gson.fromJson(body, JsonObj::class.java)

                        println(list.result)
                        println("----------------------")
                        val intent = Intent(this@AuditionListActivity, AuditionDetailActivity::class.java)
                        intent.putExtra("id", list.result[0].id)
                        intent.putExtra("title", list.result[0].title)
                        intent.putExtra("aud_image", list.result[0].aud_image)
                        intent.putExtra("rank", list.result[0].rank)
                        intent.putExtra("end_date", list.result[0].end_date)
                        intent.putExtra("name", list.result[0].name)
                        intent.putExtra("call_number", list.result[0].call_number)
                        intent.putExtra("company_name", list.result[0].company_name)
                        intent.putExtra("address", list.result[0].address)
                        intent.putExtra("content", list.result[0].content)
                        intent.putExtra("video", list.result[0].video)
                        intent.putExtra("myIp", myIp)
                        startActivity(intent)

                    }
                })

//                val intent = Intent(this@AuditionListActivity, AuditionDetailActivity::class.java)
//                startActivity(intent)
            }

        }

    }
    data class JsonObj(val result : List<AuditionDetail>)
}

class AuditionAdapter(val items: List<Audition>, private val clickListener: (audition: Audition)-> Unit) : RecyclerView.Adapter<AuditionAdapter.AuditionViewHolder>() {
    class AuditionViewHolder(val binding: ItemAuditionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audition, parent, false)
        val viewHolder = AuditionViewHolder(ItemAuditionBinding.bind(view))

        view.setOnClickListener{
            clickListener.invoke(items[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AuditionViewHolder, position: Int) {
        val myIp = EditIp()
        Glide.with(holder.itemView).load("http://${myIp.ip}:8000/storage/${items[position].aud_image}")
            .into(holder.itemView.aud_list_image)
        holder.binding.audition = items[position]
    }
}