package fr.frinn.extendedrecipes.common.registry;

import fr.frinn.extendedrecipes.ExtendedRecipes;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirementSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

@Mod.EventBusSubscriber(modid = ExtendedRecipes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryRegistry {

    public static IForgeRegistry<IRequirementSerializer<?>> REQUIREMENTS;

    @SubscribeEvent
    public static void registerRegistry(RegistryEvent.NewRegistry event) {
         makeRegistry(new ResourceLocation(ExtendedRecipes.MODID, "recipes_requirements"), IRequirementSerializer.class).disableSaving().create();

        REQUIREMENTS = RegistryManager.ACTIVE.getRegistry(new ResourceLocation(ExtendedRecipes.MODID, "recipes_requirements"));
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type) {
        return new RegistryBuilder<T>().setName(name).setType(type);
    }
}
