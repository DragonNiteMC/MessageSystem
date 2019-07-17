package com.ericlam.mc.msgsystem.manager;

import com.ericlam.mc.bungee.hnmc.container.OfflinePlayer;
import com.google.gson.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayerAdapter implements JsonDeserializer<OfflinePlayer> {

    private static final String CLASSNAME = "com.ericlam.mc.bungee.hnmc.container.OfflineData";

    @Override
    public OfflinePlayer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final String name = jsonObject.get("name").getAsString();
        final UUID uuid = jsonDeserializationContext.deserialize(jsonObject.get("uniqueId"), UUID.class);
        final boolean premium = jsonObject.get("premium").getAsBoolean();
        final long lastLogin = jsonObject.get("lastLogin").getAsLong();
        try {
            Class<?> offlineData = Class.forName(CLASSNAME);
            Constructor<?> constructor = offlineData.getConstructor(String.class, UUID.class, boolean.class, long.class);
            return (OfflinePlayer) constructor.newInstance(name, uuid, premium, lastLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
