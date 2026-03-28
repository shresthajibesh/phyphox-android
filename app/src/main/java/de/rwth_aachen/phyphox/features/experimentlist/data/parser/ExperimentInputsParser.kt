package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.ExperimentInput
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.SensorOutput
import de.rwth_aachen.phyphox.utils.XmlParser
import de.rwth_aachen.phyphox.utils.attr
import de.rwth_aachen.phyphox.utils.readImmediateChildren
import de.rwth_aachen.phyphox.utils.readText
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject

class ExperimentInputsParser @Inject constructor(
    private val inputParser: ExperimentInputParser,
) : XmlParser<XmlPullParser, List<ExperimentInput>> {
    override fun parse(input: XmlPullParser): List<ExperimentInput> {
        val sensors = mutableListOf<ExperimentInput>()

        val childrenParserMapping = mapOf(
            TAG_INPUT_SENSOR to { inputParser.parse(input)?.let { sensors.add(it) } },
        )
        input.readImmediateChildren(childrenParserMapping)
        return sensors
    }

    companion object {
        const val TAG_INPUT_SENSOR = "sensor"
    }

}

class ExperimentInputParser @Inject constructor(
    private val sensorOutputParser: InputSensorOutputParser,
) : XmlParser<XmlPullParser, ExperimentInput?> {
    override fun parse(input: XmlPullParser): ExperimentInput? {
        val type = input.attr(ATTRIBUTE_TYPE) ?: return null
        val rate = input.attr(ATTRIBUTE_RATE)?.toDoubleOrNull()
        val output = mutableListOf<SensorOutput>()

        val childrenParserMapping = mapOf(
            TAG_OUTPUT to { sensorOutputParser.parse(input)?.let { output.add(it) } },
        )
        input.readImmediateChildren(childrenParserMapping)
        return ExperimentInput(
            type = type,
            output = output,
            rate = rate,
        )
    }

    companion object {

        const val TAG_OUTPUT = "output"
        const val ATTRIBUTE_TYPE = "type"
        const val ATTRIBUTE_RATE = "rate"
    }

}

class InputSensorOutputParser @Inject constructor() : XmlParser<XmlPullParser, SensorOutput?> {
    override fun parse(input: XmlPullParser): SensorOutput? {
        val component = input.attr(ATTRIBUTE_COMPONENT)
        val name = input.readText() ?: return null
        return SensorOutput(
            component = component,
            name = name,
        )
    }

    companion object {
        const val ATTRIBUTE_COMPONENT = "component"
    }

}
