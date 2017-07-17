package ro.derbederos.crc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.Checksum;

public class CRCFactory {

    private static Map<String, CRCModel> models = new HashMap<>();

    static {
        loadModels();
    }

    private static void loadModels() {
        URL crcModelsUrl = CRCFactory.class.getClassLoader().getResource("crccataloguemodels.txt");
        if (crcModelsUrl == null) {
            return;
        }
        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(crcModelsUrl.openStream()))) {
            String line;
            Map<String, CRCModel> models = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                CRCModel model = parseLine(line);
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

    private static CRCModel parseLine(String line) {
        try {
            Properties p = new Properties();
            p.load(new StringReader(line.trim().replaceAll(" {2}", "\n")));
            int width = Integer.parseInt(p.getProperty("width"), 10);
            long poly = parseLong(p.getProperty("poly"));
            long init = parseLong(p.getProperty("init"));
            boolean refIn = Boolean.parseBoolean(p.getProperty("refin"));
            boolean refOut = Boolean.parseBoolean(p.getProperty("refout"));
            long xorOut = parseLong(p.getProperty("xorout"));
            long check = parseLong(p.getProperty("check"));
            long residue = parseLong(p.getProperty("residue"));
            String name = p.getProperty("name");
            return new CRCModel(name, width, poly, init, refIn, refOut, xorOut, check, residue);
        } catch (NumberFormatException ex) {
            System.err.println("CRCFactory: Failed to parse model " + line);
        } catch (IOException ignore) {
        }
        return null;
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
            throw new IllegalArgumentException("Undefined model " + modelName);
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
        //FIXME
        // if model <  16 => CRC16Generic
        //if model == 16 => CRC16
        //if model <  32 => CRC32Generic
        //if model == 32,CRCParam = CRC-32 => zip.CRC32
        //if model == 32 => CRC32
        //if model <  64 => CRC64Generic
        //if model == 64,refin == FALSE => CRC64Unreflected
        //if model == 64,refin == TRUE  => CRC64Reflected
        return null;
    }
}
