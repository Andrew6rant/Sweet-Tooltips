package io.github.andrew6rant.sweettooltips.mixin.client;

import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OrderedTextTooltipComponent.class)
public class OrderedTextTooltipComponentMixin {
    @Shadow @Final private OrderedText text;

    @Inject(method = "getHeight()I", at = @At(value = "HEAD"), cancellable = true)
    private void sweettooltips$changeEmptyTextTooltipComponentHeight(CallbackInfoReturnable<Integer> cir) {
        if (true) { // this'll be a config option later
            if (this.text.equals(OrderedText.EMPTY)) {
                cir.setReturnValue(0);
            } else
                cir.setReturnValue(10);
            }
        }
    }
}
