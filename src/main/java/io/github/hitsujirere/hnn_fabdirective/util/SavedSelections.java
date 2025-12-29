package io.github.hitsujirere.hnn_fabdirective.util;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.data.DataModelRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class SavedSelections {

    public static final SavedSelections EMPTY = new SavedSelections(new Object2IntOpenHashMap<>());

    public static SavedSelections read(@Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return EMPTY;
        }
        Object2IntOpenHashMap<DynamicHolder<DataModel>> map = new Object2IntOpenHashMap<>();
        for (String key : tag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.parse(key);
            DynamicHolder<DataModel> holder = DataModelRegistry.INSTANCE.holder(id);
            map.put(holder, tag.getInt(key));
        }
        return new SavedSelections(map);
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        this.selections.forEach((holder, val) -> {
            tag.putInt(holder.getId().toString(), val);
        });
        return tag;
    }

    private final Object2IntOpenHashMap<DynamicHolder<DataModel>> selections = new Object2IntOpenHashMap<>();

    public SavedSelections(Object2IntMap<DynamicHolder<DataModel>> selections) {
        this.selections.putAll(selections);
    }

    public Object2IntMap<DynamicHolder<DataModel>> getSelections() {
        return Object2IntMaps.unmodifiable(this.selections);
    }

    public boolean isEmpty() {
        return this.selections.isEmpty();
    }

    public int size() {
        return this.selections.size();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SavedSelections ss && this.selections.equals(ss.selections);
    }

    @Override
    public int hashCode() {
        return this.selections.hashCode();
    }

}
