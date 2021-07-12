package com.coderstory.flyme.fragment;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.coderstory.flyme.R;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.tools.Misc;
import com.coderstory.flyme.tools.SnackBarUtils;
import com.coderstory.flyme.tools.hostshelper.FileHelper;
import com.topjohnwu.superuser.Shell;

import java.util.List;


public class CleanFragment extends BaseFragment {
    @SuppressLint("HandlerLeak")
    private final Handler hComplete = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ((Button) $(R.id.button)).setText(R.string.starting_clean);
            $(R.id.button).setEnabled(true);
            SnackBarUtils.makeShort($(R.id.button), getString(R.string.clean_success)).info();
        }
    };
    Thread th;
    private TextView tvClean = null;
    @SuppressLint("HandlerLeak")
    private final Handler hInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvClean.append((String) msg.obj);
        }
    };

    public CleanFragment() {
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        $(R.id.button).setOnClickListener(view -> threadClean());
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_clean;
    }

    private void sendMessageStr(String str) {
        Message msg = new Message();
        msg.obj = str;
        hInfo.sendMessage(msg);
    }

    public void threadClean() {
        tvClean = $(R.id.tvClean);
        tvClean.setMovementMethod(ScrollingMovementMethod.getInstance());
        ((Button) $(R.id.button)).setText(R.string.cleaning);
        tvClean.append(getString(R.string.view_start_clean));
        $(R.id.button).setEnabled(false);
        th = new Thread(() -> {
            long totalSize = 0L; // K
            Misc.isProcessing = true;
            List<String> ret = Shell.su("find /data/data/ -type d -name \"cache\"").exec().getOut();
            CacheSize cs;
            for (String s : ret) {
                cs = getSize(s);
                if (cs.size > 16) { // clean only above 16K
                    deleteCache(s);
                    sendMessageStr(getString(R.string.view_clean_cache, s, cs.sizeReadable));
                    totalSize += cs.size;

                }
            }
            // clean anr log
            CacheSize anrSize = getSize("/data/anr/");
            deleteAnrLog();
            sendMessageStr(getString(R.string.view_clean_anr, anrSize.sizeReadable));
            totalSize += anrSize.size;

            sendMessageStr(getString(R.string.view_clean_complete, FileHelper.getReadableFileSize(totalSize)));
            hComplete.sendEmptyMessage(0);
            Misc.isProcessing = false;
        });
        th.start();
    }


    private CacheSize getSize(String path) {
        try {
            String result = Shell.su(String.format("du -s -k \"%s\"", path)).exec().getOut().get(0);
            String sizeStr = result.substring(0, result.indexOf('\t')).trim();
            long size;
            size = Long.parseLong(sizeStr);
            return new CacheSize(sizeStr + "K", size);
        } catch (Exception e) {
            return new CacheSize(0 + "K", 0);
        }

    }

    private void deleteCache(String path) {
        Shell.su(String.format("rm -r \"%s\"", path)).exec();
    }

    private void deleteAnrLog() {
        Shell.su("rm -r /data/anr/*").exec();
    }

    private class CacheSize {
        String sizeReadable = "";
        long size = 0L;

        CacheSize(String sr, long s) {
            sizeReadable = sr;
            size = s;
        }
    }

}