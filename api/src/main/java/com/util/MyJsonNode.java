package com.util;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

/**
 * Created on 2018/5/26.
 *
 * @author zhiqiang bao
 */
@Data
public class MyJsonNode {

    private JsonNode jsonNode;

    public MyJsonNode(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public String findValueAsString(String pathName) {
        JsonNode newNode = jsonNode.findValue(pathName);
        if (Objects.isNull(newNode)) {
            return "";
        } else {
            return newNode.asText("");
        }
    }

    public int findValueAsInt(String pathName) {
        JsonNode newNode = jsonNode.findValue(pathName);
        if (Objects.isNull(newNode)) {
            return 0;
        } else {
            return newNode.asInt(0);
        }
    }

    public long findValueAsLong(String pathName) {
        JsonNode newNode = jsonNode.findValue(pathName);
        if (Objects.isNull(newNode)) {
            return 0L;
        } else {
            return newNode.asLong(0L);
        }
    }

}
