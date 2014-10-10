package com.oenik.bir.skillgame;

public class Result {
    private String username;
    private int result;

    public Result(String username, int result) {
        this.username = username;
        this.result = result;
    }

    public String getResultString()
    {
        return result + " pont (" + "2012.08.31. 17:15)";
    }

}
