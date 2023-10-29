package plugins.nate.smp.utils;

import plugins.nate.smp.SMP;

public class DependencyUtils {
    public static void checkDependencies() {
        if (SMP.getCoreProtect() == null) {
            SMPUtils.log("Failed to load CoreProtect");
        }
    }
}
