package com.eternalcode.plots.utils;

import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.position.PositionAdapter;
import com.eternalcode.plots.region.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public final class LocationUtils {

    private LocationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Set<Location> getBorder(Plot plot) {

        Set<Location> locations = new HashSet<>();

        Region region = plot.getRegion();

        Location min = PositionAdapter.convert(region.getPosMin()).add(
            0,
            0,
            0
        );

        Location max = PositionAdapter.convert(region.getPosMax()).add(
            1,
            0,
            1
        );

        for (double i = 0; i <= (region.getSize() + 1); i++) {
            Location loc = new Location(min.getWorld(), min.getX() + i, min.getY(), min.getZ());
            Location loc2 = new Location(min.getWorld(), min.getX(), min.getY(), min.getZ() + i);
            locations.add(loc);
            locations.add(loc2);
        }

        for (double i = 0; i <= (region.getSize() + 1); i++) {
            Location loc = new Location(max.getWorld(), max.getX() - i, max.getY(), max.getZ());
            Location loc2 = new Location(max.getWorld(), max.getX(), max.getY(), max.getZ() - i);
            locations.add(loc);
            locations.add(loc2);
        }

        locations.add(min);
        locations.add(max);

        return locations;
    }

    public static double getDistance(Player player, Plot plot) {

        Location closest = PositionAdapter.convert(plot.getRegion().getCenter());

        for (Location location : getBorder(plot)) {

            location = new Location(location.getWorld(), location.getX(), player.getLocation().getY(), location.getZ());

            if (player.getLocation().distance(location) < player.getLocation().distance(closest)) {
                closest = location;
            }
        }

        return player.getLocation().distance(closest);
    }

    public static boolean isIn(Location pos1, Location pos2, Location loc) {
        if (pos1.getWorld() != null && loc.getWorld() != null) {
            if (pos1.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                double x = loc.getX();
                double z = loc.getZ();

                double lowx = pos2.getX();
                double lowz = pos2.getZ();

                double highx = pos1.getX();
                double highz = pos1.getZ();

                return (x <= highx && x >= lowx) && (z <= highz && z >= lowz);
            } else {
                return false;
            }
        }
        return false;
    }


    /* OTHERS

    private Option<List<Entity>> getEntitiesInRadius(Location location, double radius) throws ExecutionException, InterruptedException {
        List<Entity> entities = new ArrayList<>();
        World world = location.getWorld();

        if(world != null) {
            int smallX = (int) Math.floor((location.getX() - radius) / 16.0D);
            int bigX = (int) Math.floor((location.getX() + radius) / 16.0D);
            int smallZ = (int) Math.floor((location.getZ() - radius) / 16.0D);
            int bigZ = (int) Math.floor((location.getZ() + radius) / 16.0D);

            for (int x = smallX; x <= bigX; x++) {
                for (int z = smallZ; z <= bigZ; z++) {
                    if (world.isChunkLoaded(x, z)) {
                        Chunk chunk = PaperLib.getChunkAtAsync(world, x, z).get();
                        entities.addAll(Arrays.asList(chunk.getEntities()));
                    }
                }
            }
            entities.removeIf(Objects::isNull);
            entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);
            return Option.of(entities);
        }
        return Option.none();
    }

    private Option<Plot> getPlotInRange(Player player, int range) {

        Location loc = player.getLocation();

        List<Location> locations = Arrays.asList(
            new Location(loc.getWorld(), loc.getX() + range, loc.getY(), loc.getZ() + range),
            new Location(loc.getWorld(), loc.getX() - range, loc.getY(), loc.getZ() - range),
            new Location(loc.getWorld(), loc.getX() - range, loc.getY(), loc.getZ() + range),
            new Location(loc.getWorld(), loc.getX() + range, loc.getY(), loc.getZ() - range),
            new Location(loc.getWorld(), loc.getX() + range, loc.getY(), loc.getZ()),
            new Location(loc.getWorld(), loc.getX() - range, loc.getY(), loc.getZ()),
            new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + range),
            new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - range),
            new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ())
        );

        for (Location location : locations) {
            if(plotManager.getRegion(location).isPresent()) {
                return Option.of(plotManager.getPlot(plotManager.getRegion(location).get()).get());
            }
        }

        return Option.none();
    }

     */


}
