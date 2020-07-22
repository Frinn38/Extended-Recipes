package fr.frinn.extendedrecipes.common.registry;

import fr.frinn.extendedrecipes.ExtendedRecipes;
import fr.frinn.extendedrecipes.common.recipes.requirements.DimensionTypeRequirement;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirementSerializer;
import fr.frinn.extendedrecipes.common.recipes.requirements.TimeRequirement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = ExtendedRecipes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(ExtendedRecipes.MODID)
public class RequirementRegistry {

    public static final IRequirementSerializer<TimeRequirement> TIME = null;
    public static final IRequirementSerializer<DimensionTypeRequirement> DIMENSION_TYPE = null;

    @SubscribeEvent
    public static void registerRequirements(RegistryEvent.Register<IRequirementSerializer<?>> event) {
        event.getRegistry().registerAll(
                new TimeRequirement.Serializer().setRegistryName(new ResourceLocation(ExtendedRecipes.MODID, "time")),
                new DimensionTypeRequirement.Serializer().setRegistryName(new ResourceLocation(ExtendedRecipes.MODID, "dimension_type"))
        );
    }
}
