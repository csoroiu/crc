package ro.derbederos.crc;

import ro.derbederos.crc.purejava.CRC32SlicingBy8;
import ro.derbederos.crc.purejava.CRC64SlicingBy16;
import ro.derbederos.crc.purejava.crc32.CRC32_JAMCRC;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.zip.Checksum;

public class CRCFactory {

    public static final CRCModel CRC32;
    public static final CRCModel JAMCRC;
    public static final CRCModel CRC32C;

    private static Map<String, CRCModel> models = new LinkedHashMap<>();
    private static Map<CRCModel, Supplier<Checksum>> constructors = new HashMap<>();
    private static boolean javaCrc32cAvailable;

    static {
        loadModels();
        CRC32 = getModel("CRC-32");
        constructors.put(CRC32, java.util.zip.CRC32::new);
        JAMCRC = getModel("JAMCRC");
        constructors.put(JAMCRC, CRC32_JAMCRC::new);

        CRC32C = getModel("CRC-32C");
        try {
            Class<?> crc32cClass = Class.forName("java.util.zip.CRC32C");
            constructors.put(CRC32C, java.util.zip.CRC32C::new);
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
     * @param modelName the name of the {@link CRCModel}
     * @return the most appropriate, usually the fastest, CRC checksum calculator based on the defined model name.
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
     * @param model the {@link CRCModel}
     * @return the most appropriate, usually the fastest, CRC checksum calculator based on the model input.
     */
    public static Checksum getCRC(CRCModel model) {
        Supplier<Checksum> factory;
        if ((factory = constructors.get(model)) != null) {
            return factory.get();
        } else if (model.getWidth() <= 32) {
            return new CRC32SlicingBy8(model);
        } else if (model.getWidth() <= 64) {
            return new CRC64SlicingBy16(model);
        }
        throw new IllegalArgumentException("CRCFactory: Cannot find a generator for model " + model.getName());
    }

    private static boolean isAlias(CRCModel reference, CRCModel input) {
        return reference == input || (reference.getWidth() == input.getWidth() &&
                reference.getPoly() == input.getPoly() &&
                reference.getInit() == input.getInit() &&
                reference.getRefIn() == input.getRefIn() &&
                reference.getRefOut() == input.getRefOut() &&
                reference.getXorOut() == input.getXorOut());
    }
}
