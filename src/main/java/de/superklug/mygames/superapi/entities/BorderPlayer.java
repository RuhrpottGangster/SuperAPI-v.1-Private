package de.superklug.mygames.superapi.entities;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class BorderPlayer {
    
    private @Getter @Setter int radius;
    private @Getter @Setter Location center;

    public BorderPlayer() {
    }
    
}
