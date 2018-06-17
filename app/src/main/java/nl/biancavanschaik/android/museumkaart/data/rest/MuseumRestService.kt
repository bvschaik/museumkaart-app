package nl.biancavanschaik.android.museumkaart.data.rest

import android.arch.lifecycle.LiveData
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.rest.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MuseumRestService {
    @GET("/museum/{id}/")
    @Headers("Accept: application/json", "X-Requested-With: XMLHttpRequest")
    fun getDetails(@Path("id") museumId: String): LiveData<ApiResponse<MuseumDetails>>
}