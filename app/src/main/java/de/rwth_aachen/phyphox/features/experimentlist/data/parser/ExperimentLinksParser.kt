package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Link
import de.rwth_aachen.phyphox.utils.XmlParser
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentLinksParser @Inject constructor() : XmlParser<XmlPullParser, List<Link>> {
    override fun parse(input: XmlPullParser): List<Link> {
        return emptyList()
    }
}
