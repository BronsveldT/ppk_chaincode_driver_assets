import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import org.capgemini.DriverAsset;
import org.capgemini.DriverAssetTransfer;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InOrder;

@Testable
public class DriverAssetTransferTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockAssetResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> assetList;

        MockAssetResultsIterator() {
            super();

            assetList = new ArrayList<KeyValue>();

            DriverAsset driverAsset1 = new DriverAsset("NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    new double[]{4.5, 67.4, 30.68, 5.23, 0.0},
                    23.5);

            DriverAsset driverAsset2 = new DriverAsset("PK956L",
                    "PK956L",
                    "Prius",
                    "2",
                    new double[]{234.56, 67.4, 30.68, 43.23, 0.0},
                    96.5);
            DriverAsset driverAsset3 = new DriverAsset("BH54KL",
                    "BH54KL",
                    "Mercedes",
                    "3",
                    new double[]{67.5, 67.4, 30.68, 5.23, 0.0},
                    45.3);
            assetList.add(new MockKeyValue(driverAsset1.getDriverAssetId(),
                    driverAsset1.lazyToString()));
            assetList.add(new MockKeyValue(driverAsset2.getDriverAssetId(),
                    driverAsset2.lazyToString()));
            assetList.add(new MockKeyValue(driverAsset3.getDriverAssetId(),
                    driverAsset3.lazyToString()));

        }


        @Override
        public Iterator<KeyValue> iterator() {
            return assetList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

//    @Test
//    public void invokeUnknownTransaction() {
//        DriverAssetTransfer driverAssetTransfer = new DriverAssetTransfer();
//        Context ctx = mock(Context.class);
//
//        Throwable thrown = catchThrowable(() -> {
//            driverAssetTransfer.unknownTransaction(ctx);
//        });
//
//        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
//                .hasMessage("Undefined contract method called");
//        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);
//
//        verifyZeroInteractions(ctx);
//    }

    @Nested
    class InvokeReadAssetTransaction {

        @Test
        public void whenAssetExists() {
            DriverAssetTransfer contract = new DriverAssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("NG956L"))
                    .thenReturn("{ \"driverAssetId\": \"NG956L\", \"licensePlate\": \"NG956L\", " +
                            "\"brand\": \"toyota\", \"emissionType\": \"6\", " +
                            "\"drivenKilometersOnRoad\": [4.5, 67.4, 30.68, 5.23, 0.0]," +
                            "\"rideCosts\": 23.5 }");

            DriverAsset asset = contract.readDriverAsset(ctx, "NG956L");

            assertThat(asset).usingRecursiveComparison().isEqualTo(new DriverAsset("NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    new double[]{4.5, 67.4, 30.68, 5.23, 0.0},
                    23.5));
        }

        @Test
        public void whenAssetDoesNotExist() {
            DriverAssetTransfer contract = new DriverAssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("NG956L")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.readDriverAsset(ctx, "NG956L");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset NG956L does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class InvokeCreateAssetTransaction {

//        @Test
//        public void whenAssetExists() {
//            DriverAssetTransfer contract = new DriverAssetTransfer();
//            Context ctx = mock(Context.class);
//            ChaincodeStub stub = mock(ChaincodeStub.class);
//            when(ctx.getStub()).thenReturn(stub);
//            when(stub.getStringState("NG956L"))
//                    .thenReturn("{ \"driverAssetId\": \"NG956L\"}");
//
//
//            Throwable thrown = catchThrowable(() -> {
//                contract.createDriverAsset(ctx, "NG956L");
//            });
//
//            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
//                    .hasMessage("Asset NG956L already exists");
//            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_ALREADY_EXISTS".getBytes());
//        }

//        @Test
//        public void whenAssetDoesNotExist() {
//            DriverAssetTransfer contract = new DriverAssetTransfer();
//            Context ctx = mock(Context.class);
//            ChaincodeStub stub = mock(ChaincodeStub.class);
//            when(ctx.getStub()).thenReturn(stub);
//            when(stub.getStringState("NG956L")).thenReturn("");
//
//            DriverAsset asset = contract.createDriverAsset(ctx, "NG956L");
//
//            assertThat(asset).isEqualTo(new DriverAsset("NG956L"));
//        }
//    }
//
//    @Test
//    void invokeGetAllAssetsTransaction() {
//        Genson genson = new Genson();
//        DriverAssetTransfer contract = new DriverAssetTransfer();
//        Context ctx = mock(Context.class);
//        ChaincodeStub stub = mock(ChaincodeStub.class);
//        when(ctx.getStub()).thenReturn(stub);
//        when(stub.getStateByRange("", "")).thenReturn(new MockAssetResultsIterator());
//
//        String assets = contract.GetAllAssets(ctx);
//
//        List<DriverAsset> assetsList = genson.deserialize(assets, new GenericType<List<DriverAsset>>() {});
//
//        // Assert
//        assertThat(assets).isNotNull().hasSize(3);
//
//        // Define expected assets
//        DriverAsset expectedAsset1 = new DriverAsset("NG956L", "NG956L", "toyota", "6", new double[]{4.5, 67.4, 30.68, 5.23, 0.0}, 23.5);
//        DriverAsset expectedAsset2 = new DriverAsset("PK956L", "PK956L", "Prius", "2", new double[]{234.56, 67.4, 30.68, 43.23, 0.0}, 96.5);
//        DriverAsset expectedAsset3 = new DriverAsset("BH54KL", "BH54KL", "Mercedes", "3", new double[]{67.5, 67.4, 30.68, 5.23, 0.0}, 45.3);
//
//        // Assert that each asset is present and matches the expected values
//        assertThat(assetsList).containsExactly(expectedAsset1, expectedAsset2, expectedAsset3);
//    }
//


    @Nested
    class UpdateAssetTransaction {

        @Test
        public void whenAssetExists() {
            DriverAssetTransfer contract = new DriverAssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("NG956L"))
                    .thenReturn("{ \"driverAssetId\": \"NG956L\", \"licensePlate\": \"NG956L\", " +
                            "\"brand\": \"toyota\", \"emissionType\": \"6\", " +
                            "\"drivenKilometersOnRoad\": [4.5, 67.4, 30.68, 5.23, 0.0]," +
                            "\"rideCosts\": 23.5 }");

            DriverAsset asset = contract.updateDriverAsset(ctx, "NG956L",
                    "[0.0,0.0,0.13316690063476563,0.0,0.0]");
            assertThat(asset).usingRecursiveComparison().isEqualTo(new DriverAsset("NG956L",
                    "NG956L",
                    "toyota",
                    "6",
                    new double[]{39.0, 301.4, 121.18, 39.790000000000006, 3.5},
                    23.5));
        }

        @Test
        public void whenAssetDoesNotExist() {
            DriverAssetTransfer contract = new DriverAssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                DriverAsset asset = contract.updateDriverAsset(ctx, "NG956L",
                        "[0.0,0.0,0.13316690063476563,0.0,0.0]");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset NG956L does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class DeleteAssetTransaction {

        @Test
        public void whenAssetDoesNotExist() {
            DriverAssetTransfer contract = new DriverAssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("NG956L")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.deleteDriverAsset(ctx, "NG56L");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset NG956L does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }
    }


