package fr.frinn.extendedrecipes.common.recipes;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import fr.frinn.extendedrecipes.ExtendedRecipes;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirement;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShapedRecipeExtended extends ShapedRecipe {

    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;

    @ObjectHolder(ExtendedRecipes.MODID + ":crafting_shaped_extended")
    public static final IRecipeSerializer<ShapedRecipeExtended> CRAFTING_SHAPED_EXTENDED = null;

    private List<IRequirement> requirements;

    public ShapedRecipeExtended(ResourceLocation name, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack output) {
        super(name, group, width, height, ingredients, output);
        this.requirements = new ArrayList<>();
    }

    public ShapedRecipeExtended addRequirement(IRequirement requirement) {
        this.requirements.add(requirement);
        return this;
    }

    public ImmutableList<IRequirement> getRequirements() {
        return ImmutableList.copyOf(this.requirements);
    }

    @Override
    public boolean matches(CraftingInventory table, World world) {
        for(IRequirement requirement : this.requirements) {
            if(!requirement.test(world, null))
                return false;
        }
        return super.matches(table, world);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return CRAFTING_SHAPED_EXTENDED;
    }


    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedRecipeExtended> {

        @Override
        public ShapedRecipeExtended read(ResourceLocation name, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = RecipeUtils.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] astring = RecipeUtils.shrink(RecipeUtils.patternFromJson(JSONUtils.getJsonArray(json, "pattern"), MAX_HEIGHT, MAX_WIDTH));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = RecipeUtils.deserializeIngredients(astring, map, i, j);
            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            ShapedRecipeExtended recipe = new ShapedRecipeExtended(name, group, i, j, nonnulllist, itemstack);
            for(IRequirement requirement : RecipeUtils.readRequirements(json.getAsJsonArray("requirements")))
                recipe.addRequirement(requirement);
            return recipe;
        }

        @Nullable
        @Override
        public ShapedRecipeExtended read(ResourceLocation recipeId, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String s = buffer.readString(32767);
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for(int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, Ingredient.read(buffer));
            }

            ItemStack itemstack = buffer.readItemStack();
            ShapedRecipeExtended recipe =  new ShapedRecipeExtended(recipeId, s, i, j, nonnulllist, itemstack);
            for(IRequirement requirement : RecipeUtils.readRequirements(buffer))
                recipe.addRequirement(requirement);
            return recipe;
        }

        @Override
        public void write(PacketBuffer buffer, ShapedRecipeExtended recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeString(recipe.getGroup());

            for(Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());

            RecipeUtils.writeRequirements(buffer, recipe.requirements);
        }
    }
}
