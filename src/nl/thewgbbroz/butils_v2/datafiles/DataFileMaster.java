package nl.thewgbbroz.butils_v2.datafiles;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface DataFileMaster {
	/**
	 * This should save the data file contents to the DataOutputStream dos.
	 * Any thrown exceptions will be catched.
	 */
	public void save(DataOutputStream dos) throws Exception;
	
	/**
	 * This should load the data file contents from the DataInputStream dis.
	 * Any thrown exceptions will be catched.
	 */
	public void load(DataInputStream dis) throws Exception;
}
