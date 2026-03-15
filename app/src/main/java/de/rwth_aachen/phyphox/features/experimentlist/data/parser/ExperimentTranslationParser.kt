package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.data.parser.PhyphoxExperimentParserImpl.Companion.ATTRIBUTE_LOCALE
import de.rwth_aachen.phyphox.features.experimentlist.data.parser.PhyphoxExperimentParserImpl.Companion.TAG_CATEGORY
import de.rwth_aachen.phyphox.features.experimentlist.data.parser.PhyphoxExperimentParserImpl.Companion.TAG_DESCRIPTION
import de.rwth_aachen.phyphox.features.experimentlist.data.parser.PhyphoxExperimentParserImpl.Companion.TAG_TITLE
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.LocalizedString
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Translation
import de.rwth_aachen.phyphox.utils.XmlParser
import de.rwth_aachen.phyphox.utils.attr
import de.rwth_aachen.phyphox.utils.readImmediateChildren
import de.rwth_aachen.phyphox.utils.readText
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import javax.inject.Inject

class ExperimentTranslationParser @Inject constructor() : XmlParser<XmlPullParser, Translation?> {
    override fun parse(input: XmlPullParser): Translation? {
        val locale = input.attr(ATTRIBUTE_LOCALE)
        var title: String? = null
        var category: String? = null
        var description: String? = null
        val localizedString = mutableListOf<LocalizedString>()

        val childrenParserMapping = mapOf(
            TAG_TITLE to { title = input.readText() },
            TAG_DESCRIPTION to { description = input.readText() },
            TAG_CATEGORY to { category = input.readText() },
            TAG_STRING to { readLocalisedString(input)?.let { localizedString.add(it) } },
        )
        input.readImmediateChildren(childrenParserMapping)
        return try {
            Translation(
                locale = locale,
                title = title,
                category = category,
                description = description,
                localizedString = localizedString,
            )
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            null
        }
    }

    private fun readLocalisedString(input: XmlPullParser): LocalizedString? {
        val original = input.attr(ATTRIBUTE_ORIGINAL)
        val value = input.readText() ?: return null
        return LocalizedString(
            original = original,
            value = value,
        )
    }

    companion object {
        const val TAG_STRING = "string"
        const val ATTRIBUTE_ORIGINAL = "original"
    }
}
