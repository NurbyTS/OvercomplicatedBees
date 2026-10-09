package com.nurby.overcomplicated_bees.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;

import java.util.function.Function;

public final class LayeredBeeGeometry implements IUnbakedGeometry<LayeredBeeGeometry> {

    private static final ResourceLocation CONTEXT_NAME =
            ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "layered_bee");

    private final BlockModel baseModel;

    public LayeredBeeGeometry(BlockModel baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public BakedModel bake(
            IGeometryBakingContext context,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState,
            ItemOverrides overrides
    ) {
        // Use the model's own particle texture when it declares one,
        // otherwise fall back to the missing texture.
        Material particleMaterial = context.hasMaterial("particle")
                ? context.getMaterial("particle")
                : new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

        TextureAtlasSprite particleSprite = spriteGetter.apply(particleMaterial);

        StandaloneGeometryBakingContext beeContext =
                StandaloneGeometryBakingContext.builder(context).build(CONTEXT_NAME);

        LayeredBeeOverrideHandler dynamicOverrides =
                new LayeredBeeOverrideHandler(overrides, baker, modelState, baseModel, spriteGetter);

        return CompositeModel.Baked
                .builder(beeContext, particleSprite, dynamicOverrides, context.getTransforms())
                .build();
    }

    @Override
    public void resolveParents(
            Function<ResourceLocation, UnbakedModel> modelGetter,
            IGeometryBakingContext context
    ) {
        baseModel.resolveParents(modelGetter);
    }

    public static final class Loader implements IGeometryLoader<LayeredBeeGeometry> {

        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public LayeredBeeGeometry read(
                JsonObject jsonObject,
                JsonDeserializationContext context
        ) throws JsonParseException {
            // Work on a copy so the caller's JSON is not mutated.
            JsonObject modelJson = jsonObject.deepCopy();
            modelJson.remove("loader");

            BlockModel baseModel = context.deserialize(modelJson, BlockModel.class);

            return new LayeredBeeGeometry(baseModel);
        }
    }
}