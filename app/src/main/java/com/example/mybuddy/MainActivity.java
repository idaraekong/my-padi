package com.example.mybuddy;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private TextView count_result;
    private EditText word_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recycler_view_chat);
        chatAdapter = new ChatAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
        // Set the custom item decoration to add spacing between chat bubbles
        int spacingBetweenBubbles = getResources().getDimensionPixelSize(R.dimen.spacing_between_bubbles);
        recyclerView.addItemDecoration(new ChatItemDecoration(spacingBetweenBubbles));
        Button sendButton = findViewById(R.id.button_send);
        EditText inputEditText = findViewById(R.id.edit_text_input);
        sendButton.setOnClickListener(v -> {
            String userMessage = inputEditText.getText().toString().trim();
            //testing user input
            inputEditText.setText(userMessage);
            if (!userMessage.isEmpty()) {
                addMessageToChat(new ChatMessage(userMessage, true));
                // ask chat gpt
                askChatGpt(userMessage);
                inputEditText.setText("");
            }
        });

        count_result = (TextView) findViewById(R.id.layout_count);
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence inputEditText, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence inputEditText, int start, int before, int count) {
                int length = inputEditText.length();
                String convert = String.valueOf(length);
                count_result.setText(convert);
                // word_count.setText(String.valueOf(inputEditText.length()));
            }

            @Override
            public void afterTextChanged(Editable inputEditText) {
            }
        });


        //Check internet status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        ImageView network_status = (ImageView) findViewById(R.id.network_status);
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            //creating timer
            Timer timer_interact=new Timer();
            timer_interact.schedule(new TimerTask() {
                @Override
                public void run() {network_status.setBackgroundColor(Color.GREEN);}
            }, 3000);

        } else {
                 network_status.setBackgroundColor(Color.RED);
        }

    }




    // Method to add a new message to the chat list
    private void addMessageToChat(ChatMessage chatMessage) {
        chatAdapter.addMessage(chatMessage);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }
    // interact with chat gpt api
    private void askChatGpt(String userPrompt) {
        // Create the Retrofit client
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create();
        // Create the request model
        Message message = new Message("user", userPrompt);

        //add a prompt for AI Assistant
        String aiPrompt = "Your name na MyPadi, You be my padi and i want you to dey always respond in pidgin English. no ever answer for normal english. i dey try translate to pidgin users";
        Message assistantMessage = new Message("system",aiPrompt);
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        messageList.add(assistantMessage);
        OpenAIRequestModel requestModel = new OpenAIRequestModel("gpt-3.5-turbo", messageList, 0.7f);
        // Make the API request
        Call<OpenAIResponseModel> call = apiService.getCompletion(requestModel);
        call.enqueue(new Callback<OpenAIResponseModel>() {
            @Override
            public void onResponse(Call<OpenAIResponseModel> call, Response<OpenAIResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OpenAIResponseModel responseBody = response.body();
                    String generatedText = responseBody.getChoices()[0].getMessage().getContent();
                    addMessageToChat(new ChatMessage(generatedText, false));
                } else {
                    // Handle API error
                    addMessageToChat(new ChatMessage("API error", false));
                    //DEBUGGING
//                    addMessageToChat(new ChatMessage("FAILED API CALL "+response, false));
                }
            }
            @Override
            public void onFailure(Call<OpenAIResponseModel> call, Throwable t) {
                // Handle network or request failure
                addMessageToChat(new ChatMessage("API onFailure: "+t.getMessage(), false));
            }
        });
    }
}