package com.tony.backup.demo;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.SharedPreferences;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaBackupAgent extends BackupAgent {
    private static final String BACKUP_KEY = "BACKUP_KEY";

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        Log.d(com.tony.backup.demo.BackupRestoreActivity.TAG, "onBackup");
        SharedPreferences sp = getSharedPreferences(com.tony.backup.demo.BackupRestoreActivity.PREFS, MODE_PRIVATE);
        if (sp.contains(com.tony.backup.demo.BackupRestoreActivity.KEY)) {
            String val = sp.getString(com.tony.backup.demo.BackupRestoreActivity.KEY, null);
            String oldVal = null;
            if (val != null) {
                FileInputStream fis = new FileInputStream(oldState.getFileDescriptor());
                DataInputStream dis = new DataInputStream(fis);

                try {
                    oldVal = dis.readUTF();
                } catch (Exception e) {
                    oldVal = null;
                }
                dis.close();

                if (oldVal == null || !oldVal.equals(val)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);

                    dos.writeUTF(val);
                    dos.close();

                    byte[] buf = baos.toByteArray();
                    data.writeEntityHeader(BACKUP_KEY, buf.length);
                    data.writeEntityData(buf, buf.length);
                }

                writeNewState(val, newState);
            }
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        Log.d(com.tony.backup.demo.BackupRestoreActivity.TAG, "onRestore");
        String val = null;
        while (data.readNextHeader()) {
            String key = data.getKey();
            int size = data.getDataSize();

            if (key.equals(BACKUP_KEY)) {
                byte[] buf = new byte[size];
                data.readEntityData(buf, 0, size);
                ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                DataInputStream dis = new DataInputStream(bais);
                val = dis.readUTF();
                dis.close();
            } else {
                data.skipEntityData();
            }
        }
        if (val != null) {
            writeNewState(val, newState);
            SharedPreferences sp = getSharedPreferences(com.tony.backup.demo.BackupRestoreActivity.PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(com.tony.backup.demo.BackupRestoreActivity.KEY, val);
            editor.commit();
        }
    }

    private void writeNewState(String val, ParcelFileDescriptor newState) throws IOException {
        FileOutputStream fos = new FileOutputStream(newState.getFileDescriptor());
        DataOutputStream dos = new DataOutputStream(fos);

        dos.writeUTF(val);
        dos.close();
    }

}
