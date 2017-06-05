package farto.cleva.guilherme.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.IOUtils;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

public class Main {

	public static final String ZIP_FILE = Main.class.getResource("/files/Shape_Zip.zip").getPath();
	public static final String RAR_FILE = Main.class.getResource("/files/Shape_Rar.rar").getPath();
	public static final String _7Z_FILE = Main.class.getResource("/files/Shape_7Zip.7z").getPath();

	public static final String TEMP_DIR = "src/main/resources/temp/";

	public static final String ZIP_DIR = TEMP_DIR + "zip";
	public static final String RAR_DIR = TEMP_DIR + "rar";
	public static final String _7Z_DIR = TEMP_DIR + "_7z";

	public static void main(String[] args) {
		try {
			validateDirectories(ZIP_DIR, RAR_DIR, _7Z_DIR);

			/* Extracting from ZIP file */
			extract(ZIP_FILE);

			/* Extracting from RAR file */
			extract(RAR_FILE);

			/* Extracting from 7ZIP file */
			extract(_7Z_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void validateDirectories(String... directories) {
		if (directories != null && directories.length > 0) {
			for (String directoryPath : directories) {
				File directory = new File(directoryPath);

				if (!directory.exists()) {
					directory.mkdirs();
				}
			}
		}
	}

	private static void extract(String file) throws Exception {
		System.out.println("Extracting files from: [" + file + "]");

		if (file.endsWith(".zip")) {
			extractZip(file);
		} else if (file.endsWith(".rar")) {
			extractRar(file);
		} else if (file.endsWith(".7z")) {
			extract7Zip(file);
		}
	}

	private static void extractZip(String file) throws Exception {
		ZipFile zipFile = new ZipFile(file);

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();

			System.out.println(entry.getName());

			File entryDestination = new File(ZIP_DIR, entry.getName());

			if (entry.isDirectory()) {
				entryDestination.mkdirs();
			} else {
				entryDestination.getParentFile().mkdirs();

				InputStream in = zipFile.getInputStream(entry);
				OutputStream out = new FileOutputStream(entryDestination);

				IOUtils.copy(in, out);
				IOUtils.closeQuietly(in);

				out.close();
			}
		}

		zipFile.close();
	}

	private static void extractRar(String file) throws Exception {
		Archive rarFile = new Archive(new File(file));

		FileHeader fh = rarFile.nextFileHeader();

		while (fh != null) {
			System.out.println(fh.getFileNameString());

			File entryDestination = new File(RAR_DIR, fh.getFileNameString());

			if (entryDestination.isDirectory()) {
				entryDestination.mkdirs();
			} else {
				entryDestination.getParentFile().mkdirs();

				OutputStream out = new FileOutputStream(entryDestination);

				rarFile.extractFile(fh, out);

				out.close();
			}

			fh = rarFile.nextFileHeader();
		}

		rarFile.close();
	}

	private static void extract7Zip(String file) throws Exception {
		SevenZFile sevenZFile = new SevenZFile(new File(file));

		SevenZArchiveEntry entry = sevenZFile.getNextEntry();

		while (entry != null) {
			System.out.println(entry.getName());

			File entryDestination = new File(_7Z_DIR, entry.getName());

			FileOutputStream out = new FileOutputStream(entryDestination);

			byte[] content = new byte[(int) entry.getSize()];

			sevenZFile.read(content, 0, content.length);

			out.write(content);
			out.close();

			entry = sevenZFile.getNextEntry();
		}

		sevenZFile.close();
	}

}
