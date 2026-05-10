package de.rwth_aachen.phyphox.features.experiment.old.parser;

import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.translate;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.Helper.RGB;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.ExperimentActivity;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class XmlBlockParser {
        protected ExperimentActivity parent; //For some elements we need access to the parent activity
        private String tag; //The tag of the block that should be handled by this parser
        private int rootDepth; //The depth of the base of the block handled by this parser
        protected XmlPullParser xpp; //The pull parser used handed to this parser
        protected PhyphoxExperiment experiment; //The experiment to be loaded

        private boolean textAdvanced;

        //The constructor takes the tag, and the experiment to fill
        public XmlBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, ExperimentActivity parent) {
            this.xpp = xpp;
            this.experiment = experiment;
            this.parent = parent;
        }

        //Helper to receive the text block of a tag
        protected String getText() throws XmlPullParserException, IOException {
            String text = xpp.nextText();
            textAdvanced = true;
            if (text != null)
                return text.trim();
            else
                return null;
        }

        //Helper to receive a string typed attribute
        protected String getStringAttribute(String identifier) {
            return xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, identifier);
        }

        //Helper to receive a string typed attribute and translate it
        protected String getTranslatedAttribute(String identifier) {
            return translate(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, identifier), parent);
        }

        //Helper to receive an integer typed attribute, if invalid or not present, return default
        protected int getIntAttribute(String identifier, int defaultValue) {
            try {
                return Integer.valueOf(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, identifier));
            } catch (Exception e) {
                return defaultValue;
            }
        }

        //Helper to receive a double typed attribute, if invalid or not present, return default
        protected double getDoubleAttribute(String identifier, double defaultValue) {
            try {
                return Double.valueOf(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, identifier));
            } catch (Exception e) {
                return defaultValue;
            }
        }

        //Helper to receive a boolean attribute, if invalid or not present, return default
        protected boolean getBooleanAttribute(String identifier, boolean defaultValue) {
            final String att = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, identifier);
            if (att == null)
                return defaultValue;
            return Boolean.valueOf(att);
        }

        //Helper to receive a color attribute, if invalid or not present, return default
        protected RGB getColorAttribute(String identifier, RGB defaultValue) {
            final String att = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, identifier);
            return RGB.fromPhyphoxString(att, parent.getResources(), defaultValue);
        }

        //These functions should be overriden with block-specific code
        protected void processStartTag(String tag) throws IOException, XmlPullParserException, PhyphoxFileException {

        }
        protected void processEndTag(String tag) throws IOException, XmlPullParserException, PhyphoxFileException {

        }
        protected void done() throws IOException, XmlPullParserException, PhyphoxFileException {

        }

        public void process() throws IOException, XmlPullParserException, PhyphoxFileException {
            int eventType = xpp.getEventType();
            if (eventType != XmlPullParser.START_TAG)
                throw new PhyphoxFileException("xmlBlockParser called on something else than a start tag.", xpp.getLineNumber());

            //Remember our entry point
            this.rootDepth = xpp.getDepth();
            this.tag = xpp.getName();

            //That's it for the root element. Start to distribute the next events to processStartTag and processEndTag
            eventType = xpp.next();

            //Loop until we leave the block again. So unless the depth matches root level and we see
            //our tag as an end tag, we should continue
            while (xpp.getDepth() != rootDepth || eventType != XmlPullParser.END_TAG || !xpp.getName().equalsIgnoreCase(tag)) {
                textAdvanced = false;
                switch (eventType) {
                    case XmlPullParser.END_DOCUMENT: //We should not reach the end of the document wthin this block
                        throw new PhyphoxFileException("Unexpected end of document.", xpp.getLineNumber());
                    case XmlPullParser.START_TAG:
                        processStartTag(xpp.getName());
                        break;
                    case XmlPullParser.END_TAG:
                        processEndTag(xpp.getName());
                        break;
                }
                if (!textAdvanced)
                    eventType = xpp.next();
                else
                    eventType = xpp.getEventType();
            }

            done();
        }
    }
