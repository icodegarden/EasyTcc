package github.easytcc.support;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import github.easytcc.configuration.TccProperties;
import github.easytcc.context.TransactionContextHolder;
import github.easytcc.factory.SpringBeanFactory;

/**
 * @author Fangfang.Xu
 *
 */
public class ContextHeaders {

	public static final String XID = "X-Easytcc-Xid";
	public static final String APPLICTION = "X-Easytcc-Appliction";
	public static final String TRANSACTIONID = "X-Easytcc-TransactionId";

	static TccProperties tccProperties;

	final Iterator<KeyValuePair> iterator;

	private ContextHeaders() {
		if (tccProperties == null) {
			tccProperties = SpringBeanFactory.getBean(TccProperties.class);
		}

		List<KeyValuePair> pairs = new LinkedList<KeyValuePair>();
		pairs.add(new KeyValuePair(XID, TransactionContextHolder.getContext().getXid().id()));
		pairs.add(new KeyValuePair(APPLICTION, tccProperties.getApplication()));
		pairs.add(new KeyValuePair(TRANSACTIONID,
				TransactionContextHolder.getContext().getLocalTransaction().getTransactionId()));
		iterator = pairs.iterator();
	}

	public static ContextHeaders get() {
		return new ContextHeaders();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public KeyValuePair next() {
		return iterator.next();
	}

	public static class KeyValuePair {
		String key;
		String value;

		KeyValuePair(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "KeyValuePair [key=" + key + ", value=" + value + "]";
		}

	}
}
