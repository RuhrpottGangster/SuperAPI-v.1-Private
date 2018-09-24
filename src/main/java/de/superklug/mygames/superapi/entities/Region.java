package de.superklug.mygames.superapi.entities;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class Region {
    
    private @Getter @Setter String name;
    private @Getter @Setter Location firstLocation;
    private @Getter @Setter Location secondLocation;
    
    private @Getter @Setter List<String> regionFlags;

    public Region() {
    }

}
