/*
 * MIT License
 *
 * Copyright (c) 2025 Kinhiro Zane and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kinhiro.rgkcn.mixin;

import com.kinhiro.rgkcn.RGKCNAddon;
import com.kinhiro.rgkcn.util.FakePlayerRejoin;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import dev.anvilcraft.rg.sd.entity.FakePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Pseudo
@Mixin(FakePlayer.class)
public abstract class MixinFakePlayer {
    @ModifyArg(
        method = "lambda$createFake$1",
        at = @At(
            value = "INVOKE",
            target = "Ldev/anvilcraft/rg/sd/entity/FakePlayer;<init>(Lnet/minecraft/server/MinecraftServer;" +
                "Lnet/minecraft/server/level/ServerLevel;Lcom/mojang/authlib/GameProfile;" +
                "Lnet/minecraft/server/level/ClientInformation;Z)V"
        )
    )
    private static ServerLevel rgkcn$createFake$1$playerCommandRejoin$init(
        ServerLevel level,
        final @Local(argsOnly = true, name = "finalGP") GameProfile finalGP,
        final @Share(value = "rejoin", namespace = RGKCNAddon.NAMESPACE) LocalRef<Consumer<FakePlayer>> rejoin
    ) {
        if (FakePlayerRejoin.IS_REJOIN.get()) {
            final var server = level.getServer();
            try (final ProblemReporter.ScopedCollector collector = new ProblemReporter.ScopedCollector(RGKCNAddon.LOGGER)) {
                final var playerData = server.getPlayerList().loadPlayerData(new NameAndId(finalGP));
                if (playerData.isPresent()) {
                    @SuppressWarnings("deprecation") final var savedPosition = playerData
                        .map(nbt -> TagValueInput.create(collector, server.registryAccess(), nbt))
                        .flatMap(input -> input.read(ServerPlayer.SavedPosition.MAP_CODEC));

                    final var position = savedPosition.flatMap(ServerPlayer.SavedPosition::position);
                    final var rotation = savedPosition.flatMap(ServerPlayer.SavedPosition::rotation);
                    final var dimension = savedPosition.flatMap(ServerPlayer.SavedPosition::dimension)
                        .flatMap(dim -> Optional.ofNullable(server.getLevel(dim)));

                    if (position.isPresent() && rotation.isPresent() && dimension.isPresent()) {
                        level = dimension.get();
                        //noinspection SuspiciousNameCombination
                        rejoin.set(player -> player.snapTo(position.get(), rotation.get().x, rotation.get().y));
                    }
                }
            }
        }

        return level;
    }

    @WrapWithCondition(
        method = "lambda$createFake$1",
        at = @At(
            value = "FIELD",
            target = "Ldev/anvilcraft/rg/sd/entity/FakePlayer;fixStartingPosition:Ljava/lang/Runnable;",
            opcode = Opcodes.PUTFIELD
        )
    )
    private static boolean rgkcn$createFake$1$playerCommandRejoin$fixStartingPosition(
        final FakePlayer instance,
        final Runnable value,
        final @Share(value = "rejoin", namespace = RGKCNAddon.NAMESPACE) LocalRef<Consumer<FakePlayer>> rejoin
    ) {
        return !FakePlayerRejoin.IS_REJOIN.get() || rejoin.get() == null;
    }

    @Inject(
        method = "lambda$createFake$1",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;placeNewPlayer(Lnet/minecraft/network/Connection;" +
                "Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)V"
        )
    )
    private static void rgkcn$createFake$1$playerCommandRejoin$placeNewPlayer(
        final GameProfile finalGP,
        final MinecraftServer server,
        @SuppressWarnings("ParameterCanBeLocal") ServerLevel level,
        final Vec3 pos,
        final float yaw,
        final float pitch,
        final GameType gamemode,
        final Consumer<FakePlayer> callback,
        final boolean flying,
        final CallbackInfo ci,
        final @Local(name = "instance") FakePlayer instance,
        final @Share(value = "rejoin", namespace = RGKCNAddon.NAMESPACE) LocalRef<Consumer<FakePlayer>> rejoin
    ) {
        if (FakePlayerRejoin.IS_REJOIN.get() && rejoin.get() != null) {
            //noinspection UnusedAssignment
            level = server.getLevel(instance.level().dimension());
            instance.fixStartingPosition = () -> rejoin.get().accept(instance);
        }
    }

    @SuppressWarnings("WrapWithConditionTargetsNonVoid")
    @WrapWithCondition(
        method = "lambda$createFake$1",
        at = @At(
            value = "INVOKE",
            target = "Ldev/anvilcraft/rg/sd/entity/FakePlayer;teleportTo(Lnet/minecraft/server/level/ServerLevel;" +
                "DDDLjava/util/Set;FFZ)Z"
        )
    )
    private static boolean rgkcn$createFake$1$playerCommandRejoin$teleportTo(
        final FakePlayer instance,
        final ServerLevel level,
        final double x,
        final double y,
        final double z,
        final Set<Relative> relatives,
        final float newYRot,
        final float newXRot,
        final boolean resetCamera,
        final @Share(value = "rejoin", namespace = RGKCNAddon.NAMESPACE) LocalRef<Consumer<FakePlayer>> rejoin
    ) {
        return !FakePlayerRejoin.IS_REJOIN.get() || rejoin.get() == null;
    }
}
