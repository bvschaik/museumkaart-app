package nl.biancavanschaik.android.museumkaart.data.rest.model

data class Listings(
        val promotion: List<Exhibition>,
        val event: List<Exhibition>,
        val exhibition: List<Exhibition>,
        val permanent: List<Exhibition>
)