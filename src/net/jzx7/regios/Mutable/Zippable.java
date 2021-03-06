package net.jzx7.regios.Mutable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.jzx7.regios.Permissions.PermissionsCore;
import net.jzx7.regiosapi.entity.RegiosPlayer;

public class Zippable extends PermissionsCore {
	
	private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

	public static void zipDir(File dirObj, File zipFileName, String name, RegiosPlayer p) throws Exception {
		p.sendMessage("<DGREEN>" + "[Regios] Creating .zip backup file...");
		for(char c : name.toCharArray()){
			for(char il : ILLEGAL_CHARACTERS){
				if(c == il){
					p.sendMessage("<RED>" + "[Regios] Invalid token " + "<YELLOW>" + c + "<RED>" + " in file name!");
					return;
				}
			}
		}
		if (zipFileName.exists()) {
			p.sendMessage("<RED>" + "[Regios] A Backup already exists with the name " + "<BLUE>" + name);
			return;
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		addDir(dirObj, out);
		out.close();
		p.sendMessage("<DGREEN>" + "[Regios] .zip backup file created successfully!");
	}

	static void addDir(File dirObj, ZipOutputStream out) throws IOException {
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[4096];
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(files[i], out);
				continue;
			}
			FileInputStream in = new FileInputStream(files[i]);
			out.putNextEntry(new ZipEntry(files[i].toString()));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
		if (files.length == 0) {
			File f = new File(dirObj + File.separator + "Placeholder.placeholder");
			f.createNewFile();
			FileInputStream in = new FileInputStream(f);
			out.putNextEntry(new ZipEntry(f.toString()));
			int len;
			while ((len = in.read(tmpBuf)) > 0) {
				out.write(tmpBuf, 0, len);
			}
			out.closeEntry();
			in.close();
			f.delete();
		}
	}

}
