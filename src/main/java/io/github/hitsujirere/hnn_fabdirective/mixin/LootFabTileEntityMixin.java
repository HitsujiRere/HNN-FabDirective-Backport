package io.github.hitsujirere.hnn_fabdirective.mixin;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.tile.LootFabTileEntity;
import dev.shadowsoffire.placebo.recipe.VanillaPacketDispatcher;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.hitsujirere.hnn_fabdirective.accessor.ISelectionsAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootFabTileEntity.class)
public abstract class LootFabTileEntityMixin implements ISelectionsAccessor {

    @Shadow(remap = false)
    @Final
    protected Object2IntMap<DynamicHolder<DataModel>> savedSelections;

    @Override
    public Object2IntMap<DynamicHolder<DataModel>> getSelections() {
        return this.savedSelections;
    }

    @Override
    public void setSelections(Object2IntMap<DynamicHolder<DataModel>> selections) {
        this.savedSelections.clear();
        this.savedSelections.putAll(selections);

        BlockEntity self = (BlockEntity) (Object) this;
        VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
        self.setChanged();
    }

}
