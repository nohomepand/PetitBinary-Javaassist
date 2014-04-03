package petit.bin.example.openflow;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;

/**
 * from
 *     https://github.com/horms/openvswitch/blob/master/include/openvswitch/types.h
 *     https://github.com/horms/openvswitch/blob/master/include/openflow/openflow-common.h
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
public final class OpenflowCommon {
	
	/**
	 * Version number:
	 * Non-experimental versions released: 0x01 0x02
	 * Experimental versions released: 0x81 -- 0x99
	 */
	public enum OpenflowVersion {
	    OFP10_VERSION(0x01),
	    OFP11_VERSION(0x02),
	    OFP12_VERSION(0x03),
	    OFP13_VERSION(0x04),
	    OFP14_VERSION(0x05),
	    OFP15_VERSION(0x06),
	    ;
	    
	    public final int VERSION;
	    
	    private OpenflowVersion(final int version) {
			VERSION = version;
		}
	}
	

	
	public static interface 
	
}
