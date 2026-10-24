package com.schematic.preview.mixin;

import com.schematic.preview.SchematicPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Tab list: puts the icon on your entry and moves it above the media players. */
@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract Text getPlayerName(PlayerListEntry entry);

    @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
    private void schematic$reorder(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        if (!SchematicPreview.enabled || client.player == null || client.getNetworkHandler() == null) {
            return;
        }
        PlayerListEntry me = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        if (me == null) {
            return;
        }
        List<PlayerListEntry> list = new ArrayList<>(cir.getReturnValue());
        if (!list.remove(me)) {
            return;
        }

        // Find the first media player that is online (in the server's own order).
        PlayerListEntry anchor = null;
        int anchorIndex = -1;
        for (int i = 0; i < list.size() && anchor == null; i++) {
            String shown = getPlayerName(list.get(i)).getString().toLowerCase(Locale.ROOT);
            for (String name : SchematicPreview.anchors) {
                if (shown.contains(name.toLowerCase(Locale.ROOT))) {
                    anchor = list.get(i);
                    anchorIndex = i;
                    break;
                }
            }
        }
        if (anchor == null) {
            return; // nobody to anchor to: leave the list exactly as the server sorted it
        }

        // Go to the top of that media player's group, so you sit above ALL the media players.
        int target = anchorIndex;
        Team anchorTeam = anchor.getScoreboardTeam();
        if (anchorTeam != null) {
            for (int i = 0; i < list.size(); i++) {
                Team t = list.get(i).getScoreboardTeam();
                if (t != null && t.getName().equals(anchorTeam.getName())) {
                    target = i;
                    break;
                }
            }
        }
        list.add(target, me);
        cir.setReturnValue(list);
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void schematic$icon(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!SchematicPreview.enabled || client.player == null || client.getNetworkHandler() == null) {
            return;
        }
        if (entry == client.getNetworkHandler().getPlayerListEntry(client.player.getUuid())) {
            cir.setReturnValue(SchematicPreview.withIcon(cir.getReturnValue()));
        }
    }
}
