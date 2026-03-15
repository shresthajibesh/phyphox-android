package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.data.parser.ExperimentIconParser.Companion.ATTRIBUTE_FORMAT
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Icon
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Link
import de.rwth_aachen.phyphox.utils.XmlParser
import de.rwth_aachen.phyphox.utils.attr
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentLinksParser @Inject constructor() : XmlParser<XmlPullParser, Link?> {
    override fun parse(input: XmlPullParser): Link? {
        return try {
            Link(
                label = input.attr(ATTRIBUTE_LABEL),
                url = input.nextText(),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val ATTRIBUTE_LABEL = "label"
    }
}
