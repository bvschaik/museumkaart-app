package nl.biancavanschaik.android.museumkaart.util

import android.net.Uri
import android.os.Build
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.view.View


fun ImageView.loadLargeImage(imagePath: String) = loadImage("700x394", imagePath)

fun ImageView.loadSmallImage(imagePath: String) = loadImage("240x135", imagePath)

private fun ImageView.loadImage(size: String, path: String) {
    val uri = IMAGE_BASE.buildUpon().appendPath(size).appendPath(path).build()
    Picasso.with(this.context).load(uri).into(this)
}

fun TextView.setHtmlText(text: String?) {
    val trimmedText = text?.trimHtml()
    val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(trimmedText ?: "?", Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(trimmedText ?: "?")
    }

    val builder = SpannableStringBuilder(html)
    builder.getSpans(0, html.length, URLSpan::class.java).forEach { span ->
        builder.setSpan(
                object : ClickableSpan() {
                    override fun onClick(view: View) {
                        view.context.openWebsite(span.url)
                    }
                },
                builder.getSpanStart(span),
                builder.getSpanEnd(span),
                builder.getSpanFlags(span)
        )
        builder.removeSpan(span)
    }
    this.text = builder
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun String.trimHtml(): String {
    return this.replace(nl.biancavanschaik.android.museumkaart.util.TRIM_HTML_REGEX, "$2")
}

private val IMAGE_BASE = Uri.parse("https://cdn.museum.nl/cards")
private val TRIM_HTML_REGEX = Regex("""^(<br ?/?>|&nbsp;|\s+)*(.*?)(<br ?/?>|&nbsp;|\s+)*$""", RegexOption.DOT_MATCHES_ALL)