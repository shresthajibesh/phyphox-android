package de.rwth_aachen.phyphox.features.experiment.old.parser;

import de.rwth_aachen.phyphox.features.experiment.old.async.setBlockParser;

private static class exportBlockParser extends XmlBlockParser {

        exportBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, IOException, phyphoxFileException {
            switch (tag.toLowerCase()) {
                case "set": //An export set. These just group some dataBuffers to be exported as a set
                    DataExport.ExportSet set = experiment.exporter.new ExportSet(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "name")); //Create the set with the given name
                    (new setBlockParser(xpp, experiment, parent, set)).process(); //Parse the information within
                    experiment.exporter.addSet(set); //Add the set
                break;
                default:
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

        @Override
        protected void processEndTag(String tag) {

        }
    }
