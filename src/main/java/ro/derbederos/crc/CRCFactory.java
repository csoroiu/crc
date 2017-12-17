package ro.derbederos.crc;

import ro.derbederos.crc.purejava.CRC32Generic;
import ro.derbederos.crc.purejava.CRC64Generic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.Checksum;

public class CRCFactory {

    public static final CRCModel CRC32;
    public static final CRCModel CRC32C;

    private static Map<String, CRCModel> models = new LinkedHashMap<>();
    private static boolean javaCrc32cAvailable;

    static {
        loadModels();
        CRC32 = getModel("CRC-32");
        CRC32C = getModel("CRC-32C");
        javaCrc32cAvailable = false;
        try {
            Class.forName("java.util.zip.CRC32C");
            javaCrc32cAvailable = true;
        } catch (ClassNotFoundException ignore) {
        }
    }

    private static void loadModels() {
        URL crcModelsUrl = CRCFactory.class.getClassLoader().getResource("crc-catalogue-models.txt");
        if (crcModelsUrl == null) {
            return;
        }
        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(crcModelsUrl.openStream()))) {
            String line;
            Map<String, CRCModel> models = new LinkedHashMap<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }
                CRCModel model = parseLine(reader.getLineNumber(), line);
                if (model != null) {
                    models.put(model.getName(), model);
                }
            }
            CRCFactory.models = models;
        } catch (IOException ex) {
            System.err.println("CRCFactory: no model loaded");
            ex.printStackTrace();
        }
    }

    private static CRCModel parseLine(int lineNumber, String line) {
        try {
            Properties p = new Properties();
            p.load(new StringReader(line.replaceAll(" +", "\n")));
            int width = Integer.parseInt(p.getProperty("width"), 10);
            long poly = parseLong(p.getProperty("poly"));
            long init = parseLong(p.getProperty("init"));
            boolean refIn = Boolean.parseBoolean(p.getProperty("refin"));
            boolean refOut = Boolean.parseBoolean(p.getProperty("refout"));
            long xorOut = parseLong(p.getProperty("xorout"));
            long check = parseLong(p.getProperty("check"));
            long residue = parseLong(p.getProperty("residue"));
            String name = unQuote(p.getProperty("name"));
            return new CRCModel(name, width, poly, init, refIn, refOut, xorOut, check, residue);
        } catch (NumberFormatException ex) {
            System.err.println("CRCFactory: Failed to parse model at line " + lineNumber + ":" + line);
        } catch (IOException ignore) {
        }
        return null;
    }

    private static String unQuote(String input) {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return input.substring(1, input.length() - 1);
        } else {
            return input;
        }
    }

    private static long parseLong(String input) {
        input = input.toUpperCase();
        if (input.startsWith("0X")) {
            return Long.parseUnsignedLong(input.substring(2), 16);
        } else {
            return Long.parseUnsignedLong(input);
        }
    }

    public static CRCModel[] getDefinedModels() {
        return models.values().toArray(new CRCModel[models.size()]);
    }

    /**
     * Returns the defined model matching the name or alias.
     *
     * @param modelName the name of the model
     * @return the model for that name
     */

    public static CRCModel getModel(String modelName) {
        return models.get(modelName);
    }

    /**
     * Returns the most appropriate, usually the fastest, CRC checksum calculator based on the defined model name.
     *
     * @param modelName the name of the model
     * @return
     */
    public static Checksum getCRC(String modelName) {
        CRCModel crcModel = getModel(modelName);
        if (crcModel == null) {
            throw new IllegalArgumentException("CRCFactory: Undefined model " + modelName);
        }
        return getCRC(crcModel);
    }

    /**
     * Returns the most appropriate, usually the fastest, CRC checksum calculator based on the model input.
     *
     * @param model
     * @return
     */
    public static Checksum getCRC(CRCModel model) {
        if (isAlias(CRC32, model)) {
            return new java.util.zip.CRC32();
        } else if (javaCrc32cAvailable && isAlias(CRC32C, model)) {
            return new java.util.zip.CRC32C();
        } else if (model.getWidth() <= 32) {
            return createCrc32(model);
        } else if (model.getWidth() <= 64) {
            return createCrc64(model);
        }
        throw new IllegalArgumentException("CRCFactory: Cannot find a generator for model " + model.getName());
    }

    private static boolean isAlias(CRCModel reference, CRCModel input) {
        return reference == input || (reference.getWidth() == input.getWidth() &&
                reference.getPoly() == input.getPoly() &&
                reference.getInit() == input.getInit() &&
                reference.getRefIn() == input.getRefIn() &&
                reference.getRefOut() == input.getRefOut() &&
                reference.getXorOut() == reference.getXorOut());
    }

    private static CRC createCrc32(CRCModel crcModel) {
        return new CRC32Generic(
                crcModel.getWidth(),
                (int) crcModel.getPoly(),
                (int) crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                (int) crcModel.getXorOut());
    }

    private static CRC createCrc64(CRCModel crcModel) {
        return new CRC64Generic(
                crcModel.getWidth(),
                crcModel.getPoly(),
                crcModel.getInit(),
                crcModel.getRefIn(),
                crcModel.getRefOut(),
                crcModel.getXorOut());
    }
}
