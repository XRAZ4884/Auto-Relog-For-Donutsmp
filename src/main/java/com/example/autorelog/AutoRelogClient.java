package com.example.autorelog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public final class AutoRelogClient implements ClientModInitializer {
    private static final double TRIGGER_Y = -5.0;
    private static final long RETRY_DELAY_MS = 3000L;
    private boolean triggered = false;
    private long reconnectAt = -1L;
    private ServerInfo savedServer;
    private boolean wasConnected = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick(client));
    }

    private void tick(MinecraftClient client) {
        if (client.player != null && client.getNetworkHandler() != null) {
            wasConnected = true;
            if (!triggered && client.player.getY() <= TRIGGER_Y) {
                ServerInfo current = client.getCurrentServerEntry();
                if (current == null) {
                    client.player.sendMessage(net.minecraft.text.Text.literal("[Auto Relog] Not connected to a multiplayer server."), false);
                    triggered = true;
                    return;
                }
                savedServer = new ServerInfo(current.name, current.address, current.isLocal());
                triggered = true;
                reconnectAt = System.currentTimeMillis() + RETRY_DELAY_MS;
                client.disconnect(new TitleScreen());
            }
        } else if (wasConnected && triggered && savedServer != null && reconnectAt > 0
                && System.currentTimeMillis() >= reconnectAt && client.currentScreen instanceof TitleScreen) {
            reconnectAt = -1L;
            wasConnected = false;
            ConnectScreen.connect(client.currentScreen, client, ServerAddress.parse(savedServer.address), savedServer, false, null);
        }

        // Re-arm only after the player is back in the world and safely above the threshold.
        if (triggered && client.player != null && client.player.getY() > TRIGGER_Y) {
            triggered = false;
            savedServer = null;
            reconnectAt = -1L;
        }
    }
}
