package io.github.hitsujirere.hnn_fabdirective.accessor;

import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public interface ISelectionsAccessor {
    Object2IntMap<DynamicHolder<DataModel>> getSelections();

    void setSelections(Object2IntMap<DynamicHolder<DataModel>> selections);
}