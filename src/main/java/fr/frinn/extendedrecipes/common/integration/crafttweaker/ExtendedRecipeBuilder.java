package fr.frinn.extendedrecipes.common.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import fr.frinn.extendedrecipes.ExtendedRecipes;
import fr.frinn.extendedrecipes.common.recipes.ShapedRecipeExtended;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirement;
import fr.frinn.extendedrecipes.common.recipes.requirements.TimeRequirement;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenCodeType.Name("mods.extendedrecipes.ExtendedRecipeBuilder")
public class ExtendedRecipeBuilder {

    private ResourceLocation name;
    private String group;
    private IIngredient[][] ingredients;
    private IItemStack output;
    private List<IRequirement> requirements;

    public ExtendedRecipeBuilder(String name, String group, IIngredient[][] ingredients, IItemStack output) {
        this.name = new ResourceLocation(ExtendedRecipes.MODID, name);
        this.group = group;
        this.ingredients = ingredients;
        this.output = output;
        this.requirements = new ArrayList<>();
    }

    @ZenCodeType.Method
    public static ExtendedRecipeBuilder create(String name, String group, IItemStack output, IIngredient[][] ingredients) {
        return new ExtendedRecipeBuilder(name, group, ingredients, output);
    }

    @ZenCodeType.Method
    public void build() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for(IIngredient[] line : this.ingredients)
            for(IIngredient ingredient : line)
                ingredients.add(ingredient.asVanillaIngredient());

        if(ingredients.isEmpty()) {
            CraftTweakerAPI.logError("Error building recipe \"" + this.name + "\" - Needs at least 1 ingredient");
            return;
        }

        ShapedRecipeExtended recipe = new ShapedRecipeExtended(this.name, this.group, 3, 3, ingredients, this.output.getInternal());
        for(IRequirement requirement : this.requirements)
            recipe.addRequirement(requirement);

        CraftTweakerAPI.apply(new ActionAddRecipe(CTCraftingTableManager.INSTANCE, recipe, "ExtendedShapedRecipe"));
    }

    @ZenCodeType.Method
    public ExtendedRecipeBuilder addTimeRequirement(long from, long to) {
        this.requirements.add(new TimeRequirement(Range.between(from, to), from > to));
        return this;
    }
}
