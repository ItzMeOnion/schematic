package com.schematic.preview.mixin;

import com.schematic.preview.SchematicPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Vanilla hides your own nametag. In third person we show it (with the icon, see EntityRendererMixin). */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private void schematic$showOwnLabel(LivingEntity entity, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (SchematicPreview.enabled
                && entity == client.player
                && !client.options.getPerspective().isFirstPerson()
                && MinecraftClient.isHudEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
