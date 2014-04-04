package petit.bin.example.openflow;

import petit.bin.anno.field.EnumItem;
import petit.bin.anno.field.EnumItem.NumberedEnum;

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
	public enum OpenflowVersion implements NumberedEnum {
	    OFP10_VERSION(0x01),
	    OFP11_VERSION(0x02),
	    OFP12_VERSION(0x03),
	    OFP13_VERSION(0x04),
	    OFP14_VERSION(0x05),
	    OFP15_VERSION(0x06),
	    ;
	    
	    /**
	     * 値から {@link OpenflowMessageType} へのマッピング
	     */
	    private static final OpenflowVersion[] MAPPER;
	    
	    static {
	    	MAPPER = new OpenflowVersion[0x100];
	    	for (final OpenflowVersion elm : OpenflowVersion.values())
	    		MAPPER[elm.VERSION] = elm;
	    }
	    
	    /**
	     * 元の openflow.hの値
	     */
	    public final int VERSION;
	    
	    private OpenflowVersion(final int version) {
			VERSION = version;
		}
	    
	    @Override
	    public int toNumber() {
	    	return VERSION;
	    }
	    
	    /**
	     * {@link EnumItem} アノテーションで指定する，数値から列挙値を解決するメソッド
	     * 
	     * @param v 数値
	     * @return 解決された列挙値
	     */
	    public static final OpenflowVersion fromNumber(final int v) {
	    	if (MAPPER[v & 0xff] == null)
	    		throw new IllegalArgumentException("Openflow version " + v + " is undefined");
	    	return MAPPER[v & 0xff];
	    }
	    
	}
	
	public static enum OpenflowMessageType implements NumberedEnum {
		/* Immutable messages. */
		
		/** Symmetric message */
		OFPT_HELLO(0),
		/** Symmetric message */
		OFPT_ERROR(1),
		/** Symmetric message */
		OFPT_ECHO_REQUEST(2),
		/** Symmetric message */
		OFPT_ECHO_REPLY(3),
		/** Symmetric message */
		OFPT_EXPERIMENTER(4),
		
		
		/* Switch configuration messages. */
		
		/** Controller/switch message */
		OFPT_FEATURES_REQUEST(5),
		/** Controller/switch message */
		OFPT_FEATURES_REPLY(6),
		/** Controller/switch message */
		OFPT_GET_CONFIG_REQUEST(7),
		/** Controller/switch message */
		OFPT_GET_CONFIG_REPLY(8), 
		/** Controller/switch message */
		OFPT_SET_CONFIG(9),
		
		
		/* Asynchronous messages. */
		
		/** Async message */
		OFPT_PACKET_IN(10),
		/** Async message */
		OFPT_FLOW_REMOVED(11),
		/** Async message */
		OFPT_PORT_STATUS(12),
		
		
		/* Controller command messages. */
		
		/** Controller/switch message */
		OFPT_PACKET_OUT(13),
		/** Controller/switch message */
		OFPT_FLOW_MOD(14),
		/** Controller/switch message */
		OFPT_GROUP_MOD(15),
		/** Controller/switch message */
		OFPT_PORT_MOD(16),
		/** Controller/switch message */
		OFPT_TABLE_MOD(17),
		
		
		/* Multipart messages. */
		
		/** Controller/switch message */
		OFPT_MULTIPART_REQUEST(18),
		/** Controller/switch message */
		OFPT_MULTIPART_REPLY(19),
		
		
		/* Barrier messages. */
		
		/** Controller/switch message */
		OFPT_BARRIER_REQUEST(20),
		/** Controller/switch message */
		OFPT_BARRIER_REPLY(21),
		
		
		/* Controller role change request messages. */
		
		/** Controller/switch message */
		OFPT_ROLE_REQUEST(24),
		/** Controller/switch message */
		OFPT_ROLE_REPLY(25),
		
		
		/* Asynchronous message configuration. */
		
		/** Controller/switch message */
		OFPT_GET_ASYNC_REQUEST(26),
		/** Controller/switch message */
		OFPT_GET_ASYNC_REPLY(27),
		/** Controller/switch message */
		OFPT_SET_ASYNC(28),
		
		
		/* Meters and rate limiters configuration messages. */
		
		/** Controller/switch message */
		OFPT_METER_MOD(29),
		
		
		/* Controller role change event messages. */
		
		/** Async message */
		OFPT_ROLE_STATUS(30),
		
		
		/* Asynchronous messages. */
		
		/** Async message */
		OFPT_TABLE_STATUS(31),
		
		
		/* Request forwarding by the switch. */
		
		/** Async message */
		OFPT_REQUESTFORWARD(32),
		
		
		/* Bundle operations (multiple messages as a single operation). */
		
		/** Bundle operations (multiple messages as a single operation). */
		OFPT_BUNDLE_CONTROL(33),
		/** Bundle operations (multiple messages as a single operation). */
		OFPT_BUNDLE_ADD_MESSAGE(34),
		;
		
		/**
		 * 値から {@link OpenflowMessageType} へのマッピング
		 */
		private static final OpenflowMessageType[] MAPPER;
		
		static {
			MAPPER = new OpenflowMessageType[0x100];
			for (final OpenflowMessageType elm : OpenflowMessageType.values())
				MAPPER[elm.VALUE] = elm;
		}
		
		/**
		 * 元の openflow.hの値
		 */
		public final int VALUE;
		
		private OpenflowMessageType(final int value) {
			VALUE = value;
		}
		
		@Override
		public int toNumber() {
			return VALUE;
		}
		
		/**
	     * {@link EnumItem} アノテーションで指定する，数値から列挙値を解決するメソッド
	     * 
	     * @param v 数値
	     * @return 解決された列挙値
	     */
	    public static final OpenflowMessageType fromNumber(final int v) {
	    	if (MAPPER[v & 0xff] == null)
	    		throw new IllegalArgumentException("Openflow message type " + v + " is undefined");
	    	return MAPPER[v & 0xff];
	    } 
		
	}
	
}
