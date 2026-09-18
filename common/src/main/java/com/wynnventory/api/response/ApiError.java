package com.wynnventory.api.response;

/** The {@code error} object every /api/v2 failure carries: {"error": {"code", "message"}}. */
public record ApiError(String code, String message) {}
