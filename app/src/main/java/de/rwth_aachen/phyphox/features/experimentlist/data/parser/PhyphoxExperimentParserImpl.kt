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

class PhyphoxExperimentParserImpl @Inject constructor() : PhyphoxExperimentParser {

    override fun parse(input: InputStream): PhyphoxExperimentX {

        return try {
            val parser = Xml.newPullParser()
            parser.setInput(input, Xml.Encoding.UTF_8.name)
            parser.nextTag()
            readPhyphox(parser)
        } catch (e: Exception) {
            e.printStackTrace()
            return PhyphoxExperimentX("1")
        }
    }

    private fun readPhyphox(parser: XmlPullParser): PhyphoxExperimentX {
        parser.require(XmlPullParser.START_TAG, null, TAG_PHYPOX)
        val version = parser.getAttributeValue(null, ATTRIBUTE_VERSION)
        val locale = parser.attr(ATTRIBUTE_LOCALE)

        var icon: Icon? = null
        var title: String? = null
        var category: String? = null
        var links: List<Link> = emptyList()
        var translations: List<Translation> = emptyList()
        var dataContainers: List<Container> = emptyList()
        var experimentInput: List<ExperimentInput> = emptyList()

        val childrenParserMapping = mapOf(
            TAG_ICON to { icon = readIcon(parser) },
            TAG_TITLE to { title = readTitle(parser) },
            TAG_CATEGORY to { category = readCategory(parser) },
            TAG_LINK to { links = readLinks(parser) },
            TAG_TRANSLATIONS to { translations = readTranslations(parser) },
            TAG_DATA_CONTAINERS to { dataContainers = readContainers(parser) },
            TAG_INPUT to { experimentInput = readInput(parser) },
        )
        parser.readImmediateChildren(childrenParserMapping)
        return PhyphoxExperimentX(
            version = version,
            locale = locale,
            icon = icon,
            title = title,
            category = category,
            links = links,
            translations = translations,
            dataContainers = dataContainers,
            experimentInputs = experimentInput,
        )
    }

    private fun readIcon(parser: XmlPullParser): Icon? {
        return null
    }

    private fun readTitle(parser: XmlPullParser): String? {
        return null
    }

    private fun readCategory(parser: XmlPullParser): String? {
        return null
    }

    private fun readLinks(parser: XmlPullParser): List<Link> {
        return emptyList()
    }

    private fun readTranslations(parser: XmlPullParser): List<Translation> {
        return emptyList()
    }
    private fun readContainers(parser: XmlPullParser): List<Container> {
        return emptyList()
    }
    private fun readInput(parser: XmlPullParser): List<ExperimentInput>{
        return emptyList()
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
        const val ATTRIBUTE_FORMAT = "format"
        const val ATTRIBUTE_LABEL = "label"
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


