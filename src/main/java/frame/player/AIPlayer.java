package frame.player;

//import action.MapAction;
//import controller.Vars;

public class AIPlayer extends Player {

    public AIPlayer(int id) {
        this(id, "Computer");
    }

    public AIPlayer(int id, String name) {
        super(id, name, "AI");
        setReady(true);
    }

//    @Override
//    public void onNotify() {
//        sendNextMove();
//    }

//    public MapAction calculateNextMove() {
//        return new MapAction(0, 0, MapAction.ClickType.LEFT);
//    }
//
//    public void sendNextMove() {
//        Thread t = new Thread(new sender(calculateNextMove()), "sender");
//        t.start();
//    }


//    static class sender implements Runnable{
//        private final MapAction action;
//        public sender(MapAction a) {
//            action = a;
//        }
//        @Override
//        public void run() {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                Vars.publisher.submit(action);
//            }
//
//        }
//    }
}
