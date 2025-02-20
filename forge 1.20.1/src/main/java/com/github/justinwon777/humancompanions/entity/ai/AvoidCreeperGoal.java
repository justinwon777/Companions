package com.github.justinwon777.humancompanions.entity.ai;

import java.util.EnumSet;
import java.util.function.Predicate;

import com.github.justinwon777.humancompanions.core.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class AvoidCreeperGoal<T extends LivingEntity> extends Goal {
    protected final TamableAnimal mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    protected T toAvoid;
    protected final float maxDist;
    protected Path path;
    protected final PathNavigation pathNav;
    protected final Class<T> avoidClass;
    protected final Predicate<LivingEntity> avoidPredicate;
    protected final Predicate<LivingEntity> predicateOnAvoidEntity;
    private final TargetingConditions avoidEntityTargeting;

    public AvoidCreeperGoal(TamableAnimal p_25027_, Class<T> p_25028_, float p_25029_, double p_25030_, double p_25031_) {
        this(p_25027_, p_25028_, (p_25052_) -> true, p_25029_, p_25030_, p_25031_, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }

    public AvoidCreeperGoal(TamableAnimal p_25040_, Class<T> p_25041_, Predicate<LivingEntity> p_25042_, float p_25043_, double p_25044_, double p_25045_, Predicate<LivingEntity> p_25046_) {
        this.mob = p_25040_;
        this.avoidClass = p_25041_;
        this.avoidPredicate = p_25042_;
        this.maxDist = p_25043_;
        this.walkSpeedModifier = p_25044_;
        this.sprintSpeedModifier = p_25045_;
        this.predicateOnAvoidEntity = p_25046_;
        this.pathNav = p_25040_.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.avoidEntityTargeting = TargetingConditions.forCombat().range((double)p_25043_).selector(p_25046_.and(p_25042_));
    }

    public boolean canUse() {
        this.toAvoid = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.avoidClass, this.mob.getBoundingBox().inflate(this.maxDist, 3.0D, this.maxDist), (p_148078_) -> true), this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
            if (vec3 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }
    }

    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    public void start() {
        if (Config.CREEPER_WARNING.get()) {
            Component text = Component.literal("Creeper!");
            if (this.mob.isTame()) {
                if (this.mob.blockPosition().closerThan(this.mob.getOwner().blockPosition(), 15)) {
                    this.mob.getOwner().sendSystemMessage(Component.translatable("chat.type.text",
                            this.mob.getDisplayName(), text));
                }
            }
        }
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
    }

    public void stop() {
        this.toAvoid = null;
    }

    public void tick() {
        if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }

    }
}
