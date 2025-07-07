package xyz.agmstudio.rencharm;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

public class RenCharmStartupActivity implements StartupActivity.DumbAware {
    private static final Logger LOG = Logger.getInstance(RenCharmStartupActivity.class);

    @SuppressWarnings("deprecation")
    @Override
    public void runActivity(@NotNull Project project) {
        TransactionGuard.submitTransaction(project, () ->
                ApplicationManager.getApplication().invokeLater(() ->
                        ApplicationManager.getApplication().runWriteAction(RenCharmStartupActivity::addHiddenFormats), ModalityState.NON_MODAL));
    }

    private static void addHiddenFormats() {
        FileTypeManager ftm = FileTypeManager.getInstance();
        String ignored = ftm.getIgnoredFilesList();
        if (!ignored.contains("*.rpyc")) {
            String updated = ignored + ";*.rpyc;*.rpyb";
            ftm.setIgnoredFilesList(updated);
            LOG.info("Added *.rpyc;*.rpyb to ignored list.");
            VirtualFileManager.getInstance().asyncRefresh(null);
        } else {
            LOG.info("*.rpyc already in ignored list.");
        }
    }
}
