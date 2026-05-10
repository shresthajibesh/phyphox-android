package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.rwth_aachen.phyphox.DataBuffer;
import de.rwth_aachen.phyphox.DataInput;
import de.rwth_aachen.phyphox.DataOutput;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class ioBlockParser extends XmlBlockParser {

        public static class ioMapping {
            String name;
            boolean asRequired = true;
            int repeatableOffset = -1;
            boolean valueAllowed = true;
            boolean emptyAllowed = false;
            int minCount = 0;
            int maxCount = 0;
            int count = 0;
        }

        public static class AdditionalTag {
            String name;
            String content;
            Map<String, String> attributes = new HashMap<>();
        }
        Vector<AdditionalTag> additionalTags;

        Vector<DataInput> inputList;
        Vector<DataOutput> outputList;
        ioMapping[] inputMapping;
        ioMapping[] outputMapping;
        String mappingAttribute;

        ioBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent, Vector<DataInput> inputList, Vector<DataOutput> outputList, ioMapping[] inputMapping, ioMapping[] outputMapping, String mappingAttribute) {
            this(xpp, experiment, parent, inputList, outputList, inputMapping, outputMapping, mappingAttribute, null);
        }

        ioBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent, Vector<DataInput> inputList, Vector<DataOutput> outputList, ioMapping[] inputMapping, ioMapping[] outputMapping, String mappingAttribute, Vector<AdditionalTag> additionalTags) {
            super(xpp, experiment, parent);
            this.inputList = inputList;
            this.outputList = outputList;
            this.inputMapping = inputMapping;
            this.outputMapping = outputMapping;
            this.mappingAttribute = mappingAttribute;
            this.additionalTags = additionalTags;
        }

        @Override
        protected void processStartTag(String tag) throws IOException, XmlPullParserException, PhyphoxFileException {
            int targetIndex = -1; //This will hold the index of the inputList or outputList entry, that should be mapped to the given buffer
            int mappingIndex = -1; //This will hold the index of the inputMapping or outputMapping, that holds the rules for this mapping
            String mapping;

            //Get the mapping
            if (mappingAttribute != null)
                mapping = getStringAttribute(mappingAttribute);
            else
                mapping = null;

            AdditionalTag at = null;
            if (additionalTags != null) {
                at = new AdditionalTag();
                for (int i = 0; i < xpp.getAttributeCount(); i++)
                    at.attributes.put(xpp.getAttributeName(i).toLowerCase(), xpp.getAttributeValue(i));
                at.name = tag.toLowerCase();
            }

            switch (tag.toLowerCase()) {
                case "input":   //Input tag
                    if (inputMapping == null) //We did not even expect inputs here...
                        throw new PhyphoxFileException("No input expected.", xpp.getLineNumber());

                    //Check the type
                    String type = getStringAttribute("type");
                    if (type == null)
                        type = "buffer";

                    if (mapping != null) {
                        //An explicit mapping has been given

                        //Check if there is a matching inputMapping
                        for (int i = 0; i < inputMapping.length; i++) {
                            if (inputMapping[i].name.equals(mapping)) {
                                targetIndex = i;
                                mappingIndex = i;
                                break;
                            }
                        }

                        if (targetIndex < 0) //No mapping found at all
                            throw new PhyphoxFileException("Could not find mapping for input \""+mapping+"\".", xpp.getLineNumber());

                        //Increase the inputList if necessary
                        if (targetIndex >= inputList.size())
                            inputList.setSize(targetIndex+1);

                        //If the targetIndex is not yet mapped, we are done here. If not, we have to check if this entry is repeatable, so we can map it again
                        if (inputList.get(targetIndex) != null || inputMapping[targetIndex].repeatableOffset >= 0) {
                            if (inputMapping[targetIndex].repeatableOffset >= 0) {
                                //It is repeatable. Let's calculate a new index according to the repeatable offset
                                int repeatPeriod = inputMapping[inputMapping.length-1].repeatableOffset+1;
                                //If the value is repeatable, we want to add it to the last current repeatable group
                                while (targetIndex-inputMapping[mappingIndex].repeatableOffset+repeatPeriod < inputList.size())
                                    targetIndex += repeatPeriod;
                                //Increase the inputList if necessary
                                if (targetIndex >= inputList.size())
                                    inputList.setSize(targetIndex+1);
                                //Recalculate the index while the input entry is still in use
                                while (inputList.get(targetIndex) != null) {
                                    targetIndex += repeatPeriod;
                                    if (targetIndex >= inputList.size())
                                        inputList.setSize(targetIndex+1);
                                }
                            } else {
                                //Already set and not repeatable.
                                throw new PhyphoxFileException("The input \""+mapping+"\" has already been defined.", xpp.getLineNumber());
                            }
                        }
                    } else {
                        //No explicit mapping, we have to fill the entries that do not require the "as" attribute
                        int firstRepeatable = -1; //If there is a repeatable entry, we will remember its index here

                        //First search for an empty entry, that does not require "as"
                        for (int i = 0; i < inputMapping.length; i++) {
                            if (!inputMapping[i].asRequired) {
                                //While we are already iterating this list: Remember the repeatable entries
                                if (inputMapping[i].repeatableOffset >= 0) {
                                    if (firstRepeatable < 0)
                                        firstRepeatable = i;
                                }
                                //Resize inputList if necessary
                                if (i >= inputList.size())
                                    inputList.setSize(i+1);

                                //Is this entry empty? Great, we have found our target
                                if (inputList.get(i) == null) {
                                    targetIndex = i;
                                    mappingIndex = i;
                                    break;
                                }
                            }
                        }
                        //Target not found? Let's try to fill repeatables.
                        if (targetIndex < 0) {
                            if (firstRepeatable >= 0) {
                                //We have repeatables. So let's just dump our inputs at the end of the list
                                int repeatPeriod = inputMapping[inputMapping.length-1].repeatableOffset+1;
                                targetIndex = inputMapping.length;
                                int repeatIndex = 0; //We have to keep track of where we place it, so we know which mapping we just used
                                if (targetIndex >= inputList.size())
                                    inputList.setSize(targetIndex+1);
                                while (inputList.get(targetIndex) != null || inputMapping[firstRepeatable+repeatIndex].asRequired) {
                                    targetIndex++; //Still not empty. Next one.
                                    repeatIndex = (repeatIndex+1)%repeatPeriod; //Next also means, that we have the next mapping. At the end of all repeatables we start over again.
                                    if (targetIndex >= inputList.size())
                                        inputList.setSize(targetIndex+1);
                                }
                                mappingIndex = firstRepeatable + repeatIndex;
                            } else //Not found and no repeatables. Let's complain.
                                throw new PhyphoxFileException("The non-mapped input from buffer " + getText() + " could not be matched.", xpp.getLineNumber());
                        }
                    }

                    //targetIndex should now point to the index in input list, where the input should be placed.
                    //mappingIndex points to the index in inputMapping, which describes its mapping
                    inputMapping[mappingIndex].count++;

                    //The input may have different types...
                    if (type.equals("value")) {
                        //Just a value, Is this allowed?
                        if (inputMapping[mappingIndex].valueAllowed) {
                            double value;
                            try {
                                value = Double.valueOf(getText());
                            } catch (NumberFormatException e) {
                                throw new PhyphoxFileException("Invalid number format.", xpp.getLineNumber());
                            }
                            inputList.set(targetIndex, new DataInput(value));
                        } else {
                            throw new PhyphoxFileException("Value-type not allowed for input \""+inputMapping[mappingIndex].name+"\".", xpp.getLineNumber());
                        }
                    } else if (type.equals("buffer")) {

                        //Check the type
                        boolean clearAfterRead = getBooleanAttribute("clear", true); //Deprecated
                        boolean keep = getBooleanAttribute("keep", !clearAfterRead); //New attribute keep = !clear,

                        //This is a buffer. Let's see if it exists
                        String bufferName = getText();
                        if (additionalTags != null)
                            at.content = bufferName;
                        DataBuffer buffer = experiment.getBuffer(bufferName);
                        if (buffer == null)
                            throw new PhyphoxFileException("Buffer \""+bufferName+"\" not defined.", xpp.getLineNumber());
                        else {
                            inputList.set(targetIndex, new DataInput(buffer, keep));
                        }
                    } else if (type.equals("empty")) {
                        //No input, Is this allowed?
                        if (inputMapping[mappingIndex].emptyAllowed) {
                            inputList.set(targetIndex, new DataInput());
                        } else {
                            throw new PhyphoxFileException("Value-type not allowed for input \""+inputMapping[mappingIndex].name+"\".", xpp.getLineNumber());
                        }
                    } else {
                        throw new PhyphoxFileException("Unknown input type \""+type+"\".", xpp.getLineNumber());
                    }

                    break;
                case "output":
                    if (outputMapping == null)
                        throw new PhyphoxFileException("No output expected.", xpp.getLineNumber());

                    //Check the type
                    boolean clearBeforeWrite = getBooleanAttribute("clear", true); //Deprecated
                    boolean append = getBooleanAttribute("append", !clearBeforeWrite); //New attribute append = !clear,

                    if (mapping != null) {
                        for (int i = 0; i < outputMapping.length; i++) {
                            if (outputMapping[i].name.equals(mapping)) {
                                targetIndex = i;
                                mappingIndex = i;
                                break;
                            }
                        }
                        if (targetIndex < 0)
                            throw new PhyphoxFileException("Could not find mapping for output \""+mapping+"\".", xpp.getLineNumber());
                        if (targetIndex >= outputList.size())
                            outputList.setSize(targetIndex+1);
                        if (outputList.get(targetIndex) != null) {
                            if (outputMapping[targetIndex].repeatableOffset >= 0) {
                                targetIndex = outputMapping.length + outputMapping[targetIndex].repeatableOffset;
                                if (targetIndex >= outputList.size())
                                    outputList.setSize(targetIndex+1);
                                while (outputList.get(targetIndex) != null) {
                                    targetIndex += outputMapping[outputMapping.length-1].repeatableOffset+1;
                                    if (targetIndex >= outputList.size())
                                        outputList.setSize(targetIndex+1);
                                }
                            } else {
                                throw new PhyphoxFileException("The output \""+mapping+"\" has already been defined.", xpp.getLineNumber());
                            }
                        }
                    } else {
                        int firstRepeatable = -1;
                        int repeatPeriod = 0;
                        for (int i = 0; i < outputMapping.length; i++) {
                            if (!outputMapping[i].asRequired) {
                                if (outputMapping[i].repeatableOffset >= 0) {
                                    if (firstRepeatable < 0)
                                        firstRepeatable = i;
                                    repeatPeriod = outputMapping[i].repeatableOffset+1;
                                }
                                if (i >= outputList.size())
                                    outputList.setSize(i+1);
                                if (outputList.get(i) == null) {
                                    targetIndex = i;
                                    mappingIndex = i;
                                    break;
                                }
                            }
                        }
                        if (targetIndex < 0) {
                            if (firstRepeatable >= 0) {
                                targetIndex = outputMapping.length;
                                int repeatIndex = 0;
                                if (targetIndex >= outputList.size())
                                    outputList.setSize(targetIndex+1);
                                while (outputList.get(targetIndex) != null) {
                                    targetIndex++;
                                    repeatIndex = (repeatIndex+1)%repeatPeriod;
                                    if (targetIndex >= outputList.size())
                                        outputList.setSize(targetIndex+1);
                                }
                                mappingIndex = firstRepeatable + repeatIndex;
                            } else
                                throw new PhyphoxFileException("The non-mapped output could not be matched.", xpp.getLineNumber());
                        }
                    }

                    //targetIndex should now point to the index in output list, where the output should be placed.
                    //mappingIndex points to the index in outputMapping, which describes its mapping
                    outputMapping[mappingIndex].count++;


                    String bufferName = getText();
                    if (additionalTags != null)
                        at.content = bufferName;
                    DataBuffer buffer = experiment.getBuffer(bufferName);
                    if (buffer == null)
                        throw new PhyphoxFileException("Buffer \""+bufferName+"\" not defined.", xpp.getLineNumber());
                    else {
                        outputList.set(targetIndex, new DataOutput(buffer, append));
                    }
                    break;
                default: //Unknown tag...
                    if (additionalTags == null)
                        throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
                    at.content = getText();
            }
            if (additionalTags != null) {
                if (targetIndex > -1) {
                    if (targetIndex >= additionalTags.size())
                        additionalTags.setSize(targetIndex + 1);
                    additionalTags.set(targetIndex, at);
                } else
                    additionalTags.add(at);
            }
        }

        @Override
        protected void done() throws PhyphoxFileException {
            //Check if the number of inputs and outputs are valid
            if (inputMapping != null) {
                for (int i = 0; i < inputMapping.length; i++) {
                    if ((inputMapping[i].maxCount > 0 && inputMapping[i].count > inputMapping[i].maxCount))
                        throw new PhyphoxFileException("A maximum of " + inputMapping[i].maxCount + " inputs was expected for " + inputMapping[i].name + " but " + inputMapping[i].count + " were found.", xpp.getLineNumber());
                    if (inputMapping[i].count < inputMapping[i].minCount)
                        throw new PhyphoxFileException("A minimum of " + inputMapping[i].minCount + " inputs was expected for " + inputMapping[i].name + " but " + inputMapping[i].count + " were found.", xpp.getLineNumber());
                }
            }
            if (outputMapping != null) {
                for (int i = 0; i < outputMapping.length; i++) {
                    if ((outputMapping[i].maxCount > 0 && outputMapping[i].count > outputMapping[i].maxCount))
                        throw new PhyphoxFileException("A maximum of " + outputMapping[i].maxCount + " outputs was expected for " + outputMapping[i].name + " but " + outputMapping[i].count + " were found.", xpp.getLineNumber());
                    if (outputMapping[i].count < outputMapping[i].minCount)
                        throw new PhyphoxFileException("A minimum of " + outputMapping[i].minCount + " outputs was expected for " + outputMapping[i].name + " but " + outputMapping[i].count + " were found.", xpp.getLineNumber());
                }
            }
        }
    }
