package de.rwth_aachen.phyphox.features.experimentlist.data.parser

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX
import de.rwth_aachen.phyphox.utils.XmlParser
import java.io.InputStream

interface PhyphoxExperimentParser : XmlParser<InputStream, PhyphoxExperimentX>
