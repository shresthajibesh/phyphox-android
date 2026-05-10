package de.rwth_aachen.phyphox.features.experiment.old.parser;

import de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile;

private static class setBlockParser extends PhyphoxFile.xmlBlockParser {
        private DataExport.ExportSet set;

        //This constructor takes an additional argument: The export set to be filled
        setBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent, DataExport.ExportSet set) {
            super(xpp, experiment, parent);
            this.set = set;
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, phyphoxFileException, IOException {
            switch (tag.toLowerCase()) {
                case "data": //Add this data buffer to the set
                    String name = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "name");
                    String src = getText();
                    if (experiment.getBuffer(src) != null)
                        set.addSource(name, src);
                    else
                        throw new phyphoxFileException("Export buffer " + src + " has not been defined as a buffer.", xpp.getLineNumber());
                    break;
                default:
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
