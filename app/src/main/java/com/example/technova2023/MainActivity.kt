package com.example.technova2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val etSituation=findViewById<EditText>(R.id.etSituation)
        val btnSubmit=findViewById<Button>(R.id.btnSubmit)
        val txtResponse=findViewById<TextView>(R.id.txtResponse)


        fun getResponse(situation:String,callback: (String) -> Unit) {
            val apiKey = "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"
            val url = "https://api.openai.com/v1/completions"
            val requestBody = """
            {
              "model": "gpt-3.5-turbo-instruct",
              "prompt": "use the following prompt $situation to chose one of the following options of helpline numbers to call (Option 1) Health Services Helpline  (Option 2) suicide prevention helpline number (Option 3) regional police (Option 4) animal care helpline number (Option 5) regional fire department. just give a one digit answer prompt number only and not the full paragraph",
              "max_tokens": 7,
              "temperature": 0
            }""".trimIndent()

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e( "error","API failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body=response.body?.string()
                    if (body != null) {
                        Log.v("data", body)
                    }
                    else {
                        Log.v("data","empty")
                    }
                    val jsonObject= JSONObject (body)
                    val jsonArray:JSONArray=jsonObject.getJSONArray("choices")
                    val textResult=jsonArray.getJSONObject(0).getString("text")
                    val resultText = when (textResult.trim()) {
                        "1" -> "Ambulance"
                        "2" -> "Suicide Prevention Helpline number"
                        "3" -> "Regional Police"
                        "4" -> "Animal Care helpline"
                        "5" -> "Fire department"
                        else -> "Unknown"
                    }
                    callback(resultText)
                }
            })
        }

        btnSubmit.setOnClickListener {
            val situation=etSituation.text.toString()
            Toast.makeText( this, situation,Toast.LENGTH_SHORT).show()
            getResponse(situation) {response ->
                runOnUiThread {
                    txtResponse.text = response
                }
            }
        }

    }
}