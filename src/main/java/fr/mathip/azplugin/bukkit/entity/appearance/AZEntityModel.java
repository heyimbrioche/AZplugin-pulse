package fr.mathip.azplugin.bukkit.entity.appearance;

import org.bukkit.entity.EntityType;

import fr.mathip.azplugin.bukkit.handlers.PLSPPlayerModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pactify.client.api.plprotocol.metadata.ImmutablePactifyModelMetadata;
import pactify.client.api.plprotocol.metadata.PactifyModelMetadata;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class AZEntityModel {

    private final int modelId;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float eyeHeightStand;
    private float eyeHeightSneak;
    private float eyeHeightSleep;
    private float eyeHeightElytra;

    public AZEntityModel() {
        this.modelId = -1;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.eyeHeightStand = Float.NaN;
        this.eyeHeightSneak = Float.NaN;
        this.eyeHeightSleep = Float.NaN;
        this.eyeHeightElytra = Float.NaN;
    }

    public AZEntityModel(EntityType entityType) {
        this.modelId = entityType.getTypeId();
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.eyeHeightStand = Float.NaN;
        this.eyeHeightSneak = Float.NaN;
        this.eyeHeightSleep = Float.NaN;
        this.eyeHeightElytra = Float.NaN;
    }

    public AZEntityModel(PLSPPlayerModel modelType) {
        this.modelId = modelType.getId();
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.eyeHeightStand = Float.NaN;
        this.eyeHeightSneak = Float.NaN;
        this.eyeHeightSleep = Float.NaN;
        this.eyeHeightElytra = Float.NaN;
    }

    public boolean isNull() {
        return modelId == -1
                && offsetX == 0
                && offsetY == 0
                && offsetZ == 0
                && Float.isNaN(eyeHeightStand)
                && Float.isNaN(eyeHeightSneak)
                && Float.isNaN(eyeHeightSleep)
                && Float.isNaN(eyeHeightElytra);
    }

    public PactifyModelMetadata toPacMetadata() {
        if (!isNull()) {
            PactifyModelMetadata modelMetadata = new PactifyModelMetadata(
                    modelId >= 0 ? modelId : -1,
                    offsetX, offsetY, offsetZ,
                    null,
                    eyeHeightStand, eyeHeightSneak, eyeHeightSleep, eyeHeightElytra
            );
            return modelMetadata;
        } else {
            return new ImmutablePactifyModelMetadata();
        }
    }
}
