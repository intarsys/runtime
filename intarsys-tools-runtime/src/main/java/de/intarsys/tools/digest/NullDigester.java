package de.intarsys.tools.digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import de.intarsys.tools.stream.StreamTools;

public class NullDigester implements IDigester {

	public class NullDigest extends Digest {

		public NullDigest(byte[] digest) {
			super(ALGORITHM_NAME, digest);
		}

		@Override
		public byte[] getEncoded() throws IOException {
			return getBytes();
		}

	}

	private static final String ALGORITHM_NAME = "NULL";

	private ByteArrayOutputStream os = new ByteArrayOutputStream() {
		@Override
		public void reset() {
			super.reset();
			Arrays.fill(buf, (byte) 0x00);
		}
	};

	@Override
	public IDigest digest(byte[] bytes) {
		os.writeBytes(null);
		return digestFinal();
	}

	@Override
	public IDigest digestFinal() {
		return new NullDigest(os.toByteArray());
	}

	@Override
	public void digestUpdate(InputStream is) throws IOException {
		StreamTools.copy(is, os);
	}

	@Override
	public String getAlgorithmName() {
		return ALGORITHM_NAME;
	}

	@Override
	public int getDigestLength() {
		return os.size();
	}

	@Override
	public void reset() {
		os.reset();
	}

}
