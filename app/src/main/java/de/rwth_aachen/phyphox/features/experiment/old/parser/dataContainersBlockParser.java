package de.rwth_aachen.phyphox.features.experiment.old.parser;

private static class dataContainersBlockParser extends XmlBlockParser {

        dataContainersBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, phyphoxFileException {
            switch (tag.toLowerCase()) {
                case "container": //A view defines an arangement of elements displayed to the user
                    String type = getStringAttribute("type");
                    if (type != null && !type.equals("buffer")) //There currently is only one buffer type. This tag is for future additions.
                        throw new phyphoxFileException("Unknown container type \"" + type + "\".", xpp.getLineNumber());

                    int size = getIntAttribute("size",1);
                    String strInit = getStringAttribute("init");
                    boolean isStatic = getBooleanAttribute("static", false);

                    String name = getText();
                    if (!isValidIdentifier(name))
                        throw new phyphoxFileException("\"" + name + "\" is not a valid name for a data-container.", xpp.getLineNumber());

                    DataBuffer newBuffer = experiment.createBuffer(name, size, experiment.experimentTimeReference);
                    newBuffer.setStatic(isStatic);

                    if (strInit != null && !strInit.isEmpty()) {
                        String strInitArray[] = strInit.split(",");
                        Double init[] = new Double[strInitArray.length];
                        for (int i = 0; i < init.length; i++) {
                            try {
                                init[i] = Double.parseDouble(strInitArray[i].trim());
                            } catch (Exception e) {
                                init[i] = Double.NaN;
                            }
                        }
                        newBuffer.setInit(init);
                    }
                    break;
                default: //Unknown tag
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
