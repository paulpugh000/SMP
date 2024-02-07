package plugins.nate.smp.managers;

import java.util.HashMap;
import java.util.UUID;

public class PvPManager {
    public static HashMap<UUID, Long> lastDmgTime = new HashMap<>();
    public static final long DAMAGE_COOLDOWN = 15000L; // 15s
}
