package com.nurby.overcomplicated_bees.client.model;

import com.nurby.overcomplicated_bees.library.bee.species.SpeciesDefinition;
import com.nurby.overcomplicated_bees.library.bee.species.SpeciesRegistry;
import com.nurby.overcomplicated_bees.library.genetics.genes.GeneSpecies;
import com.nurby.overcomplicated_bees.util.GeneticHelper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class LayeredBeeOverrideHandler extends ItemOverrides {

    private final ItemOverrides parent;
    private final ModelBaker baker;
    private final IGeometryBakingContext context;
    private final ModelState modelState;

    private final BlockModel baseModel;
    private final Function<Material, TextureAtlasSprite> spriteGetter;

    private final ResourceLocation beeTexture = ResourceLocation.fromNamespaceAndPath("complicated_bees", "item/bee/default_bee");

    private final ResourceLocation outlineTexture = ResourceLocation.fromNamespaceAndPath("complicated_bees", "item/bee/default_outline");

    private final Map<String, BakedModel> cache = new LinkedHashMap<>();

    public LayeredBeeOverrideHandler(ItemOverrides parent, ModelBaker baker, IGeometryBakingContext context, ModelState modelState, BlockModel baseModel, Function<Material, TextureAtlasSprite> spriteGetter) {
        this.parent = parent;
        this.baker = baker;
        this.context = context;
        this.modelState = modelState;
        this.baseModel = baseModel;
        this.spriteGetter = spriteGetter;
    }

    private BakedModel bakeModel(SpeciesDefinition species) {
        return baker.bakeUncached(baseModel, modelState, material -> resolveTexture(material, species));
    }

    private TextureAtlasSprite resolveTexture(Material template, SpeciesDefinition species) {
        ResourceLocation texture = template.texture();

        if (texture.equals(beeTexture)) {
            return spriteGetter.apply(new Material(template.atlasLocation(), species.texture()));
        }

        if (texture.equals(outlineTexture)) {
            return spriteGetter.apply(new Material(template.atlasLocation(), species.outlineTexture()));
        }

        return spriteGetter.apply(template);
    }

    @Override
    public BakedModel resolve(BakedModel original, ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.world.entity.LivingEntity entity, int seed) {
        BakedModel parentModel = parent.resolve(original, stack, level, entity, seed);

        ResourceLocation speciesId = getSpeciesId(stack);

        if (speciesId == null) {
            return parentModel;
        }

        SpeciesDefinition species = SpeciesRegistry.get(speciesId);

        if (species == null) {
            return parentModel;
        }

        String key = species.texture() + "|" + species.outlineTexture();

        BakedModel cached = cache.get(key);

        if (cached != null) {
            return cached;
        }

        BakedModel baked = bakeModel(species);

        if (baked == null) {
            return parentModel;
        }

        cache.put(key, baked);

        return baked;
    }

    private ResourceLocation getSpeciesId(ItemStack stack) {
        GeneSpecies gene = GeneticHelper.getSpeciesGene(stack);
        if (gene != null) {
            return gene.getSpecies();
        }

        return null;
    }
}