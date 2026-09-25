package org.geysermc.integratedpack.renderers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.geysermc.integratedpack.*;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class MountTranslationsRenderer implements Renderer {
    @Override
    public String getName() {
        return "Mount Translation Strings";
    }

    @Override
    public void render() throws IOException {
        log("Downloading entity identifiers...");
        NbtMap nbt = WebUtils.getAsNbt(
                "https://raw.githubusercontent.com/CloudburstMC/Data/%s/entity_identifiers.dat"
                        .formatted(Constants.CB_DATA_COMMIT_HASH)
        );

        List<NbtMap> entityIdNbtList = nbt.getList("idlist", NbtType.COMPOUND);

        Set<String> entityIds = new HashSet<>();

        for (NbtMap entityId : entityIdNbtList) {
            String id = entityId.getString("id");
            if (id.startsWith("minecraft:")) id = id.substring(10);
            entityIds.add(id);
        }

        log("Downloading language list...");
        JsonArray languages = JsonParser.parseString(
                BedrockResourcesWrapper.getResourceAsString("texts/languages.json")
        ).getAsJsonArray();

        List<String> languageCodes = new ArrayList<>();

        for (JsonElement element : languages) {
            if (!element.isJsonPrimitive()) throw new IllegalArgumentException("Language code isn't a string.");
            languageCodes.add(element.getAsString());
        }

        Files.createDirectories(IntegratedPack.WORKING_PATH.resolve("texts"));

        for (String languageCode : languageCodes) {
            log("Applying mount translation fixes to %s".formatted(languageCode));
            Properties properties = new Properties();
            String downloadedTranslations = BedrockResourcesWrapper.getResourceAsString(
                    "texts/%s.lang".formatted(languageCode)
            );
            properties.load(new StringReader(downloadedTranslations));

            String normalHint = properties.getProperty("action.hint.exit.pig", "Tap sneak to dismount");
            String schemeHint = properties.getProperty("action.hint.exit.scheme.pig", "Tap dismount to dismount");
            String consoleHint = properties.getProperty("action.hint.exit.console.pig", "Press :_input_key.sneak: to dismount");

            Map<String, String> generatedLangProperties = new LinkedHashMap<>();

            for (String entityId : entityIds) {
                if (properties.containsKey("action.hint.exit." + entityId)) continue; // Vanilla has it, keep

                generatedLangProperties.put("action.hint.exit." + entityId, normalHint);
                generatedLangProperties.put("action.hint.exit.scheme." + entityId, schemeHint);
                generatedLangProperties.put("action.hint.exit.console." + entityId, consoleHint);
            }

            String langOutput = generatedLangProperties.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("\n"));

            Files.writeString(IntegratedPack.WORKING_PATH.resolve("texts/%s.lang".formatted(languageCode)), langOutput);
        }
    }
}
