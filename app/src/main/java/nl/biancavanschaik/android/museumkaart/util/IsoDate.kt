package nl.biancavanschaik.android.museumkaart.util

data class IsoDate(
        val year: Int,
        val month: Int,
        val day: Int
) {

    fun toIsoString() = String.format("%04d-%02d-%02d", year, month, day)
    fun toHumanString() = String.format("%02d-%02d-%04d", day, month, year)

    companion object {
        fun fromIsoString(value: String?): IsoDate? {
            return if (value == null) {
                null
            } else {
                val (year, month, day) = value.split("-", "T").take(3).map { it.toInt() }
                IsoDate(year, month, day)
            }
        }
    }
}

fun String.toHumanReadableDate() = IsoDate.fromIsoString(this)?.toHumanString()