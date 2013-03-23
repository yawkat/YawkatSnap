package at.yawk.snap;

public abstract class KeyboardControl extends Thread implements Runnable {
    private final int pollTimeout;
    private final YawkatSnap snapper;
    
    public KeyboardControl(YawkatSnap snapper, int pollTimeout) {
        this.pollTimeout = pollTimeout;
        this.snapper = snapper;
    }
    
    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                if (pollKeyboard()) {
                    snapper.doSnap();
                }
                Thread.sleep(pollTimeout);
            }
        } catch (InterruptedException e) {
        }
    }
    
    protected abstract boolean pollKeyboard();
}
