package de.rwth_aachen.phyphox.features.experiment.old.parser.error;

public class PhyphoxFileException extends Exception {
        public PhyphoxFileException(String message) {
            super(message);
        }

        public PhyphoxFileException(String message, int line) {
            super("Line " + line + ": " + message);
        }
    }
