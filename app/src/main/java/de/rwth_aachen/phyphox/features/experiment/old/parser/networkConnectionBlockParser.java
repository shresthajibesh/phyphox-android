package de.rwth_aachen.phyphox.features.experiment.old.parser;

private static class networkConnectionBlockParser extends XmlBlockParser {
        Map<String, NetworkConnection.NetworkSendableData> send;
        Map<String, NetworkConnection.NetworkReceivableData> receive;

        networkConnectionBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent, Map<String, NetworkConnection.NetworkSendableData> send, Map<String, NetworkConnection.NetworkReceivableData> receive) {
            super(xpp, experiment, parent);
            this.send = send;
            this.receive = receive;
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, phyphoxFileException, IOException {
            switch (tag.toLowerCase()) {
                case "send": {
                    NetworkConnection.NetworkSendableData sendable;

                    String id = getStringAttribute("id");
                    if (id == null)
                        throw new phyphoxFileException("Missing id in send element.", xpp.getLineNumber());

                    String datatype = getStringAttribute("datatype");

                    String type = getStringAttribute("type");
                    if (type == null || type.equals("buffer")) {
                        boolean clear = getBooleanAttribute("clear", false); //Deprecated
                        boolean keep = getBooleanAttribute("keep", !clear); //New attribute keep = !clear,
                        String bufferName = getText();
                        DataBuffer buffer = experiment.getBuffer(bufferName);
                        if (buffer == null)
                            throw new phyphoxFileException("Buffer \"" + bufferName + "\" not defined.", xpp.getLineNumber());
                        sendable = new NetworkConnection.NetworkSendableData(buffer, keep);
                        if (datatype != null) {
                            sendable.additionalAttributes = new HashMap<>();
                            sendable.additionalAttributes.put("datatype", datatype);
                        }
                    } else if (type.equals("meta")) {
                        String metaName = getText();
                        try {
                            sendable = new NetworkConnection.NetworkSendableData(new Metadata(metaName, parent));
                        } catch (IllegalArgumentException e) {
                            throw new phyphoxFileException("Unknown meta data \"" + metaName + "\".", xpp.getLineNumber());
                        }
                    } else if (type.equals("time")) {
                        sendable = new NetworkConnection.NetworkSendableData(experiment.experimentTimeReference);
                    } else {
                        throw new phyphoxFileException("Unknown type \"" + type + "\".", xpp.getLineNumber());
                    }
                    send.put(id, sendable);
                    break;
                }
                case "receive": {
                    NetworkConnection.NetworkReceivableData receivable;

                    String id = getStringAttribute("id");
                    if (id == null)
                        throw new phyphoxFileException("Missing id in receive element.", xpp.getLineNumber());

                    boolean clear = getBooleanAttribute("clear", false); //Deprecated
                    boolean append = getBooleanAttribute("append", !clear); //New attribute append = !clear,

                    String bufferName = getText();
                    DataBuffer buffer = experiment.getBuffer(bufferName);
                    if (buffer == null)
                        throw new phyphoxFileException("Buffer \"" + bufferName + "\" not defined.", xpp.getLineNumber());
                    receivable = new NetworkConnection.NetworkReceivableData(buffer, append);

                    receive.put(id, receivable);
                    break;
                }
                default: //Unknown tag
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
