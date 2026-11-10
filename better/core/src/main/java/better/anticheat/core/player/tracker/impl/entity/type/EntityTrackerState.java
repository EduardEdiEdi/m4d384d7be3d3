package better.anticheat.core.player.tracker.impl.entity.type;

import better.anticheat.core.util.MathUtil;
import better.anticheat.core.util.type.entity.AxisAlignedBB;
import lombok.*;
import wtf.spare.sparej.fastlist.FastObjectArrayList;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EntityTrackerState {
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final EntityTrackerState parent;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final EntityData data;

    private final AxisAlignedBB bb;

    private double otherPlayerMPPosRotationIncrements;
    private double otherPlayerMPX;
    private double otherPlayerMPY;
    private double otherPlayerMPZ;

    private double posX;
    private double posY;
    private double posZ;

    private double potentialOffsetAmountX = 0;
    private double potentialOffsetAmountY = 0;
    private double potentialOffsetAmountZ = 0;

    private FastObjectArrayList<EntityTrackerState> children = new FastObjectArrayList<>();

    public EntityTrackerState(EntityTrackerState parent, EntityData data, AxisAlignedBB bb, double posZ, double posY, double posX) {
        this.parent = parent;
        this.data = data;
        this.bb = bb;
        this.posZ = posZ;
        this.posY = posY;
        this.posX = posX;
        this.otherPlayerMPX = posX;
        this.otherPlayerMPY = posY;
        this.otherPlayerMPZ = posZ;
        this.otherPlayerMPPosRotationIncrements = 0;
    }

    public EntityTrackerState cloneWithoutChildren() {
        return new EntityTrackerState(parent, data, bb, otherPlayerMPPosRotationIncrements, otherPlayerMPX, otherPlayerMPY, otherPlayerMPZ, posX, posY, posZ, 0,0,0, new FastObjectArrayList<>());
    }

    public EntityTrackerState newChild(boolean copyChildrenChildren) {
        final var neww = new EntityTrackerState(this, data, bb, otherPlayerMPPosRotationIncrements, otherPlayerMPX, otherPlayerMPY, otherPlayerMPZ, posX, posY, posZ, this.potentialOffsetAmountX, this.potentialOffsetAmountY, this.potentialOffsetAmountZ, new FastObjectArrayList<>());
        for (final var child : this.children) {
            neww.children.add(child.newChild(neww, copyChildrenChildren, copyChildrenChildren));
        }
        return neww;
    }

    public EntityTrackerState newChild(final EntityTrackerState parent, boolean copyChildrenChildren) {
        final var neww = new EntityTrackerState(parent, data, bb, otherPlayerMPPosRotationIncrements, otherPlayerMPX, otherPlayerMPY, otherPlayerMPZ, posX, posY, posZ, this.potentialOffsetAmountX, this.potentialOffsetAmountY, this.potentialOffsetAmountZ, new FastObjectArrayList<>());
        for (final var child : this.children) {
            neww.children.add(child.newChild(neww, copyChildrenChildren, copyChildrenChildren));
        }
        return neww;
    }

    public EntityTrackerState newChild(final EntityTrackerState parent, boolean copyChildren, boolean copyChildrenChildren) {
        final var neww = new EntityTrackerState(parent, data, bb, otherPlayerMPPosRotationIncrements, otherPlayerMPX, otherPlayerMPY, otherPlayerMPZ, posX, posY, posZ,
                this.potentialOffsetAmountX, this.potentialOffsetAmountY, this.potentialOffsetAmountZ, new FastObjectArrayList<>(copyChildren ? this.children.size() : 16));
        if (copyChildren) {
            for (final var child : this.children) {
                neww.children.add(child.newChild(neww, copyChildrenChildren, copyChildrenChildren));
            }
        }
        return neww;
    }

    public double distance(EntityTrackerState other) {
        return MathUtil.hypot((this.posX - other.getPosX()), (this.posY - other.getPosY()), (this.posZ - other.getPosZ()));
    }

    public double getPotentialOffsetAmount() {
        return MathUtil.hypot(potentialOffsetAmountX, potentialOffsetAmountY, potentialOffsetAmountZ);
    }

    public void addOffsetABS(final double x, final double y, final double z) {
        potentialOffsetAmountX += Math.abs(x);
        potentialOffsetAmountY += Math.abs(y);
        potentialOffsetAmountZ += Math.abs(z);
    }

    public int hashCodePositionsAndIncrementsOnly() {
        int result = getData().getId();
        result = 31 * result + Double.hashCode(getOtherPlayerMPPosRotationIncrements());
        result = 31 * result + Double.hashCode(getOtherPlayerMPX());
        result = 31 * result + Double.hashCode(getOtherPlayerMPY());
        result = 31 * result + Double.hashCode(getOtherPlayerMPZ());
        result = 31 * result + Double.hashCode(getPosX());
        result = 31 * result + Double.hashCode(getPosY());
        result = 31 * result + Double.hashCode(getPosZ());
        return result;
    }

    @Override
    public int hashCode() {
        int result = getParent() instanceof EntityTrackerState ?
                getParent().liteHashCode() :
                getBb().hashCode();
        result = 31 * result + getData().getId();
        result = 31 * result + Double.hashCode(getOtherPlayerMPPosRotationIncrements());
        result = 31 * result + Double.hashCode(getOtherPlayerMPX());
        result = 31 * result + Double.hashCode(getOtherPlayerMPY());
        result = 31 * result + Double.hashCode(getOtherPlayerMPZ());
        result = 31 * result + Double.hashCode(getPosX());
        result = 31 * result + Double.hashCode(getPosY());
        result = 31 * result + Double.hashCode(getPosZ());
        for (final var child : getChildren()) {
            result = 31 * result + child.liteHashCode();
        }
        return result;
    }

    public int liteHashCode() {
        if (parent == null) return hashCode();

        int result = getData().getId();
        result = 31 * result + Double.hashCode(getOtherPlayerMPPosRotationIncrements());
        result = 31 * result + Double.hashCode(getOtherPlayerMPX());
        result = 31 * result + Double.hashCode(getOtherPlayerMPY());
        result = 31 * result + Double.hashCode(getOtherPlayerMPZ());
        result = 31 * result + Double.hashCode(getPosX());
        result = 31 * result + Double.hashCode(getPosY());
        result = 31 * result + Double.hashCode(getPosZ());
        result = 31 * result + getChildren().size();
        return result;
    }
}
