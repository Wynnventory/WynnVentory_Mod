package com.wynnventory.feature.joinmessage;

import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import com.wynnventory.util.ChatUtils;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class ServerJoinMessageFeature {
    private static final Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldStateChange(WorldStateEvent e) {
        if (e.isFirstJoinWorld() || e.getNewState() == WorldState.WORLD) {
            processMessages();
        }
    }

    public static void queueMessage(MessageSeverity severity, Component message) {
        messages.offer(new ChatMessage(severity, message));
    }

    public static void queueMessage(MessageSeverity severity, String message) {
        queueMessage(severity, Component.translatable(message));
    }

    private void processMessages() {
        ChatMessage msg;
        while ((msg = messages.poll()) != null) {
            ChatUtils.sendChatMessage(msg);
        }
    }
}
