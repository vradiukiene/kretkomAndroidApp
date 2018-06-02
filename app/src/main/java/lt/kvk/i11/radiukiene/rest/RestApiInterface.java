package lt.kvk.i11.radiukiene.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vita on 4/23/2018.
 */
   /*

http://localost:8080/streets
http://localhost:8080/wastecollections?street_id=1
http://localost:8080/wastes

 */

public interface RestApiInterface {

    // Get Streets
    @GET("streets")
    public Call<ResponseBody> getStreets();

    // Get TimeTable
    @GET("wastecollections")
    Call<ResponseBody> getTimetable(@Query("street_id") String street_id);

    // Get Wastes
    @GET("wastes")
    Call<ResponseBody> getWastes();

}
