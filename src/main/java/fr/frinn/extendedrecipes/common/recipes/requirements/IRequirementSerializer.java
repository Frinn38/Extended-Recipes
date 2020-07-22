package fr.frinn.extendedrecipes.common.recipes.requirements;

import com.google.gson.JsonObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IRequirementSerializer<T extends IRequirement> extends IForgeRegistryEntry<IRequirementSerializer<?>> {

    T read(JsonObject json);

    JsonObject write(T requirement);
}
