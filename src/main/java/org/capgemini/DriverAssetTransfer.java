package org.capgemini;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import javax.ws.rs.core.Response;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

@Contract(
        name = "driverAsset",
        info = @Info(
                title = "Driver Asset Transfer",
                description = "Capgemini initial contract for driver assets",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Capgemini"
                ),
                contact = @Contact(
                        email = "Bronsveld.T@gmail.com", //placeholder
                        name = "Thomas Bronsveld" //placeholder
                )
        )
)
@Default
public class DriverAssetTransfer {

    private final Genson genson = new Genson();

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<DriverAsset> queryResults = new ArrayList<>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            DriverAsset asset = genson.deserialize(result.getStringValue(), DriverAsset.class);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);
        System.out.println(response);
        return response;
    }

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean driverAssetExists(final Context ctx, final String driverId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(driverId);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    /**
     * Creates a new driverAsset on the blockchain network.
     *
     *
     * @param ctx
     * @param driverId
     * @return
     */

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DriverAsset createDriverAsset(final Context ctx,
                                      final String driverId) {

        ChaincodeStub stub = ctx.getStub();
        if(driverAssetExists(ctx, driverId)){
            String errorMessages = String.format("Asset %s already exists", driverId);
            System.out.println(errorMessages);
            throw new ChaincodeException(errorMessages, DriverAssetTransfer.AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        DriverAsset driverAsset = new DriverAsset(driverId);

        String sortedJson = genson.serialize(driverAsset);
        stub.putStringState(driverId, sortedJson);
        return driverAsset;
    }

    /**
     * Retrieves an asset with the specified ID from the ledger
     * @param ctx
     * @param driverId
     * @return
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public DriverAsset readDriverAsset(final Context ctx, final String driverId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(driverId);

        if(assetJSON == null || assetJSON.isEmpty()){
            String errorMessage = String.format("Asset %s does not exist", driverId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DriverAssetTransfer.AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        System.out.println(assetJSON);
        DriverAsset driverAsset = genson.deserialize(assetJSON, DriverAsset.class);
        return driverAsset;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DriverAsset updateDriverAsset(final Context ctx,
                                      final String driverId,
                                      final double[] distanceTravelledOn) {

        ChaincodeStub stub = ctx.getStub();

        if(!driverAssetExists(ctx, driverId)){
            String errorMessages = String.format("Asset %s does not exist", driverId);
            System.out.println(errorMessages);
            throw new ChaincodeException(errorMessages, DriverAssetTransfer.AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        String assetJSON =  stub.getStringState(driverId);
        DriverAsset driverAsset = genson.deserialize(assetJSON, DriverAsset.class);
        driverAsset.addDrivenKilometersOnRoad(distanceTravelledOn);
        String sortedJson = genson.serialize(driverAsset);
        stub.putStringState(driverAsset.getDriverAssetId(), sortedJson);
        return driverAsset;
    }

    /**
     *
     * @param ctx
     * @param driverId
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Response deleteDriverAsset(final Context ctx, final String driverId) {
        ChaincodeStub stub = ctx.getStub();

        if(!driverAssetExists(ctx,driverId)) {
            String errorMessage = String.format("Road asset %s does not exist", driverId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DriverAssetTransfer.AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        stub.delState(driverId);
        return Response.ok("The driverAsset was deleted successfully").build();
    }

}
