package com.geo.points.distance.elasticSearchGeoDisanceDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApiResponse<T> {
    private T data;
    private ApiResult result;

    @JsonProperty(value="errors")
    private String errorMessage;

    public T getData() {
        return data;
    }

    public ApiResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public ApiResult getResult() {
        return result;
    }

    public ApiResponse<T> setResult(ApiResult result) {
        this.result = result;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
