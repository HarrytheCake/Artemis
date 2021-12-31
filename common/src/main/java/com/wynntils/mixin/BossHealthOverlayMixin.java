/*
 * Copyright © Wynntils 2021.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mixin;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket.Operation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {
    @Shadow @Final private final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

    @Inject(
            method = "update(Lnet/minecraft/network/protocol/game/ClientboundBossEventPacket;)V",
            at = @At("HEAD"),
            cancellable = true)
    public void updatePre(ClientboundBossEventPacket packet, CallbackInfo ci) {
        // Work around bug in Wynncraft that causes NPEs in Vanilla
        Operation operation = packet.getOperation();
        if (operation != Operation.ADD && operation != Operation.REMOVE) {
            if (!events.containsKey(packet.getId())) {
                ci.cancel();
            }
        }
    }
}