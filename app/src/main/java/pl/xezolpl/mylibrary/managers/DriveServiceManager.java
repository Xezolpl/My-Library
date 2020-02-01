package pl.xezolpl.mylibrary.managers;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceManager {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private Drive driveService;

    public DriveServiceManager(Drive driveService) {
        this.driveService = driveService;
    }

    public Task<String> createDatabaseFile(){

        return Tasks.call(executor,() -> {

            File fileMetaData = new File();
            fileMetaData.setName("library_database");

            java.io.File db = new java.io.File("/data/data/pl.xezolpl.mylibrary/databases/library_database.db");

            FileContent content = new FileContent(null, db);

            File finalFile = null;
            try{
                finalFile = driveService.files().create(fileMetaData, content).execute();
            }catch (Exception e){
                e.printStackTrace();
            }

            if (finalFile == null){
                throw new IOException("Null result when creating database google object");
            }


        return finalFile.getId();});
    }
}
