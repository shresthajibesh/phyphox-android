package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Link
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Translation
import de.rwth_aachen.phyphox.utils.XmlParser
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentTranslationsParser @Inject constructor() : XmlParser<XmlPullParser, List<Translation>> {
    override fun parse(input: XmlPullParser): List<Translation> {
        return emptyList()
    }
}
