package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Translation
import de.rwth_aachen.phyphox.utils.XmlParser
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentTranslationsParser @Inject constructor() : XmlParser<XmlPullParser, Translation?> {
    override fun parse(input: XmlPullParser): Translation? {
        return null
    }
}
