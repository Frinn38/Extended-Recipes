package fr.frinn.extendedrecipes.common.recipes.requirements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import fr.frinn.extendedrecipes.common.registry.RequirementRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Optional;

public class DimensionTypeRequirement implements IRequirement {

    private DimensionType type;

    public DimensionTypeRequirement(DimensionType type) {
        this.type = type;
    }

    @Override
    public boolean test(@Nullable World world, @Nullable PlayerEntity player) {
        if(world != null)
            return world.dimension.getType() == this.type;

        return false;
    }

    @Override
    public IRequirementSerializer getSerializer() {
        return RequirementRegistry.DIMENSION_TYPE;
    }

    @Override
    public String getMessage() {
        return "Dimension : " + this.type.getRegistryName();
    }

    public static class Serializer extends ForgeRegistryEntry<IRequirementSerializer<?>> implements IRequirementSerializer<DimensionTypeRequirement> {

        @Override
        public DimensionTypeRequirement read(JsonObject json) {
            String dim = JSONUtils.getString(json, "dimension");
            Optional<DimensionType> type = DimensionManager.getRegistry().getValue(new ResourceLocation(dim));
            if(type.isPresent())
                return new DimensionTypeRequirement(type.get());

            throw new JsonParseException("Error parsing Dimension Type requirement : " + dim + " does not correspond to a valid dimension type - Eg : minecraft:the_nether");
        }

        @Override
        public JsonObject write(DimensionTypeRequirement requirement) {
            JsonObject json = new JsonObject();
            json.addProperty("dimension", requirement.type.getRegistryName().toString());
            return json;
        }
    }
}
