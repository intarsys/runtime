package de.intarsys.tools.oid;

import java.util.UUID;

public class UUIDGenerator extends CommonOIDGenerator<String> {

	@Override
	public String createOID() {
		return UUID.randomUUID().toString();
	}

}
