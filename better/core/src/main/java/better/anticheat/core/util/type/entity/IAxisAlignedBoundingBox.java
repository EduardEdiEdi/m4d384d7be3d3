package better.anticheat.core.util.type.entity;

import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.util.Vector3d;
import wtf.spare.sparej.Pair;

/**
 * Interface defining the contract for axis-aligned bounding boxes (AABB) used in collision detection and spatial queries within the Minecraft anticheat system.
 * Implementations must provide methods for manipulation, intersection checks, and distance calculations.
 *
 * @see AxisAlignedBB for a concrete implementation
 */
public interface IAxisAlignedBoundingBox {
    /**
     * Expands the bounding box by the specified amounts in all directions.
     *
     * @param x the amount to expand in the X direction
     * @param y the amount to expand in the Y direction
     * @param z the amount to expand in the Z direction
     * @return a new or modified bounding box instance
     */
    IAxisAlignedBoundingBox expand(double x, double y, double z);

    /**
     * Expands the maximum coordinates of the bounding box by the specified amounts.
     *
     * @param x the amount to expand max X
     * @param y the amount to expand max Y
     * @param z the amount to expand max Z
     * @return a new or modified bounding box instance
     */
    IAxisAlignedBoundingBox expandMax(double x, double y, double z);

    /**
     * Expands the minimum coordinates of the bounding box by the specified amounts.
     *
     * @param x the amount to expand min X
     * @param y the amount to expand min Y
     * @param z the amount to expand min Z
     * @return a new or modified bounding box instance
     */
    IAxisAlignedBoundingBox expandMin(double x, double y, double z);

    /**
     * Translates the bounding box by the specified amounts in all directions.
     *
     * @param x the amount to add to X coordinates
     * @param y the amount to add to Y coordinates
     * @param z the amount to add to Z coordinates
     * @return a new or modified bounding box instance
     */
    IAxisAlignedBoundingBox add(double x, double y, double z);

    /**
     * Checks if this bounding box collides with another bounding box.
     *
     * @param other the other bounding box to check against
     * @return true if the boxes overlap, false otherwise
     */
    boolean collides(IAxisAlignedBoundingBox other);

    /**
     * Checks if this bounding box collides with another from below (i.e., top of this touches bottom of other).
     *
     * @param other the other bounding box to check against
     * @return true if colliding from below, false otherwise
     */
    boolean collidesUnder(IAxisAlignedBoundingBox other);

    /**
     * Checks if this bounding box collides with another from above (i.e., bottom of this touches top of other).
     *
     * @param other the other bounding box to check against
     * @return true if colliding from above, false otherwise
     */
    boolean collidesAbove(IAxisAlignedBoundingBox other);

    /**
     * Checks if this bounding box collides horizontally with another (i.e., sharing a side in X or Z, and overlapping in Y).
     *
     * @param other the other bounding box to check against
     * @return true if colliding horizontally, false otherwise
     */
    boolean collidesHorizontally(IAxisAlignedBoundingBox other);

    /**
     * Calculates the interception point of a line segment with this bounding box.
     *
     * @param vecA the starting point of the line segment
     * @param vecB the ending point of the line segment
     * @return the interception point if it exists, null otherwise
     */
    Vector3d calculateIntercept(Vector3d vecA, Vector3d vecB);

    /**
     * Calculates the interception point and the face hit by a line segment with this bounding box.
     *
     * @param vecA the starting point of the line segment
     * @param vecB the ending point of the line segment
     * @return a pair containing the interception point and the BlockFace hit, or both null if no interception
     */
    Pair<Vector3d, BlockFace> calculateInterceptWithDirection(Vector3d vecA, Vector3d vecB);

    /**
     * Creates a copy of this bounding box.
     *
     * @return a new bounding box instance with the same coordinates
     */
    IAxisAlignedBoundingBox copy();

    /**
     * Gets the X-position of the center of the bounding box.
     *
     * @return the center X-coordinate
     */
    double posX();

    /**
     * Gets the Y-position of the minimum Y-coordinate (bottom) of the bounding box.
     *
     * @return the min Y-coordinate
     */
    double posY();

    /**
     * Gets the Z-position of the center of the bounding box.
     *
     * @return the center Z-coordinate
     */
    double posZ();

    /**
     * Creates a new bounding box offset from this one by the specified amounts.
     *
     * @param x the offset in X
     * @param y the offset in Y
     * @param z the offset in Z
     * @return a new bounding box instance with offset coordinates
     */
    IAxisAlignedBoundingBox offset(double x, double y, double z);

    /**
     * Checks if the given vector is within the YZ plane of this bounding box.
     *
     * @param vec the vector to check (expected to be of type Vector3d)
     * @return true if the vector's Y and Z coordinates are within bounds, false otherwise
     */
    boolean isVecInYZ(Vector3d vec);

    /**
     * Checks if the given vector is within the XZ plane of this bounding box.
     *
     * @param vec the vector to check
     * @return true if the vector's X and Z coordinates are within bounds, false otherwise
     */
    boolean isVecInXZ(Vector3d vec);

    /**
     * Checks if the given vector is within the XY plane of this bounding box.
     *
     * @param vec the vector to check
     * @return true if the vector's X and Y coordinates are within bounds, false otherwise
     */
    boolean isVecInXY(Vector3d vec);

    /**
     * Finds the intermediate point along a line segment at a specific X-value.
     *
     * @param original the starting point of the line segment
     * @param vec the ending point of the line segment
     * @param x the X-value to interpolate at
     * @return the interpolated point, or null if not within the segment
     */
    Vector3d getIntermediateWithXValue(Vector3d original, Vector3d vec, double x);

    /**
     * Finds the intermediate point along a line segment at a specific Y-value.
     *
     * @param original the starting point of the line segment
     * @param vec the ending point of the line segment
     * @param y the Y-value to interpolate at
     * @return the interpolated point, or null if not within the segment
     */
    Vector3d getIntermediateWithYValue(Vector3d original, Vector3d vec, double y);

    /**
     * Finds the intermediate point along a line segment at a specific Z-value.
     *
     * @param original the starting point of the line segment
     * @param vec the ending point of the line segment
     * @param z the Z-value to interpolate at
     * @return the interpolated point, or null if not within the segment
     */
    Vector3d getIntermediateWithZValue(Vector3d original, Vector3d vec, double z);

    /**
     * Calculates the squared distance between two points.
     *
     * @param original the first point
     * @param vec the second point
     * @return the squared distance between the points
     */
    double squareDistanceTo(Vector3d original, Vector3d vec);

    /**
     * Computes the hash code for this bounding box.
     *
     * @return the hash code value
     */
    int hashCode();

    /**
     * Checks if this bounding box is equal to another object.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    boolean equals(Object o);

    /**
     * Creates a clone of this bounding box.
     *
     * @return a new bounding box instance
     */
    IAxisAlignedBoundingBox clone();

    /**
     * Returns a string representation of this bounding box.
     *
     * @return a string describing the bounding box coordinates
     */
    String toString();

    /**
     * Gets the minimum X-coordinate of the bounding box.
     *
     * @return the min X value
     */
    double getMinX();

    /**
     * Sets the minimum X-coordinate of the bounding box.
     *
     * @param minX the new min X value
     */
    void setMinX(double minX);

    /**
     * Gets the minimum Y-coordinate of the bounding box.
     *
     * @return the min Y value
     */
    double getMinY();

    /**
     * Sets the minimum Y-coordinate of the bounding box.
     *
     * @param minY the new min Y value
     */
    void setMinY(double minY);

    /**
     * Gets the minimum Z-coordinate of the bounding box.
     *
     * @return the min Z value
     */
    double getMinZ();

    /**
     * Sets the minimum Z-coordinate of the bounding box.
     *
     * @param minZ the new min Z value
     */
    void setMinZ(double minZ);

    /**
     * Gets the maximum X-coordinate of the bounding box.
     *
     * @return the max X value
     */
    double getMaxX();

    /**
     * Sets the maximum X-coordinate of the bounding box.
     *
     * @param maxX the new max X value
     */
    void setMaxX(double maxX);

    /**
     * Gets the maximum Y-coordinate of the bounding box.
     *
     * @return the max Y value
     */
    double getMaxY();

    /**
     * Sets the maximum Y-coordinate of the bounding box.
     *
     * @param maxY the new max Y value
     */
    void setMaxY(double maxY);

    /**
     * Gets the maximum Z-coordinate of the bounding box.
     *
     * @return the max Z value
     */
    double getMaxZ();

    /**
     * Sets the maximum Z-coordinate of the bounding box.
     *
     * @param maxZ the new max Z value
     */
    void setMaxZ(double maxZ);

    /**
     * Checks if this bounding box intersects with another.
     *
     * @param other the other bounding box to check against
     * @return true if the boxes intersect, false otherwise
     */
    boolean intersectsWith(IAxisAlignedBoundingBox other);

    /**
     * Adds coordinates to the bounding box and returns a new instance.
     *
     * @param x the X offset
     * @param y the Y offset
     * @param z the Z offset
     * @return a new bounding box instance with added coordinates
     */
    IAxisAlignedBoundingBox addCoord(double x, double y, double z);

    /**
     * Calculates the X-offset to resolve collision with another bounding box.
     *
     * @param other the other bounding box
     * @param offsetX the proposed X offset
     * @return the adjusted X offset to prevent overlap
     */
    double calculateXOffset(IAxisAlignedBoundingBox other, double offsetX);

    /**
     * Calculates the Y-offset to resolve collision with another bounding box.
     *
     * @param other the other bounding box
     * @param offsetY the proposed Y offset
     * @return the adjusted Y offset to prevent overlap
     */
    double calculateYOffset(IAxisAlignedBoundingBox other, double offsetY);

    /**
     * Calculates the Z-offset to resolve collision with another bounding box.
     *
     * @param other the other bounding box
     * @param offsetZ the proposed Z offset
     * @return the adjusted Z offset to prevent overlap
     */
    double calculateZOffset(IAxisAlignedBoundingBox other, double offsetZ);

    /**
     * Subtracts values from the minimum coordinates of the bounding box.
     *
     * @param x the amount to subtract from min X
     * @param y the amount to subtract from min Y
     * @param z the amount to subtract from min Z
     * @return a new or modified bounding box instance
     */
    IAxisAlignedBoundingBox subtractMin(final double x, final double y, final double z);

    /**
     * Subtracts values from the maximum coordinates of the bounding box.
     *
     * @param x the amount to subtract from max X
     * @param y the amount to subtract from max Y
     * @param z the amount to subtract from max Z
     * @return a new or modified bounding box instance
     */
    IAxisAlignedBoundingBox subtractMax(final double x, final double y, final double z);
}
