package com.nurby.overcomplicated_bees.client.model;

import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Bakes one model per distinct (texture, outline) pair on first use and
 * caches it. A new instance is created on every resource reload, so the
 * cache never outlives the sprites it references.
 */
public final class LayeredBeeOverrideHandler extends ItemOverrides {

    private static final ResourceLocation TEMPLATE_BEE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "item/bee/default_bee");

    private static final ResourceLocation TEMPLATE_OUTLINE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "item/bee/default_outline");

    /**
     * Species that share textures share a baked model.
     */
    private record TextureKey(ResourceLocation texture, ResourceLocation outline) {
    }

    private final ItemOverrides parent;
    private final ModelBaker baker;
    private final ModelState modelState;
    private final BlockModel baseModel;
    private final Function<Material, TextureAtlasSprite> spriteGetter;

    private final Map<TextureKey, BakedModel> cache = new ConcurrentHashMap<>();

    public LayeredBeeOverrideHandler(
            ItemOverrides parent,
            ModelBaker baker,
            ModelState modelState,
            BlockModel baseModel,
            Function<Material, TextureAtlasSprite> spriteGetter
    ) {
        this.parent = parent;
        this.baker = baker;
        this.modelState = modelState;
        this.baseModel = baseModel;
        this.spriteGetter = spriteGetter;
    }

    @Override
    public BakedModel resolve(
            BakedModel original,
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity,
            int seed
    ) {
        SpeciesDefinition species = getSpecies(stack);

        if (species == null) {
            return parent.resolve(original, stack, level, entity, seed);
        }

        TextureKey key = new TextureKey(species.texture(), species.outlineTexture());

        BakedModel cached = cache.get(key);

        if (cached != null) {
            return cached;
        }

        BakedModel baked = baker.bakeUncached(
                baseModel,
                modelState,
                material -> resolveTexture(material, key)
        );

        cache.put(key, baked);

        return baked;
    }

    private TextureAtlasSprite resolveTexture(Material template, TextureKey key) {
        ResourceLocation texture = template.texture();

        if (texture.equals(TEMPLATE_BEE_TEXTURE)) {
            return spriteGetter.apply(new Material(template.atlasLocation(), key.texture()));
        }

        if (texture.equals(TEMPLATE_OUTLINE_TEXTURE)) {
            return spriteGetter.apply(new Material(template.atlasLocation(), key.outline()));
        }

        return spriteGetter.apply(template);
    }

    @Nullable
    private static SpeciesDefinition getSpecies(ItemStack stack) {
        GeneSpecies gene = GeneticHelper.getSpeciesGene(stack);

        if (gene == null) {
            return null;
        }

        return SpeciesRegistry.get(gene.getSpecies());
    }
}