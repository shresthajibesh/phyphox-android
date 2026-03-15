package de.rwth_aachen.phyphox.utils

import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.attr(name: String, namespace:String? = null): String? = getAttributeValue(null, name)
fun XmlPullParser.skip() {
    if (eventType != XmlPullParser.START_TAG) return
    var depth = 1
    while (depth != 0) {
        when (next()) {
            XmlPullParser.START_TAG -> depth++
            XmlPullParser.END_TAG -> depth--
        }
    }
}


fun <T> XmlPullParser.readChildren(
    handlers: Map<String, XmlPullParser.() -> T>
): List<T> {
    val results = mutableListOf<T>()

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) continue

        val parser = handlers[name]
        if (parser != null) {
            results += parser()
        } else {
            skip()  // unknown tag
        }
    }

    return results
}

fun XmlPullParser.readImmediateChildren(
    handlers: Map<String, () -> Unit>
) {
    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) continue

        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue
            handlers[name]?.invoke() ?: skip()
        }
    }
}
