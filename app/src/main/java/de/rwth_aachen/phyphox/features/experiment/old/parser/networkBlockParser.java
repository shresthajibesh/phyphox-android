package de.rwth_aachen.phyphox.features.experiment.old.parser;

private static class networkBlockParser extends XmlBlockParser {

        networkBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, phyphoxFileException {
            switch (tag.toLowerCase()) {
                case "connection":
                    String id = getStringAttribute("id");
                    String privacyURL = getStringAttribute("privacy");
                    String address = getStringAttribute("address");
                    String discoveryAddress = getStringAttribute("discoveryAddress");
                    boolean autoConnect = getBooleanAttribute("autoConnect", false);
                    double interval = getDoubleAttribute("interval", 0.0);

                    String discoveryStr = getStringAttribute("discovery");
                    NetworkDiscovery.Discovery discovery = null;
                    if (discoveryStr != null) {
                        switch (discoveryStr) {
                            case "http":
                                discovery = new NetworkDiscovery.Http(discoveryAddress);
                                break;
                            default:
                                throw new phyphoxFileException("Unknown discovery "+discoveryStr, xpp.getLineNumber());
                        }
                    }

                    String serviceStr = getStringAttribute("service");
                    NetworkService.Service service = null;
                    if (serviceStr != null) {
                        switch (serviceStr) {
                            case "http/get":
                                service = new NetworkService.HttpGet();
                                break;
                            case "http/post":
                                service = new NetworkService.HttpPost();
                                break;
                            case "mqtt/csv": {
                                    String receiveTopicStr = getStringAttribute("receiveTopic");
                                    if (receiveTopicStr == null)
                                        receiveTopicStr = "";
                                    service = new MqttCsv(receiveTopicStr, parent.getApplicationContext());
                                }
                                break;
                            case "mqtt/json": {
                                    String receiveTopicStr = getStringAttribute("receiveTopic");
                                    String sendTopicStr = getStringAttribute("sendTopic");
                                    boolean persistence = getBooleanAttribute("persistence",false);
                                    if (receiveTopicStr == null)
                                        receiveTopicStr = "";
                                    if (sendTopicStr == null || sendTopicStr.isEmpty())
                                        throw new phyphoxFileException("sendTopic must be set for the mqtt/json service. Use mqtt/csv if you do not intent to send anything.", xpp.getLineNumber());
                                    service = new MqttJson(receiveTopicStr, sendTopicStr, parent.getApplicationContext(),persistence);
                                }
                                break;
                            case "mqtts/json" : {
                                String receiveTopicStr = getStringAttribute("receiveTopic");
                                String sendTopicStr = getStringAttribute("sendTopic");
                                String password = getStringAttribute("password");
                                String username = getStringAttribute("username");
                                boolean persistence = getBooleanAttribute("persistence",false);

                                if (receiveTopicStr == null)
                                    receiveTopicStr = "";
                                if (sendTopicStr == null || sendTopicStr.isEmpty())
                                    throw new phyphoxFileException("sendTopic must be set for the mqtts/json service. Use mqtt/csv if you do not intent to send anything.", xpp.getLineNumber());
                                if (password == null || password.isEmpty())
                                    throw new phyphoxFileException("password must be set for the mqtts/json service.", xpp.getLineNumber());
                                if (username == null || username.isEmpty())
                                    throw new phyphoxFileException("username must be set for the mqtts/json service.", xpp.getLineNumber());
                                service = new MqttTlsJson(receiveTopicStr,sendTopicStr,username,password,parent.getApplicationContext(),persistence);
                            }
                            break;
                            case "mqtts/csv" : {
                                String receiveTopicStr = getStringAttribute("receiveTopic");
                                String password = getStringAttribute("password");
                                String username = getStringAttribute("username");

                                if (receiveTopicStr == null)
                                    receiveTopicStr = "";
                                if (password == null || password.isEmpty())
                                    throw new phyphoxFileException("password must be set for the mqtts/csv service.", xpp.getLineNumber());
                                if (username == null || username.isEmpty())
                                    throw new phyphoxFileException("username must be set for the mqtts/csv service.", xpp.getLineNumber());
                                service = new MqttTlsCsv(receiveTopicStr,username,password,parent.getApplicationContext());
                            }
                            break;
                            default:
                                throw new phyphoxFileException("Unknown service "+serviceStr, xpp.getLineNumber());
                        }
                    }

                    String conversionStr = getStringAttribute("conversion");
                    NetworkConversion.Conversion conversion = null;
                    if (conversionStr != null) {
                        switch (conversionStr) {
                            case "none":
                                conversion = new NetworkConversion.None();
                                break;
                            case "csv":
                                conversion = new NetworkConversion.Csv();
                                break;
                            case "json":
                                conversion = new NetworkConversion.Json();
                                break;
                            default:
                                throw new phyphoxFileException("Unknown conversion "+conversionStr, xpp.getLineNumber());
                        }
                    } else
                        conversion = new NetworkConversion.None();

                    Map<String, NetworkConnection.NetworkSendableData> send = new HashMap<>();
                    Map<String, NetworkConnection.NetworkReceivableData> receive = new HashMap<>();


                    (new networkConnectionBlockParser(xpp, experiment, parent, send, receive)).process();

                    experiment.networkConnections.add(new NetworkConnection(id, privacyURL, address, discovery, autoConnect, service, conversion, send, receive, interval, parent));

                    break;
                default: //Unknown tag...
                    throw new phyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
