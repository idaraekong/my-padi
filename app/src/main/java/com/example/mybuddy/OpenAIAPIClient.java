//public class OpenAIAPIClient {
//}

package com.example.mybuddy;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public class OpenAIAPIClient {
    private static final String BASE_URL = "https://api.openai.com/v1/";
    public interface OpenAIAPIService {
        @Headers("Authorization: Bearer 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")
        @POST("chat/completions")
        Call<OpenAIResponseModel> getCompletion(@Body OpenAIRequestModel requestModel);
    }
    public static OpenAIAPIService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(OpenAIAPIService.class);
    }
}
