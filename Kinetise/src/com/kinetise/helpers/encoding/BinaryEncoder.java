package com.kinetise.helpers.encoding;

/**
 * Defines common encoding methods for byte array encoders.
 * 
 * @author Apache Software Foundation
 * @version $Id: BinaryEncoder.java,v 1.10 2004/02/29 04:08:31 tobrien Exp $
 */
public interface BinaryEncoder extends Encoder {
    
    /**
     * Encodes a byte array and return the encoded data
     * as a byte array.
     * 
     * @param pArray Data to be encoded
     *
     * @return A byte array containing the encoded data
     * 
     * @throws EncoderException thrown if the Encoder
     *      encounters a failure condition during the
     *      encoding process.
     */
    byte[] encode(byte[] pArray) throws Exception;
}  
