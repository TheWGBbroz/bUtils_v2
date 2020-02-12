package nl.thewgbbroz.butils_v2.datafiles;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DataFile {
	private final File file;
	private final DataFileMaster master;
	
	private BukkitRunnable saveRunnable;
	private boolean saveScheduled = false;
	
	/**
	 * Creates a DataFile.
	 * Automatically calls {@link DataFile#load()}, which internally calls {@link DataFileMaster#load(DataInputStream)}.
	 */
	public DataFile(File file, DataFileMaster master) {
		this.file = file;
		this.master = master;
		
		load();
	}
	
	/**
	 * Saves the data file periodically.
	 * When this method gets called, any {@link DataFile#save()} calls will not result in a direct save.
	 * Instead, changes will be saved periodically like the name suggests.
	 */
	public void savePeriodically(JavaPlugin plugin, int ticksDelay) {
		stopPeriodicallySaving();
		
		saveRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(saveScheduled) {
					save(true);
					saveScheduled = false;
				}
			}
		};
		saveRunnable.runTaskTimerAsynchronously(plugin, ticksDelay, ticksDelay);
	}
	
	/**
	 * Wrapper function for {@link DataFile#savePeriodically(JavaPlugin, int)} to save periodically every minute.
	 */
	public void savePeriodicallyMinute(JavaPlugin plugin) {
		savePeriodically(plugin, 20 * 60);
	}
	
	/**
	 * Stops periodically auto-saving. Any {@link DataFile#save()} calls will now result in a blocking save again.
	 */
	public void stopPeriodicallySaving() {
		if(saveRunnable != null) {
			saveRunnable.cancel();
			saveRunnable = null;
		}
	}
	
	/**
	 * Calls {@link DataFileMaster#load(DataInputStream)} with the stream of the file on disk.
	 * If a file does not exist, {@link DataFile#save()} will get called first, which internally calles {@link DataFileMaster#load(DataInputStream)}.
	 */
	public void load() {
		if(!file.exists())
			save(true);
		
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(file));
			
			master.load(dis);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			// Always close!
			dis.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param force Whether or not to force a blocking save. If {@link DataFile#savePeriodically(JavaPlugin, int)} has been called before, this will not directly save
	 * the data file if force is false.
	 * 
	 * Calls {@link DataFileMaster#save(DataOutputStream)}.
	 */
	public void save(boolean force) {
		if(force || saveRunnable == null) {
			if(!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
				file.getParentFile().mkdirs();
			}
			
			DataOutputStream dos = null;
			try {
				dos = new DataOutputStream(new FileOutputStream(file));
				
				master.save(dos);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				// Always close!
				dos.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else {
			saveScheduled = true;
		}
	}
	
	/**
	 * Wrapper function for {@link DataFile#save(boolean)} with false as the force parameter.
	 */
	public void save() {
		save(false);
	}
	
	/**
	 * @return The data file on disk.
	 */
	public File getFile() {
		return file;
	}
}
