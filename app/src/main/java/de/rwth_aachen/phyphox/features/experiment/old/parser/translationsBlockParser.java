package de.rwth_aachen.phyphox.features.experiment.old.parser;

private static class translationsBlockParser extends XmlBlockParser {

        translationsBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, phyphoxFileException {
            switch (tag.toLowerCase()) {
                case "translation": //A translation block holds all translation information for a single language
                    String thisLocale = getStringAttribute("locale");
                    int thisLaguageRating = Helper.getLanguageRating(parent.getResources(), thisLocale);
                    if (thisLaguageRating > languageRating) { //Check if the language matches better than previous ones...
                        languageRating = thisLaguageRating;
                        (new translationBlockParser(xpp, experiment, parent)).process(); //Jepp, use it!
                    } else
                        (new XmlBlockParser(xpp, experiment, parent)).process(); //Nope. Use the empty block parser to skip it
                    break;
                default: //Unknown tag...
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
