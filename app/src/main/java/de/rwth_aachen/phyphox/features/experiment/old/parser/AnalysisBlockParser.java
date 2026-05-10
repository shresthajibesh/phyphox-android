package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Vector;

import de.rwth_aachen.phyphox.Analysis;
import de.rwth_aachen.phyphox.DataInput;
import de.rwth_aachen.phyphox.DataOutput;
import de.rwth_aachen.phyphox.FormulaParser;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class AnalysisBlockParser extends XmlBlockParser {

        AnalysisBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, PhyphoxFileException, IOException {

            Vector<Analysis.AnalysisModule.CycleRange> cycles = new Vector<>();
            String cyclesStr = getStringAttribute("cycles");
            if (cyclesStr != null) {
                for (String cycleStr : cyclesStr.split(" ")) {
                    String[] cycleParts = cycleStr.trim().split("-", 3);
                    if (cycleParts.length == 1) {
                        try {
                            int value = Integer.parseInt(cycleParts[0]);
                            cycles.add(new Analysis.AnalysisModule.CycleRange(value, value));
                        } catch (Exception e) {
                            throw new PhyphoxFileException("Invalid cycles attribute "+cyclesStr+".", xpp.getLineNumber());
                        }
                    } else if (cycleParts.length == 2) {
                        try {
                            int start, stop;
                            if (cycleParts[0].length() == 0)
                                start = -1;
                            else
                                start = Integer.parseInt(cycleParts[0]);
                            if (cycleParts[1].length() == 0)
                                stop = -1;
                            else
                                stop = Integer.parseInt(cycleParts[1]);
                            cycles.add(new Analysis.AnalysisModule.CycleRange(start, stop));
                        } catch (Exception e) {
                            throw new PhyphoxFileException("Invalid cycles attribute "+cyclesStr+".", xpp.getLineNumber());
                        }
                    } else {
                        throw new PhyphoxFileException("Invalid cycles attribute "+cyclesStr+".", xpp.getLineNumber());
                    }
                }
            }
            //The cycles string is simply set after the analysis module is instantiated below

            Vector<DataInput> inputs = new Vector<>(); //Will hold the inputs
            Vector<DataOutput> outputs = new Vector<>(); //Will hold the output buffers

            switch (tag.toLowerCase()) {
                case "timer": { //Start-time of analysis

                    boolean linearTime = getBooleanAttribute("linearTime", false);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "offset1970"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.timerAM(experiment, inputs, outputs, linearTime));
                } break;
                case "info": {

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "batteryLevel"; asRequired = false; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "wifiSignalStrength"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "systemVolume"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.infoAM(experiment, inputs, outputs, parent.getBaseContext()));
                } break;
                case "formula": {
                    String formula = getStringAttribute("formula");

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 0; maxCount = 0; valueAllowed = false; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    if (formula == null)
                        throw new PhyphoxFileException("Formula module needs a formula.", xpp.getLineNumber());
                    try {
                        experiment.analysis.add(new Analysis.formulaAM(experiment, inputs, outputs, formula));
                    } catch (FormulaParser.FormulaException e) {
                        throw new PhyphoxFileException("Formula error: " + e.getMessage(), xpp.getLineNumber());
                    }
                } break;
                case "count": { //Absolute value

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "buffer"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "count"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.countAM(experiment, inputs, outputs));
                } break;
                case "if": { //Absolute value

                    boolean less = getBooleanAttribute("less", false);
                    boolean equal = getBooleanAttribute("equal", false);
                    boolean greater = getBooleanAttribute("greater", false);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "a"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "b"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "true"; asRequired = false; minCount = 0; maxCount = 1; valueAllowed = true; emptyAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "false"; asRequired = false; minCount = 0; maxCount = 1; valueAllowed = true; emptyAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "result"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.ifAM(experiment, inputs, outputs, less, equal, greater));
                } break;
                case "average": { //Absolute value

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "buffer"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "average"; asRequired = false; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "stddev"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.averageAM(experiment, inputs, outputs));
                } break;
                case "add": { //input1+input2+input3...

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "summand"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = true; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "sum"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.addAM(experiment, inputs, outputs));
                } break;
                case "subtract": { //input1-input2-input3...

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "minuend"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "subtrahend"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = true; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "difference"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.subtractAM(experiment, inputs, outputs));
                } break;
                case "multiply": { //input1*input2*input3...

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "factor"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = true; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "product"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.multiplyAM(experiment, inputs, outputs));
                } break;
                case "divide": { //input1/input2/input3...

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "dividend"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "divisor"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = true; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "quotient"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.divideAM(experiment, inputs, outputs));
                } break;
                case "power": {//(input1^input2)

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "base"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "exponent"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "power"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.powerAM(experiment, inputs, outputs));
                } break;
                case "gcd": { //Greatest common divisor

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 2; maxCount = 2; valueAllowed = true; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "gcd"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.gcdAM(experiment, inputs, outputs));
                } break;
                case "lcm": { //Least common multiple

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 2; maxCount = 2; valueAllowed = true; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "lcm"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.lcmAM(experiment, inputs, outputs));
                } break;
                case "abs": { //Absolute value

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "abs"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.absAM(experiment, inputs, outputs));
                } break;
                case "round": { //Round
                    boolean floor = getBooleanAttribute("floor", false);
                    boolean ceil = getBooleanAttribute("ceil", false);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "round"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.roundAM(experiment, inputs, outputs, floor, ceil));
                } break;
                case "log": { //nat. logarithm
                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "log"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.logAM(experiment, inputs, outputs));
                } break;
                case "sin": { //Sine
                    boolean deg = getBooleanAttribute("deg", false); //Use degree instead of radians

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "sin"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.sinAM(experiment, inputs, outputs, deg));
                } break;
                case "cos": { //Cosine
                    boolean deg = getBooleanAttribute("deg", false); //Use degree instead of radians

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "cos"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.cosAM(experiment, inputs, outputs, deg));
                } break;
                case "tan": { //Tangens
                    boolean deg = getBooleanAttribute("deg", false); //Use degree instead of radians

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "tan"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.tanAM(experiment, inputs, outputs, deg));
                } break;
                case "sinh": { //Sine

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "sinh"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.sinhAM(experiment, inputs, outputs));
                } break;
                case "cosh": { //Cosine

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "cosh"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.coshAM(experiment, inputs, outputs));
                } break;
                case "tanh": { //Tangens

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "tanh"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.tanhAM(experiment, inputs, outputs));
                } break;
                case "asin": { //Sine
                    boolean deg = getBooleanAttribute("deg", false); //Use degree instead of radians

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "asin"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.asinAM(experiment, inputs, outputs, deg));
                } break;
                case "acos": { //Cosine
                    boolean deg = getBooleanAttribute("deg", false); //Use degree instead of radians

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "acos"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.acosAM(experiment, inputs, outputs, deg));
                } break;
                case "atan": { //Tangens
                    boolean deg = getBooleanAttribute("deg", false); //Use degree instead of radians

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "atan"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.atanAM(experiment, inputs, outputs, deg));
                } break;
                case "atan2": { //Tangens
                    boolean deg = getBooleanAttribute("deg", false); //Use degree instead of radians

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "atan2"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.atan2AM(experiment, inputs, outputs, deg));
                } break;
                case "first": { //First value of each buffer

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = false; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "first"; asRequired = false; minCount = 1; maxCount = 0; repeatableOffset = 0; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.firstAM(experiment, inputs, outputs));
                } break;
                case "max": { //Maximum (takes y as first input and may take x as an optional second, same for outputs)
                    boolean multiple = getBooleanAttribute("multiple", false); //Positive or negative flank

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "threshold"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "max"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "position"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.maxAM(experiment, inputs, outputs, multiple));
                } break;
                case "min": { //Minimum (takes y as first input and may take x as an optional second, same for outputs)
                    boolean multiple = getBooleanAttribute("multiple", false); //Positive or negative flank

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "threshold"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "min"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "position"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.minAM(experiment, inputs, outputs, multiple));
                } break;
                case "threshold": { //Find the index at which the input crosses a threshold
                    boolean falling = getBooleanAttribute("falling", false); //Positive or negative flank

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "threshold"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "position"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.thresholdAM(experiment, inputs, outputs, falling));
                } break;
                case "binning": { //count number of values falling into binning ranges
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "x0"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "dx"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "binStarts"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "binCounts"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.binningAM(experiment, inputs, outputs));
                } break;
                case "map": { //rearrange data from unsorted x, y, and z values suitable for map graphs
                    String zModeStr = getStringAttribute("zMode"); //Positive or negative flank
                    if (zModeStr == null)
                        zModeStr = "average";

                    Analysis.mapAM.ZMode zMode = Analysis.mapAM.ZMode.average;

                    switch (zModeStr) {
                        case "count":   zMode = Analysis.mapAM.ZMode.count;
                                        break;
                        case "sum":     zMode = Analysis.mapAM.ZMode.sum;
                                        break;
                        case "average": zMode = Analysis.mapAM.ZMode.average;
                                        break;
                        default:        throw new PhyphoxFileException("Unknown zMode " + zModeStr, xpp.getLineNumber());
                    }

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "mapWidth"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "minX"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "maxX"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "mapHeight"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "minY"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "maxY"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "z"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "z"; asRequired = true; minCount = 1; maxCount = 1; repeatableOffset = -1; }}
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.mapAM(experiment, inputs, outputs, zMode));
                } break;
                case "append": { //Append the inputs to each other

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = true; emptyAllowed = true; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.appendAM(experiment, inputs, outputs));
                } break;
                case "reduce": { //Reduce number of entries in a buffer

                    boolean averageX = getBooleanAttribute("averageX", false);
                    boolean sumY= getBooleanAttribute("sumY", false);
                    boolean averageY = getBooleanAttribute("averageY", false);

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "factor"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.reduceAM(experiment, inputs, outputs, averageX, sumY, averageY));
                } break;
                case "fft": { //Fourier transform

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "re"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "im"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "re"; asRequired = false; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "im"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.fftAM(experiment, inputs, outputs));
                } break;
                case "autocorrelation": { //Autocorrelation. First in/out is y, second in/out may be x

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "minX"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "maxX"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    Analysis.autocorrelationAM acAM = new Analysis.autocorrelationAM(experiment, inputs, outputs);
                    experiment.analysis.add(acAM);
                } break;
                case "periodicity": { //Periodicity

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "dx"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "overlap"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "min"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "max"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "time"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "period"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }}
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    Analysis.periodicityAM pAM = new Analysis.periodicityAM(experiment, inputs, outputs);
                    experiment.analysis.add(pAM);
                } break;
                case "differentiate": { //Differentiate by subtracting neighboring values

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.differentiateAM(experiment, inputs, outputs));
                } break;
                case "integrate": { //Integration from first value of buffer to each point in buffer

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.integrateAM(experiment, inputs, outputs));
                } break;
                case "crosscorrelation": { //Crosscorrelation requires two inputs and a single output. y only.

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 2; maxCount = 2; valueAllowed = false; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.crosscorrelationAM(experiment, inputs, outputs));
                } break;
                case "gausssmooth": { //Smooth the data with a Gauss profile
                    double sigma = getDoubleAttribute("sigma", 0.);

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    Analysis.gaussSmoothAM gsAM = new Analysis.gaussSmoothAM(experiment, inputs, outputs);
                    if (sigma > 0)
                        gsAM.setSigma(sigma);
                    experiment.analysis.add(gsAM);
                } break;
                case "loess": { //Smooth data with LOESS

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "d"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "xi"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "yi0"; asRequired = false; minCount = 1; maxCount = 0; repeatableOffset = 0; }},
                            new IoBlockParser.ioMapping() {{name = "yi1"; asRequired = true; minCount = 0; maxCount = 0; repeatableOffset = 0; }},
                            new IoBlockParser.ioMapping() {{name = "yi2"; asRequired = true; minCount = 0; maxCount = 0; repeatableOffset = 0; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.loessAM(experiment, inputs, outputs));
                } break;
                case "interpolate": { //Smooth data with LOESS
                    String interpolationMethodStr = getStringAttribute("method");
                    if (interpolationMethodStr == null)
                        interpolationMethodStr = "linear";

                    Analysis.interpolateAM.InterpolationMethod method = Analysis.interpolateAM.InterpolationMethod.linear;

                    switch (interpolationMethodStr) {
                        case "previous":   method = Analysis.interpolateAM.InterpolationMethod.previous;
                            break;
                        case "next":   method = Analysis.interpolateAM.InterpolationMethod.next;
                            break;
                        case "nearest":   method = Analysis.interpolateAM.InterpolationMethod.nearest;
                            break;
                        case "linear":   method = Analysis.interpolateAM.InterpolationMethod.linear;
                            break;
                        default:        throw new PhyphoxFileException("Unknown interpolation methode " + interpolationMethodStr, xpp.getLineNumber());
                    }

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "xi"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 0; repeatableOffset = 0; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.interpolateAM(experiment, inputs, outputs, method));
                } break;
                case "match": { //Arbitrary inputs and outputs, for each input[n] a min[n] and max[n] can be defined. The module filters the inputs in parallel and returns only those sets that match the filters

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = false; repeatableOffset = 0; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 0; repeatableOffset = 0; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.matchAM(experiment, inputs, outputs));
                } break;
                case "rangefilter": { //Arbitrary inputs and outputs, for each input[n] a min[n] and max[n] can be defined. The module filters the inputs in parallel and returns only those sets that match the filters

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = false; repeatableOffset = 0; }},
                            new IoBlockParser.ioMapping() {{name = "min"; asRequired = true; minCount = 0; maxCount = 0; valueAllowed = true; repeatableOffset = 1; }},
                            new IoBlockParser.ioMapping() {{name = "max"; asRequired = true; minCount = 0; maxCount = 0; valueAllowed = true; repeatableOffset = 2; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 0; repeatableOffset = 0; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.rangefilterAM(experiment, inputs, outputs));
                } break;
                case "subrange": { //from, to or length may be defined, arbitrary number of additional inputs and outputs

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "from"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "to"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "length"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = false; repeatableOffset = 0; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 0; repeatableOffset = 0; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.subrangeAM(experiment, inputs, outputs));
                } break;
                case "sort": {

                    boolean descending= getBooleanAttribute("descending", false);

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "in"; asRequired = false; minCount = 1; maxCount = 0; valueAllowed = false; repeatableOffset = 0; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 0; repeatableOffset = 0; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.sortAM(experiment, inputs, outputs, descending));
                } break;
                case "ramp": { //Create a linear ramp (great for creating time-bases)

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "start"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "stop"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "length"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    Analysis.rampGeneratorAM rampAM = new Analysis.rampGeneratorAM(experiment, inputs, outputs);
                    experiment.analysis.add(rampAM);
                } break;
                case "const": { //Initialize a buffer with constant values

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "length"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }}
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    Analysis.constGeneratorAM constAM = new Analysis.constGeneratorAM(experiment, inputs, outputs);
                    experiment.analysis.add(constAM);
                } break;
                case "eventstream": { //Find events in a datastream (i.e. acoustic stopwatch)
                    String triggerModeStr = getStringAttribute("mode");
                    if (triggerModeStr == null)
                        triggerModeStr = "above";

                    Analysis.eventstreamAM.TriggerMode triggerMode;

                    try {
                        triggerMode = Analysis.eventstreamAM.TriggerMode.valueOf(triggerModeStr);
                    } catch (Exception e) {
                        throw new PhyphoxFileException("Unknown trigger mode " + triggerModeStr, xpp.getLineNumber());
                    }

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "data"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "threshold"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "distance"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "index"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "skip"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "last"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "events"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "index"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "skip"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "last"; asRequired = true; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.eventstreamAM(experiment, inputs, outputs, triggerMode));
                } break;
                case "movingaverage": { //Find events in a datastream (i.e. acoustic stopwatch)
                    boolean dropIncomplete = getBooleanAttribute("dropIncomplete", false);

                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "data"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "width"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "data"; asRequired = false; minCount = 1; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.movingaverageAM(experiment, inputs, outputs, dropIncomplete));
                } break;
                case "split": { //Find events in a datastream (i.e. acoustic stopwatch)
                    IoBlockParser.ioMapping[] inputMapping = {
                            new IoBlockParser.ioMapping() {{name = "data"; asRequired = true; minCount = 1; maxCount = 1; valueAllowed = false; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "index"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "overlap"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = true; repeatableOffset = -1; }},
                    };
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out1"; asRequired = false; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                            new IoBlockParser.ioMapping() {{name = "out2"; asRequired = false; minCount = 0; maxCount = 1; repeatableOffset = -1; }},
                    };
                    (new IoBlockParser(xpp, experiment, parent, inputs, outputs, inputMapping, outputMapping, "as")).process(); //Load inputs and outputs

                    experiment.analysis.add(new Analysis.splitAM(experiment, inputs, outputs));
                } break;
                default: //Unknown tag...
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
            experiment.analysis.lastElement().setCycles(cycles);
        }

    }
