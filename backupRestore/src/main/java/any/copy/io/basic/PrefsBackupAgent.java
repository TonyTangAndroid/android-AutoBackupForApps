package any.copy.io.basic;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class PrefsBackupAgent extends BackupAgentHelper {
    private static final String PREFS_BACKUP_KEY = "prefs";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, any.copy.io.basic.BackupRestoreActivity.PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
