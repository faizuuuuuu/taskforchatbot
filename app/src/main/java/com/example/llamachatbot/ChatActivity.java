package com.example.llamachatbot;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList;
    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Configure Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")  // Use your backend IP
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)      // ← Increased from 20 to 60
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        chatService = retrofit.create(ChatService.class);

        sendButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                messageList.add(new Message(text, Message.USER));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                messageInput.setText("");

                chatService.sendMessage(text).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String reply = response.body().trim();
                            Log.d("ChatActivity", "Bot response: " + reply);
                            messageList.add(new Message(reply, Message.BOT));
                        } else {
                            Log.e("ChatActivity", "Empty or unsuccessful response");
                            messageList.add(new Message("No reply from bot.", Message.BOT));
                        }
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("ChatActivity", "API call failed", t);
                        Toast.makeText(ChatActivity.this, "Bot is offline. Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        messageList.add(new Message("Bot is offline. Try again later.", Message.BOT));
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
            }
        });
    }
}
