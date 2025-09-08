package dev.ultreon.devices.client.components;

import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ultreon.devices.components.GameComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RenderDataComponent implements GameComponent {
    private final Map<String, MeshData> meshData = new HashMap<>();

    public void addMeshData(String name, MeshData meshData) {
        if (!this.meshData.containsKey(name)) {
            this.meshData.put(name, meshData);
        }
    }

    public void removeMeshData(String name) {
        if (this.meshData.containsKey(name)) {
            MeshData remove = this.meshData.remove(name);
            remove.close();
        }
    }

    public MeshData getMeshData(String name) {
        return this.meshData.get(name);
    }

    public boolean hasMeshData(String name) {
        return this.meshData.containsKey(name);
    }

    public MeshData getOrCreateMeshData(String name, Supplier<MeshData> factory) {
        if (!this.meshData.containsKey(name)) {
            this.meshData.put(name, factory.get());
        }
        return this.meshData.get(name);
    }

    public void clearMeshData() {
        for (MeshData meshData : this.meshData.values()) {
            meshData.close();
        }
        this.meshData.clear();
    }

    public List<MeshData> getMeshData() {
        return new ArrayList<>(this.meshData.values());
    }

    public void render(String name, @NotNull PoseStack pose, @NotNull VertexConsumer bufferSource, int packedLight, int packedOverlay) {
        MeshData meshData = this.meshData.get(name);
        if (meshData == null) return;
    }
}
