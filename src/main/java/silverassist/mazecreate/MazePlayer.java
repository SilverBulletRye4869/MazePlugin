package silverassist.mazecreate;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.UUID;

public class MazePlayer {
    private long startTime = 0;
    private long endTime = 0;
    private final UUID uuid;

    public MazePlayer(UUID uuid){
        this.uuid = uuid;
    }
    public void start(){
        startTime = System.currentTimeMillis();
    }
    public void finish(){
        endTime = System.currentTimeMillis();
    }
    public String getTime(){
        return DurationFormatUtils.formatPeriod(startTime, endTime, "mm:ss.SSS");
    }
    public UUID getUuid(){
        return uuid;
    }
}
