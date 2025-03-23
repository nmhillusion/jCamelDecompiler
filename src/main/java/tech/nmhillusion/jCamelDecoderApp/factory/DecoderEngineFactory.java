package tech.nmhillusion.jCamelDecoderApp.factory;

import tech.nmhillusion.jCamelDecoderApp.constant.DecoderEngineEnum;
import tech.nmhillusion.jCamelDecoderApp.constant.PathsConstant;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.n2mix.helper.YamlReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

/**
 * created by: minguy1
 * <p>
 * created date: 2025-02-12
 */
public class DecoderEngineFactory {
    private final static DecoderEngineFactory INSTANCE = new DecoderEngineFactory();
    private final Map<DecoderEngineEnum, DecoderEngineModel> storage = new TreeMap<>();

    private DecoderEngineFactory() {
    }

    public static DecoderEngineFactory getInstance() {
        return INSTANCE;
    }

    private <T> T getConfigOfDecompiler(String configKey, Class<T> classOfValue) throws IOException {
        try (final InputStream fis = Files.newInputStream(PathsConstant.DECOMPILER_CONFIG_PATH.getAbsolutePath())) {
            return new YamlReader(fis).getProperty(configKey, classOfValue);
        }
    }

    public DecoderEngineModel getEngine(DecoderEngineEnum engineId) {
        return storage.computeIfAbsent(engineId, mId -> {
            try {
                final DecoderEngineModel engine = new DecoderEngineModel()
                        .setEngineId(mId);

                switch (mId) {
                    case CFR -> engine.setEngineName("CFR Decoder")
                            .setLibFilename(
                                    getConfigOfDecompiler("decompiler.cfr.libFilename", String.class)
                            )
                            .setCompilerOptions(
                                    getConfigOfDecompiler("decompiler.cfr.options", String.class)
                            )
                    ;
                    case Procyon -> engine.setEngineName("Procyon Decoder")
                            .setLibFilename(
                                    getConfigOfDecompiler("decompiler.procyon.libFilename", String.class)
                            )
                            .setCompilerOptions(
                                    getConfigOfDecompiler("decompiler.procyon.options", String.class)
                            )
                    ;
                }

                return engine;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
