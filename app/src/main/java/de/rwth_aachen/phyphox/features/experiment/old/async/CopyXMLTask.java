package de.rwth_aachen.phyphox.features.experiment.old.async;

//This asyncTask just copies the resource provided by an intent to the private data storage
//It calls onCopyXMLCompleted of the activity given in the constructor when it's done.
public  class CopyXMLTask extends AsyncTask<String, Void, String> {
        private Intent intent; //The intent to read from
        private WeakReference<Experiment> parent; //The calling Activity

        //The constructor takes the intent to copy from and the parent activity to call back when finished.
        CopyXMLTask(Intent intent, Experiment parent) {
            this.intent = intent;
            this.parent = new WeakReference<Experiment>(parent);
        }

        //Copying is done on a second thread...
        protected String doInBackground(String... params) {
            InputStream input;
            if (parent.get().experiment.source != null) {
                //We have stored the original source file...
                input = new ByteArrayInputStream(parent.get().experiment.source);
            } else {
                //If not, open the remote source, but usually this should not happen...
                PhyphoxFile.PhyphoxStream ps = PhyphoxFile.openXMLInputStream(intent, parent.get());
                input = ps.inputStream;
            }
            if (input == null)
                return "Error loading the original XML file again. This should not have happend."; //Abort and relay the rror message, if this failed

            //Copy the input stream to a random file name
            try {
                String file = UUID.randomUUID().toString().replaceAll("-", "") + ".phyphox"; //Random file name
                FileOutputStream output = parent.get().openFileOutput(file, Activity.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int count;
                while ((count = input.read(buffer)) != -1)
                    output.write(buffer, 0, count);
                output.close();
                input.close();
            } catch (Exception e) {
                return "Error loading the original XML file again: " + e.getMessage();
            }
            PhyphoxExperiment exp = parent.get().experiment;
            String warnings = "";
            if (exp.resourceFolder != null && !exp.resources.isEmpty()) {
                File newResFolder = new File(parent.get().getFilesDir(), Long.toHexString(exp.crc32).toLowerCase());
                newResFolder.mkdirs();
                for (String src : exp.resources) {
                    try {
                        Helper.copyFile(new File(exp.resourceFolder, src), new File(newResFolder, src));
                    } catch (Exception e) {
                        Log.e("CopyXML", "Could not save resource: " + e.getMessage());
                        warnings += "Could not save resource: " + src + "\n";
                    }
                }
            }
            return warnings;
        }

        @Override
        //Call the parent callback when we are done.
        protected void onPostExecute(String result) {
            parent.get().onCopyXMLCompleted(result);
        }
    }
