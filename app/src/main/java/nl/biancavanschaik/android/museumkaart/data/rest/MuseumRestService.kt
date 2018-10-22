package nl.biancavanschaik.android.museumkaart.data.rest

import androidx.lifecycle.LiveData
import nl.biancavanschaik.android.museumkaart.data.rest.model.RestMuseum
import nl.biancavanschaik.android.museumkaart.util.rest.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MuseumRestService {
    @GET("/?lat=52&lon=5")
    @Headers("Accept: application/json", "X-Requested-With: XMLHttpRequest")
    fun getList(@Query("page") page: Int): LiveData<ApiResponse<List<RestMuseum>>>

    @GET("/museum/{id}/")
    @Headers("Accept: application/json", "X-Requested-With: XMLHttpRequest")
    fun getDetails(@Path("id") museumId: String): LiveData<ApiResponse<RestMuseum>>
}