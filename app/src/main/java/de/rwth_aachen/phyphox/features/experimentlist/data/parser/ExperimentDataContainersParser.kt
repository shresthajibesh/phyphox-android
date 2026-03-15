package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.data.parser.ExperimentDataContainersParser.Companion.TAG_CONTAINER
import de.rwth_aachen.phyphox.features.experimentlist.data.parser.ExperimentTranslationsParser.Companion.TAG_TRANSLATION
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Container
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.Translation
import de.rwth_aachen.phyphox.utils.XmlParser
import de.rwth_aachen.phyphox.utils.attr
import de.rwth_aachen.phyphox.utils.readImmediateChildren
import de.rwth_aachen.phyphox.utils.readText
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentDataContainersParser @Inject constructor(
    private val containerParser: ContainerParser
) : XmlParser<XmlPullParser, List<Container>> {
    override fun parse(input: XmlPullParser): List<Container> {
        val containers = mutableListOf<Container>()

        val childrenParserMapping = mapOf(
            TAG_CONTAINER to { containerParser.parse(input)?.let { containers.add(it) } },
        )
        input.readImmediateChildren(childrenParserMapping)
        return containers
    }

    companion object{
        const val TAG_CONTAINER = "container"
    }
}

class ContainerParser @Inject constructor() : XmlParser<XmlPullParser, Container?>{
    override fun parse(input: XmlPullParser): Container? {
        val size = input.attr( ATTRIBUTE_SIZE)?.toDoubleOrNull()
        val init = input.attr( "init")?.toDoubleOrNull()
        val name = input.readText() ?: return null
        return Container(
            size = size,
            init = init,
            name = name
        )
    }

    companion object {
        const val ATTRIBUTE_SIZE = "size"
    }
}

