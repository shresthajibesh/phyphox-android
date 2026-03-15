package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.ExperimentInput
import de.rwth_aachen.phyphox.utils.XmlParser
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentInputParser @Inject constructor() : XmlParser<XmlPullParser, List<ExperimentInput>> {
    override fun parse(input: XmlPullParser): List<ExperimentInput> {
        return emptyList()
    }

    companion object {
        const val ATTRIBUTE_FORMAT = "format"
    }

}
