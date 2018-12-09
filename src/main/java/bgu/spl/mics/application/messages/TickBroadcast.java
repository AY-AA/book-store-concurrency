package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private final int currenTick;

    public TickBroadcast(int currenTick) {
        this.currenTick = currenTick;
    }

    public int getCurrenTick() {
        return currenTick;
    }
}
