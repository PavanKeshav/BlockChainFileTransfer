package dbc.core;
import dbc.BCbuilder;
import dbc.utils.StringUtil;

import java.security.*;
import java.util.ArrayList;

public class Transaction {

    public String transactionId; //Contains a hash of transaction*
    public PublicKey sender; //Senders address/public key.
    public PublicKey reciepient; //Recipients address/public key.
    public data value; //Contains the amount we wish to send to the recipient.
    public byte[] signature; //This is to prevent anybody else from spending funds in our wallet.

    //public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; //A rough count of how many transactions have been generated

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, data value)//,  ArrayList<TransactionInput> inputs)
    {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        //this.inputs = inputs;
    }

    public boolean processTransaction() {

        if(!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //Gathers transaction inputs (Making sure they are unspent):
        /*for(TransactionInput i : inputs) {
            i.UTXO = BCbuilder.UTXOs.get(i.transactionOutputId);
        }

        //Checks if transaction is valid:
        if(getInputsValue() < BCbuilder.minimumTransaction) {
            System.out.println("Transaction Inputs too small: " + getInputsValue());
            System.out.println("Please enter the amount greater than " + BCbuilder.minimumTransaction);
            return false;
        }*/

        //Generate transaction outputs:
        //float leftOver = getInputsValue() - value; //get value of inputs then the left over change:

        transactionId = calulateHash();
        //outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
        //outputs.add(new TransactionOutput( this.sender, new data(null,null,"Acknowledgement"),transactionId)); //send the ack to sender

        //Add outputs to Unspent list
        /*for(TransactionOutput o : outputs) {
            BCbuilder.UTXOs.put(o.id , o);
        }*/

        //Remove transaction inputs from UTXO lists as spent:
        /*for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            BCbuilder.UTXOs.remove(i.UTXO.id);
        }*/

        return true;
    }
/*
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it, This behavior may not be optimal.
            total += i.UTXO.value;
        }
        return total;
    }*/

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + value	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + value	;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /*public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }*/

    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        value + sequence
        );
    }
}
