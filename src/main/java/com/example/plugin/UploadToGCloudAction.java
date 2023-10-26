package com.example.plugin;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.nio.file.Paths;
import java.nio.file.Files;

public class UploadToGCloudAction extends AnAction {

    private static final String BUCKET_NAME = "arctic-moon-403008.appspot.com";
    private static final String KEY_PATH = "E:\\SWIBL\\arctic-moon-403008-ecb55d4c6b72.json";
    private static final String FILE_NAME = "Library-0.0.1-SNAPSHOT.jar";

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        String projectPath = project.getBasePath();

        try {
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(Files.newInputStream(Paths.get(KEY_PATH))))
                    .build()
                    .getService();

            String objectName = FILE_NAME;
            assert projectPath != null;
            byte[] bytes = Files.readAllBytes(Paths.get(projectPath, objectName));

            BlobId blobId = BlobId.of(BUCKET_NAME, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            storage.create(blobInfo, bytes);

            Messages.showInfoMessage("File uploaded successfully!", "Upload to Google Cloud");
        } catch (Exception ex) {
            Messages.showErrorDialog("Failed to upload file: " + ex.getMessage(), "Error");
        }
    }
}
