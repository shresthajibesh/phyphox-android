package de.rwth_aachen.phyphox.features.experiment.old.parser;

private static class viewsBlockParser extends XmlBlockParser {

        viewsBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, phyphoxFileException {
            switch (tag.toLowerCase()) {
                case "view": //A view defines an arangement of elements displayed to the user
                    ExpView newView = new ExpView(); //Create a new view
                    newView.name = getTranslatedAttribute("label"); //Fill its name
                    (new viewBlockParser(xpp, experiment, parent, newView)).process(); //And load its elements
                    if (newView.name != null && newView.elements.size() > 0) //We will only add it if it has a name and at least a single view
                        experiment.experimentViews.add(newView);
                    else {
                        //No name or no views. Complain!
                        throw new phyphoxFileException("Invalid view.", xpp.getLineNumber());
                    }
                    break;
                default: //Unknown tag
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
