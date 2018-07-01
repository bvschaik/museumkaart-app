package nl.biancavanschaik.android.museumkaart.util

import java.util.Calendar

data class IsoDate(
        val year: Int,
        val month: Int,
        val day: Int
): Comparable<IsoDate> {

    fun toIsoString() = String.format("%04d-%02d-%02d", year, month, day)
    fun toHumanString() = String.format("%02d-%02d-%04d", day, month, year)

    override fun compareTo(other: IsoDate): Int {
        if (year != other.year) return year - other.year
        if (month != other.month) return month - other.month
        return day - other.day
    }

    companion object {
        fun fromIsoString(value: String?): IsoDate? {
            return if (value == null) {
                null
            } else {
                val (year, month, day) = value.split("-", "T").take(3).map { it.toInt() }
                IsoDate(year, month, day)
            }
        }

        fun today(): IsoDate {
            val now = Calendar.getInstance()
            return IsoDate(
                    year = now.get(Calendar.YEAR),
                    month = now.get(Calendar.MONTH) + 1,
                    day = now.get(Calendar.DAY_OF_MONTH)
            )
        }
    }
}
