package fr.frinn.extendedrecipes.common.integration.jei;

import fr.frinn.extendedrecipes.ExtendedRecipes;
import fr.frinn.extendedrecipes.common.recipes.ShapedRecipeExtended;
import fr.frinn.extendedrecipes.common.recipes.ShapelessRecipeExtended;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class ExtendedRecipesJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExtendedRecipes.MODID, "jei_plugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IExtendableRecipeCategory<ICraftingRecipe, ICraftingCategoryExtension> craftingCategory = registration.getCraftingCategory();
        craftingCategory.addCategoryExtension(ShapedRecipeExtended.class, (recipe) -> new ExtendedShapedRecipeWrapper(recipe));
        craftingCategory.addCategoryExtension(ShapelessRecipeExtended.class, (recipe) -> new ExtendedShapelessRecipeWrapper(recipe));
    }
}
