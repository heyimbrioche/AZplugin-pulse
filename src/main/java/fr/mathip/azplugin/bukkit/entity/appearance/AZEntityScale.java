package fr.mathip.azplugin.bukkit.entity.appearance;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pactify.client.api.plprotocol.metadata.PactifyScaleMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketAbstractMeta;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public final class AZEntityScale {
    private Float bboxWidth;
    private Float bboxHeight;
    private Float renderWidth;
    private Float renderDepth;
    private Float renderHeight;
    private Float itemInHandWidth;
    private Float itemInHandDepth;
    private Float itemInHandHeight;
    private Float nameTags;

    public AZEntityScale(Float scale) {
        this.bboxWidth = scale;
        this.bboxHeight = scale;
        this.renderWidth = scale;
        this.renderDepth = scale;
        this.renderHeight = scale;
        this.itemInHandWidth = scale;
        this.itemInHandDepth = scale;
        this.itemInHandHeight = scale;
        this.nameTags = scale;
    }

    public boolean isNull() {
        return (bboxWidth == null &&
                bboxHeight == null &&
                renderWidth == null &&
                renderDepth == null &&
                renderHeight == null &&
                itemInHandWidth == null &&
                itemInHandDepth == null &&
                itemInHandHeight == null &&
                nameTags == null);
    }

    public PactifyScaleMetadata toPacMetadata() {
        if (isNull()) {
            return PLSPPacketAbstractMeta.DEFAULT_SCALE;
        }
        float rw = renderWidth != null ? renderWidth : 1.0f;
        float rh = renderHeight != null ? renderHeight : 1.0f;
        float rd = renderDepth != null ? renderDepth : 1.0f;
        float bw = bboxWidth != null ? bboxWidth : 1.0f;
        float bh = bboxHeight != null ? bboxHeight : 1.0f;
        float iw = itemInHandWidth != null ? itemInHandWidth : 1.0f;
        float ih = itemInHandHeight != null ? itemInHandHeight : 1.0f;
        float id = itemInHandDepth != null ? itemInHandDepth : 1.0f;
        float nt = nameTags != null ? nameTags : 1.0f;
        PactifyScaleMetadata scaleMetadata = new PactifyScaleMetadata(rw, rh, rd, bw, bh, iw, ih, id, nt);
        return scaleMetadata;
    }
}
