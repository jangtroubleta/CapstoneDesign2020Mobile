package com.example.capstonedesign2020mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            val userId: String = userIdEditText.text.toString()
            var password: String = passwordEditText.text.toString()
            val myIp = EditIp()

            val url = "http://${myIp.ip}:8000/mobile/services/Login.php"
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
                    var dump: User? = null

                    println(list.result)
                    for (user in list.result) {
                        if (userId == user.userid) {
                            println(user.userid)
                            dump = user
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("id", dump.id)
                            intent.putExtra("userid", dump.userid)
                            intent.putExtra("name", dump.name)
                            intent.putExtra("port", dump.port)
                            intent.putExtra("expert_id", dump.expert_id)
                            intent.putExtra("myIp", myIp)
                            startActivity(intent)
                            finish()
                        }
                    }
                    if (dump == null) {
                        println("아이디 또는 비밀번호 틀렸습니다~~~~~~~")
                    }

                }
            })
        }
    }
    data class JsonObj(val result : List<User>)
    data class User (val id: String, val userid : String, val name : String, val port : String, val expert_id : String)
}
