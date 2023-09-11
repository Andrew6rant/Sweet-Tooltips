package io.github.andrew6rant.sweettooltips.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipBackgroundRenderer.class)
public abstract class TooltipBackgroundRendererMixin {
    @Shadow
    private static void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int startColor, int endColor) {
    }

    @Inject(method = "renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V",
    at = @At("HEAD"))
    private static void sweettooltips$injectTooltipVerticalLine(DrawContext context, int x, int y, int width, int height, int z, int startColor, int endColor, CallbackInfo ci) {
        //System.out.println("x: " + x + ", y: " + y + ", width: "+width + ", height: "+height+", z: " + z);
        //renderVerticalLine(context, x + 35, y, height - 2, z, startColor, endColor);
    }
}
