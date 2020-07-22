package fr.frinn.extendedrecipes.common.recipes;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import fr.frinn.extendedrecipes.ExtendedRecipes;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipeExtended extends ShapelessRecipe {

    @ObjectHolder(ExtendedRecipes.MODID + ":crafting_shapeless_extended")
    public static IRecipeSerializer<ShapelessRecipeExtended> CRAFTING_SHAPELESS_EXTENDED = null;

    private List<IRequirement> requirements;

    public ShapelessRecipeExtended(ResourceLocation name, String group, ItemStack output, NonNullList<Ingredient> ingredients) {
        super(name, group, output, ingredients);
        this.requirements = new ArrayList<>();
    }

    public ShapelessRecipeExtended addRequirement(IRequirement requirement) {
        this.requirements.add(requirement);
        return this;
    }

    public ImmutableList<IRequirement> getRequirements() {
        return ImmutableList.copyOf(this.requirements);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return CRAFTING_SHAPELESS_EXTENDED;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessRecipeExtended> {

        public ShapelessRecipeExtended read(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else {
                ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
                ShapelessRecipeExtended recipe = new ShapelessRecipeExtended(recipeId, s, itemstack, nonnulllist);
                for(IRequirement requirement : RecipeUtils.readRequirements(json.getAsJsonArray("requirements")))
                    recipe.addRequirement(requirement);
                return recipe;
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < p_199568_0_.size(); ++i) {
                Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
                if (!ingredient.hasNoMatchingItems()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public ShapelessRecipeExtended read(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readString(32767);
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.read(buffer));
            }
            ItemStack itemstack = buffer.readItemStack();
            ShapelessRecipeExtended recipe = new ShapelessRecipeExtended(recipeId, s, itemstack, nonnulllist);
            for(IRequirement requirement : RecipeUtils.readRequirements(buffer))
                recipe.addRequirement(requirement);
            return recipe;
        }

        public void write(PacketBuffer buffer, ShapelessRecipeExtended recipe) {
            buffer.writeString(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());

            RecipeUtils.writeRequirements(buffer, recipe.requirements);
        }
    }
}
