package com.xxsx.earthonline.xuanhuan.entity;

import com.xxsx.earthonline.xuanhuan.EarthOnlineXuanhuan;
import com.xxsx.earthonline.xuanhuan.XuanhuanJourney;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class ContractableSpiritBeastEntity extends TamableAnimal {
    public static final int MAX_AFFINITY = 3;
    private static final EntityDataAccessor<Integer> DATA_AFFINITY = SynchedEntityData.defineId(
            ContractableSpiritBeastEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_MODE = SynchedEntityData.defineId(
            ContractableSpiritBeastEntity.class, EntityDataSerializers.BYTE);

    private final Kind kind;

    protected ContractableSpiritBeastEntity(EntityType<? extends ContractableSpiritBeastEntity> type,
                                             Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes(double health, double speed, double attack,
                                                              double armor, double knockbackResistance) {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.ATTACK_DAMAGE, attack)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.KNOCKBACK_RESISTANCE, knockbackResistance)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.SAFE_FALL_DISTANCE, 8.0D);
    }

    public static boolean checkSpawnRules(EntityType<? extends ContractableSpiritBeastEntity> type,
                                          LevelAccessor level, EntitySpawnReason reason,
                                          BlockPos pos, RandomSource random) {
        return Animal.checkAnimalSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_AFFINITY, 0);
        builder.define(DATA_MODE, (byte) CompanionMode.FOLLOW.id());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SpiritAffinity", getAffinity());
        output.putInt("CompanionMode", getCompanionMode().id());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setAffinity(input.getIntOr("SpiritAffinity", 0));
        setCompanionMode(CompanionMode.byId(input.getIntOr("CompanionMode", 0)));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.08D, true));
        this.goalSelector.addGoal(3, new ModeAwareFollowOwnerGoal(this, 1.15D, 9.0F, 2.2F));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.12D,
                stack -> stack.is(EarthOnlineXuanhuan.SPIRIT_GRASS.get()), false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.92D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 9.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isTame()) {
            if (stack.is(EarthOnlineXuanhuan.SPIRIT_GRASS.get())) {
                if (!this.level().isClientSide()) {
                    stack.consume(1, player);
                    setAffinity(getAffinity() + 1);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                getX(), getY() + getBbHeight() * 0.75D, getZ(),
                                7, 0.35D, 0.3D, 0.35D, 0.02D);
                    }
                    player.sendSystemMessage(Component.translatable(
                            "message.earth_online_xuanhuan.beast.affinity", getAffinity(), MAX_AFFINITY));
                }
                return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            if (stack.is(EarthOnlineXuanhuan.SPIRIT_BEAST_SEAL.get())) {
                if (!this.level().isClientSide()) {
                    if (getAffinity() < MAX_AFFINITY) {
                        player.sendSystemMessage(Component.translatable(
                                "message.earth_online_xuanhuan.beast.affinity_required",
                                getAffinity(), MAX_AFFINITY));
                    } else if (!net.neoforged.neoforge.event.EventHooks.onAnimalTame(this, player)) {
                        stack.consume(1, player);
                        tame(player);
                        setCompanionMode(CompanionMode.FOLLOW);
                        setHealth(getMaxHealth());
                        this.navigation.stop();
                        this.setTarget(null);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        player.sendSystemMessage(Component.translatable(
                                "message.earth_online_xuanhuan.beast.contracted", getDisplayName()));
                        if (player instanceof ServerPlayer serverPlayer) {
                            XuanhuanJourney.complete(serverPlayer, XuanhuanJourney.Milestone.BEAST_CONTRACT);
                        }
                    }
                }
                return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
        } else if (this.isOwnedBy(player)) {
            if (stack.is(EarthOnlineXuanhuan.SPIRIT_GRASS.get()) && getHealth() < getMaxHealth()) {
                if (!this.level().isClientSide()) {
                    stack.consume(1, player);
                    heal(5.0F);
                }
                return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
            // Empty hand is checked directly; AIR is not a held item and must not be treated as one.
            if (stack.isEmpty()) {
                if (!this.level().isClientSide()) {
                    setCompanionMode(getCompanionMode().next());
                    player.sendSystemMessage(Component.translatable(
                            "message.earth_online_xuanhuan.beast.mode",
                            Component.translatable(getCompanionMode().translationKey())));
                }
                return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(EarthOnlineXuanhuan.SPIRIT_GRASS.get());
    }

    @Override
    public @Nullable ContractableSpiritBeastEntity getBreedOffspring(ServerLevel level, AgeableMob partner) {
        var child = this.getType().create(level, EntitySpawnReason.BREEDING);
        if (child instanceof ContractableSpiritBeastEntity beast) {
            if (this.isTame()) {
                beast.setOwnerReference(this.getOwnerReference());
                beast.setTame(true, true);
                beast.setCompanionMode(CompanionMode.FOLLOW);
            }
            return beast;
        }
        return null;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide() && this.tickCount % kind.particleInterval() == 0
                && this.random.nextFloat() < 0.76F) {
            this.level().addParticle(kind.particle(), this.getRandomX(0.7D),
                    this.getRandomY() + 0.08D, this.getRandomZ(0.7D), 0.0D, 0.018D, 0.0D);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount % 100 == 0 && this.isTame()
                && getCompanionMode() != CompanionMode.ROAM
                && this.getOwner() instanceof ServerPlayer owner && this.distanceToSqr(owner) <= 196.0D) {
            applyOwnerSupport(owner);
        }
    }

    private void applyOwnerSupport(ServerPlayer owner) {
        switch (kind) {
            case SPIRIT_FOX -> owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 140, 0, true, false, true));
            case WIND_WOLF -> owner.addEffect(new MobEffectInstance(MobEffects.SPEED, 140, 0, true, false, true));
            case STONEHORN_GOAT -> owner.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 140, 0, true, false, true));
            case CRYSTAL_TURTLE -> owner.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 140, 0, true, false, true));
            case EMBER_CRANE -> owner.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 140, 0, true, false, true));
        }
    }

    public Kind kind() {
        return kind;
    }

    public int getAffinity() {
        return this.entityData.get(DATA_AFFINITY);
    }

    public void setAffinity(int affinity) {
        this.entityData.set(DATA_AFFINITY, Math.max(0, Math.min(MAX_AFFINITY, affinity)));
    }

    public CompanionMode getCompanionMode() {
        return CompanionMode.byId(this.entityData.get(DATA_MODE));
    }

    public void setCompanionMode(CompanionMode mode) {
        this.entityData.set(DATA_MODE, (byte) mode.id());
        this.setOrderedToSit(mode == CompanionMode.GUARD);
        this.setInSittingPose(mode == CompanionMode.GUARD);
        if (mode != CompanionMode.FOLLOW) {
            this.navigation.stop();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return switch (kind) {
            case SPIRIT_FOX -> SoundEvents.FOX_AMBIENT;
            case WIND_WOLF -> SoundEvents.BREEZE_IDLE_GROUND;
            case STONEHORN_GOAT -> SoundEvents.GOAT_AMBIENT;
            case CRYSTAL_TURTLE -> SoundEvents.TURTLE_AMBIENT_LAND;
            case EMBER_CRANE -> SoundEvents.PARROT_AMBIENT;
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (kind) {
            case SPIRIT_FOX -> SoundEvents.FOX_HURT;
            case WIND_WOLF -> SoundEvents.BREEZE_HURT;
            case STONEHORN_GOAT -> SoundEvents.GOAT_HURT;
            case CRYSTAL_TURTLE -> SoundEvents.TURTLE_HURT;
            case EMBER_CRANE -> SoundEvents.PARROT_HURT;
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return switch (kind) {
            case SPIRIT_FOX -> SoundEvents.FOX_DEATH;
            case WIND_WOLF -> SoundEvents.BREEZE_DEATH;
            case STONEHORN_GOAT -> SoundEvents.GOAT_DEATH;
            case CRYSTAL_TURTLE -> SoundEvents.TURTLE_DEATH;
            case EMBER_CRANE -> SoundEvents.PARROT_DEATH;
        };
    }

    public enum Kind {
        SPIRIT_FOX(ParticleTypes.ENCHANT, 16),
        WIND_WOLF(ParticleTypes.GUST, 13),
        STONEHORN_GOAT(ParticleTypes.WAX_ON, 18),
        CRYSTAL_TURTLE(ParticleTypes.GLOW, 15),
        EMBER_CRANE(ParticleTypes.FLAME, 12);

        private final net.minecraft.core.particles.ParticleOptions particle;
        private final int particleInterval;

        Kind(net.minecraft.core.particles.ParticleOptions particle, int particleInterval) {
            this.particle = particle;
            this.particleInterval = particleInterval;
        }

        public net.minecraft.core.particles.ParticleOptions particle() {
            return particle;
        }

        public int particleInterval() {
            return particleInterval;
        }
    }

    public enum CompanionMode {
        FOLLOW(0, "message.earth_online_xuanhuan.beast.mode.follow"),
        GUARD(1, "message.earth_online_xuanhuan.beast.mode.guard"),
        ROAM(2, "message.earth_online_xuanhuan.beast.mode.roam");

        private final int id;
        private final String translationKey;

        CompanionMode(int id, String translationKey) {
            this.id = id;
            this.translationKey = translationKey;
        }

        public int id() {
            return id;
        }

        public String translationKey() {
            return translationKey;
        }

        public CompanionMode next() {
            return values()[(ordinal() + 1) % values().length];
        }

        public static CompanionMode byId(int id) {
            for (CompanionMode mode : values()) {
                if (mode.id == id) {
                    return mode;
                }
            }
            return FOLLOW;
        }
    }

    private static final class ModeAwareFollowOwnerGoal extends FollowOwnerGoal {
        private final ContractableSpiritBeastEntity beast;

        private ModeAwareFollowOwnerGoal(ContractableSpiritBeastEntity beast, double speed,
                                         float startDistance, float stopDistance) {
            super(beast, speed, startDistance, stopDistance);
            this.beast = beast;
        }

        @Override
        public boolean canUse() {
            return beast.getCompanionMode() == CompanionMode.FOLLOW && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return beast.getCompanionMode() == CompanionMode.FOLLOW && super.canContinueToUse();
        }
    }
}
