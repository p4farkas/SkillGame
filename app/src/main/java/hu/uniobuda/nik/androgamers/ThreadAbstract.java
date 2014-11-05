package hu.uniobuda.nik.androgamers;

public abstract class ThreadAbstract extends Thread {
    @Override
    public abstract void run();

    public abstract boolean SendMessage(String message);
}
