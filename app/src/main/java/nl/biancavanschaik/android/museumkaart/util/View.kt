package nl.biancavanschaik.android.museumkaart.util

import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.TextView

fun TextView.setHtmlText(text: String?) {
    val trimmedText = text?.trimHtml()
    Log.w("HTML", "$text -> $trimmedText")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(trimmedText ?: "?", Html.FROM_HTML_MODE_COMPACT)
    } else {
        this.text = Html.fromHtml(trimmedText ?: "?")
    }
}

fun String.trimHtml(): String {
    return this.replace(nl.biancavanschaik.android.museumkaart.util.TRIM_HTML_REGEX, "$2")
}

private val TRIM_HTML_REGEX = Regex("""^(<br ?/?>|&nbsp;|\s+)*(.*?)(<br ?/?>|&nbsp;|\s+)*$""", RegexOption.DOT_MATCHES_ALL)