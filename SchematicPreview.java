package com.schematic.preview;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-only and cosmetic. Adds an icon next to YOUR name in third person and in the tab list,
 * and moves your tab list entry. Nothing is sent to the server; only you see it.
 */
public class SchematicPreview implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("schematic_preview");

    /** The font that holds the icon (assets/schematic_preview/font/icon.json). */
    public static final Identifier ICON_FONT = Identifier.of("schematic_preview", "icon");
    public static final String GLYPH = "";

    public static volatile boolean enabled = true;
    /** Names of media players. Your tab entry is placed just above the first one found online. */
    public static volatile List<String> anchors = List.of("Abudoee");

    private static final Set<String> SENT = ConcurrentHashMap.newKeySet();

    private static final Path CONFIG =
            FabricLoader.getInstance().getConfigDir().resolve("schematic_preview.properties");

    @Override
    public void onInitializeClient() {
        load();
        LOGGER.info("[Schematic preview] loaded");

        // Tells you the mod is installed correctly: one chat line each time you join a server.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                notice("join", "mod loaded (you'll get one more line per feature when it kicks in)", true));
    }

    /** icon + space + the original name text (the empty parent keeps the font from leaking onto the name). */
    public static Text withIcon(Text original) {
        return Text.empty()
                .append(Text.literal(GLYPH).setStyle(Style.EMPTY.withFont(ICON_FONT)))
                .append(Text.literal(" "))
                .append(original);
    }

    /** Logs a message and shows it in your chat, once per key (per game session). */
    public static void notice(String key, String message) {
        notice(key, message, false);
    }

    private static void notice(String key, String message, boolean everyJoin) {
        if (everyJoin) {
            SENT.remove(key);
        }
        if (!SENT.add(key)) {
            return;
        }
        LOGGER.info("[Schematic preview] {}", message);
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (client.player != null) {
                client.player.sendMessage(Text.literal("[Schematic preview] " + message), false);
            }
        });
    }

    private static void load() {
        try {
            if (Files.exists(CONFIG)) {
                Properties p = new Properties();
                try (InputStream in = Files.newInputStream(CONFIG)) {
                    p.load(in);
                }
                enabled = Boolean.parseBoolean(p.getProperty("enabled", "true"));
                List<String> list = new ArrayList<>();
                for (String s : p.getProperty("media_players", "Abudoee").split(",")) {
                    if (!s.trim().isEmpty()) {
                        list.add(s.trim());
                    }
                }
                if (!list.isEmpty()) {
                    anchors = list;
                }
            } else {
                Properties p = new Properties();
                p.setProperty("enabled", "true");
                p.setProperty("media_players", "Abudoee");
                try (OutputStream out = Files.newOutputStream(CONFIG)) {
                    p.store(out, "Schematic preview (client only, cosmetic)\n"
                            + "media_players = comma separated names of media players on the server;\n"
                            + "you are listed just above the first of them that is online.");
                }
            }
        } catch (IOException ignored) {
        }
    }
}
