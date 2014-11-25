package hu.uniobuda.nik.androgamers;

public class Result {
    private int result;

    public Result(int result) {
        this.result = result;
    }

    public String getResultString() {
        return result + " pont";
    }

}
