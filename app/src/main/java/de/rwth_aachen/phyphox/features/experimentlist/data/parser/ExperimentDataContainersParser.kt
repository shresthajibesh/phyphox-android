package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Container
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Link
import de.rwth_aachen.phyphox.utils.XmlParser
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentDataContainersParser @Inject constructor() : XmlParser<XmlPullParser, List<Container>> {
    override fun parse(input: XmlPullParser): List<Container> {
        return emptyList()
    }
}
