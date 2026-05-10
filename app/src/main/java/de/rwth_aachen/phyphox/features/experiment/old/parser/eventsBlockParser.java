package de.rwth_aachen.phyphox.features.experiment.old.parser;

private static class eventsBlockParser extends XmlBlockParser {

        eventsBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, phyphoxFileException {
            ExperimentTimeReference.TimeMappingEvent event;
            switch (tag.toLowerCase()) {
                case "start":
                    event = ExperimentTimeReference.TimeMappingEvent.START;
                    break;
                case "pause":
                    event = ExperimentTimeReference.TimeMappingEvent.PAUSE;
                    break;
                default: //Unknown tag
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
            Double experimentTime = getDoubleAttribute("experimentTime", -1.0);
            String systemTimeStr = getStringAttribute("systemTime");
            if (systemTimeStr == null)
                throw new phyphoxFileException("An event requires both, an experiment time and a system time.", xpp.getLineNumber());
            Long systemTime = Long.parseLong(systemTimeStr);
            if (experimentTime < 0 || systemTime < 0)
                throw new phyphoxFileException("An event requires both, an experiment time and a system time.", xpp.getLineNumber());
            experiment.experimentTimeReference.timeMappings.add(new ExperimentTimeReference.TimeMapping(event, experimentTime, 0, systemTime));
        }

    }
