package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.ExperimentView
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.GraphElement
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.InfoElement
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.SeparatorElement
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.ValueElement
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.ViewElement
import de.rwth_aachen.phyphox.utils.XmlParser
import de.rwth_aachen.phyphox.utils.attr
import de.rwth_aachen.phyphox.utils.readImmediateChildren
import de.rwth_aachen.phyphox.utils.readText
import de.rwth_aachen.phyphox.utils.skip
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentViewsParser @Inject constructor(
    private val viewParser: ExperimentViewParser,
) : XmlParser<XmlPullParser, List<ExperimentView>> {

    override fun parse(input: XmlPullParser): List<ExperimentView> {
        val views = mutableListOf<ExperimentView>()

        val mapping = mapOf(
            TAG_VIEW to { viewParser.parse(input)?.let { views.add(it) } },
        )

        input.readImmediateChildren(mapping)
        return views
    }

    companion object {
        const val TAG_VIEW = "view"
    }
}


class ExperimentViewParser @Inject constructor(
    private val graphParser: GraphElementParser,
    private val valueParser: ValueElementParser,
    private val separatorParser: SeparatorElementParser,
    private val infoParser: InfoElementParser,
) : XmlParser<XmlPullParser, ExperimentView?> {

    override fun parse(input: XmlPullParser): ExperimentView? {
        val label = input.attr(ATTRIBUTE_LABEL)

        val elements = mutableListOf<ViewElement>()

        val mapping = mapOf(
            TAG_GRAPH to { graphParser.parse(input)?.let { elements.add(it) } },
            TAG_VALUE to { valueParser.parse(input).let { elements.add(it) } },
            TAG_SEPARATOR to { separatorParser.parse(input).let { elements.add(it) } },
            TAG_INFO to { infoParser.parse(input).let { elements.add(it) } },
        )

        input.readImmediateChildren(mapping)

        return ExperimentView(label, elements)
    }

    companion object {
        const val TAG_GRAPH = "graph"
        const val TAG_VALUE = "value"
        const val TAG_SEPARATOR = "separator"
        const val TAG_INFO = "info"

        const val ATTRIBUTE_LABEL = "label"
    }
}


class GraphElementParser @Inject constructor() :
    XmlParser<XmlPullParser, GraphElement?> {

    override fun parse(input: XmlPullParser): GraphElement? {
        val label = input.attr("label")
        val logX: Boolean = input.attr("logX").toBoolean()
        val logY: Boolean = input.attr("logY").toBoolean()
        val labelX: String? = input.attr("labelX")
        val unitX: String? = input.attr("unitX")
        val labelY: String? = input.attr("labelY")
        val unitY: String? = input.attr("unitY")
        val logZ: Boolean = input.attr("logZ").toBoolean()
        val labelZ: String? = input.attr("labelZ")
        val unitZ: String? = input.attr("unitZ")
        val aspectRatio: String? = input.attr("aspectRatio")
        val style: String? = input.attr("style")
        val mapWidth: String? = input.attr("mapWidth")
        val partialUpdate: Boolean = input.attr("partialUpdate").toBoolean()
        val inputs = mutableListOf<String>()

        val mapping = mapOf(
            TAG_INPUT to { input.readText()?.let { inputs.add(it) } },
        )

        input.readImmediateChildren(mapping)

        return GraphElement(
            label = label,
            logX = logX,
            logY = logY,
            labelX = labelX,
            unitX = unitX,
            labelY = labelY,
            unitY = unitY,
            inputs = inputs,
            logZ = logZ,
            labelZ = labelZ,
            unitZ = unitZ,
            aspectRatio = aspectRatio,
            style = style,
            mapWidth = mapWidth,
            partialUpdate = partialUpdate,
        )
    }

    companion object {
        const val TAG_INPUT = "input"
    }
}

class ValueElementParser @Inject constructor() :
    XmlParser<XmlPullParser, ValueElement?> {

    override fun parse(input: XmlPullParser): ValueElement {
        val label = input.attr("label")
        val inputs = mutableListOf<String>()

        val mapping = mapOf(
            TAG_INPUT to { input.readText()?.let { inputs.add(it) } },
        )

        input.readImmediateChildren(mapping)

        return ValueElement(label, inputs)
    }

    companion object {
        const val TAG_INPUT = "input"
    }
}

class SeparatorElementParser @Inject constructor() :
    XmlParser<XmlPullParser, SeparatorElement?> {

    override fun parse(input: XmlPullParser): SeparatorElement {
        val height = input.attr("height")?.toIntOrNull()

        input.skip() // no children

        return SeparatorElement(height)
    }
}

class InfoElementParser @Inject constructor() :
    XmlParser<XmlPullParser, InfoElement?> {

    override fun parse(input: XmlPullParser): InfoElement {
        val label = input.attr("label")

        input.skip()

        return InfoElement(label)
    }
}
