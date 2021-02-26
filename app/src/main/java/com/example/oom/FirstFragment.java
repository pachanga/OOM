package com.example.oom;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FirstFragment extends Fragment {
    private int MEGS_TO_ALLOC = 100;

    private ArrayList<Bitmap> data_array = new ArrayList<Bitmap>();
    private int allocs = 0;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView text_view = (TextView)view.findViewById(R.id.textview_first);
        text_view.setText("Allocated: ???");

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++allocs;

                if(data_array.size() == 0) {
                    Bitmap tpl = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ALPHA_8);
                    for(int x=0;x<1024;++x) {
                        for(int y = 0; y < 1024; ++y)
                            tpl.setPixel(x, y, 1);
                    }
                    data_array.add(tpl);
                }

                for(int i=0;i<MEGS_TO_ALLOC;++i) {
                    Bitmap bitmap = data_array.get(0).copy(data_array.get(0).getConfig(), true);
                    data_array.add(bitmap);
                }

                updateInfo(text_view);
            }
        });

        view.findViewById(R.id.button_first2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data_array.size() <= 1)
                    return;
                --allocs;

                for(int i=0;i<MEGS_TO_ALLOC;++i) {
                    Bitmap bitmap = data_array.get(0);
                    bitmap.recycle();
                    data_array.remove(0);
                }

                updateInfo(text_view);
            }
        });
    }

    void updateInfo(TextView text_view)
    {
        text_view.setText(String.format("Allocated: %dMb, Prv %dMb, PSS %dMb,  VM %dMb,  is low: %s",
                allocs * MEGS_TO_ALLOC,
                getPrivateCleanAndDirtyMemory() / 1024,
                getVMMemory() / 1024,
                getPSSMemory() / 1024,
                getMemInfo().lowMemory)
        );
    }

     ActivityManager.MemoryInfo getMemInfo() {
        ActivityManager activityManager = (ActivityManager) this.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    int getPrivateCleanAndDirtyMemory() {
        Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memInfo);
        int res = memInfo.getTotalPrivateDirty();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            res += memInfo.getTotalPrivateClean();

        return res;
    }

    int getPSSMemory() {
        Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memInfo);
        return memInfo.getTotalPss();
    }

    static String readFile(String path) throws IOException {
        BufferedReader reader = null;
        try {
            StringBuilder output = new StringBuilder();
            reader = new BufferedReader(new FileReader(path));
            for (String line = reader.readLine(), newLine = ""; line != null; line = reader.readLine()) {
                output.append(newLine).append(line);
                newLine = "\n";
            }
            return output.toString();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    int getVMMemory() {
        long pageSize = 4 * 1024; //`sysconf(_SC_PAGESIZE)`
        try {
            String stats = readFile("/proc/self/statm");
            String[] statsArr = stats.split("\t|\n|\\s", 3);

            if (statsArr.length < 2)
                return 0;

            return (int) ((Long.parseLong(statsArr[1]) * pageSize) / 1024);
        }
        catch(Exception ignored) {
            return 0;
        }
    }
}