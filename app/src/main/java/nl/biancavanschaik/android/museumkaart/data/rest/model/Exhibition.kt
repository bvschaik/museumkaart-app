package nl.biancavanschaik.android.museumkaart.data.rest.model

data class Exhibition(
        val id: Long,
        val permanentid: String,
        val museumid: Int,
        val type: Int,
        val startdate: String?,
        val enddate: String?,
        val name: String,
        val locationid: Int?,
        val description: String,
        val openinghours: String?,
        val admissionprice: String?,
        val website: String?,
        val path: String?,
        val city: String?
)