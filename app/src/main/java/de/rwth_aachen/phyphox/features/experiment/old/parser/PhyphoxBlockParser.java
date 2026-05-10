package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.ExperimentActivity;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class PhyphoxBlockParser extends XmlBlockParser {

        public PhyphoxBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, ExperimentActivity parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag) throws IOException, XmlPullParserException, PhyphoxFileException {
            switch (tag.toLowerCase()) {
                case "title": //The experiment's title (might be replaced by a later translation block)
                    experiment.baseTitle = getText();
                    experiment.title = experiment.baseTitle;
                    break;
                case "state-title":
                    experiment.stateTitle = getText();
                    break;
                case "icon": //The experiment's icon
                    // We currently do not show the icon while the experiment is open, so we do not need to read it.
                    //experiment.icon = getText();
                    break;
                case "color": //The experiment's base color
                    // We currently do not use this color in the experiment view.
                    break;
                case "description": //The experiment's description (might be replaced by a later translation block)
                    experiment.description = getText().trim().replaceAll("(?m) +$", "").replaceAll("(?m)^ +", "");
                    break;
                case "link": //Links to external sources like documentation (might be replaced by a later translation block)
                    boolean highlighted = getBooleanAttribute("highlight", false);
                    String label = getStringAttribute("label");
                    String link = getText().trim().replaceAll("(?m) +$", "").replaceAll("(?m)^ +", "");
                    experiment.links.put(label, link);
                    if (highlighted)
                        experiment.highlightedLinks.put(label, link);
                    break;
                case "category": //The experiment's category (might be replaced by a later translation block)
                    experiment.baseCategory = getText();
                    experiment.category = experiment.baseCategory;
                    break;
                case "translations": //A translations block may containing multiple translation-blocks
                    (new TranslationsBlockParser(xpp, experiment, parent)).process();
                    break;
                case "data-containers": //The data-containers block defines all buffers used in this experiment
                    (new DataContainersBlockParser(xpp, experiment, parent)).process();
                    break;
                case "events": //The events block stores events and their timestamps
                    (new EventsBlockParser(xpp, experiment, parent)).process();
                    break;
                case "views": //A Views block may contain multiple view-blocks
                    (new ViewsBlockParser(xpp, experiment, parent)).process();
                    break;
                case "input": //Holds inputs like sensors or the microphone
                    (new InputBlockParser(xpp, experiment, parent)).process();
                    break;
                case "network": //Holds inputs like sensors or the microphone
                    (new NetworkBlockParser(xpp, experiment, parent)).process();
                    break;
                case "analysis": //Holds a number of math modules which will be executed in the order they occur
                    experiment.analysisSleep = getDoubleAttribute("sleep", 0.0); //Time between executions
                    String dynamicSleep = getStringAttribute("dynamicSleep"); //Time between executions
                    if (dynamicSleep != null) {
                        if (experiment.getBuffer(dynamicSleep) != null)
                            experiment.analysisDynamicSleep = experiment.getBuffer(dynamicSleep);
                        else
                            throw new PhyphoxFileException("Dynamic sleep buffer " + dynamicSleep + " has not been defined as a buffer.", xpp.getLineNumber());
                    }
                    experiment.analysisOnUserInput = getBooleanAttribute("onUserInput", false); //Only execute when the user changed something?

                    String requireFill = getStringAttribute("requireFill");
                    experiment.requireFillThreshold = getIntAttribute("requireFillThreshold", 1);
                    String requireFillDynamic = getStringAttribute("requireFillDynamic");
                    if (requireFill != null) {
                        if (experiment.getBuffer(requireFill) != null)
                            experiment.requireFill = experiment.getBuffer(requireFill);
                        else
                            throw new PhyphoxFileException("Require fill buffer " + requireFill + " has not been defined as a buffer.", xpp.getLineNumber());
                    }
                    if (requireFillDynamic != null) {
                        if (experiment.getBuffer(requireFillDynamic) != null)
                            experiment.requireFillDynamic = experiment.getBuffer(requireFillDynamic);
                        else
                            throw new PhyphoxFileException("Require fill buffer " + requireFillDynamic + " has not been defined as a buffer.", xpp.getLineNumber());
                    }

                    experiment.timedRun = getBooleanAttribute("timedRun", false);
                    experiment.timedRunStartDelay = getDoubleAttribute("timedRunStartDelay", 3.0);
                    experiment.timedRunStopDelay = getDoubleAttribute("timedRunStopDelay", 10.0);
                    (new AnalysisBlockParser(xpp, experiment, parent)).process();
                    break;
                case "output": //Holds outputs like the speaker
                    (new OutputBlockParser(xpp, experiment, parent)).process();
                    break;
                case "export": //Holds multiple set-blocks, which in turn describe which buffer should be exported as a set
                    (new ExportBlockParser(xpp, experiment, parent)).process();
                    break;
                default: //Unknown tag,,,
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }
    }
