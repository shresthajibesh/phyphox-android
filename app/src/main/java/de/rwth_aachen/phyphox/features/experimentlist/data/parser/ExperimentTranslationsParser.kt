package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Translation
import de.rwth_aachen.phyphox.utils.XmlParser
import de.rwth_aachen.phyphox.utils.readImmediateChildren
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentTranslationsParser @Inject constructor(
    val translationParser: ExperimentTranslationParser,
) : XmlParser<XmlPullParser, List<Translation>> {
    override fun parse(input: XmlPullParser): List<Translation> {

        val translations = mutableListOf<Translation>()

        val childrenParserMapping = mapOf(
            TAG_TRANSLATION to { translationParser.parse(input)?.let { translations.add(it) } },
        )
        input.readImmediateChildren(childrenParserMapping)
        return translations
    }

    companion object {
        const val TAG_TRANSLATION = "translation"
    }
}

