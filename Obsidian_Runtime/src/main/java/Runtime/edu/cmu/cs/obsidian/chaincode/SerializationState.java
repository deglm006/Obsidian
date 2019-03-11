/**
 * Created by Miles Baker, 7/25/18.
 *
 * Holds the current serialization state of a contract.
 * It contains a map of GUIDs to already-deserialized
 * objects, and a chaincode stub to get objects out
 * of the blockchain that aren't already loaded. */
package edu.cmu.cs.obsidian.chaincode;

import org.hyperledger.fabric.shim.ChaincodeStub;
import edu.cmu.cs.obsidian.chaincode.ObsidianSerialized;
import java.util.Map;
import java.util.HashMap;

public class SerializationState {
    private Map<String, ObsidianSerialized> guidMap;
    private ChaincodeStub stub;
    UUIDFactory uuidFactory;

    public SerializationState() {
        guidMap = new HashMap<String, ObsidianSerialized>();
    }

    public void setStub(ChaincodeStub newStub) {
        stub = newStub;
        uuidFactory = new UUIDFactory(newStub.getTxId());
    }

    public ChaincodeStub getStub() {
        return stub;
    }

    public ObsidianSerialized getEntry(String guid) {
        return guidMap.get(guid);
    }

    public void putEntry(String guid, ObsidianSerialized obj) {
        guidMap.put(guid, obj);
    }

    public void flushEntries() {
        // Fabric requires that all endorsers produce identical read sets.
        // But the peer on which instantiation happened will have some objects cached, resulting in an
        // inconsistent write set with the other peers.
        // To work around this problem, we flush all flushable objects.

        for (ObsidianSerialized obj : guidMap.values()) {
            obj.flush();
        }
    }

    public UUIDFactory getUUIDFactory() {
        return uuidFactory;
    }
}
