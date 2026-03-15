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
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject


class PhyphoxExperimentParserImpl @Inject constructor(
    val iconParser: ExperimentIconParser,
    val linksParser: ExperimentLinksParser,
    val translationsParser: ExperimentTranslationsParser,
    val containersParser: ExperimentDataContainersParser,
    val inputParser: ExperimentInputParser,
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
        val translations = mutableListOf<Translation>()
        val dataContainers = mutableListOf<Container>()
        val experimentInput = mutableListOf<ExperimentInput>()

        val childrenParserMapping = mapOf(
            TAG_ICON to { icon = iconParser.parse(parser) },
            TAG_TITLE to { title = readTitle(parser) },
            TAG_DESCRIPTION to { description = readDescription(parser) },
            TAG_CATEGORY to { category = readCategory(parser) },
            TAG_LINK to { linksParser.parse(parser)?.let { links.add(it) } },
            TAG_TRANSLATIONS to { translationsParser.parse(parser)?.let { translations.add(it) } },
            TAG_DATA_CONTAINERS to { containersParser.parse(parser)?.let { dataContainers.add(it) } },
            TAG_INPUT to { inputParser.parse(parser)?.let { experimentInput.add(it) } },
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
            experimentInputs = experimentInput,
        )
    }

    private fun readTitle(parser: XmlPullParser): String? {
        return parser.nextText()
    }

    private fun readCategory(parser: XmlPullParser): String? {
        return parser.nextText()
    }

    private fun readDescription(parser: XmlPullParser): String? {
        return parser.nextText()
    }


    companion object {
        const val TAG_PHYPOX = "phyphox"
        const val TAG_ICON = "icon"
        const val TAG_LINK = "link"
        const val TAG_TRANSLATIONS = "translations"
        const val TAG_TRANSLATION = "translation"
        const val TAG_TITLE = "title"
        const val TAG_CATEGORY = "category"
        const val TAG_DESCRIPTION = "description"
        const val TAG_STRING = "string"
        const val TAG_DATA_CONTAINERS = "data-containers"
        const val TAG_CONTAINER = "container"
        const val TAG_INPUT = "input"
        const val TAG_SENSOR = "sensor"
        const val TAG_OUTPUT = "output"
        const val TAG_VIEWS = "views"
        const val TAG_VIEW = "view"
        const val TAG_GRAPH = "graph"
        const val TAG_SEPARATOR = "separator"
        const val TAG_INFO = "info"
        const val TAG_VALUE = "value"
        const val TAG_EXPORT = "export"
        const val TAG_SET = "set"
        const val TAG_DATA = "data"

        const val ATTRIBUTE_VERSION = "version"


        const val ATTRIBUTE_LOCALE = "locale"
        const val ATTRIBUTE_ORIGINAL = "original"
        const val ATTRIBUTE_SIZE = "size"
        const val ATTRIBUTE_TYPE = "type"
        const val ATTRIBUTE_COMPONENT = "component"
        const val ATTRIBUTE_TIME_ON_X = "timeOnX"
        const val ATTRIBUTE_LABEL_X = "labelX"
        const val ATTRIBUTE_UNIT_X = "unitX"
        const val ATTRIBUTE_LABEL_Y = "labelY"
        const val ATTRIBUTE_UNIT_Y = "unitY"
        const val ATTRIBUTE_PARTIAL_UPDATE = "partialUpdate"
        const val ATTRIBUTE_AXIS = "axis"
        const val ATTRIBUTE_HEIGHT = "height"
        const val ATTRIBUTE_PRECISION = "precision"
        const val ATTRIBUTE_UNIT = "unit"
        const val ATTRIBUTE_NAME = "name"
    }

}


