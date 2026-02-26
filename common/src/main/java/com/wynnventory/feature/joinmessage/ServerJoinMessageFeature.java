package com.wynnventory.feature.joinmessage;

import com.wynntils.mc.event.PlayerInfoEvent;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import com.wynnventory.util.ChatUtils;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class ServerJoinMessageFeature {
    private static final Queue<ChatMessage> inGameMessages = new ConcurrentLinkedQueue<>();
    private static final Queue<ChatMessage> inCharSelectionMessages = new ConcurrentLinkedQueue<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldStateChange(WorldStateEvent e) {
        if (e.isFirstJoinWorld() || e.getNewState() == WorldState.WORLD) {
            processMessages(inGameMessages);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDisplayNameChange(PlayerInfoEvent.PlayerDisplayNameChangeEvent e) {
        processMessages(inCharSelectionMessages);
    }

    public static void queueGameMessage(MessageSeverity severity, Component message) {
        inGameMessages.offer(new ChatMessage(severity, message));
    }

    public static void queueGameMessage(MessageSeverity severity, String message) {
        queueGameMessage(severity, Component.translatable(message));
    }

    public static void queueCharSelectionMessage(MessageSeverity severity, Component message) {
        inCharSelectionMessages.offer(new ChatMessage(severity, message));
    }

    public static void queueCharSelectionMessage(MessageSeverity severity, String message) {
        queueCharSelectionMessage(severity, Component.translatable(message));
    }

    private void processMessages(Queue<ChatMessage> queue) {
        ChatMessage msg;
        while ((msg = queue.poll()) != null) {
            ChatUtils.sendChatMessage(msg);
        }
    }
}
