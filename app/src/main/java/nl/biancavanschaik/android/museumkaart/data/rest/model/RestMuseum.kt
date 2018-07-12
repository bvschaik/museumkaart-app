package nl.biancavanschaik.android.museumkaart.data.rest.model

data class RestMuseum (
        val id: Long,
        val permanentid: String,
        val displayname: String,
        val telephone: String?,
        val email: String?,
        val website: String?,
        val museumcardparticipant: Boolean,
        val path: String?,
        val admissionprice: String?,
        val openinghours: String?,
        val streetandnumber: String?,
        val city: String?,
        val lat: Double?,
        val lon: Double?,
        val listings: Listings
)