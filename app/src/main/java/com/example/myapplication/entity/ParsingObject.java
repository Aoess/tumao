package com.example.myapplication.entity;

import java.io.Serializable;

import okhttp3.Response;

public class ParsingObject implements Serializable {

    private Response response;

    public ParsingObject(Response response) {
        this.response = response;
    }

    public ParsingObject() {
    }

    @Override
    public String toString() {
        return "ParsingObject{" +
                "response=" + response +
                '}';
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
