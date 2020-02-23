package com.ericlam.mc.msgsystem.manager;

import com.ericlam.mc.bungee.hnmc.container.OfflinePlayer;
import com.ericlam.mc.msgsystem.api.ListSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ArrayListSerializer implements ListSerializer {

    @Override
    public String serialize(List<?> list) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(new Gson().toJson(list.get(i)));
            if (i != list.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public <E> List<E> deserialize(String string, Class<E> type) {
        Gson gson;
        if (type.getSimpleName().equalsIgnoreCase("OfflinePlayer")) {
            gson = new GsonBuilder().registerTypeAdapter(OfflinePlayer.class, new OfflinePlayerAdapter()).create();
        } else {
            gson = new Gson();
        }
        List<E> list = new ArrayList<>();
        String[] elements = string.replace("[", "").replace("]", "").split(", ");
        for (String element : elements) {
            if (element.isBlank()) continue;
            E e = gson.fromJson(element, type);
            list.add(e);
        }
        return list;
    }
}
