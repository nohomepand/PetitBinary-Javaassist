package petit.bin.example.openflow;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.field.EnumItem;
import petit.bin.anno.field.ExternStruct;
import petit.bin.anno.field.UInt16;
import petit.bin.example.openflow.OpenflowCommon.OpenflowMessageType;
import petit.bin.example.openflow.OpenflowCommon.OpenflowVersion;
import petit.bin.store.Store.SerializationByteOrder;

/**
 * Openflow の制御通信上のパケットを表す
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Struct(byteOrder = SerializationByteOrder.BIG_ENDIAN)
public final class OpenflowPacket {
	
	/**
	 *  Header on all OpenFlow packets.<br />
	 *  https://github.com/horms/openvswitch/blob/master/include/openflow/openflow-common.h
	 *  OpenflowPacketHeader
	 */
	@Struct
	public static final class OpenflowPacketHeader {
		
		/** An OpenFlow version number, e.g. OFP10_VERSION. */
		@StructMember(0)
		@EnumItem(storeType = byte.class, enumResolver = "fromNumber")
		protected OpenflowVersion _version;
		
		/** One of the OFPT_ constants. */
		@StructMember(1)
		@EnumItem(storeType = byte.class, enumResolver = "fromNumber")
		protected OpenflowMessageType _type;
		
		/** Length including this OpenflowPacketHeader. */
		@StructMember(2)
		@UInt16
		protected int _length;
		
		/**
		 *  Transaction id associated with this packet.
		 *  Replies use the same id as was in the request
		 *   to facilitate pairing. */
		@StructMember(3)
		protected int _xid;
	}
	
	@StructMember(0)
	protected OpenflowPacketHeader _header;
	
	@StructMember(1)
	@ExternStruct("resolveBody")
	protected OpenflowPacketBody _body;
	
	protected final <T extends OpenflowPacketBody> T resolveBody() {
		
	}
	
}
