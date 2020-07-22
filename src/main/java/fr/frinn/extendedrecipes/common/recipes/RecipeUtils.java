package fr.frinn.extendedrecipes.common.recipes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirement;
import fr.frinn.extendedrecipes.common.recipes.requirements.IRequirementSerializer;
import fr.frinn.extendedrecipes.common.registry.RegistryRegistry;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeUtils {

    public static List<IRequirement> readRequirements(JsonArray json) {
        List<IRequirement> requirements = new ArrayList<>();
        json.forEach(jsonElement -> {
            IRequirementSerializer type = RegistryRegistry.REQUIREMENTS.getValue(new ResourceLocation(JSONUtils.getString(jsonElement.getAsJsonObject(), "type")));
            if(type != null)
                requirements.add(type.read(jsonElement.getAsJsonObject()));
        });
        return requirements;
    }

    public static List<IRequirement> readRequirements(PacketBuffer buffer) {
        List<IRequirement> requirements = new ArrayList<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++) {
            JsonObject json = JSONUtils.fromJson(buffer.readString());
            IRequirementSerializer type = RegistryRegistry.REQUIREMENTS.getValue(new ResourceLocation(JSONUtils.getString(json,"type")));
            if(type != null)
                requirements.add(type.read(json));
        }
        return requirements;
    }

    public static void writeRequirements(PacketBuffer buffer, List<IRequirement> requirements) {
        buffer.writeInt(requirements.size());
        for(IRequirement requirement : requirements)
            buffer.writeString(requirement.getSerializer().write(requirement).toString());
    }

    public static String[] patternFromJson(JsonArray jsonArr, int maxheight, int maxWidth) {
        String[] astring = new String[jsonArr.size()];
        if (astring.length > maxheight) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + maxheight + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");
                if (s.length() > maxWidth) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + maxWidth + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    public static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");

        for(int i = 0; i < pattern.length; ++i) {
            for(int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + patternWidth * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    public static String[] shrink(String... toShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int i1 = 0; i1 < toShrink.length; ++i1) {
            String s = toShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (toShrink.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[toShrink.length - l - k];

            for(int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = toShrink[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    private static int firstNonSpace(String str) {
        int i;
        for(i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
            ;
        }

        return i;
    }

    private static int lastNonSpace(String str) {
        int i;
        for(i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
            ;
        }

        return i;
    }

    public static Map<String, Ingredient> deserializeKey(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }
}
