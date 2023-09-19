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
        //@Headers("Authorization: Bearer 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")
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
