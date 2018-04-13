package coop.magnesium.vanadium.system;

import java.io.Serializable;

public class DataTimer implements Serializable {
    public TimerType timerType;
    public Serializable obj;

    public DataTimer(TimerType timerType, Serializable obj) {
        this.timerType = timerType;
        this.obj = obj;
    }
}
