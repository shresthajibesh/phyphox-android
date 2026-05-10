package de.rwth_aachen.phyphox.features.experiment.old.parser;

import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.translate;

import android.view.Gravity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.rwth_aachen.phyphox.DataBuffer;
import de.rwth_aachen.phyphox.DataInput;
import de.rwth_aachen.phyphox.DataOutput;
import de.rwth_aachen.phyphox.ExpView;
import de.rwth_aachen.phyphox.GraphView;
import de.rwth_aachen.phyphox.Helper.RGB;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.R;
import de.rwth_aachen.phyphox.SpectroscopyCalibrationManager;
import de.rwth_aachen.phyphox.camera.model.ShowCameraControls;
import de.rwth_aachen.phyphox.features.experiment.ExperimentActivity;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class ViewBlockParser extends XmlBlockParser {
        private ExpView newView;

        GraphView.scaleMode parseScaleMode(String attribute) {
            String scaleStr = getStringAttribute(attribute);
            GraphView.scaleMode scale = GraphView.scaleMode.auto;
            if (scaleStr != null) {
                switch (scaleStr) {
                    case "auto": scale = GraphView.scaleMode.auto;
                        break;
                    case "extend": scale = GraphView.scaleMode.extend;
                        break;
                    case "fixed": scale = GraphView.scaleMode.fixed;
                        break;
                }
            }
            return scale;
        }

        //The viewBlockParser takes an additional argument, which is the expView instance it should fill
        ViewBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, ExperimentActivity parent, ExpView newView) {
            super(xpp, experiment, parent);
            this.newView = newView;
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, PhyphoxFileException, IOException {
            String label = getTranslatedAttribute("label");
            String visibility = getStringAttribute("visibility");
            double factor = getDoubleAttribute("factor", 1.);
            String unit = getTranslatedAttribute("unit");
            Vector<DataInput> inputs = new Vector<>();
            Vector<DataOutput> outputs = new Vector<>();
            switch (tag.toLowerCase()) {
                case "value": { //A value element displays a single value to the user
                    int precision = getIntAttribute("precision", 2);
                    boolean scientific = getBooleanAttribute("scientific", false);
                    double size = getDoubleAttribute("size", 1.0);
                    RGB color = getColorAttribute("color", new RGB(parent.getResources().getColor(R.color.phyphox_white_100)));
                    String format = getStringAttribute("format");
                    String positiveUnit = getTranslatedAttribute("positiveUnit");
                    String negativeUnit = getTranslatedAttribute("negativeUnit");
                    //Allowed input/output configuration
                    Vector<IoBlockParser.AdditionalTag> ats = new Vector<>();
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false;}}
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, null, inputMapping, null, null, ats)).process(); //Load inputs and outputs

                    Vector<String> inStrings = new Vector<>();
                    inStrings.add(inputs.get(0).buffer.name);
                    ExpView.valueElement ve = newView.new valueElement(label, visibility,null, inStrings, parent.getResources()); //Only a value input
                    for (IoBlockParser.AdditionalTag at : ats) {
                        if (at.name.equals("input"))
                            continue;
                        if (!at.name.equals("map")) {
                            throw new PhyphoxFileException("Unknown tag "+at.name+" found by ioBlockParser.", xpp.getLineNumber());
                        }
                        ExpView.valueElement.Mapping map = ve.new Mapping(translate(at.content, parent));
                        if (at.attributes.containsKey("min")) {
                            try {
                                map.min = Double.valueOf(at.attributes.get("min"));
                            } catch (Exception e) {
                                throw new PhyphoxFileException("Could not parse min of map tag.", xpp.getLineNumber());
                            }
                        }
                        if (at.attributes.containsKey("max")) {
                            try {
                                map.max = Double.valueOf(at.attributes.get("max"));
                            } catch (Exception e) {
                                throw new PhyphoxFileException("Could not parse max of map tag.", xpp.getLineNumber());
                            }
                        }
                        ve.addMapping(map);
                    }
                    ve.setPrecision(precision); //Floating point precision
                    ve.setScientificNotation(scientific); //Scientific notation vs. fixed point
                    ve.setUnit(unit); //We can have a unit after the value
                    ve.setFactor(factor); //A conversion factor. Usually for the unit
                    ve.setSize(size); //A conversion factor. Usually for the unit
                    ve.setColor(color);
                    ve.setGpsFormat(format); // Format for Lat Long to represent in degree/degree-minutes/degree-minutes-seconds
                    //A unit to represent direction for gps location
                    ve.setPositiveUnit(positiveUnit);
                    ve.setNegativeUnit(negativeUnit);
                    newView.elements.add(ve);
                    break;
                }
                case "info": { //An info element just shows some text
                    RGB color = getColorAttribute("color", new RGB(parent.getResources().getColor(R.color.phyphox_white_100)));

                    boolean bold = getBooleanAttribute("bold", false);
                    boolean italic = getBooleanAttribute("italic", false);
                    String gravityString = getStringAttribute("align");
                    int gravity = Gravity.START;
                    if (gravityString != null && gravityString.equals("right"))
                        gravity = Gravity.END;
                    else if (gravityString != null && gravityString.equals("center"))
                        gravity = Gravity.CENTER;
                    float size = (float)getDoubleAttribute("size", 1.0);

                    ExpView.infoElement infoe = newView.new infoElement(label, visibility,null, null, parent.getResources()); //No inputs, just the label and resources
                    infoe.setColor(color);
                    infoe.setFormatting(bold, italic, gravity, size);
                    newView.elements.add(infoe);
                    break;
                }
                case "separator": {
                    //An info element just shows some text
                    ExpView.separatorElement separatore = newView.new separatorElement(null, visibility, null, parent.getResources()); //No inputs, just the label and resources
                    RGB c = getColorAttribute("color", new RGB(parent.getResources().getColor(R.color.phyphox_black_60)));
                    float height = (float)getDoubleAttribute("height", 0.1);
                    separatore.setColor(c);
                    separatore.setHeight(height);
                    newView.elements.add(separatore);
                    break;
                }
                case "graph": { //A graph element displays a graph of an y array or two arrays x and y
                    double aspectRatio = getDoubleAttribute("aspectRatio", 2.5);
                    String lineStyle = getStringAttribute("style"); //Line style defaults to "line", but may be "dots"
                    int mapWidth= getIntAttribute("mapWidth", 0);
                    boolean showColorScale = getBooleanAttribute("showColorScale", true);
                    boolean partialUpdate = getBooleanAttribute("partialUpdate", false);
                    int history = getIntAttribute("history", 1);
                    String labelX = getTranslatedAttribute("labelX");
                    String labelY = getTranslatedAttribute("labelY");
                    String labelZ = getTranslatedAttribute("labelZ");
                    String unitX = getTranslatedAttribute("unitX");
                    String unitY = getTranslatedAttribute("unitY");
                    String unitZ = getTranslatedAttribute("unitZ");
                    String unitYX = getTranslatedAttribute("unitYperX");
                    String calibrationMode = getStringAttribute("calibrationMode");

                    Vector<Integer> colorScale = new Vector<>();
                    int colorStepIndex = 1;
                    while (xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE,"mapColor"+colorStepIndex) != null) {
                        RGB color = getColorAttribute("mapColor"+colorStepIndex, new RGB(parent.getResources().getColor(R.color.phyphox_primary)));
                        colorScale.add(color.intColor());
                        colorStepIndex++;
                    }

                    if (unitX == null && unitY == null && unitZ == null && labelX != null && labelY != null) {
                        Pattern pattern = Pattern.compile("^(.+)\\ \\((.+)\\)$");

                        Matcher matcherX = pattern.matcher(labelX);
                        if (matcherX.find()) {
                            labelX =  matcherX.group(1);
                            unitX =  matcherX.group(2);
                        }

                        Matcher matcherY = pattern.matcher(labelY);
                        if (matcherY.find()) {
                            labelY =  matcherY.group(1);
                            unitY =  matcherY.group(2);
                        }

                        if (labelZ != null) {
                            Matcher matcherZ = pattern.matcher(labelZ);
                            if (matcherZ.find()) {
                                labelZ = matcherZ.group(1);
                                unitZ = matcherZ.group(2);
                            }
                        }
                    }
                    boolean timeOnX = getBooleanAttribute("timeOnX", false);
                    boolean timeOnY = getBooleanAttribute("timeOnY", false);
                    boolean systemTime = getBooleanAttribute("systemTime", false);
                    boolean linearTime = getBooleanAttribute("linearTime", false);
                    boolean hideTimeMarkers = getBooleanAttribute("hideTimeMarkers", false);
                    boolean logX = getBooleanAttribute("logX", false);
                    boolean logY = getBooleanAttribute("logY", false);
                    boolean logZ = getBooleanAttribute("logZ", false);
                    double lineWidth = getDoubleAttribute("lineWidth", 1.0);
                    int xPrecision = getIntAttribute("xPrecision", -1);
                    int yPrecision = getIntAttribute("yPrecision", -1);
                    int zPrecision = getIntAttribute("zPrecision", -1);

                    boolean suppressScientificNotation = getBooleanAttribute("suppressScientificNotation", false);
                    RGB color = new RGB(parent.getResources().getColor(R.color.phyphox_primary));
                    boolean globalColor = false;
                    if (xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "color") != null) {
                        color = getColorAttribute("color", new RGB(parent.getResources().getColor(R.color.phyphox_primary)));
                        globalColor = true;
                    }


                    GraphView.scaleMode scaleMinX = parseScaleMode("scaleMinX");
                    GraphView.scaleMode scaleMaxX = parseScaleMode("scaleMaxX");
                    GraphView.scaleMode scaleMinY = parseScaleMode("scaleMinY");
                    GraphView.scaleMode scaleMaxY = parseScaleMode("scaleMaxY");
                    GraphView.scaleMode scaleMinZ = parseScaleMode("scaleMinZ");
                    GraphView.scaleMode scaleMaxZ = parseScaleMode("scaleMaxZ");

                    double minX = getDoubleAttribute("minX", 0.);
                    double maxX = getDoubleAttribute("maxX", 0.);
                    double minY = getDoubleAttribute("minY", 0.);
                    double maxY = getDoubleAttribute("maxY", 0.);
                    double minZ = getDoubleAttribute("minZ", 0.);
                    double maxZ = getDoubleAttribute("maxZ", 0.);

                    boolean followX = getBooleanAttribute("followX", false);

                    //Allowed input/output configuration
                    Vector<IoBlockParser.AdditionalTag> ats = new Vector<>();
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = false; repeatableOffset = 0;}},
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 0; valueAllowed = false; repeatableOffset = 1;}},
                            new IoBlockParser.ioMapping() {{name = "z"; asRequired = true; minCount = 0; maxCount = 0; valueAllowed = false; repeatableOffset = 2;}}
                    };

                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{ name = "slope"; asRequired = false; minCount = 0; maxCount = 0; valueAllowed = false; repeatableOffset = 0;}},
                            new IoBlockParser.ioMapping() {{ name = "intercept"; asRequired = false; minCount = 0; maxCount = 0; valueAllowed = false; repeatableOffset = 0;}}
                    };

                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "axis", ats)).process(); //Load inputs and outputs

                    Vector<String> inStrings = new Vector<>();
                    for (int i = 0; i < inputs.size(); i++) {
                        if (i % 3 == 2) {
                            //This is a z entry. For efficiency reasons, we only handle x and y and encode z as an additional graph of different style
                            if (inputs.get(i) != null) {
                                inStrings.add(inputs.get(i).buffer.name);
                                inStrings.add(null);
                                IoBlockParser.AdditionalTag at = new IoBlockParser.AdditionalTag();
                                at.name = ats.get(i).name;
                                at.attributes.put("style", "mapZ");
                                ats.add(i+1, at);
                            }
                        } else {
                            if (inputs.get(i) != null)
                                inStrings.add(inputs.get(i).buffer.name);
                            else
                                inStrings.add(null);
                        }
                    }

                    Vector<String> outStrings = new Vector<>();
                    for(int i=0; i < outputs.size(); i++){
                        if(outputs.get(i) != null){
                            outStrings.add(outputs.get(i).buffer.name);
                        } else {
                            outStrings.add(null);
                        }
                    }

                    ExpView.graphElement ge = newView.new graphElement(label, visibility, outStrings, inStrings, parent.getResources()); //Two array inputs
                    ge.setAspectRatio(aspectRatio); //Aspect ratio of the whole element area icluding axes

                    if (lineStyle != null) {
                        ge.setStyle(GraphView.styleFromStr(lineStyle));
                    }
                    ge.setShowColorScale(showColorScale);
                    ge.setMapWidth(mapWidth);
                    ge.setColorScale(colorScale);
                    ge.setLineWidth(lineWidth);
                    ge.setColor(color, parent.getResources());
                    ge.setScaleModeX(scaleMinX, minX, scaleMaxX, maxX);
                    ge.setScaleModeY(scaleMinY, minY, scaleMaxY, maxY);
                    ge.setScaleModeZ(scaleMinZ, minZ, scaleMaxZ, maxZ);
                    ge.setPartialUpdate(partialUpdate); //Will data only be appended? Will save bandwidth if we do not need to update the whole graph each time, especially on the web-interface
                    ge.setFollowX(followX);
                    ge.setHistoryLength(history); //If larger than 1 the previous n graphs remain visible in a different color
                    ge.setLabel(labelX, labelY, labelZ, unitX, unitY, unitZ, unitYX);  //x- and y- label and units
                    ge.setTimeAxes(timeOnX, timeOnY, systemTime, linearTime, hideTimeMarkers);
                    ge.setLogScale(logX, logY, logZ); //logarithmic scales for x/y axes
                    ge.setPrecision(xPrecision, yPrecision, zPrecision); //logarithmic scales for x/y axes
                    ge.setSuppressScientificNotation(suppressScientificNotation);
                    ge.setCalibrationMode(SpectroscopyCalibrationManager.calibrationModeFromString(calibrationMode));

                    if (!globalColor) {
                        for (int i = 0; i < Math.ceil(ats.size() / 3); i++) {
                            switch (i % 6) {
                                case 0: ge.setColor(new RGB(parent.getResources().getColor(R.color.phyphox_primary)), i, parent.getResources());
                                    break;
                                case 1: ge.setColor(new RGB(parent.getResources().getColor(R.color.phyphox_green)), i, parent.getResources());
                                    break;
                                case 2: ge.setColor(new RGB(parent.getResources().getColor(R.color.phyphox_blue_60)), i, parent.getResources());
                                    break;
                                case 3: ge.setColor(new RGB(parent.getResources().getColor(R.color.phyphox_yellow)), i, parent.getResources());
                                    break;
                                case 4: ge.setColor(new RGB(parent.getResources().getColor(R.color.phyphox_magenta)), i, parent.getResources());
                                    break;
                                case 5: ge.setColor(new RGB(parent.getResources().getColor(R.color.phyphox_red)), i, parent.getResources());
                                    break;
                            }
                        }
                    }
                    for (int i = 0; i < ats.size(); i++) {
                        IoBlockParser.AdditionalTag at = ats.get(i);
                        if (at == null)
                            continue;
                        if (!at.name.equals("input") && !at.name.equals("output") ) {
                            throw new PhyphoxFileException("Unknown tag "+at.name+" found by ioBlockParser.", xpp.getLineNumber());
                        }
                        if (at.attributes.containsKey("style")) {
                            try {
                                GraphView.Style style = GraphView.styleFromStr(at.attributes.get("style"));
                                if (style == GraphView.Style.unknown)
                                    throw new PhyphoxFileException("Unknown value for style of input tag.", xpp.getLineNumber());
                                ge.setStyle(style, i/3);
                            } catch (Exception e) {
                                throw new PhyphoxFileException("Could not parse style of input tag.", xpp.getLineNumber());
                            }
                        }
                        if (at.attributes.containsKey("color")) {
                            RGB localColor = RGB.fromPhyphoxString(at.attributes.get("color"), parent.getResources(), new RGB(parent.getResources().getColor(R.color.phyphox_primary)));
                            ge.setColor(localColor, i/3, parent.getResources());
                        }
                        if (at.attributes.containsKey("linewidth")) {
                            try {
                                ge.setLineWidth(Double.valueOf(at.attributes.get("linewidth")), i/3);
                            } catch (Exception e) {
                                throw new PhyphoxFileException("Could not parse linewidth of input tag.", xpp.getLineNumber());
                            }
                        }
                        if (at.attributes.containsKey("mapwidth")) {
                            try {
                                ge.setMapWidth(Integer.valueOf(at.attributes.get("mapwidth")), i/3);
                            } catch (Exception e) {
                                throw new PhyphoxFileException("Could not parse mapWidth of input tag.", xpp.getLineNumber());
                            }
                        }
                    }

                    newView.elements.add(ge);
                    break;
                }
                case "edit": { //The edit element can take input from the user
                    boolean signed = getBooleanAttribute("signed", true);
                    boolean decimal = getBooleanAttribute("decimal", true);
                    double defaultValue = getDoubleAttribute("default", 0.);
                    boolean isEditable = getBooleanAttribute("editable", true);

                    double min = getDoubleAttribute("min", Double.NEGATIVE_INFINITY);
                    double max = getDoubleAttribute("max", Double.POSITIVE_INFINITY);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; }}
                    };
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, null)).process(); //Load inputs and outputs

                    ExpView.editElement ie = newView.new editElement(label, visibility, outputs.get(0).buffer.name, null, parent.getResources()); //Ouput only
                    ie.setUnit(unit); //A unit displayed next to the input box
                    ie.setFactor(factor); //A scaling factor. Mostly for matching units
                    ie.setSigned(signed); //May the entered number be negative?
                    ie.setDecimal(decimal); //May the user enter a decimal point (non-integer values)?
                    ie.setDefaultValue(defaultValue); //Default value before the user entered anything
                    ie.setLimits(min, max);
                    ie.setEditable(isEditable);
                    newView.elements.add(ie);
                    break;
                }
                case "button": { //The edit element can take input from the user
                    String dynamicBuffer = getStringAttribute("dynamicLabel");
                    //Allowed input/output configuration
                    Vector<IoBlockParser.AdditionalTag> ats = new Vector<>();
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 0; maxCount = 0; valueAllowed = true; emptyAllowed = true; repeatableOffset = 0;}},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 0; maxCount = 0; repeatableOffset = 0;}}
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, null, ats)).process(); //Load inputs and outputs

                    Vector<String> inStrings = new Vector<>();
                    if (dynamicBuffer != null)
                        inStrings.add(dynamicBuffer);

                    ExpView.buttonElement be = newView.new buttonElement(label, visibility, null, inStrings, parent.getResources()); //This one is user-event driven and does not regularly read or write values
                    be.setIO(inputs, outputs);
                    Vector<String> triggers = new Vector<>();
                    for (IoBlockParser.AdditionalTag at : ats) {
                        if (at.name.equals("input"))
                            continue;
                        if (at.name.equals("output"))
                            continue;
                        if (at.name.equals("map")) {
                            ExpView.buttonElement.ButtonMapping map = be.new ButtonMapping(translate(at.content, parent));
                            if (at.attributes.containsKey("min")) {
                                try {
                                    map.min = Double.valueOf(at.attributes.get("min"));
                                } catch (Exception e) {
                                    throw new PhyphoxFileException("Could not parse min of map tag.", xpp.getLineNumber());
                                }
                            }
                            if (at.attributes.containsKey("max")) {
                                try {
                                    map.max = Double.valueOf(at.attributes.get("max"));
                                } catch (Exception e) {
                                    throw new PhyphoxFileException("Could not parse max of map tag.", xpp.getLineNumber());
                                }
                            }
                            be.addMapping(map);
                            continue;
                        }
                        if (!at.name.equals("trigger")) {
                            throw new PhyphoxFileException("Unknown tag " + at.name + " found by ioBlockParser.", xpp.getLineNumber());
                        }

                        triggers.add(at.content);
                    }


                    if(dynamicBuffer != null){
                        DataBuffer buffer = experiment.getBuffer(dynamicBuffer);
                        if(buffer == null){
                            throw new PhyphoxFileException("Could not parse buffer with name " + dynamicBuffer, xpp.getLineNumber());
                        }
                        be.setDynamicBuffer(buffer);
                    }

                    be.setTriggers(triggers);
                    newView.elements.add(be);
                    break;
                }
                case "depth-gui": {
                    //GUI for the depth input (LiDAR/ToF)
                    double aspectRatio = getDoubleAttribute("aspectRatio", 2.5);
                    ExpView.depthGuiElement dge = newView.new depthGuiElement(label, visibility,null, null, parent.getResources()); //Two array inputs
                    dge.setAspectRatio(aspectRatio);
                    newView.elements.add(dge);
                    break;
                }
                case "image": { // Shows an image
                    String src = getStringAttribute("src");
                    if (src == null || src.isEmpty())
                        throw new PhyphoxFileException("Image element requires src attribute.", xpp.getLineNumber());

                    ExpView.imageElement img = newView.new imageElement(null, visibility,null, parent.getResources(), src); //No inputs (for now?)

                    float scale = (float)getDoubleAttribute("scale", 1.0);
                    img.setScale(scale);

                    ExpView.ImageFilter darkFilter = ExpView.ImageFilter.none;
                    ExpView.ImageFilter lightFilter = ExpView.ImageFilter.none;
                    String darkFilterStr = getStringAttribute("darkFilter");
                    String lightFilterStr = getStringAttribute("lightFilter");
                    if (darkFilterStr != null && !darkFilterStr.isEmpty()) {
                        try {
                            darkFilter = ExpView.ImageFilter.valueOf(darkFilterStr);
                        } catch (Exception e) {
                            throw new PhyphoxFileException("Unknown image filter: " + darkFilterStr, xpp.getLineNumber());
                        }
                    }
                    if (lightFilterStr != null && !lightFilterStr.isEmpty()) {
                        try {
                            lightFilter = ExpView.ImageFilter.valueOf(lightFilterStr);
                        } catch (Exception e) {
                            throw new PhyphoxFileException("Unknown image filter: " + lightFilterStr, xpp.getLineNumber());
                        }
                    }


                    img.setFilters(darkFilter, lightFilter);

                    newView.elements.add(img);
                    experiment.resources.add(src);
                    break;
                }
                case "camera-gui": {
                    String showControls = getStringAttribute("show_controls");
                    if (showControls == null) {
                        showControls = "full_view_only";
                    } else {
                        showControls = showControls.toLowerCase();
                    }

                    int exposureAdjustmentLevel = getIntAttribute("exposure_adjustment_level", 1);

                    ShowCameraControls showCameraControls;
                    switch (showControls){
                        case "always":{
                            showCameraControls = ShowCameraControls.Always;
                            break;
                        } case "never": {
                            showCameraControls = ShowCameraControls.Never;
                            break;
                        } case "full_view_only": {
                            showCameraControls = ShowCameraControls.FullViewOnly;
                            break;
                        }
                        default: {
                            throw new PhyphoxFileException("Unknown show controls name: " + showControls, xpp.getLineNumber());
                        }
                    }

                    boolean grayscale = getBooleanAttribute("grayscale", false);
                    RGB markOverexposure = getColorAttribute("markOverexposure", null);
                    RGB markUnderexposure = getColorAttribute("markUnderexposure", null);

                    ExpView.cameraElement cameraElement = newView.new cameraElement(label, visibility, null, null, parent.getResources());
                    cameraElement.applyControlSettings(showCameraControls, exposureAdjustmentLevel);
                    cameraElement.setPreviewParameters(grayscale, markOverexposure, markUnderexposure);
                    newView.elements.add(cameraElement);
                    break;
                }
                case "toggle": {

                    double defaultValue = getDoubleAttribute("default", 0.0);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; }}
                    };
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, null)).process(); //Load inputs and outputs


                    ExpView.toggleElement toggleElement = newView.new toggleElement(label, visibility, outputs.get(0).buffer.name, null, parent.getResources());
                    toggleElement.setDefaultValue(defaultValue);
                    newView.elements.add(toggleElement);
                    break;

                }
                case "dropdown": {
                    double defaultValue = getDoubleAttribute("default", 0.0);
                    RGB color = getColorAttribute("color", new RGB(parent.getResources().getColor(R.color.phyphox_white_100)));

                    Vector<IoBlockParser.AdditionalTag> ats = new Vector<>();
                    //Allowed output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; }}
                    };
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, null, ats)).process(); //Load inputs and outputs

                    ExpView.dropDownElement dropDownElement = newView.new dropDownElement(label, visibility, outputs.get(0).buffer.name, null, parent.getResources());
                    dropDownElement.setDefaultValue(defaultValue);
                    dropDownElement.setColor(color);
                    for(IoBlockParser.AdditionalTag at: ats){
                        if(at.name.equals("output")){
                            continue;
                        }
                        if (!at.name.equals("map")) {
                            throw new PhyphoxFileException("Unknown tag "+at.name+" found by ioBlockParser.", xpp.getLineNumber());
                        }
                        ExpView.dropDownElement.Mapping map = dropDownElement.new Mapping(translate(at.content, parent));
                        if(at.attributes.containsKey("value")){
                            try {
                                map.value = at.attributes.get("value");
                            } catch (Exception e){
                                throw new PhyphoxFileException("Could not parse value tag.", xpp.getLineNumber());
                            }
                        }
                        dropDownElement.addMapping(map);
                    }


                    newView.elements.add(dropDownElement);
                    break;

                }
                case "slider" : {
                    double defaultValue = getDoubleAttribute("default", 0.0);
                    double minValue = getDoubleAttribute("minValue", 0.0);
                    double maxValue = getDoubleAttribute("maxValue", 1.0);
                    double stepSize = getDoubleAttribute("stepSize", 1.0);
                    int precision = getIntAttribute("precision", 2);
                    String type = getStringAttribute("type");
                    Boolean showValue = getBooleanAttribute("showValue", true);
                    RGB color = getColorAttribute("color", new RGB(parent.getResources().getColor(R.color.phyphox_white_100)));

                    ExpView.SliderType sliderType = (Objects.equals(type, "range")) ? ExpView.SliderType.Range : ExpView.SliderType.Normal;

                    Vector<String> outStrings = new Vector<>();
                    if(sliderType == ExpView.SliderType.Normal){
                        IoBlockParser.ioMapping[] outputMapping = {
                                new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; }}
                        };
                        (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, null)).process(); //Load inputs and outputs
                        outStrings.add(outputs.get(0).buffer.name);
                    } else {
                        IoBlockParser.ioMapping[] outputMapping = {
                                new IoBlockParser.ioMapping() {{name = "lowerValue"; asRequired = false; minCount = 1; maxCount = 1; }},
                                new IoBlockParser.ioMapping() {{name = "upperValue"; asRequired = false; minCount = 1; maxCount = 1; }}
                        };
                        (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, "value")).process(); //Load inputs and outputs

                        outStrings.add(outputs.get(0).buffer.name);
                        outStrings.add(outputs.get(1).buffer.name);
                    }

                    ExpView.sliderElement sliderElement = newView.new sliderElement(label, visibility, outStrings, null, parent.getResources());
                    sliderElement.setDefaultValue(defaultValue);
                    sliderElement.setColor(color);
                    sliderElement.setMinValue(minValue);
                    sliderElement.setMaxValue(maxValue);
                    sliderElement.setStepSize(stepSize);
                    sliderElement.setPrecision(precision);
                    sliderElement.setType(sliderType);
                    sliderElement.setShowValue(showValue);

                    newView.elements.add(sliderElement);
                    break;
                }
                default: //Unknown tag...
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
