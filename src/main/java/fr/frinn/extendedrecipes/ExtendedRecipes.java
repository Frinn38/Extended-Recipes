package fr.frinn.extendedrecipes;

import fr.frinn.extendedrecipes.common.recipes.ShapedRecipeExtended;
import fr.frinn.extendedrecipes.common.recipes.ShapelessRecipeExtended;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExtendedRecipes.MODID)
public class ExtendedRecipes {
    public static final String MODID = "extendedrecipes";

    public static final IRecipeType<ShapedRecipeExtended> SHAPED_RECIPE_EXTENDED_TYPE = IRecipeType.register("shaped_recipe_extended");

    public ExtendedRecipes() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRecipeSerializer);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
    }

    public void registerRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        if(event.getGenericType() == IRecipeSerializer.class) {
            event.getRegistry().registerAll(
                    new ShapedRecipeExtended.Serializer().setRegistryName(new ResourceLocation(MODID, "crafting_shaped_extended")),
                    new ShapelessRecipeExtended.Serializer().setRegistryName(new ResourceLocation(MODID, "crafting_shapeless_extended"))
            );
        }
    }
}
