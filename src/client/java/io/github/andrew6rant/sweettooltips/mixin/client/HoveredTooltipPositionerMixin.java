package io.github.andrew6rant.sweettooltips.mixin.client;

import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HoveredTooltipPositioner.class)
public class HoveredTooltipPositionerMixin {
    @ModifyArg(method = "getPosition(IIIIII)Lorg/joml/Vector2ic;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/tooltip/HoveredTooltipPositioner;preventOverflow(IILorg/joml/Vector2i;II)V"
            ),
            index = 0)
    private int sweettooltips$modifyPreventOverflow(int x) {
        return x - 33;
    }
}
