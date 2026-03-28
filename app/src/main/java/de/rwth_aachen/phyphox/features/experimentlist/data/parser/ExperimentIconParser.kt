package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Icon
import de.rwth_aachen.phyphox.utils.XmlParser
import de.rwth_aachen.phyphox.utils.attr
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentIconParser @Inject constructor() : XmlParser<XmlPullParser, Icon?> {
    override fun parse(input: XmlPullParser): Icon? {
        return try {
            Icon(
                format = input.attr(ATTRIBUTE_FORMAT),
                value = input.nextText(),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    companion object {
        const val ATTRIBUTE_FORMAT = "format"
    }

}
