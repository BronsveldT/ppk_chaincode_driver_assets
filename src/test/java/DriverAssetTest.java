import org.capgemini.DriverAsset;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DriverAssetTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            double[] drivenKilometers = {4.5, 67.4, 30.68, 5.23, 0};
            DriverAsset driverAsset = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );

            assertThat(driverAsset).isEqualTo(driverAsset);
        }

        @Test
        public void isSymmetric() {
            double[] drivenKilometers = {4.5, 67.4, 30.68, 5.23, 0};
            DriverAsset driverAssetA = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );
            DriverAsset driverAssetB = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );

            assertThat(driverAssetA).isEqualTo(driverAssetB);
            assertThat(driverAssetB).isEqualTo(driverAssetA);
        }

        @Test
        public void isTransitive() {
            double[] drivenKilometers = {4.5, 67.4, 30.68, 5.23, 0};
            DriverAsset driverAsset = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );
            DriverAsset driverAssetB = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );
            DriverAsset driverAssetC = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );

            assertThat(driverAsset).isEqualTo(driverAssetB);
            assertThat(driverAssetB).isEqualTo(driverAssetC);
            assertThat(driverAsset).isEqualTo(driverAssetC);
        }

        @Test
        public void handlesInequality() {
            double[] drivenKilometers = {4.5, 67.4, 30.68, 5.23, 0};
            DriverAsset driverAsset = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );
            DriverAsset driverAssetB = new DriverAsset(
                    "FT854Z",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );
            assertThat(driverAsset).isNotEqualTo(driverAssetB);
        }

        @Test
        public void handlesOtherObjects() {
            double[] drivenKilometers = {4.5, 67.4, 30.68, 5.23, 0};
            DriverAsset driverAsset = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );
            String driverAssetB = "not a RoadAsset";

            assertThat(driverAsset).isNotEqualTo(driverAssetB);
        }

        @Test
        public void handlesNull() {
            double[] drivenKilometers = {4.5, 67.4, 30.68, 5.23, 0};
            DriverAsset driverAsset = new DriverAsset(
                    "NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    drivenKilometers,
                    23.5
            );

            assertThat(driverAsset).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesRoadAsset() {
        double[] drivenKilometers = {4.5, 67.4, 30.68, 5.23, 0};
        DriverAsset driverAsset = new DriverAsset(
                "NG956L",
                "NG956L",
                "toyota",
                "6",
                drivenKilometers,
                23.5
        );
        System.out.println(driverAsset);
        assertThat(driverAsset.toString()).isEqualTo("DriverAsset@799d8f5c [driverAssetId=NG956L, " +
                "licensePlate=NG956L, " +
                "brand=toyota, emissionType=6, " +
                "drivenKilometersOnRoad=[4.5, 67.4, 30.68, 5.23, 0.0], rideCosts=23.5]");
    }
}
