class Starter {
    public static void startRunnables(Runnable[] runnables) {
        for(Runnable counter : runnables)
        {
            Thread thread= new Thread(counter);
            thread.start();
        }
    }
}