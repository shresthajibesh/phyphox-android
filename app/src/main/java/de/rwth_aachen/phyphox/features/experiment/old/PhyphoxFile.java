package de.rwth_aachen.phyphox.features.experiment.old;

import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_ISASSET;
import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_ISTEMP;
import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_XML;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.model.PhyphoxStream;

//phyphoxFile implements the loading of an experiment from a *.phyphox file as well as the copying
//of a remote phyphox-file to the local collection. Both are implemented as an AsyncTask
public abstract class PhyphoxFile {

    public final static String phyphoxFileVersion = "1.19";

    //translation maps any term for which a suitable translation is found to the current locale or, as fallback, to English
    public static Map<String, String> translation = new HashMap<>();
    public static int languageRating = 0; //If we find a locale, it replaces previous translations as long as it has a higher rating than the previous one.

    //Simple helper to return either the translated term or the original one, if no translation could be found
    public static String translate(String input, Experiment parent) {
        if (input == null)
            return null;
        if (translation.containsKey(input.trim()))
            return translation.get(input.trim());
        if (!(input.startsWith("[[") && input.endsWith("]]")))
            return input;
        int id = parent.getResources().getIdentifier("common_" + input.substring(2, input.length() - 2), "string", parent.getBaseContext().getPackageName());
        if (id > 0)
            return parent.getResources().getString(id);
        return input;
    }

    //Returns true if the string is a valid identifier for a dataBuffer, very early versions had some rules here, but we now allow anything as long as it is not empty.
    public static boolean isValidIdentifier(String s) {
        if (s.isEmpty()) {
            return false;
        }
        return true;
    }

    //PhyphoxStream bundles the result of an opened stream

    //Helper function to read an input stream into memory and return an input stream to the data in memory as well as the data
    public static void remoteInputToMemory(PhyphoxStream stream, String resourceFolder, boolean resourceViaCRC32) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        CRC32 crc32 = new CRC32();

        int n;
        byte[] buffer = new byte[1024];
        while ((n = stream.getInputStream().read(buffer, 0, 1024)) != -1) {
            os.write(buffer, 0, n);
            crc32.update(buffer, 0, n);
        }

        os.flush();
        stream.setSource(os.toByteArray());
        stream.setInputStream(new ByteArrayInputStream(stream.getSource()));
        stream.setCrc32(crc32.getValue());

        if (resourceFolder != null)
            stream.setResourceFolder(resourceFolder + "/" + (resourceViaCRC32 ? Long.toHexString(crc32.getValue()).toLowerCase() : "res"));
        else
            stream.setResourceFolder(null);
    }

    //Helper function to open an inputStream from various intents
    public static PhyphoxStream openXMLInputStream(Intent intent, Activity parent) {
        languageRating = 0;//If we find a locale, it replaces previous translations as long as it has a higher rating than the previous one.
        translation = new HashMap<>();

        PhyphoxStream phyphoxStream = new PhyphoxStream();

        //We only respond to view-action-intents
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_VIEW)) {

            //We need to perform slightly different actions for all the different schemes
            String scheme = intent.getScheme();

            if (intent.getStringExtra(EXPERIMENT_XML) != null) { //If the file location is found in the extra EXPERIMENT_XML, it is a local file
                String isTemp = intent.getStringExtra(EXPERIMENT_ISTEMP);
                phyphoxStream.setLocal((isTemp == null || isTemp.isEmpty()));
                if (intent.getBooleanExtra(EXPERIMENT_ISASSET, true)) { //The local file is an asser
                    AssetManager assetManager = parent.getAssets();
                    try {
                        phyphoxStream.setInputStream(assetManager.open("experiments/" + intent.getStringExtra(EXPERIMENT_XML)));
                        remoteInputToMemory(phyphoxStream, "ASSET", true);
                    } catch (Exception e) {
                        phyphoxStream.setErrorMessage("Error loading this experiment from assets: " + e.getMessage());
                    }
                } else if (intent.getStringExtra(EXPERIMENT_ISTEMP) != null) {
                    //This is a temporary file. Typically from a zip file. It's in the private directory, but in a subfolder called "temp"
                    try {
                        File tempDir = new File(parent.getFilesDir(), intent.getStringExtra(EXPERIMENT_ISTEMP));
                        File file = new File(tempDir, intent.getStringExtra(EXPERIMENT_XML));
                        phyphoxStream.setInputStream(new FileInputStream(file));
                        remoteInputToMemory(phyphoxStream, file.getParentFile().getAbsolutePath(), false);
                    } catch (Exception e) {
                        phyphoxStream.setErrorMessage("Error loading this experiment from local storage: " + e.getMessage());
                    }
                } else { //The local file is in the private directory
                    try {
                        phyphoxStream.setInputStream(parent.openFileInput(intent.getStringExtra(EXPERIMENT_XML)));
                        File file = new File(parent.getFilesDir(), intent.getStringExtra(EXPERIMENT_XML));
                        remoteInputToMemory(phyphoxStream, file.getParentFile().getAbsolutePath(), true);
                    } catch (Exception e) {
                        phyphoxStream.setErrorMessage("Error loading this experiment from local storage: " + e.getMessage());
                    }
                }
                return phyphoxStream;

            } else if (scheme.equals(ContentResolver.SCHEME_FILE )) {//The intent refers to a file
                phyphoxStream.setLocal(false);
                Uri uri = intent.getData();
                if (uri == null) {
                    phyphoxStream.setErrorMessage("Missing uri.");
                    return phyphoxStream;
                }
                ContentResolver resolver = parent.getContentResolver();
                //We will need read permissions for pretty much any file...
                if (!uri.getPath().startsWith(parent.getFilesDir().getPath()) && ContextCompat.checkSelfPermission(parent, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Android 6.0: No permission? Request it!
                    ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    //We will stop with a no permission error. If the user grants the permission, the permission callback will restart the action with the same intent
                    phyphoxStream.setErrorMessage("Permission needed to read external storage.");
                    return phyphoxStream;
                }
                try {
                    phyphoxStream.setInputStream(resolver.openInputStream(uri));
                    File containingFolder = new File(uri.getPath());
                    remoteInputToMemory(phyphoxStream, containingFolder.getParent(), false);
                } catch (Exception e) {
                    phyphoxStream.setErrorMessage("Error loading experiment from file: " + e.getMessage());
                }
                return phyphoxStream;

            } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {//The intent refers to a content (like the attachment from a mailing app)
                phyphoxStream.setLocal(false);
                Uri uri = intent.getData();
                ContentResolver resolver = parent.getContentResolver();
                try {
                    phyphoxStream.setInputStream(resolver.openInputStream(uri));
                    remoteInputToMemory(phyphoxStream, null, false);
                } catch (Exception e) {
                    phyphoxStream.setErrorMessage("Error loading experiment from content: " + e.getMessage());
                }
                return phyphoxStream;
            } else if (scheme.equals("phyphox")) { //The intent refers to an online resource, but we need to figure out if we can use https or should fallback to http
                phyphoxStream.setLocal(false);
                Uri uri = intent.getData();
                try {
                    URL url = new URL("https", uri.getHost(), uri.getPort(), uri.getPath() + (uri.getQuery() != null ? ("?" + uri.getQuery()) : ""));
                    phyphoxStream.setInputStream(url.openStream());
                    remoteInputToMemory(phyphoxStream, null, false);
                } catch (Exception e) {
                    //ok, https did not work. Maybe we success with http?
                    try {
                        URL url = new URL("http", uri.getHost(), uri.getPort(), uri.getPath() + (uri.getQuery() != null ? ("?" + uri.getQuery()) : ""));
                        phyphoxStream.setInputStream(url.openStream());
                        remoteInputToMemory(phyphoxStream, null, false);
                    } catch (Exception e2) {
                        phyphoxStream.setErrorMessage("Error loading experiment from phyphox: " + e2.getMessage());
                    }
                }
                return phyphoxStream;
            }
            phyphoxStream.setErrorMessage("Unknown scheme.");
            return phyphoxStream;
        } else {
            phyphoxStream.setErrorMessage("No run-intent.");
            return phyphoxStream;
        }
    }

}
