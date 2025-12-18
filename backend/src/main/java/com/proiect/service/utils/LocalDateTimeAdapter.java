package com.proiect.service.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Serializeaza LocalDateTime in JSON.
     * @param src LocalDateTime-ul
     * @param typeOfSrc tipul
     * @param context contextul
     * @return elementul JSON
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        return new JsonPrimitive(src.format(ISO));
    }

    /**
     * Deserializeaza JSON in LocalDateTime.
     * @param json elementul JSON
     * @param typeOfT tipul
     * @param context contextul
     * @return LocalDateTime-ul
     * @throws JsonParseException daca parsarea esueaza
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        return LocalDateTime.parse(json.getAsString(), ISO);
    }
}
