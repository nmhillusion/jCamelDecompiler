package tech.nmhillusion.jCamelDecoderApp.loader;

import tech.nmhillusion.jCamelDecoderApp.helper.PathHelper;
import tech.nmhillusion.jCamelDecoderApp.model.DecoderEngineModel;
import tech.nmhillusion.n2mix.helper.YamlReader;
import tech.nmhillusion.n2mix.helper.log.LogHelper;
import tech.nmhillusion.n2mix.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-04-04
 */
public class DecompilerLoader {
    private static final DecompilerLoader INSTANCE = new DecompilerLoader();
    private final List<DecoderEngineModel> DECODER_ENGINES = new ArrayList<>();

    private DecompilerLoader() {
    }

    public static DecompilerLoader getInstance() {
        return INSTANCE;
    }

    public List<DecoderEngineModel> loadEngines() {
        if (null == DECODER_ENGINES || DECODER_ENGINES.isEmpty()) {
            final List<DecoderEngineModel> engines = new ArrayList<>();

            final Path resourcePath = PathHelper.getPathOfResource("decoder/decompilers.config.yml");
            try (final InputStream fis = Files.newInputStream(resourcePath)) {
                final List<?> decompilers = new YamlReader(fis).getProperty("decompilers", List.class);

                for (Object decompiler : decompilers) {
                    LogHelper.getLogger(DecompilerLoader.class).info("decompiler = {}", decompiler);

                    if (decompiler instanceof Map<?, ?> decompilerMap) {
                        engines.add(
                                new DecoderEngineModel()
                                        .setEngineId(
                                                StringUtil.trimWithNull(decompilerMap.get("id"))
                                        )
                                        .setEngineName(
                                                StringUtil.trimWithNull(decompilerMap.get("name"))
                                        )
                                        .setCompilerOptions(
                                                StringUtil.trimWithNull(decompilerMap.get("options"))
                                        )
                                        .setLibFilename(
                                                StringUtil.trimWithNull(decompilerMap.get("libFilename"))
                                        )
                                        .setExecScriptFilename(
                                                StringUtil.trimWithNull(decompilerMap.get("execScriptFilename"))
                                        )
                        );
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            DECODER_ENGINES.addAll(engines);
        }

        return DECODER_ENGINES;
    }

    public DecoderEngineModel loadEngine(String engineId) {
        return loadEngines()
                .stream()
                .filter(it -> it.getEngineId().equals(engineId))
                .findFirst()
                .orElseThrow();
    }
}
