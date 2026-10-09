
package com.example.autorelog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public final class AutoRelogClient implements ClientModInitializer {
    private static final double TRIGGER_Y = -5.0;

    private boolean triggered;
    private ServerInfo savedServer;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        if (client.player != null && client.getNetworkHandler() != null) {
            if (!triggered && client.player.getY() <= TRIGGER_Y) {
                ServerInfo current = client.getCurrentServerEntry();

                if (current == null) {
                    triggered = true;
                    return;
                }

                savedServer = new ServerInfo(
                    current.name,
                    current.address,
                    current.getServerType()
                );

                triggered = true;
                client.disconnect(Text.literal("AutoRelog"));
                client.setScreen(new AutoRelogScreen(savedServer, true));
            }
        }
    }
}
