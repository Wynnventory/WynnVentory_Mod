package com.wynnventory.feature.joinmessage;

import net.minecraft.network.chat.Component;

public record ChatMessage(MessageSeverity severity, Component message) {}
