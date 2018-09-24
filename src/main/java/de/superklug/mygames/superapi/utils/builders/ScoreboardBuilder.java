package de.superklug.mygames.superapi.utils.builders;

import de.superklug.mygames.superapi.SuperAPI;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardBuilder {
    
    private final SuperAPI module;
    
    private final Player player;
    
    private final Scoreboard scoreboard;
    private final Objective objective;
    
    
    private int animationTick = 0;
    private int task;

    public ScoreboardBuilder(final SuperAPI module, final Player player) {
        this.module = module;
        
        this.player = player;
        
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("Board", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public ScoreboardBuilder setScoreboardTitle(final String name) {
        this.objective.setDisplayName(name);
        return this;
    }
    
    public ScoreboardBuilder addScoreboardLine(final String line, final int lineId) {
        this.objective.getScore(line).setScore(lineId);
        return this;
    }
    
    public ScoreboardBuilder addUpdateableScoreboardLine(final String prefix, final String suffix, final String entry, final int lineId) {
        
        Team team = this.scoreboard.registerNewTeam("x" + lineId);
        
            team.setPrefix(prefix);
            team.setSuffix(suffix);
            team.addEntry(entry);
        
        this.objective.getScore(entry).setScore(lineId);
        return this;
    }
    
    public ScoreboardBuilder createTeam(final int sortId, final String name, final String prefix, final String suffix, final boolean seeFriendlyInvisilbes, final NameTagVisibility nameTagVisibility) {
        
        Team team = this.scoreboard.registerNewTeam(sortId + "-" + name);
        
            team.setPrefix(prefix);
            team.setSuffix(suffix);
            team.setNameTagVisibility(nameTagVisibility);
            team.setCanSeeFriendlyInvisibles(seeFriendlyInvisilbes);
        
            return this;
    }
    
    public ScoreboardBuilder deleteTeam(final String name) {
        this.scoreboard.getTeam(name).unregister();
        return this;
    }
    
    public void build() {
        this.player.setScoreboard(this.scoreboard);
    }
    
    public void update(final String prefix, final String suffix, final int lineId) {
        if(this.player.getScoreboard() == null | Objects.requireNonNull(this.player.getScoreboard()).getObjective(DisplaySlot.SIDEBAR) == null) {
            return;
        }
        
        if(prefix != null && !prefix.isEmpty())
            this.player.getScoreboard().getTeam("x" + lineId).setPrefix(prefix);
        
        if (suffix != null && !suffix.isEmpty()) {
            this.player.getScoreboard().getTeam("x" + lineId).setSuffix(suffix);
        }
        
    }
    
    public void animate(final String[] displaynames) {
        
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(module.getPlugin(), () -> {

            this.animationTick = 0;

            this.task = Bukkit.getScheduler().scheduleAsyncRepeatingTask(module.getPlugin(), () -> {

                if (this.animationTick == displaynames.length) {
                    Bukkit.getScheduler().cancelTask(this.task);
                }

                Bukkit.getOnlinePlayers().forEach((players) -> {
                    if (players.getScoreboard() != null) {
                        try {
                            players.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(displaynames[this.animationTick]);
                        } catch (Exception exception) {}
                    }
                });

                this.animationTick++;

            }, 2, 2);

        }, 100, 100);
        
    }

}
