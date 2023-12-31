package io.github.andrew6rant.sweettooltips.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow @Final private MatrixStack matrices;

    @Shadow public abstract void drawItem(ItemStack item, int x, int y);

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y);

    @Shadow public abstract void fillGradient(int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd);

    @Unique
    private static ItemStack durabilityrender$savedFocusedStack;

    // modified from my own DurabilityRender mod's DrawContextMixin (Andrew Grant)
    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "HEAD"))
    private void sweettooltips$saveFocusedStack(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        Screen currentScreen = client.currentScreen;
        if (currentScreen != null && currentScreen instanceof HandledScreen<?> handledScreen) {
            Slot focusedSlot = ((HandledScreenAccessor)handledScreen).getFocusedSlot();
            if (focusedSlot != null) {
                ItemStack focusedStack = focusedSlot.getStack();
                if (!focusedStack.isEmpty()) {
                    durabilityrender$savedFocusedStack = focusedSlot.getStack();
                }
            }
        }
    }

    // modified from my own DurabilityRender mod's DrawContextMixin (Andrew Grant)
    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "TAIL"))
    private void sweettooltips$cullFocusedStack(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        durabilityrender$savedFocusedStack = null; // hovering over an item and then immediately pausing and viewing a settings tooltip would otherwise render the durability tooltip
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
            at = @At(value = "TAIL", shift = At.Shift.BEFORE))
    private void sweettooltips$drawItems(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        if (durabilityrender$savedFocusedStack != null) {
            /*this.matrices.push();
            this.matrices.translate(x +10, y - 14, 256.0F); // needs to be this high to render blockItems
            this.matrices.scale(2f, 2f, 1);
            this.drawItem(durabilityrender$savedFocusedStack, 0, 0);
            //this.drawItemInSlot(textRenderer, durabilityrender$savedFocusedStack, 0, 0); // use the count override one with "" to remove item count
            this.matrices.pop();*/
        }
    }

    @ModifyArg(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawText(Lnet/minecraft/client/font/TextRenderer;IILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V"
            ),
            index = 1)
    private int sweettooltips$modifyTextX(int n) {
        if (durabilityrender$savedFocusedStack != null) {
            return n+33;
        } return n;
    }

    @ModifyArg(method = "method_51743(IIII)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;render(Lnet/minecraft/client/gui/DrawContext;IIIII)V"
            ),
            index = 3)
    private int sweettooltips$modifyTooltipBackgroundWidth(int width) {
        if (durabilityrender$savedFocusedStack != null) {
            return width+33;
        } return width;
    }

    @ModifyArg(method = "method_51743(IIII)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;render(Lnet/minecraft/client/gui/DrawContext;IIIII)V"
            ),
            index = 4)
    private int sweettooltips$modifyTooltipBackgroundHeight(int height) {
        if (durabilityrender$savedFocusedStack != null) {
            //System.out.println(height);
            height = height - 2;
            return Math.max(height, 28);
        } return height;
    }

    @Inject(method = "method_51743(IIII)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;render(Lnet/minecraft/client/gui/DrawContext;IIIII)V",
                    shift = At.Shift.AFTER))
    private void sweettooltips$injectVerticalTooltipLine(int x, int y, int width, int height, CallbackInfo ci) {
        if (durabilityrender$savedFocusedStack != null) {
            this.matrices.push();
            this.matrices.translate(x-2, y-2, 256.0F); // needs to be this high to render blockItems
            this.matrices.scale(2f, 2f, 1);
            this.drawItem(durabilityrender$savedFocusedStack, 0, 0);
            //this.drawItemInSlot(textRenderer, durabilityrender$savedFocusedStack, 0, 0); // use the count override one with "" to remove item count
            this.matrices.pop();
            this.fillGradient(x + 30, y-2, x + 31, y + 30, 400, 1347420415, 1344798847);
            if (height > 30) {
                this.fillGradient(x-2, y+30, x + 31, y + 31, 400, 1347420415, 1344798847);
            }
        }
    }

    @ModifyConstant(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
    constant = @Constant(intValue = 2))
    private int sweettooltips$modifyTooltipTitleHeight(int x) {
        return 1;
    }
}