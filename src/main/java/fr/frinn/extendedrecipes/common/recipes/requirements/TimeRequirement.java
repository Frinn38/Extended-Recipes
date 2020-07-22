package fr.frinn.extendedrecipes.common.recipes.requirements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import fr.frinn.extendedrecipes.common.registry.RequirementRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;

public class TimeRequirement implements IRequirement {

    private Range<Long> timeRange;
    private boolean inverted;

    public TimeRequirement(Range<Long> timeRange, boolean inverted) {
        this.timeRange = timeRange;
        this.inverted = inverted;
    }

    @Override
    public boolean test(@Nullable World world, @Nullable PlayerEntity player) {
        if(world != null)
            if(!inverted)
                return this.timeRange.contains(world.getDayTime() % 24000L);
            else
                return !this.timeRange.contains(world.getDayTime() % 24000L);
        else
            return false;
    }

    @Override
    public String getMessage() {
        if(!inverted)
            return "DayTime between " + timeRange.getMinimum() + " and " + timeRange.getMaximum() + " ticks";
        else
            return "DayTime between " + timeRange.getMaximum() + " and " + timeRange.getMinimum() + " ticks";
    }

    @Override
    public IRequirementSerializer<TimeRequirement> getSerializer() {
        return RequirementRegistry.TIME;
    }

    public static class Serializer extends ForgeRegistryEntry<IRequirementSerializer<?>> implements IRequirementSerializer<TimeRequirement> {

        @Override
        public TimeRequirement read(JsonObject json) {
            TimeRequirement requirement;
            try {
                requirement = getRequirement(JSONUtils.getString(json, "time"));
            }
            catch (Exception e) {
                e.printStackTrace();
                requirement = null;
            }
            return requirement;
        }

        @Override
        public JsonObject write(TimeRequirement requirement) {
            JsonObject json = new JsonObject();
            json.addProperty("type", this.getRegistryName().toString());
            String inverted = requirement.inverted ? "--" : "++";
            json.addProperty("time", requirement.timeRange.getMinimum() + ":" + requirement.timeRange.getMaximum() + ":" + inverted);
            return json;
        }

        private TimeRequirement getRequirement(String s) {
            TimeRequirement requirement;
            String[] ticks = s.split(":");
            if(ticks.length == 1) {
                Range<Long> timeRange;
                if (s.substring(0, 1).equals(">")) {
                    if(s.substring(1, 2).equals("="))
                        timeRange = Range.between(Long.parseLong(s.substring(2)), 24000L);
                    else
                        timeRange = Range.between(Long.parseLong(s.substring(1)) + 1L, 24000L);
                }
                else if(s.substring(0, 1).equals("="))
                    timeRange = Range.between(Long.parseLong(s.substring(1)), Long.parseLong(s.substring(1)));
                else if(s.substring(0, 1).equals("<")) {
                    if(s.substring(1, 2).equals("="))
                        timeRange = Range.between(0L, Long.parseLong(s.substring(2)));
                    else
                        timeRange = Range.between(0L, Long.parseLong(s.substring(1)) - 1L);
                }
                else throw new JsonParseException("Error parsing Time Requirement value :  \"" + s + "\" - Exepted \"number:number\" or \"[>|>=|=|<=|<]number\"");
                requirement = new TimeRequirement(timeRange, false);
            }
            else if(ticks.length == 2) {
                if(Long.parseLong(ticks[0]) <= Long.parseLong(ticks[1]))
                    requirement = new TimeRequirement(Range.between(Long.parseLong(ticks[0]), Long.parseLong(ticks[1])), false);
                else
                    requirement = new TimeRequirement(Range.between(Long.parseLong(ticks[0]), Long.parseLong(ticks[1])), true);
            }
            else if(ticks.length == 3) {
                if(ticks[2].equals("--"))
                    requirement = new TimeRequirement(Range.between(Long.parseLong(ticks[0]), Long.parseLong(ticks[1])), true);
                else
                    requirement = new TimeRequirement(Range.between(Long.parseLong(ticks[0]), Long.parseLong(ticks[1])), true);
            }
            else throw new JsonParseException("Error parsing Time Requirement value :  \"" + s + "\" - Exepted \"number:number\" or \"[>|>=|=|<=|<]number\"");
            return requirement;
        }
    }
}
