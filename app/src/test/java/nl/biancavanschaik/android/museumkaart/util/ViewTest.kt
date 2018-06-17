package nl.biancavanschaik.android.museumkaart.util

import org.junit.Test

import org.junit.Assert.*

class ViewTest {
    @Test
    fun `all trailing whitespace is trimmed from html`() {
        val input = "This is some<br>\r\ntext with a linebreak&nbsp; <br/>\r\n&nbsp; <br>\n&nbsp;\r\t<br />"

        val output = input.trimHtml()

        assertEquals("This is some<br>\r\ntext with a linebreak", output)
    }

    @Test
    fun `all leading whitespace is trimmed from html`() {
        val input = "&nbsp; <br/>\r\n&nbsp; <br>\n&nbsp;\r\t<br />This is some<br>\r\ntext with a linebreak"

        val output = input.trimHtml()

        assertEquals("This is some<br>\r\ntext with a linebreak", output)
    }

    @Test
    fun `both leading and trailing whitespace is trimmed from html`() {
        val input = "&nbsp; <br/>\r\n&nbsp; <br>\n&nbsp;\r\t<br />This is some<br>text with a linebreak&nbsp; <br/>\r\n&nbsp; <br>\n&nbsp;\r\t<br />"

        val output = input.trimHtml()

        assertEquals("This is some<br>text with a linebreak", output)
    }
}