
package com.nurby.overcomplicated_bees.library.apiary;

import com.nurby.overcomplicated_bees.library.bee.BeeProduct;
import com.nurby.overcomplicated_bees.library.bee.component.BeeState;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.flower.FlowerDefinition;
import com.nurby.overcomplicated_bees.library.flower.FlowerRegistry;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Humidity;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Temperature;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Territory;
import com.nurby.overcomplicated_bees.library.genetics.gene_values.Tolerance;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneActiveTime;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneFertility;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneFlower;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneHumidity;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneLifespan;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneTemperature;
import com.nurby.overcomplicated_bees.registry.BeeDataComponents;
import com.nurby.overcomplicated_bees.registry.BeeGenes;
import com.nurby.overcomplicated_bees.registry.BeeItems;
import com.nurby.overcomplicated_bees.service.BeeBreedingService;
import com.nurby.overcomplicated_bees.util.BeeFailureReason;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class BeeLogicHandler {
    private static final long FLOWER_CACHE_REFRESH_INTERVAL = 100L;

    // Testing values. Restore the intended cycle lengths when testing is done.
    private static final int PRODUCTION_CYCLE_LENGTH = 10;
    private static final int ENVIRONMENT_CYCLE_LENGTH = 10;
    private static final int MATING_CYCLE_LENGTH = 20;

    private static final String PRODUCTION_PROGRESS_TAG = "production_progress";
    private static final String ENVIRONMENT_PROGRESS_TAG = "environment_progress";
    private static final String MATING_PROGRESS_TAG = "mating_progress";
    private static final String OUTPUT_BUFFER_TAG = "output_buffer";

    private final IBeeHousing housing;

    // Cached environment
    private Float cachedTemperature;
    private Float cachedHumidity;

    // Cached flower search
    private ItemStack cachedQueen;
    private Territory cachedFlowerTerritory;
    private List<BlockPos> cachedFlowers = List.of();
    private boolean flowerCacheDirty = true;
    private long nextFlowerRefreshTick;

    // Simulation state
    private int productionProgress;
    private int environmentProgress;
    private int matingProgress;

    private BeeProductionState beeState =
            new BeeProductionState(
                    BeeProductionState.WorkingState.NOT_WORKING,
                    List.of()
            );

    private boolean outputBlocked;
    private final List<ItemStack> outputBuffer = new ArrayList<>();

    public BeeLogicHandler(IBeeHousing housing) {
        this.housing = Objects.requireNonNull(housing);
    }

    // ==================== Housing Access ====================

    private Level getLevel() {
        return housing.getLevel();
    }

    private BlockPos getBlockPos() {
        return housing.getBlockPos();
    }

    // ==================== State Access ====================

    public int getProductionProgress() {
        return productionProgress;
    }

    public int getEnvironmentProgress() {
        return environmentProgress;
    }

    public int getMatingProgress() {
        return matingProgress;
    }

    public int getMaxMatingProgress() {
        return MATING_CYCLE_LENGTH;
    }

    public BeeProductionState getBeeState() {
        return beeState;
    }

    public boolean isOutputBlocked() {
        return outputBlocked;
    }

    public void resetProgressForQueenChange() {
        productionProgress = 0;
        environmentProgress = 0;
    }

    // ==================== Environment Cache ====================

    private float getBaseTemperature() {
        if (cachedTemperature != null) {
            return cachedTemperature;
        }

        Level level = getLevel();

        if (level == null) {
            return Temperature.NORMAL;
        }

        cachedTemperature = Temperature.getFromLocation(level, getBlockPos());
        return cachedTemperature;
    }

    private float getBaseHumidity() {
        if (cachedHumidity != null) {
            return cachedHumidity;
        }

        Level level = getLevel();

        if (level == null) {
            return Humidity.NORMAL;
        }

        cachedHumidity = Humidity.getFromLocation(level, getBlockPos());
        return cachedHumidity;
    }

    public void clearEnvironmentCache() {
        cachedTemperature = null;
        cachedHumidity = null;
    }

    // ==================== Frame Modifiers ====================

    private <T> T applyFrameModifiers(
            T value,
            BiFunction<IBeeModifier, T, T> modifier
    ) {
        IItemHandler frames = housing.getFrameInventory();

        for (int slot = 0; slot < frames.getSlots(); slot++) {
            ItemStack stack = frames.getStackInSlot(slot);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() instanceof IBeeModifier beeModifier) {
                value = modifier.apply(beeModifier, value);
            }
        }

        return value;
    }

    private boolean applyBooleanFrameModifiers(
            boolean value,
            BiFunction<IBeeModifier, Boolean, Boolean> modifier
    ) {
        IItemHandler frames = housing.getFrameInventory();

        for (int slot = 0; slot < frames.getSlots(); slot++) {
            ItemStack stack = frames.getStackInSlot(slot);

            if (stack.isEmpty()
                    || !(stack.getItem() instanceof IBeeModifier beeModifier)) {
                continue;
            }

            Boolean modified = modifier.apply(beeModifier, value);

            if (modified != null) {
                value = modified;
            }
        }

        return value;
    }

    // ==================== Effective Bee Stats ====================

    private float getTemperature() {
        return applyFrameModifiers(
                getBaseTemperature(),
                IBeeModifier::modifyTemperature
        );
    }

    private float getHumidity() {
        return applyFrameModifiers(
                getBaseHumidity(),
                IBeeModifier::modifyHumidity
        );
    }

    public float getProductivity() {
        return applyFrameModifiers(1.0f, IBeeModifier::modifyProductivity);
    }

    public float getMutationRate() {
        return applyFrameModifiers(1.0f, IBeeModifier::modifyMutationRate);
    }

    public float getLifespan() {
        return applyFrameModifiers(1.0f, IBeeModifier::modifyLifespan);
    }

    private Territory getEffectiveTerritory(ItemStack queen) {
        Territory baseTerritory =
                GeneticHelper.getTerritoryGene(queen).getTerritory();

        int horizontal = applyFrameModifiers(
                baseTerritory.horizontalRadius(),
                IBeeModifier::modifyTerritoryHorizontal
        );

        int vertical = applyFrameModifiers(
                baseTerritory.verticalRadius(),
                IBeeModifier::modifyTerritoryVertical
        );

        return new Territory(
                Math.max(0, horizontal),
                Math.max(0, vertical)
        );
    }

    // ==================== Flower Search ====================

    private List<BlockPos> findFlowers(
            Level level,
            Territory territory,
            FlowerDefinition definition
    ) {
        BlockPos center = getBlockPos();
        List<BlockPos> flowers = new ArrayList<>();

        int horizontal = territory.horizontalRadius();
        int vertical = territory.verticalRadius();

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = -horizontal; x <= horizontal; x++) {
            for (int y = -vertical; y <= vertical; y++) {
                for (int z = -horizontal; z <= horizontal; z++) {
                    cursor.set(
                            center.getX() + x,
                            center.getY() + y,
                            center.getZ() + z
                    );

                    if (!level.hasChunk(cursor.getX() >> 4, cursor.getZ() >> 4)) {
                        continue;
                    }

                    if (definition.isFlower(level, cursor)) {
                        flowers.add(cursor.immutable());
                    }
                }
            }
        }

        return List.copyOf(flowers);
    }

    private List<BlockPos> getNearbyFlowers(ItemStack queen) {
        Level level = getLevel();

        if (level == null || level.isClientSide || queen.isEmpty()) {
            return List.of();
        }

        Territory territory = getEffectiveTerritory(queen);

        boolean queenChanged = cachedQueen == null
                || !ItemStack.matches(cachedQueen, queen);

        boolean territoryChanged =
                !territory.equals(cachedFlowerTerritory);

        boolean refreshDue =
                level.getGameTime() >= nextFlowerRefreshTick;

        if (queenChanged || territoryChanged
                || flowerCacheDirty || refreshDue) {
            GeneFlower flowerGene = GeneticHelper.getFlowerGene(queen);

            FlowerDefinition definition =
                    FlowerRegistry.get(flowerGene.getFlower());

            cachedFlowers = definition == null
                    ? List.of()
                    : findFlowers(level, territory, definition);

            cachedQueen = queen.copy();
            cachedFlowerTerritory = territory;
            flowerCacheDirty = false;
            nextFlowerRefreshTick =
                    level.getGameTime() + FLOWER_CACHE_REFRESH_INTERVAL;
        }

        return cachedFlowers;
    }

    private void clearFlowerCache() {
        flowerCacheDirty = true;
        cachedQueen = null;
        cachedFlowerTerritory = null;
        cachedFlowers = List.of();
    }

    public void invalidateFlowerCache() {
        clearFlowerCache();
    }

    public boolean hasNearbyFlowers(ItemStack queen) {
        return !getNearbyFlowers(queen).isEmpty();
    }

    // ==================== Environmental Conditions ====================

    public boolean hasSkyAccess() {
        Level level = getLevel();

        boolean hasSkyAccess = level != null
                && level.canSeeSky(getBlockPos().above());

        return applyBooleanFrameModifiers(
                hasSkyAccess,
                IBeeModifier::modifySkyAccess
        );
    }

    public boolean isExposedToRain() {
        Level level = getLevel();

        boolean exposedToRain = level != null
                && level.isRainingAt(getBlockPos().above());

        return applyBooleanFrameModifiers(
                exposedToRain,
                IBeeModifier::modifyRainExposure
        );
    }

    public boolean isActiveTime(boolean geneticallyActive) {
        return applyBooleanFrameModifiers(
                geneticallyActive,
                IBeeModifier::modifyActiveTime
        );
    }

    private boolean isWithinTolerance(
            int actualLevel,
            int preferredLevel,
            Tolerance tolerance
    ) {
        return actualLevel >= preferredLevel - tolerance.down()
                && actualLevel <= preferredLevel + tolerance.up();
    }

    private boolean matchesExactly(int actualLevel, int preferredLevel) {
        return actualLevel == preferredLevel;
    }

    // ==================== Bee State Evaluation ====================

    public BeeProductionState evaluateBee(ItemStack queen) {
        List<Component> failures = new ArrayList<>();

        Level level = getLevel();

        if (level == null || queen.isEmpty()) {
            return new BeeProductionState(
                    BeeProductionState.WorkingState.NOT_WORKING,
                    failures
            );
        }

        // Flowers
        boolean hasFlowers = hasNearbyFlowers(queen);

        if (!hasFlowers) {
            failures.add(BeeFailureReason.flowerComponent(queen));
        }

        // Temperature
        GeneTemperature temperatureGene =
                GeneticHelper.getTemperatureGene(queen);

        int temperatureLevel = Temperature.getLevel(getTemperature());
        int preferredTemperatureLevel =
                Temperature.getLevel(temperatureGene.getTemperature());

        boolean temperatureComfortable = isWithinTolerance(
                temperatureLevel,
                preferredTemperatureLevel,
                temperatureGene.getTolerance()
        );

        boolean temperaturePerfect = matchesExactly(
                temperatureLevel,
                preferredTemperatureLevel
        );

        if (!temperatureComfortable) {
            failures.add(
                    BeeFailureReason.tempComponent(queen, getTemperature())
            );
        }

        // Humidity
        GeneHumidity humidityGene =
                GeneticHelper.getHumidityGene(queen);

        int humidityLevel = Humidity.getLevel(getHumidity());
        int preferredHumidityLevel =
                Humidity.getLevel(humidityGene.getHumidity());

        boolean humidityComfortable = isWithinTolerance(
                humidityLevel,
                preferredHumidityLevel,
                humidityGene.getTolerance()
        );

        boolean humidityPerfect = matchesExactly(
                humidityLevel,
                preferredHumidityLevel
        );

        if (!humidityComfortable) {
            failures.add(
                    BeeFailureReason.humidComponent(queen, getHumidity())
            );
        }

        // Active time
        GeneActiveTime activeTimeGene =
                GeneticHelper.getActiveTimeGene(queen);

        boolean active = isActiveTime(
                activeTimeGene.isSatisfied(level)
        );

        if (!active) {
            failures.add(BeeFailureReason.timeComponent(queen));
        }

        // Weatherproofing
        boolean weatherproof = GeneticHelper.getBooleanGene(
                queen,
                BeeGenes.WEATHERPROOF.getId()
        ).getValue();

        boolean exposedToRain = isExposedToRain();

        if (exposedToRain && !weatherproof) {
            failures.add(BeeFailureReason.weatherComponent(queen));
        }

        // Cave dwelling
        boolean caveDwelling = GeneticHelper.getBooleanGene(
                queen,
                BeeGenes.CAVE_DWELLING.getId()
        ).getValue();

        boolean hasSky = hasSkyAccess();

        if (caveDwelling && hasSky) {
            failures.add(BeeFailureReason.notUndergroundComponent(queen));
        } else if (!caveDwelling && !hasSky) {
            failures.add(BeeFailureReason.undergroundComponent(queen));
        }

        if (!failures.isEmpty()) {
            return new BeeProductionState(
                    BeeProductionState.WorkingState.NOT_WORKING,
                    failures
            );
        }

        boolean ecstatic = temperaturePerfect
                && humidityPerfect
                && active;

        return new BeeProductionState(
                ecstatic
                        ? BeeProductionState.WorkingState.ECSTATIC
                        : BeeProductionState.WorkingState.COMFORTABLE,
                failures
        );
    }

    // ==================== Simulation Lifecycle ====================

    public void tick() {
        if (!(getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        flushOutputBuffer();

        IItemHandler bees = housing.getBeeInventory();
        ItemStack first = bees.getStackInSlot(0);
        ItemStack second = bees.getStackInSlot(1);

        // Mating takes priority over production.
        if (isPrincess(first) && isDrone(second)) {
            tickMating(serverLevel, first, second);
            return;
        }

        matingProgress = 0;

        if (!isQueen(first)) {
            boolean hadState =
                    beeState.getWorkingState()
                            != BeeProductionState.WorkingState.NOT_WORKING
                            || !beeState.getFailureReasons().isEmpty();

            productionProgress = 0;
            environmentProgress = 0;

            beeState = new BeeProductionState(
                    BeeProductionState.WorkingState.NOT_WORKING,
                    List.of()
            );

            if (hadState) {
                housing.syncBeeState();
            }

            return;
        }

        if (++environmentProgress >= ENVIRONMENT_CYCLE_LENGTH) {
            environmentProgress = 0;
            beeState = evaluateBee(first);
            housing.syncBeeState();
        }

        if (!beeState.isWorking() || !outputBuffer.isEmpty()) {
            return;
        }

        productionProgress++;

        if (productionProgress >= PRODUCTION_CYCLE_LENGTH) {
            productionProgress = 0;
            produceCycle(serverLevel, first);
            housing.setHousingChanged();
        }
    }

    private void tickMating(
            ServerLevel serverLevel,
            ItemStack princess,
            ItemStack drone
    ) {
        if (++matingProgress < MATING_CYCLE_LENGTH) {
            housing.setHousingChanged();
            return;
        }

        ItemStack queen = BeeBreedingService.createQueen(princess, drone);

        if (queen.isEmpty()) {
            matingProgress = 0;
            housing.setHousingChanged();
            return;
        }

        housing.setBeeStackInSlot(0, queen);
        housing.setBeeStackInSlot(1, ItemStack.EMPTY);

        matingProgress = 0;
        productionProgress = 0;
        environmentProgress = 0;

        invalidateFlowerCache();
        clearEnvironmentCache();

        beeState = evaluateBee(queen);
        housing.syncBeeState();
        housing.setHousingChanged();
    }

    // ==================== Production ====================

    private void produceCycle(ServerLevel serverLevel, ItemStack queen) {
        // TODO: Execute the queen's GeneEffect here once the effect
        // registry and execution interface have been implemented.

        generateProducts(queen);
        ageQueen(serverLevel, queen);
        damageFrames(serverLevel);
    }

    private void generateProducts(ItemStack queen) {
        GeneSpecies speciesGene = GeneticHelper.getSpeciesGene(queen);

        if (speciesGene == null) {
            return;
        }

        SpeciesDefinition species = SpeciesRegistry.get(
                speciesGene.getSpecies()
        );

        if (species == null) {
            return;
        }

        float productivity = getProductivity();

        float effectiveProductivity =
                (10.0f * productivity) / (productivity + 10.0f);

        if (effectiveProductivity < 0.0f) {
            effectiveProductivity = 0.0f;
        }

        for (BeeProduct product : species.products()) {
            enqueueProduct(product.getStackResult(effectiveProductivity));
        }

        if (beeState.isEcstatic()) {
            for (BeeProduct product : species.specialtyProducts()) {
                enqueueProduct(product.getStackResult(effectiveProductivity));
            }
        }

        housing.setHousingChanged();
    }

    private void ageQueen(ServerLevel serverLevel, ItemStack queen) {
        BeeState state = queen.get(BeeDataComponents.STATE);

        if (state == null) {
            state = new BeeState(0.0f, false, 0);
        }

        GeneLifespan lifespanGene = GeneticHelper.getLifespanGene(queen);
        float lifespan = lifespanGene.getLifespan();

        if (lifespan <= 0.0f) {
            produceOffspring(serverLevel, queen);
            return;
        }

        float lifespanModifier = getLifespan();

        if (lifespanModifier <= 0.0f) {
            return;
        }

        float ageIncrement = 1.0f / lifespanModifier;
        float newAge = state.age() + ageIncrement;

        queen.set(
                BeeDataComponents.STATE,
                new BeeState(
                        newAge,
                        state.analyzed(),
                        state.generation()
                )
        );

        if (newAge >= lifespan) {
            produceOffspring(serverLevel, queen);
        }

        housing.setHousingChanged();
    }

    private void produceOffspring(
            ServerLevel serverLevel,
            ItemStack queen
    ) {
        GeneFertility fertilityGene = GeneticHelper.getFertilityGene(queen);

        int fertility = fertilityGene == null
                ? 0
                : Math.max(0, fertilityGene.getFertility());

        ItemStack princess = BeeBreedingService.createOffspring(
                serverLevel.getRandom(),
                queen,
                BeeItems.PRINCESS.get()
        );

        enqueueProduct(princess);

        for (int i = 0; i < fertility; i++) {
            ItemStack drone = BeeBreedingService.createOffspring(
                    serverLevel.getRandom(),
                    queen,
                    BeeItems.DRONE.get()
            );

            enqueueProduct(drone);
        }

        housing.setBeeStackInSlot(0, ItemStack.EMPTY);

        productionProgress = 0;
        environmentProgress = 0;

        beeState = new BeeProductionState(
                BeeProductionState.WorkingState.NOT_WORKING,
                List.of()
        );

        invalidateFlowerCache();

        housing.syncBeeState();
        housing.setHousingChanged();
    }

    // ==================== Output Buffer ====================

    private void enqueueProduct(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        outputBuffer.add(stack.copy());
    }

    private void flushOutputBuffer() {
        outputBlocked = false;

        IItemHandler output = housing.getOutputInventory();

        while (!outputBuffer.isEmpty()) {
            ItemStack next = outputBuffer.getFirst();

            ItemStack remainder = ItemHandlerHelper.insertItemStacked(
                    output,
                    next,
                    false
            );

            if (remainder.isEmpty()) {
                outputBuffer.removeFirst();
                housing.setHousingChanged();
            } else {
                outputBuffer.set(0, remainder);
                outputBlocked = true;
                housing.setHousingChanged();
                break;
            }
        }
    }

    public void dropOutputBuffer(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }

        for (ItemStack stack : outputBuffer) {
            if (!stack.isEmpty()) {
                Containers.dropItemStack(
                        level,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        stack
                );
            }
        }

        outputBuffer.clear();
    }

    // ==================== Frames ====================

    private void damageFrames(ServerLevel serverLevel) {
        IItemHandler frames = housing.getFrameInventory();

        for (int slot = 0; slot < frames.getSlots(); slot++) {
            ItemStack frame = frames.getStackInSlot(slot);

            if (frame.isEmpty() || !frame.isDamageableItem()) {
                continue;
            }

            int finalSlot = slot;

            frame.hurtAndBreak(
                    1,
                    serverLevel,
                    null,
                    brokenItem -> housing.setFrameStackInSlot(
                            finalSlot,
                            ItemStack.EMPTY
                    )
            );
        }
    }

    // ==================== Item Checks ====================

    private boolean isQueen(ItemStack stack) {
        return !stack.isEmpty() && stack.is(BeeItems.QUEEN.get());
    }

    private boolean isPrincess(ItemStack stack) {
        return !stack.isEmpty() && stack.is(BeeItems.PRINCESS.get());
    }

    private boolean isDrone(ItemStack stack) {
        return !stack.isEmpty() && stack.is(BeeItems.DRONE.get());
    }

    // ==================== Persistence ====================

    public void saveState(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        tag.putInt(PRODUCTION_PROGRESS_TAG, productionProgress);
        tag.putInt(ENVIRONMENT_PROGRESS_TAG, environmentProgress);
        tag.putInt(MATING_PROGRESS_TAG, matingProgress);

        serializeBeeState(tag, registries);

        ListTag bufferTag = new ListTag();

        for (ItemStack stack : outputBuffer) {
            if (!stack.isEmpty()) {
                bufferTag.add(stack.save(registries));
            }
        }

        tag.put(OUTPUT_BUFFER_TAG, bufferTag);
    }

    public void loadState(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        productionProgress = tag.getInt(PRODUCTION_PROGRESS_TAG);
        environmentProgress = tag.getInt(ENVIRONMENT_PROGRESS_TAG);
        matingProgress = tag.getInt(MATING_PROGRESS_TAG);

        outputBuffer.clear();

        if (tag.contains(OUTPUT_BUFFER_TAG, Tag.TAG_LIST)) {
            ListTag bufferTag = tag.getList(
                    OUTPUT_BUFFER_TAG,
                    Tag.TAG_COMPOUND
            );

            for (int i = 0; i < bufferTag.size(); i++) {
                ItemStack stack = ItemStack.parseOptional(
                        registries,
                        bufferTag.getCompound(i)
                );

                if (!stack.isEmpty()) {
                    outputBuffer.add(stack);
                }
            }
        }

        beeState = deserializeBeeState(tag, registries);
        outputBlocked = false;

        invalidateFlowerCache();
        clearEnvironmentCache();
    }

    public void saveClientState(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        serializeBeeState(tag, registries);
    }

    public void loadClientState(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        beeState = deserializeBeeState(tag, registries);
    }

    private void serializeBeeState(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        tag.putInt("bee_working_state", beeState.getWorkingState().ordinal());

        ListTag failures = new ListTag();

        for (Component reason : beeState.getFailureReasons()) {
            CompoundTag entry = new CompoundTag();

            entry.putString(
                    "component",
                    Component.Serializer.toJson(reason, registries)
            );

            failures.add(entry);
        }

        tag.put("bee_failure_reasons", failures);
    }

    private BeeProductionState deserializeBeeState(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        int stateOrdinal = tag.getInt("bee_working_state");

        BeeProductionState.WorkingState[] states =
                BeeProductionState.WorkingState.values();

        BeeProductionState.WorkingState workingState =
                stateOrdinal >= 0 && stateOrdinal < states.length
                        ? states[stateOrdinal]
                        : BeeProductionState.WorkingState.NOT_WORKING;

        List<Component> failures = new ArrayList<>();

        ListTag failureTag = tag.getList(
                "bee_failure_reasons",
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < failureTag.size(); i++) {
            String json = failureTag.getCompound(i).getString("component");

            Component component =
                    Component.Serializer.fromJson(json, registries);

            if (component != null) {
                failures.add(component);
            }
        }

        return new BeeProductionState(workingState, failures);
    }
}