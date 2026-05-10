package de.rwth_aachen.phyphox.features.experiment.old.parser;

private static class bluetoothIoBlockParser extends XmlBlockParser {
        protected static Class conversionsInput = (new ConversionsInput()).getClass();
        protected static Class conversionsOutput = (new ConversionsOutput()).getClass();
        protected static Class conversionsConfig = (new ConversionsConfig()).getClass();
        Vector<DataOutput> outputList;
        Vector<DataInput> inputList;
        Vector<Bluetooth.CharacteristicData> characteristics; // characteristics of the bluetooth input / output
        HashSet<UUID> characteristicsWithExtraTime; // uuids of all characteristics that have extra=time to make sure they can't have it twice

        bluetoothIoBlockParser (XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent, Vector<DataOutput> outputList, Vector<DataInput> inputList, Vector<Bluetooth.CharacteristicData> characteristics) {
            super(xpp, experiment, parent);
            this.outputList = outputList;
            this.inputList = inputList;
            this.characteristics = characteristics;
            characteristicsWithExtraTime = new HashSet<>();
        }

        @Override
        protected void processStartTag(String tag) throws IOException, XmlPullParserException, phyphoxFileException {
            // get and check "char" attribute
            String charUuid = getStringAttribute("char");
            if (charUuid == null) {
                throw new phyphoxFileException("Tag needs a char attribute.", xpp.getLineNumber());
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(charUuid);
            } catch (IllegalArgumentException e) {
                throw new phyphoxFileException("invalid UUID.", xpp.getLineNumber());
            }

            // get "conversion" attribute
            String conversionFunctionName = getStringAttribute("conversion");
            ConversionsConfig.ConfigConversion configConversionFunction = null;
            ConversionsInput.InputConversion inputConversionFunction = null;
            ConversionsOutput.OutputConversion outputConversionFunction = null;

            switch (tag.toLowerCase()) {
                case "input": {
                    // check if "input" is allowed here
                    if (inputList == null) {
                        throw new phyphoxFileException("No output expected.", xpp.getLineNumber());
                    }

                    // check conversion attribute
                    if (conversionFunctionName == null) {
                        throw new phyphoxFileException("Tag needs a conversion attribute.", xpp.getLineNumber());
                    }
                    try {
                        try {
                            try {
                                Class conversionClass = Class.forName("de.rwth_aachen.phyphox.Bluetooth.ConversionsOutput$" + conversionFunctionName);
                                Constructor constructor = conversionClass.getConstructor(XmlPullParser.class);
                                constructor.setAccessible(true);
                                outputConversionFunction = (ConversionsOutput.OutputConversion) constructor.newInstance(xpp);
                            } catch (Exception e) {
                                Method conversionMethod = conversionsOutput.getDeclaredMethod(conversionFunctionName, new Class[]{DataBuffer.class});
                                outputConversionFunction = new ConversionsOutput.SimpleOutputConversion(conversionMethod);
                            }
                        } catch (Exception e) {
                            Method conversionMethod = conversionsOutput.getDeclaredMethod(conversionFunctionName, new Class[]{double.class});
                            outputConversionFunction = new ConversionsOutput.SimpleOutputConversion(conversionMethod);
                        }
                    } catch (NoSuchMethodException e) {
                        throw new phyphoxFileException("invalid conversion function: " + conversionFunctionName, xpp.getLineNumber());
                    }

                    short offset = (short)getIntAttribute("offset", 0);

                    boolean keep = getBooleanAttribute("keep", true);

                    // check if buffer exists
                    String bufferName = getText();
                    DataBuffer buffer = experiment.getBuffer(bufferName);
                    if (buffer == null) {
                        throw new phyphoxFileException("Buffer \"" + bufferName + "\" not defined.", xpp.getLineNumber());
                    }

                    inputList.add(new DataInput(buffer, keep));

                    // add data to characteristics
                    characteristics.add(new Bluetooth.OutputData(uuid, inputList.size()-1, outputConversionFunction, offset));

                    break;
                }

                case "output": {
                    // check if "output" is allowed here
                    if (outputList == null) {
                        throw new phyphoxFileException("No output expected.", xpp.getLineNumber());
                    }

                    // get and check "extra" attribute
                    boolean extraTime = false;
                    String extra = this.getStringAttribute("extra");
                    if (extra != null) {
                        if (extra.equals("time")) {
                            extraTime = true;
                            if (characteristicsWithExtraTime.contains(uuid)) {
                                throw new phyphoxFileException("extra=time can be used only once for a characteristic.");
                            } else {
                                characteristicsWithExtraTime.add(uuid);
                            }
                        } else {
                            throw new phyphoxFileException("unknown value for extra attribute.", xpp.getLineNumber());
                        }
                    }

                    // check conversion attribute
                    if (!extraTime) {
                       if (conversionFunctionName == null) {
                           throw new phyphoxFileException("Tag needs a conversion attribute.", xpp.getLineNumber());
                       }
                        try {
                            try {
                                Class conversionClass = Class.forName("de.rwth_aachen.phyphox.Bluetooth.ConversionsInput$" + conversionFunctionName);
                                Constructor constructor = conversionClass.getDeclaredConstructor(new Class[]{XmlPullParser.class});
                                constructor.setAccessible(true);
                                inputConversionFunction = (ConversionsInput.InputConversion)constructor.newInstance(xpp);
                            } catch (Exception e) {
                                Method conversionMethod = conversionsInput.getDeclaredMethod(conversionFunctionName, new Class[]{byte[].class});
                                inputConversionFunction = new ConversionsInput.SimpleInputConversion(conversionMethod, xpp);
                            }
                        } catch (NoSuchMethodException e) {
                            throw new phyphoxFileException("invalid conversion function: " + conversionFunctionName, xpp.getLineNumber());
                        }
                    }

                    // check if buffer exists
                    String bufferName = getText();
                    DataBuffer buffer = experiment.getBuffer(bufferName);
                    if (buffer == null) {
                        throw new phyphoxFileException("Buffer \"" + bufferName + "\" not defined.", xpp.getLineNumber());
                    }

                    outputList.add(new DataOutput(buffer, false));

                    // add data to characteristics
                    characteristics.add(new Bluetooth.InputData(uuid, extraTime, outputList.size()-1, inputConversionFunction));
                    break;
                }

                case "config": {
                    // check if conversion attribute exists
                    if (conversionFunctionName == null) {
                        throw new phyphoxFileException("Tag needs a conversion attribute.", xpp.getLineNumber());
                    }
                    try {
                        try {
                            Class conversionClass = Class.forName("de.rwth_aachen.phyphox.Bluetooth.ConversionsConfig$" + conversionFunctionName);
                            Constructor constructor = conversionClass.getConstructor(XmlPullParser.class);
                            constructor.setAccessible(true);
                            configConversionFunction = (ConversionsConfig.ConfigConversion)constructor.newInstance(xpp);
                        } catch (Exception e) {
                            Method conversionMethod = conversionsConfig.getDeclaredMethod(conversionFunctionName, String.class);
                            configConversionFunction = new ConversionsConfig.SimpleConfigConversion(conversionMethod);
                        }
                    } catch (NoSuchMethodException e) {
                        throw new phyphoxFileException("invalid conversion function: " + conversionFunctionName, xpp.getLineNumber());
                    }
                    try {
                        // add data to configs
                        String text = getText();
                        characteristics.add(new Bluetooth.ConfigData(uuid, text, configConversionFunction));
                    } catch (NumberFormatException e) {
                        throw new phyphoxFileException("Configuration data has to be a valid double value.", xpp.getLineNumber());
                    } catch (phyphoxFileException e) {
                        throw new phyphoxFileException(e.getMessage(), xpp.getLineNumber()); // throw it again but with LineNumber
                    }
                    break;
                }
                default: {
                    throw new phyphoxFileException("Unknown tag \""+tag+"\"", xpp.getLineNumber());
                }
            }
        }

    }
