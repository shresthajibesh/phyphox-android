package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX
import java.io.InputStream
import javax.inject.Inject

class PhyphoxExperimentParserImpl @Inject constructor() : PhyphoxExperimentParser {
    override fun parse(input: InputStream): PhyphoxExperimentX {

        return PhyphoxExperimentX("1")

    }
}
