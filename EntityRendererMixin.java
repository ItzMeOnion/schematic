package com.schematic.preview.mixin;

import com.schematic.preview.SchematicPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Puts the icon in front of your own nametag text. */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V",
            at = @At("RETURN"))
    private void schematic$icon(Entity entity, EntityRenderState state, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (SchematicPreview.enabled && entity == client.player && state.displayName != null) {
            state.displayName = SchematicPreview.withIcon(state.displayName);
        }
    }
}
