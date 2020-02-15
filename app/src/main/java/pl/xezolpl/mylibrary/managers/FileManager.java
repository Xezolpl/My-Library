package pl.xezolpl.mylibrary.managers;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileManager {
    private static final String TAG = "FileManager";


    private final File dbOriginal;
    private final File dbBackupDir;
    private final String databaseFolderPath;
    private String time;

    public FileManager(Context context) {
        dbOriginal = context.getDatabasePath("library_database.db");
        databaseFolderPath = dbOriginal.getAbsolutePath().replace("library_database.db", "");
        dbBackupDir = new File(databaseFolderPath + "/backup");

        time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    /**
     * Checks if backup folder exists
     *
     * @return true if exists else false
     */
    private boolean checkBackupDir() {
        if (!dbBackupDir.exists()) {
            if (!dbBackupDir.mkdir()) {
                return false;
            }
        }
        return true;
    }
    //Create a copy of original database and transport it to the backup folder

    private File createBackupFile() throws IOException {
        File[] listFiles = dbBackupDir.listFiles();
        int length = listFiles.length;

        //Delete old copies
        if (length > 2) {
            for (int i = 0; i < length - 2; i++) {
                listFiles[i].delete();
            }
        }

        File dbBackup = new File(databaseFolderPath + "/backup/db" + time + ".db");
        if (!dbBackup.exists()) {
            if (!dbBackup.createNewFile()) {
                throw new IOException("Cannot create backup file.");
            }

        }
        return dbBackup;
    }

    /**
     * Imports database
     *
     * @param importedFile file from intent / from anywhere
     * @return true if success else false
     * @throws IOException in some cases Channels can throw unexpected Exceptions
     */
    public boolean importDatabaseFile(File importedFile) throws IOException {
        boolean result = false;

        if (!checkBackupDir()) {
            return false;
        }

        //Check the database exists
        if (!dbOriginal.exists()) {
            return false;
        }

        File dbBackup = createBackupFile();

        try {
            FileChannel sourceChannel = new FileInputStream(dbOriginal).getChannel();
            FileChannel destChannel = new FileOutputStream(dbBackup).getChannel();

            //Transfer original to the copy -> if transferredBytes>0 then successfully transferred
            if (sourceChannel.transferTo(0, sourceChannel.size(), destChannel) > 0) {

                sourceChannel.close();
                destChannel.close();

                //Now let's transfer importedFile to the original database's place
                FileChannel importChannel = new FileInputStream(importedFile).getChannel();
                FileChannel destinationChannel = new FileOutputStream(dbOriginal).getChannel();

                //if transferredBytes>0 again then we successfully imported the database file
                if (importChannel.transferTo(0, importChannel.size(), destinationChannel) > 0) {
                    result = true;
                }

                importChannel.close();
                destinationChannel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return result;
    }

    public boolean exportDatabaseFile(File file) {
        return true;
    }
}
