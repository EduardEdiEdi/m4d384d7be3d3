package better.anticheat.core.util.type.entity;

import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.util.Vector3d;
import lombok.Getter;
import lombok.Setter;
import wtf.spare.sparej.Pair;

/**
 * Represents an axis-aligned bounding box (AABB) used for collision detection and spatial queries in the Minecraft anticheat system.
 * This class provides methods for expanding, offsetting, intersecting, and calculating intercepts with other bounding boxes.
 * It implements the IAxisAlignedBoundingBox interface for consistency.
 *
 * @see IAxisAlignedBoundingBox
 */
@Getter
@Setter
public class AxisAlignedBB implements IAxisAlignedBoundingBox {
    /**
     * The minimum X-coordinate of the bounding box.
     */
    private double minX;
    /**
     * The minimum Y-coordinate of the bounding box.
     */
    private double minY;
    /**
     * The minimum Z-coordinate of the bounding box.
     */
    private double minZ;
    /**
     * The maximum X-coordinate of the bounding box.
     */
    private double maxX;
    /**
     * The maximum Y-coordinate of the bounding box.
     */
    private double maxY;
    /**
     * The maximum Z-coordinate of the bounding box.
     */
    private double maxZ;

    /**
     * Constructs a new AxisAlignedBB with the specified minimum and maximum coordinates.
     * Ensures min values are less than or equal to max values by swapping if necessary.
     *
     * @param minX the minimum X-coordinate
     * @param minY the minimum Y-coordinate
     * @param minZ the minimum Z-coordinate
     * @param maxX the maximum X-coordinate
     * @param maxY the maximum Y-coordinate
     * @param maxZ the maximum Z-coordinate
     */
    public AxisAlignedBB(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
        this.maxZ = Math.max(maxZ, minZ);
    }

    /**
     * Constructs a new AxisAlignedBB centered at the specified position with given width and height.
     * The depth is assumed to be equal to the width for symmetry.
     *
     * @param x the center X-coordinate
     * @param y the center Y-coordinate (min Y is set to y)
     * @param z the center Z-coordinate
     * @param width the width of the box (extends equally in positive and negative X and Z)
     * @param height the height of the box (extends from y to y + height)
     */
    public AxisAlignedBB(final double x, final double y, final double z, final double width, final double height) {
        this(x, y, z, x + (width / 2.0F), y + height, z + (width / 2.0F));
    }

    /**
     * Expands the bounding box by the specified amounts in all directions.
     *
     * @param x the amount to expand in the X direction
     * @param y the amount to expand in the Y direction
     * @param z the amount to expand in the Z direction
     * @return this bounding box for chaining
     */
    @Override
    public AxisAlignedBB expand(final double x, final double y, final double z) {
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    /**
     * Expands the maximum coordinates of the bounding box by the specified amounts.
     *
     * @param x the amount to expand max X
     * @param y the amount to expand max Y
     * @param z the amount to expand max Z
     * @return this bounding box for chaining
     */
    @Override
    public AxisAlignedBB expandMax(final double x, final double y, final double z) {
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    /**
     * Expands the minimum coordinates of the bounding box by the specified amounts.
     *
     * @param x the amount to expand min X
     * @param y the amount to expand min Y
     * @param z the amount to expand min Z
     * @return this bounding box for chaining
     */
    @Override
    public AxisAlignedBB expandMin(final double x, final double y, final double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;

        return this;
    }

    /**
     * Translates the bounding box by the specified amounts in all directions.
     *
     * @param x the amount to add to X coordinates
     * @param y the amount to add to Y coordinates
     * @param z the amount to add to Z coordinates
     * @return this bounding box for chaining
     */
    @Override
    public AxisAlignedBB add(final double x, final double y, final double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    /**
     * Checks if this bounding box collides with another bounding box.
     *
     * @param other the other bounding box to check against
     * @return true if the boxes overlap, false otherwise
     */
    @Override
    public boolean collides(IAxisAlignedBoundingBox other) {
        return other.getMaxX() >= this.minX
                && other.getMinX() <= this.maxX
                && other.getMaxY() >= this.minY
                && other.getMinY() <= this.maxY
                && other.getMaxZ() >= this.minZ
                && other.getMinZ() <= this.maxZ;
    }

    /**
     * Checks if this bounding box collides with another from below (i.e., top of this touches bottom of other).
     *
     * @param other the other bounding box to check against
     * @return true if colliding from below, false otherwise
     */
    @Override
    public boolean collidesUnder(IAxisAlignedBoundingBox other) {
        return maxY == other.getMinY()
                && minZ < other.getMaxZ()
                && minX < other.getMaxX()
                && maxZ > other.getMinZ()
                && maxX > other.getMinX();
    }

    /**
     * Checks if this bounding box collides with another from above (i.e., bottom of this touches top of other).
     *
     * @param other the other bounding box to check against
     * @return true if colliding from above, false otherwise
     */
    @Override
    public boolean collidesAbove(IAxisAlignedBoundingBox other) {
        return minY == other.getMaxY()
                && minZ < other.getMaxZ()
                && minX < other.getMaxX()
                && maxZ > other.getMinZ()
                && maxX > other.getMinX();
    }

    /**
     * Checks if this bounding box collides horizontally with another (i.e., sharing a side in X or Z, and overlapping in Y).
     *
     * @param other the other bounding box to check against
     * @return true if colliding horizontally, false otherwise
     */
    @Override
    public boolean collidesHorizontally(IAxisAlignedBoundingBox other) {
        boolean vertical = maxY > other.getMinY() && minY < other.getMaxY();
        boolean horizontal = minX == other.getMaxX() || maxX == other.getMinX() || minZ == other.getMaxX() || maxZ == other.getMinZ();

        return vertical && horizontal;
    }

    /**
     * Calculates the interception point of a line segment with this bounding box.
     *
     * @param vecA the starting point of the line segment
     * @param vecB the ending point of the line segment
     * @return the interception point if it exists, null otherwise
     */
    @Override
    public Vector3d calculateIntercept(Vector3d vecA, Vector3d vecB) {
        var vec3 = getIntermediateWithXValue(vecA, vecB, this.minX);
        var vec31 = getIntermediateWithXValue(vecA, vecB, this.maxX);
        var vec32 = getIntermediateWithYValue(vecA, vecB, this.minY);
        var vec33 = getIntermediateWithYValue(vecA, vecB, this.maxY);
        var vec34 = getIntermediateWithZValue(vecA, vecB, this.minZ);
        var vec35 = getIntermediateWithZValue(vecA, vecB, this.maxZ);

        if (!this.isVecInYZ(vec3)) {
            vec3 = null;
        }

        if (!this.isVecInYZ(vec31)) {
            vec31 = null;
        }

        if (!this.isVecInXZ(vec32)) {
            vec32 = null;
        }

        if (!this.isVecInXZ(vec33)) {
            vec33 = null;
        }

        if (!this.isVecInXY(vec34)) {
            vec34 = null;
        }

        if (!this.isVecInXY(vec35)) {
            vec35 = null;
        }

        Vector3d vec36 = null;

        if (vec3 != null) {
            vec36 = vec3;
        }

        if (vec31 != null && (vec36 == null || squareDistanceTo(vecA, vec31) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec31;
        }

        if (vec32 != null && (vec36 == null || squareDistanceTo(vecA, vec32) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec32;
        }

        if (vec33 != null && (vec36 == null || squareDistanceTo(vecA, vec33) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec33;
        }

        if (vec34 != null && (vec36 == null || squareDistanceTo(vecA, vec34) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec34;
        }

        if (vec35 != null && (vec36 == null || squareDistanceTo(vecA, vec35) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec35;
        }

        return vec36;
    }

    /**
     * Calculates the interception point and the face hit by a line segment with this bounding box.
     *
     * @param vecA the starting point of the line segment
     * @param vecB the ending point of the line segment
     * @return a pair containing the interception point and the BlockFace hit, or both null if no interception
     */
    @Override
    public Pair<Vector3d, BlockFace> calculateInterceptWithDirection(Vector3d vecA, Vector3d vecB) {
        var vec3 = getIntermediateWithXValue(vecA, vecB, this.minX);
        var vec31 = getIntermediateWithXValue(vecA, vecB, this.maxX);
        var vec32 = getIntermediateWithYValue(vecA, vecB, this.minY);
        var vec33 = getIntermediateWithYValue(vecA, vecB, this.maxY);
        var vec34 = getIntermediateWithZValue(vecA, vecB, this.minZ);
        var vec35 = getIntermediateWithZValue(vecA, vecB, this.maxZ);

        if (!this.isVecInYZ(vec3)) {
            vec3 = null;
        }

        if (!this.isVecInYZ(vec31)) {
            vec31 = null;
        }

        if (!this.isVecInXZ(vec32)) {
            vec32 = null;
        }

        if (!this.isVecInXZ(vec33)) {
            vec33 = null;
        }

        if (!this.isVecInXY(vec34)) {
            vec34 = null;
        }

        if (!this.isVecInXY(vec35)) {
            vec35 = null;
        }

        Vector3d vec36 = null;

        if (vec3 != null) {
            vec36 = vec3;
        }

        if (vec31 != null && (vec36 == null || squareDistanceTo(vecA, vec31) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec31;
        }

        if (vec32 != null && (vec36 == null || squareDistanceTo(vecA, vec32) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec32;
        }

        if (vec33 != null && (vec36 == null || squareDistanceTo(vecA, vec33) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec33;
        }

        if (vec34 != null && (vec36 == null || squareDistanceTo(vecA, vec34) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec34;
        }

        if (vec35 != null && (vec36 == null || squareDistanceTo(vecA, vec35) < squareDistanceTo(vecA, vec36))) {
            vec36 = vec35;
        }

        if (vec36 == null) {
            return new Pair<>(null, null);
        } else {
            BlockFace enumfacing;

            if (vec36 == vec3) {
                enumfacing = BlockFace.WEST;
            } else if (vec36 == vec31) {
                enumfacing = BlockFace.EAST;
            } else if (vec36 == vec32) {
                enumfacing = BlockFace.DOWN;
            } else if (vec36 == vec33) {
                enumfacing = BlockFace.UP;
            } else if (vec36 == vec34) {
                enumfacing = BlockFace.NORTH;
            } else {
                enumfacing = BlockFace.SOUTH;
            }

            return new Pair<>(vec36, enumfacing);
        }
    }

    /**
     * Creates a copy of this bounding box.
     *
     * @return a new AxisAlignedBB with the same coordinates
     */
    @Override
    public IAxisAlignedBoundingBox copy() {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Gets the X-position of the center of the bounding box.
     *
     * @return the center X-coordinate
     */
    @Override
    public double posX() {
        return (maxX + minX) / 2.0;
    }

    /**
     * Gets the Y-position of the minimum Y-coordinate (bottom) of the bounding box.
     *
     * @return the min Y-coordinate
     */
    @Override
    public double posY() {
        return minY;
    }

    /**
     * Gets the Z-position of the center of the bounding box.
     *
     * @return the center Z-coordinate
     */
    @Override
    public double posZ() {
        return (maxZ + minZ) / 2.0;
    }

    /**
     * Creates a new bounding box offset from this one by the specified amounts.
     *
     * @param x the offset in X
     * @param y the offset in Y
     * @param z the offset in Z
     * @return a new AxisAlignedBB with offset coordinates
     */
    @Override
    public IAxisAlignedBoundingBox offset(double x, double y, double z) {
        return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * Checks if the given vector is within the YZ plane of this bounding box.
     *
     * @param vec the vector to check
     * @return true if the vector's Y and Z coordinates are within bounds, false otherwise
     */
    @Override
    public boolean isVecInYZ(Vector3d vec) {
        return vec != null && vec.y >= this.minY && vec.y <= this.maxY && vec.z >= this.minZ && vec.z <= this.maxZ;
    }

    /**
     * Checks if the given vector is within the XZ plane of this bounding box.
     *
     * @param vec the vector to check
     * @return true if the vector's X and Z coordinates are within bounds, false otherwise
     */
    @Override
    public boolean isVecInXZ(Vector3d vec) {
        return vec != null && vec.x >= this.minX && vec.x <= this.maxX && vec.z >= this.minZ && vec.z <= this.maxZ;
    }

    /**
     * Checks if the given vector is within the XY plane of this bounding box.
     *
     * @param vec the vector to check
     * @return true if the vector's X and Y coordinates are within bounds, false otherwise
     */
    @Override
    public boolean isVecInXY(Vector3d vec) {
        return vec != null && vec.x >= this.minX && vec.x <= this.maxX && vec.y >= this.minY && vec.y <= this.maxY;
    }

    /**
     * Finds the intermediate point along a line segment at a specific X-value.
     *
     * @param original the starting point of the line segment
     * @param vec the ending point of the line segment
     * @param x the X-value to interpolate at
     * @return the interpolated point, or null if not within the segment
     */
    @Override
    public Vector3d getIntermediateWithXValue(Vector3d original, Vector3d vec, double x) {
        var d0 = vec.x - original.x;
        var d1 = vec.y - original.y;
        var d2 = vec.z - original.z;

        if (d0 * d0 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (x - original.x) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3d(original.x + d0 * d3, original.y + d1 * d3, original.z + d2 * d3) : null;
        }
    }

    /**
     * Finds the intermediate point along a line segment at a specific Y-value.
     *
     * @param original the starting point of the line segment
     * @param vec the ending point of the line segment
     * @param y the Y-value to interpolate at
     * @return the interpolated point, or null if not within the segment
     */
    @Override
    public Vector3d getIntermediateWithYValue(Vector3d original, Vector3d vec, double y) {
        double d0 = vec.x - original.x;
        double d1 = vec.y - original.y;
        double d2 = vec.z - original.z;

        if (d1 * d1 < 1.0000000116860974E-7D) {
            return null;
        } else {
            double d3 = (y - original.y) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3d(original.x + d0 * d3, original.y + d1 * d3, original.z + d2 * d3) : null;
        }
    }

    /**
     * Finds the intermediate point along a line segment at a specific Z-value.
     *
     * @param original the starting point of the line segment
     * @param vec the ending point of the line segment
     * @param z the Z-value to interpolate at
     * @return the interpolated point, or null if not within the segment
     */
    @Override
    public Vector3d getIntermediateWithZValue(Vector3d original, Vector3d vec, double z) {
        var d0 = vec.x - original.x;
        var d1 = vec.y - original.y;
        var d2 = vec.z - original.z;

        if (d2 * d2 < 1.0000000116860974E-7D) {
            return null;
        } else {
            var d3 = (z - original.z) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3d(original.x + d0 * d3, original.y + d1 * d3, original.z + d2 * d3) : null;
        }
    }

    /**
     * Calculates the squared distance between two points.
     *
     * @param original the first point
     * @param vec the second point
     * @return the squared distance between the points
     */
    @Override
    public double squareDistanceTo(Vector3d original, Vector3d vec) {
        double d0 = vec.x - original.x;
        double d1 = vec.y - original.y;
        double d2 = vec.z - original.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Calculates the Euclidean distance to another bounding box.
     *
     * @param box the other bounding box
     * @return the distance as a double
     */
    public double distance(final AxisAlignedBB box) {
        double minDiffX = box.minX - minX;
        double maxDiffX = box.maxX - maxX;
        double minDiffY = box.minY - minY;
        double maxDiffY = box.maxY - maxY;
        double minDiffZ = box.minZ - minZ;
        double maxDiffZ = box.maxZ - maxZ;

        final double dx = Math.min(minDiffX * minDiffX, maxDiffX * maxDiffX);
        final double dy = Math.min(minDiffY * minDiffY, maxDiffY * maxDiffY);
        final double dz = Math.min(minDiffZ * minDiffZ, maxDiffZ * maxDiffZ);
        return Math.sqrt(dx + dy + dz);
    }

    /**
     * Checks if this bounding box intersects with another.
     *
     * @param other the other bounding box to check against
     * @return true if the boxes intersect, false otherwise
     */
    public boolean intersectsWith(IAxisAlignedBoundingBox other) {
        return other.getMaxX() >= this.minX && other.getMinX() <= this.maxX && (other.getMaxY() >= this.minY && other.getMinY() <= this.maxY && other.getMaxZ() > this.minZ && other.getMinZ() <= this.maxZ);
    }

    /**
     * Adds coordinates to the bounding box and returns a new instance.
     *
     * @param x the X offset
     * @param y the Y offset
     * @param z the Z offset
     * @return a new AxisAlignedBB with added coordinates
     */
    public AxisAlignedBB addCoord(double x, double y, double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D) {
            d0 += x;
        } else if (x > 0.0D) {
            d3 += x;
        }

        if (y < 0.0D) {
            d1 += y;
        } else if (y > 0.0D) {
            d4 += y;
        }

        if (z < 0.0D) {
            d2 += z;
        } else if (z > 0.0D) {
            d5 += z;
        }

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Calculates the X-offset to resolve collision with another bounding box.
     *
     * @param other the other bounding box
     * @param offsetX the proposed X offset
     * @return the adjusted X offset to prevent overlap
     */
    public double calculateXOffset(IAxisAlignedBoundingBox other, double offsetX) {
        if (other.getMaxY() > this.minY && other.getMinY() < this.maxY && other.getMaxZ() > this.minZ && other.getMinZ() < this.maxZ) {
            if (offsetX > 0.0D && other.getMaxX() <= this.minX) {
                double d1 = this.minX - other.getMaxX();

                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0D && other.getMinX() >= this.maxX) {
                double d0 = this.maxX - other.getMinX();

                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }

        }
        return offsetX;
    }

    /**
     * Calculates the Y-offset to resolve collision with another bounding box.
     *
     * @param other the other bounding box
     * @param offsetY the proposed Y offset
     * @return the adjusted Y offset to prevent overlap
     */
    public double calculateYOffset(IAxisAlignedBoundingBox other, double offsetY) {
        if (other.getMaxX() > this.minX && other.getMinX() < this.maxX && other.getMaxZ() > this.minZ && other.getMinZ() < this.maxZ) {
            if (offsetY > 0.0D && other.getMaxY() <= this.minY) {
                double d1 = this.minY - other.getMaxY();

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.getMinY() >= this.maxY) {
                double d0 = this.maxY - other.getMinY();

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

        }
        return offsetY;
    }

    /**
     * Calculates the Z-offset to resolve collision with another bounding box.
     *
     * @param other the other bounding box
     * @param offsetZ the proposed Z offset
     * @return the adjusted Z offset to prevent overlap
     */
    public double calculateZOffset(IAxisAlignedBoundingBox other, double offsetZ) {
        if (other.getMaxX() > this.minX && other.getMinX() < this.maxX && other.getMaxY() > this.minY && other.getMinY() < this.maxY) {
            if (offsetZ > 0.0D && other.getMaxZ() <= this.minZ) {
                double d1 = this.minZ - other.getMaxZ();

                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0D && other.getMinZ() >= this.maxZ) {
                double d0 = this.maxZ - other.getMinZ();

                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }

        }
        return offsetZ;
    }

    /**
     * Subtracts values from the minimum coordinates of the bounding box.
     *
     * @param x the amount to subtract from min X
     * @param y the amount to subtract from min Y
     * @param z the amount to subtract from min Z
     * @return this bounding box for chaining
     */
    public AxisAlignedBB subtractMin(final double x, final double y, final double z) {
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;
        return this;
    }

    /**
     * Subtracts values from the maximum coordinates of the bounding box.
     *
     * @param x the amount to subtract from max X
     * @param y the amount to subtract from max Y
     * @param z the amount to subtract from max Z
     * @return this bounding box for chaining
     */
    public AxisAlignedBB subtractMax(final double x, final double y, final double z) {
        this.maxX -= x;
        this.maxY -= y;
        this.maxZ -= z;
        return this;
    }

    /**
     * Computes the hash code for this bounding box based on its coordinates.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        int result = Double.hashCode(minX);
        result = 31 * result + Double.hashCode(minY);
        result = 31 * result + Double.hashCode(minZ);
        result = 31 * result + Double.hashCode(maxX);
        result = 31 * result + Double.hashCode(maxY);
        result = 31 * result + Double.hashCode(maxZ);
        return result;
    }

    /**
     * Checks if this bounding box is equal to another object.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AxisAlignedBB)) return false;

        AxisAlignedBB axisAlignedBB = (AxisAlignedBB) o;
        return Double.compare(minX, axisAlignedBB.minX) == 0 && Double.compare(minY, axisAlignedBB.minY) == 0 && Double.compare(minZ, axisAlignedBB.minZ) == 0 && Double.compare(maxX, axisAlignedBB.maxX) == 0 && Double.compare(maxY, axisAlignedBB.maxY) == 0 && Double.compare(maxZ, axisAlignedBB.maxZ) == 0;
    }

    /**
     * Creates a clone of this bounding box.
     *
     * @return a new AxisAlignedBB with the same coordinates
     */
    @Override
    public AxisAlignedBB clone() {
        return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    /**
     * Returns a string representation of this bounding box.
     *
     * @return a string in the format "AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)"
     */
    @Override
    public String toString() {
        return "AxisAlignedBB(minX=" + this.getMinX() + ", minY=" + this.getMinY() + ", minZ=" + this.getMinZ() + ", maxX=" + this.getMaxX() + ", maxY=" + this.getMaxY() + ", maxZ=" + this.getMaxZ() + ")";
    }
}