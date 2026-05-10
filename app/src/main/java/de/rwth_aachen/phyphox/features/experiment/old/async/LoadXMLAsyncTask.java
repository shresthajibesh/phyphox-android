package de.rwth_aachen.phyphox.features.experiment.old.async;

import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.openXMLInputStream;
import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.languageRating;
import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.phyphoxFileVersion;

import android.content.Intent;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import android.util.Xml;

import de.rwth_aachen.phyphox.DataBuffer;
import de.rwth_aachen.phyphox.ExpView;
import de.rwth_aachen.phyphox.Helper.Helper;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.ExperimentActivity;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;
import de.rwth_aachen.phyphox.features.experiment.old.parser.model.PhyphoxStream;
import de.rwth_aachen.phyphox.features.experiment.old.parser.PhyphoxBlockParser;

//This AsyncTask will load a phyphoxExperiment from an intent and return it by passing it to
//onExperimentLoaded of the activity given in the constructor.
public class LoadXMLAsyncTask extends AsyncTask<String, Void, PhyphoxExperiment> {
        private Intent intent;
        private WeakReference<ExperimentActivity> parent;

        public LoadXMLAsyncTask(Intent intent, ExperimentActivity parent) {
            this.intent = intent;
            this.parent = new WeakReference<ExperimentActivity>(parent);
        }

        //Load the file from the intent
        protected PhyphoxExperiment doInBackground(String... params) {
            //New experiment
            PhyphoxExperiment experiment = new PhyphoxExperiment();

            //Open the input stream (see above)
            PhyphoxStream input = openXMLInputStream(intent, parent.get());
            if (input.getInputStream() == null) { //If this failed, abort and relay the error message
                experiment.message = input.getErrorMessage();
                return experiment;
            }

            experiment.isLocal = input.isLocal(); //The experiment needs to know if it is local
            experiment.source = input.getSource();
            experiment.crc32 = input.getCrc32();
            experiment.resourceFolder = input.getResourceFolder();
            try {
                //Setup the pull parser
                BufferedReader reader = new BufferedReader(new InputStreamReader(input.getInputStream()));

                XmlPullParser xpp = Xml.newPullParser();
                xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                xpp.setInput(reader);

                //We can just race through all start tags until we reach the phyphox tag. Then let out phyphoxBlockParser take over.
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("phyphox")) {
                        //Phyphox tag. This is what we need to read, but let's check the file version first.
                        String fileVersion = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "version");
                        if (fileVersion != null) {
                            //A file version has been given. (If not, some user probably created the file manually. Let's allow this although it should not be encouraged.)
                            //Parse the file version and the version of this class
                            int split = fileVersion.indexOf('.'); //File version
                            int phyphoxSplit = phyphoxFileVersion.indexOf('.'); //Class version
                            try {
                                //Version strings are supposed to be of the form "x.y" with x being the major version number and y being minor.

                                //File versions
                                experiment.versionMajor = Integer.valueOf(fileVersion.substring(0, split));
                                experiment.versionMinor = Integer.valueOf(fileVersion.substring(split + 1));

                                //Class versions
                                int phyphoxMajor = Integer.valueOf(phyphoxFileVersion.substring(0, phyphoxSplit));
                                int phyphoxMinor = Integer.valueOf(phyphoxFileVersion.substring(phyphoxSplit + 1));

                                //This class needs to be newer than the file. Otherwise ask the user to update.
                                if (experiment.versionMajor > phyphoxMajor || (experiment.versionMajor == phyphoxMajor && experiment.versionMinor > phyphoxMinor)) {
                                    experiment.message = "This experiment has been created for a more recent version of phyphox. Please update phyphox to load this experiment.";
                                    return experiment;
                                }
                            } catch (NumberFormatException e) {
                                experiment.message = "Unable to interpret the file version of this experiment.";
                                return experiment;
                            }

                            String globalLocale = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "locale");
                            languageRating = Helper.getLanguageRating(parent.get().getResources(), globalLocale);
                        }
                        (new PhyphoxBlockParser(xpp, experiment, parent.get())).process();
                    }
                    eventType = xpp.next();
                }
            } catch (XmlPullParserException e) { //Catch pullparser errors
                experiment.message = "XML Error in line "+ e.getLineNumber() +": " + e.getMessage();
                return experiment;
            } catch (PhyphoxFileException e) { //Catch our own errors
                experiment.message = e.getMessage();
                return experiment;
            } catch (IOException e) { //Catch IO errors
                experiment.message = "Unhandled IO error while loading this experiment: " + e.getMessage();
                return experiment;
            } catch (RuntimeException e) { //Those are a thing, too... For example, for some reason an undefined xml prefix throws a RuntimeException.
                experiment.message = "Unhandled RuntimeException while loading this experiment: " + e.getMessage();
                return experiment;

            }

            for (ExpView v : experiment.experimentViews) {
                for (ExpView.expViewElement ev : v.elements) {
                    if (ev instanceof ExpView.editElement) {
                        DataBuffer buffer = experiment.getBuffer(((ExpView.editElement) ev).valueOutput);
                        if (buffer != null)
                            experiment.getBuffer(((ExpView.editElement)ev).valueOutput).linkedToUserInput = true;
                    }
                }
            }

            //Sanity check: If the experiment did not define any views, we cannot use it
            if (experiment.experimentViews.size() == 0) {
                experiment.message = "Bad experiment definition: No valid view found.";
                return experiment;
            }

            //We are done without any problems that we know of.
            experiment.loaded = true;
            return experiment;

        }

        @Override
        //Call the parent callback when we are done.
        protected void onPostExecute(PhyphoxExperiment experiment) {
            parent.get().onExperimentLoaded(experiment);
        }
    }
