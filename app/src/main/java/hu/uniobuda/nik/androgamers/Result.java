package hu.uniobuda.nik.androgamers;

public class Result {
    private String username;
    private int result;

    public Result(String username, int result) {
        this.username = username;
        this.result = result;
    }

    public String getResultString() {
        return result + " pont";
    }

}
