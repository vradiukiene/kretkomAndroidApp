package lt.kvk.i11.radiukiene.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by Vita on 4/23/2018.
 */

public class RestApiClient {
    // public  static final String BASE_URL = "http://10.0.2.2:8080/";  localhost for Android
    public  static final String BASE_URL = "http://kretkomunal.herokuapp.com/";
    private  static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
