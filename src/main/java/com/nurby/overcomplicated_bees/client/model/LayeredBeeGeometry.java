package com.nurby.overcomplicated_bees.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nurby.overcomplicated_bees.OvercomplicatedBees;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;

import java.util.function.Function;

public final class LayeredBeeGeometry implements IUnbakedGeometry<LayeredBeeGeometry> {

    private final BlockModel baseModel;

    public LayeredBeeGeometry(BlockModel baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, net.minecraft.client.renderer.texture.TextureAtlasSprite> spriteGetter, ModelState modelState, net.minecraft.client.renderer.block.model.ItemOverrides overrides) {
        var particleSprite = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()));

        StandaloneGeometryBakingContext beeContext = StandaloneGeometryBakingContext.builder(context).build(ResourceLocation.fromNamespaceAndPath(OvercomplicatedBees.MOD_ID, "layered_bee"));

        LayeredBeeOverrideHandler dynamicOverrides = new LayeredBeeOverrideHandler(overrides, baker, beeContext, modelState, baseModel, spriteGetter);

        return CompositeModel.Baked.builder(beeContext, particleSprite, dynamicOverrides, context.getTransforms()).build();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, net.minecraft.client.resources.model.UnbakedModel> modelGetter, IGeometryBakingContext context) {
        baseModel.resolveParents(modelGetter);
    }

    public static final class Loader implements IGeometryLoader<LayeredBeeGeometry> {

        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public LayeredBeeGeometry read(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
            jsonObject.remove("loader");

            BlockModel baseModel = context.deserialize(jsonObject, BlockModel.class);

            return new LayeredBeeGeometry(baseModel);
        }
    }
}