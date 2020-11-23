package net.artux.pda.utils;

import android.app.Notification;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

public class MyDownloadService extends DownloadService {

    private DownloadManager downloadManager;
    private SimpleCache downloadCache;
    private DefaultHttpDataSourceFactory dataSourceFactory;

    public MyDownloadService(){
        super(1);
    }

    protected MyDownloadService(int foregroundNotificationId) {
        super(foregroundNotificationId);
    }

    @Override
    protected DownloadManager getDownloadManager() {
        if (downloadManager==null) {
            DatabaseProvider databaseProvider = new ExoDatabaseProvider(getApplicationContext());

// A download cache should not evict media, so should use a NoopCacheEvictor.
            downloadCache = new SimpleCache(
                    new File("/"),
                    new NoOpCacheEvictor(),
                    databaseProvider);

// Create a factory for reading the data from the network.
            dataSourceFactory = new DefaultHttpDataSourceFactory();

// Choose an executor for downloading data. Using Runnable::run will cause each download task to
// download data on its own thread. Passing an executor that uses multiple threads will speed up
// download tasks that can be split into smaller parts for parallel execution. Applications that
// already have an executor for background downloads may wish to reuse their existing executor.
            Executor downloadExecutor = Runnable::run;

// Create the download manager.
            downloadManager = new DownloadManager(
                    getApplicationContext(),
                    databaseProvider,
                    downloadCache,
                    dataSourceFactory,
                    downloadExecutor);

// Optionally, setters can be called to configure the download manager.
            downloadManager.addListener(new DownloadManager.Listener() {
                @Override
                public void onDownloadChanged(DownloadManager downloadManager, Download download, @Nullable Exception finalException) {

                }
            });

            downloadManager.setMaxParallelDownloads(3);
        }
        return downloadManager;
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {
        return null;
    }

    public SimpleCache getDownloadCache() {
        return downloadCache;
    }


    public DefaultHttpDataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }
}
