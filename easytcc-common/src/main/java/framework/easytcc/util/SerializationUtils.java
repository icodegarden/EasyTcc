package framework.easytcc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.common.serialize.hessian2.Hessian2Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.easytcc.exception.TccException;

/**
 * @author Fangfang.Xu
 *
 */
public class SerializationUtils {
	
	static Logger logger = LoggerFactory.getLogger(SerializationUtils.class);
	
	static Serialization s = new Hessian2Serialization();
	
	public static byte[] seriaObject(Object obj){
		ByteArrayOutputStream b = null;
		try {
			b = new ByteArrayOutputStream();
			ObjectOutput serialize = s.serialize(null, b);
			serialize.writeObject(obj);
			serialize.flushBuffer();
			return b.toByteArray();
		} catch (IOException e) {
			throw new TccException("seria IOException",e);
		}finally {
			if(b != null) {
				try {
					b.close();
				} catch (IOException e) {
					logger.error("seria close error",e);						
				}
			}
		}
	}
	
	public static Object deseriaObject(byte[] bytes){
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(bytes);
			ObjectInput deserialize = s.deserialize(null,byteArrayInputStream);
			return deserialize.readObject();
		} catch (Exception e) {
			throw new TccException("deseria IOException",e);
		}finally {
			if(byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					logger.error("deseria close error",e);
				}
			}
		}
	}
}
