package nl.biancavanschaik.android.museumkaart.util

import org.junit.Assert.*
import org.junit.Test

class IsoDateTest {
    @Test
    fun `iso date can be parsed from date string`() {
        val date = IsoDate.fromIsoString("2018-01-31")
        assertEquals(IsoDate(2018, 1, 31), date)
    }

    @Test
    fun `iso date can be parsed from date time string`() {
        val date = IsoDate.fromIsoString("2018-01-31T01:23:45")
        assertEquals(IsoDate(2018, 1, 31), date)
    }

    @Test
    fun `date with smaller year is smaller`() {
        val largerDate = IsoDate(2018, 1, 1)
        val smallerDate = IsoDate(2017, 12, 31)
        assertTrue(smallerDate < largerDate)
    }

    @Test
    fun `date with smaller month is smaller`() {
        val largerDate = IsoDate(2018, 2, 1)
        val smallerDate = IsoDate(2018, 1, 31)
        assertTrue(smallerDate < largerDate)
    }

    @Test
    fun `date with smaller day is smaller`() {
        val largerDate = IsoDate(2018, 1, 31)
        val smallerDate = IsoDate(2018, 1, 30)
        assertTrue(smallerDate < largerDate)
    }
}