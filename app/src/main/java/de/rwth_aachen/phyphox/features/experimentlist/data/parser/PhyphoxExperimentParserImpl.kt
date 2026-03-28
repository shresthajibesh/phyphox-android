package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import android.util.Xml
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Container
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.ExperimentInput
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Icon
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Link
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Translation
import de.rwth_aachen.phyphox.utils.attr
import de.rwth_aachen.phyphox.utils.readImmediateChildren
import de.rwth_aachen.phyphox.utils.readText
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject


class PhyphoxExperimentParserImpl @Inject constructor(
    val iconParser: ExperimentIconParser,
    val linksParser: ExperimentLinksParser,
    val translationsParser: ExperimentTranslationsParser,
    val containersParser: ExperimentDataContainersParser,
    val inputParser: ExperimentInputsParser,
) : PhyphoxExperimentParser {

    override fun parse(input: InputStream): PhyphoxExperimentX {

        return try {
            val parser = Xml.newPullParser()
            parser.setInput(input, Xml.Encoding.UTF_8.name)
            parser.nextTag()
            readPhyphox(parser)
        } catch (e: Exception) {
            e.printStackTrace()
            return PhyphoxExperimentX("1")
        } finally {
            input.close()
        }
    }

    private fun readPhyphox(parser: XmlPullParser): PhyphoxExperimentX {
        parser.require(XmlPullParser.START_TAG, null, TAG_PHYPOX)
        val version = parser.attr(ATTRIBUTE_VERSION) ?: "1"
        val locale = parser.attr(ATTRIBUTE_LOCALE)

        var icon: Icon? = null
        var title: String? = null
        var description: String? = null
        var category: String? = null

        val links = mutableListOf<Link>()
        var translations = emptyList<Translation>()
        var dataContainers = emptyList<Container>()
        var experimentInputs = emptyList<ExperimentInput>()

        val childrenParserMapping = mapOf(
            TAG_ICON to { icon = iconParser.parse(parser) },
            TAG_TITLE to { title = parser.readText() },
            TAG_DESCRIPTION to { description = parser.readText() },
            TAG_CATEGORY to { category = parser.readText() },
            TAG_LINK to { linksParser.parse(parser)?.let { links.add(it) } },
            TAG_TRANSLATIONS to { translations = translationsParser.parse(parser) },
            TAG_DATA_CONTAINERS to { dataContainers = containersParser.parse(parser)},
            TAG_INPUT to { experimentInputs = inputParser.parse(parser) },
        )
        parser.readImmediateChildren(childrenParserMapping)
        return PhyphoxExperimentX(
            version = version,
            locale = locale,
            icon = icon,
            title = title,
            description = description,
            category = category,
            links = links,
            translations = translations,
            dataContainers = dataContainers,
            experimentInputs = experimentInputs,
        )
    }






    companion object {
        const val TAG_PHYPOX = "phyphox"
        const val TAG_ICON = "icon"
        const val TAG_LINK = "link"
        const val TAG_TRANSLATIONS = "translations"
        const val TAG_TITLE = "title"
        const val TAG_CATEGORY = "category"
        const val TAG_DESCRIPTION = "description"
        const val TAG_DATA_CONTAINERS = "data-containers"

        const val TAG_INPUT = "input"
        const val ATTRIBUTE_VERSION = "version"
        const val ATTRIBUTE_LOCALE = "locale"
    }

}


