package plugins.nate.smp.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import plugins.nate.smp.SMP;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AutoRestarter {

    public static void scheduleRestart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        long midnightMillis = calendar.getTimeInMillis();
        long currentMillis = System.currentTimeMillis();
        scheduleWarning(midnightMillis - currentMillis - 600_000, "Server will restart in 10 minutes!");
        scheduleWarning(midnightMillis - currentMillis - 300_000, "Server will restart in 5 minutes!");
        scheduleWarning(midnightMillis - currentMillis - 60_000, "Server will restart in 1 minute!");
        scheduleWarning(midnightMillis - currentMillis - 30_000, "Server will restart in 30 seconds!");


        scheduleRestart(midnightMillis - currentMillis);
    }

    public static long getTimeUntilRestart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }

    private static void scheduleWarning(long delay, String message) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatUtils.coloredChat(ChatUtils.SERVER_PREFIX + message));
            }
        }.runTaskLaterAsynchronously(SMP.getPlugin(SMP.class), delay / 50);
    }

    private static void scheduleRestart(long delay) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }.runTaskLater(SMP.getPlugin(SMP.class), delay / 50);
    }
}
