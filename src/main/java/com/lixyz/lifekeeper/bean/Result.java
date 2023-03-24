package com.lixyz.lifekeeper.bean;

public class Result {
    private boolean result;
    private String exceptionMessage;
    private Object exceptionObject;
    private Object resultObject;

    public Result() {
    }

    public Result(boolean result, String exceptionMessage, Object exceptionObject, Object resultObject) {
        this.result = result;
        this.exceptionMessage = exceptionMessage;
        this.exceptionObject = exceptionObject;
        this.resultObject = resultObject;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Object getExceptionObject() {
        return exceptionObject;
    }

    public void setExceptionObject(Object exceptionObject) {
        this.exceptionObject = exceptionObject;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }
}
