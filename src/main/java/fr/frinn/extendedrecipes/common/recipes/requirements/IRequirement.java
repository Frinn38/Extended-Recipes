package fr.frinn.extendedrecipes.common.recipes.requirements;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IRequirement {

    boolean test(@Nullable World world, @Nullable PlayerEntity player);

    IRequirementSerializer getSerializer();

    String getMessage();
}
