import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class RotationTest {

    // Test T-block rotations
    @Test
    public void testTBlockRotation() {
        BlockRotator3D.placeAllUniqueRotations(Main.tBlockShape,  BlockRotator3D.TBLOCK_ROTATION_INDICES);
        assertEquals(12, BlockRotator3D.TBLOCK_ROTATION_INDICES.length);
    }

    // Test L-Block rotations
    @Test
    public void testLBlockRotation() {
        BlockRotator3D.placeAllUniqueRotations(Main.lBlockShape,  BlockRotator3D.LBLOCK_ROTATION_INDICES);
        assertEquals(24, BlockRotator3D.LBLOCK_ROTATION_INDICES.length);
    }

    @Test
    public void testZBlockRotation() {
        BlockRotator3D.placeAllUniqueRotations(Main.zBlockShape,  BlockRotator3D.ZBLOCK_ROTATION_INDICES);
        assertEquals(24, BlockRotator3D.ZBLOCK_ROTATION_INDICES.length);
    }

    @Test
    public void testOBlockRotation() {
        BlockRotator3D.placeAllUniqueRotations(Main.oBlockShape,  BlockRotator3D.OBLOCK_ROTATION_INDICES);
        assertEquals(12, BlockRotator3D.OBLOCK_ROTATION_INDICES.length);
    }



}
