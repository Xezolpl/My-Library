package pl.xezolpl.mylibrary.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import pl.xezolpl.mylibrary.database.LibraryDatabase;

public class BackupManager {
    private final File dbOriginal;
    private final File dbBackupDir;
    private final String databaseFolderPath;
    private String time;
    private Context context;
    public final String standardCoverUrl;

    /**
     * Sets up the attributes
     *
     * @param context needed for getting database path
     */
    public BackupManager(Context context) {
        this.context = context;
        dbOriginal = context.getDatabasePath("library_database.db");
        databaseFolderPath = dbOriginal.getAbsolutePath().replace("library_database.db", "");
        dbBackupDir = new File(databaseFolderPath + "/backup");

        time = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(new Date());
        standardCoverUrl = context.getApplicationInfo().dataDir + "/files/covers/standard_cover.jpg";
    }

    /**
     * Checks if backup folder exists
     *
     * @return true if exists else false
     */
    private boolean checkBackupDir() {
        if (!dbBackupDir.exists()) {
            return dbBackupDir.mkdir();
        }
        return true;
    }

    /**
     * Creates a copy file of original database, and deletes old backups
     *
     * @return created, raw, empty copy
     * @throws IOException if something got wrong or the pathName was wrong
     */
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
     * @throws IOException in some cases createBackupFile can throw unexpected exceptions
     */
    public boolean importDatabaseFile(File importedFile) throws IOException {
        boolean result = false;
        if (!checkBackupDir()) {
            return false;
        }

        //Check the database exists
        if (!dbOriginal.exists()) {
            if(!dbOriginal.createNewFile()){
                return false;
            }
        }
        if (!importedFile.getName().contains(".db")){
            return false;
        }

        if (importedFile.getAbsolutePath().substring(0, 5).contains("/root")) {
            importedFile = new File(importedFile.getAbsolutePath().substring(5));
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
                LibraryDatabase.getDatabase(context.getApplicationContext()).close();
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

    public boolean exportDatabaseFile(File exportFile) {
        String fileName = "library_database-" + time + ".db";
        String dirPath = (Environment.getExternalStorageDirectory().getPath() +
                exportFile.getParentFile()).replace("document/primary:", "");
        String absoluteFilePath = dirPath + "/" + fileName;

        new File(dirPath + "/" + exportFile.getName()).delete();

        File dbCopy = new File(absoluteFilePath);

        boolean result = false;

        try {
            if (!dbCopy.exists()) {
                if (!dbCopy.createNewFile()) {
                    return false;
                }
            }

            FileChannel sourceChannel = new FileInputStream(dbOriginal).getChannel();
            FileChannel destChannel = new FileOutputStream(dbCopy).getChannel();

            if (sourceChannel.transferTo(0, sourceChannel.size(), destChannel) > 0) {
                result = true;
            }

            sourceChannel.close();
            destChannel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean downloadCover(String imgUrl){

        File filesDir = new File(imgUrl.replace("/covers/standard_cover.jpg",""));
        if (!filesDir.exists()) filesDir.mkdir();

        File coversDir = new File(imgUrl.replace("/standard_cover.jpg",""));
        if (!coversDir.exists()) coversDir.mkdir();

        try {
            return new CoverDownloader().execute(imgUrl).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class CoverDownloader extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL("https://i.pinimg.com/236x/90/49/e5/9049e5a4e33d49807bbbccf25339d266--old-books-vintage-books.jpg");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                //create a file to write bitmap data
                File f = new File(strings[0]);
                f.createNewFile();

                //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 80 , bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
