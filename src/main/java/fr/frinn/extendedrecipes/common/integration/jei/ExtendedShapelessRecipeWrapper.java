package fr.frinn.extendedrecipes.common.integration.jei;

import fr.frinn.extendedrecipes.common.recipes.ShapedRecipeExtended;
import fr.frinn.extendedrecipes.common.recipes.ShapelessRecipeExtended;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirement;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.gui.elements.DrawableBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class ExtendedShapelessRecipeWrapper implements ICraftingCategoryExtension {

    private ShapelessRecipeExtended recipe;

    public ExtendedShapelessRecipeWrapper(ShapelessRecipeExtended recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputIngredients(this.recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getRecipeOutput());
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, double mouseX, double mouseY) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(recipe != null && player != null && player.world != null) {
            boolean flag = false;
            for(IRequirement requirement : recipe.getRequirements()) {
                if(!requirement.test(player.world, player)) {
                    flag = true;
                    break;
                }
            }
            if(flag) {
                IDrawableBuilder builder = new DrawableBuilder(new ResourceLocation("minecraft:textures/gui/spectator_widgets.png"), 128, 0, 16, 16);
                builder.trim(1, 1, 1, 0);
                builder.build().draw(65, 19);
            }
        }
    }

    @Override
    public List<String> getTooltipStrings(double mouseX, double mouseY) {
        List<String> tooltip = new ArrayList<>();
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(recipe != null && player != null && player.world != null) {
            for(IRequirement requirement : recipe.getRequirements()) {
                if(mouseX >= 60 && mouseX <= 85 && mouseY >= 15 && mouseY <= 35) {
                    if(requirement.test(player.world, player))
                        tooltip.add(new StringTextComponent(requirement.getMessage()).setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
                    else
                        tooltip.add(new StringTextComponent(requirement.getMessage()).setStyle(new Style().setColor(TextFormatting.RED)).getFormattedText());
                }
            }
        }
        return tooltip;
    }
}
