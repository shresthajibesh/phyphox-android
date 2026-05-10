package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.ExperimentTimeReference;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class eventsBlockParser extends XmlBlockParser {

        eventsBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, PhyphoxFileException {
            ExperimentTimeReference.TimeMappingEvent event;
            switch (tag.toLowerCase()) {
                case "start":
                    event = ExperimentTimeReference.TimeMappingEvent.START;
                    break;
                case "pause":
                    event = ExperimentTimeReference.TimeMappingEvent.PAUSE;
                    break;
                default: //Unknown tag
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
            Double experimentTime = getDoubleAttribute("experimentTime", -1.0);
            String systemTimeStr = getStringAttribute("systemTime");
            if (systemTimeStr == null)
                throw new PhyphoxFileException("An event requires both, an experiment time and a system time.", xpp.getLineNumber());
            Long systemTime = Long.parseLong(systemTimeStr);
            if (experimentTime < 0 || systemTime < 0)
                throw new PhyphoxFileException("An event requires both, an experiment time and a system time.", xpp.getLineNumber());
            experiment.experimentTimeReference.timeMappings.add(new ExperimentTimeReference.TimeMapping(event, experimentTime, 0, systemTime));
        }

    }
